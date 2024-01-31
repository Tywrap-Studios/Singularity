package redstonedev.singularity;

import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Clear {
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

        Dumps.create(server.getServerDirectory().toPath(), positions);
    }
}
