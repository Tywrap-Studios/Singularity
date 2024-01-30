package redstonedev.singularity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.ItemEntity;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Clear {
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void clearItems(MinecraftServer server) {
        int cleared = 0;
        List<GlobalPos> positions = new ArrayList<>();

        for (ServerLevel level : server.getAllLevels()) {
            for (Entity entity : level.getAllEntities()) {
                if (entity instanceof ItemEntity) {
                    // Items can stack as one entity, this needs to be accurate
                    for (int i = 0; i < ((ItemEntity) entity).getItem().getCount(); i++) {
                        positions.add(GlobalPos.of(entity.getLevel().dimension(), entity.blockPosition()));
                    }

                    entity.kill();
                }
            }
        }

        Util.chat(server, Component.translatable("singularity.chat.clearingItems.done"), false);
        Util.chat(server, Component.translatable("singularity.chat.clearingItems.count", cleared), !Singularity.CONFIG.clearOptions.showCleared);

        List<GlobalPos> detected = Detect.detectSingularities(server, positions);

        for (GlobalPos p : detected) {
            for (GlobalPos pos : positions) {
                if (Detector.isInRange(p, pos, Singularity.CONFIG.generalOptions.singularityRadius)) {
                    for (Entity entity : Objects.requireNonNull(server.getLevel(pos.dimension())).getAllEntities()) {
                        if (entity instanceof ItemEntity) {
                            cleared += ((ItemEntity) entity).getItem().getCount();

                            if (entity.blockPosition() == pos.pos()) {
                                entity.kill();
                            }
                        }
                    }
                }
            }
        }

        Path path = server.getServerDirectory().toPath().resolve("singularity").resolve("dump-" + LocalDate.now() + "-" + LocalTime.now().toString().replaceAll(":", "-") + ".txt");
        StringBuilder data = new StringBuilder();

        for (GlobalPos pos : positions) {
            data.append("(").append(pos.pos().getX()).append(",").append(pos.pos().getY()).append(",").append(pos.pos().getZ()).append(")\n");
        }

        String dataStr = data.toString();

        if (!path.getParent().toFile().exists()) {
            path.getParent().toFile().mkdirs();
        }

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile()));

            writer.write(dataStr);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
