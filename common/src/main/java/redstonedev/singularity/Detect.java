package redstonedev.singularity;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.ArrayList;
import java.util.List;

public class Detect {
    public static void detectSingularitiesAuto(MinecraftServer server) {
        List<GlobalPos> positions = new ArrayList<>();

        for (ServerLevel level : server.getAllLevels()) {
            for (Entity entity : level.getAllEntities()) {
                if (entity instanceof ItemEntity) {
                    // Items can stack as one entity, this needs to be accurate
                    for (int i = 0; i < ((ItemEntity) entity).getItem().getCount(); i++) {
                        positions.add(GlobalPos.of(entity.getLevel().dimension(), new BlockPos(entity.getBlockX(), entity.getBlockY(), entity.getBlockZ())));
                    }
                }
            }
        }

        detectSingularities(server, positions);
    }

    public static List<GlobalPos> detectSingularities(MinecraftServer server, List<GlobalPos> positions) {
        List<GlobalPos> detected = Detector.detect(positions);

        for (GlobalPos pos : detected) {
            Component x = Component.literal(Integer.toString(pos.pos().getX())).withStyle(ChatFormatting.AQUA);
            Component y = Component.literal(Integer.toString(pos.pos().getY())).withStyle(ChatFormatting.AQUA);
            Component z = Component.literal(Integer.toString(pos.pos().getZ())).withStyle(ChatFormatting.AQUA);

            Util.chat(server, Component.translatable("singularity.chat.detection.find", x, y, z), Singularity.CONFIG.generalOptions.displayPotentialsPublicly);
        }

        return detected;
    }
}
