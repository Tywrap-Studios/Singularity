package redstonedev.singularity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Clear {
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void clearItems(MinecraftServer server) {
        int cleared = 0;
        List<BlockPos> positions = new ArrayList<>();

        for (ServerLevel level : server.getAllLevels()) {
            for (Entity entity : level.getAllEntities()) {
                if (entity instanceof ItemEntity) {
                    // Items can stack as one entity, this needs to be accurate
                    for (int i = 0; i < ((ItemEntity) entity).getItem().getCount(); i++) {
                        cleared++;
                        positions.add(new BlockPos(entity.getBlockX(), entity.getBlockY(), entity.getBlockZ()));
                    }

                    entity.kill();
                }
            }
        }

        Util.chat(server, Component.translatable("singularity.chat.clearingItems.done"), false);
        Util.chat(server, Component.translatable("singularity.chat.clearingItems.count", cleared), !Singularity.CONFIG.clearOptions.showCleared);

        Detect.detectSingularities(server, positions);

        Path path = server.getServerDirectory().toPath().resolve("singularity").resolve("dump-" + LocalDate.now() + "-" + LocalTime.now().toString().replaceAll(":", "-") + ".txt");
        StringBuilder data = new StringBuilder();

        for (BlockPos pos : positions) {
            data.append("(").append(pos.getX()).append(",").append(pos.getY()).append(",").append(pos.getZ()).append(")\n");
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
