package redstonedev.singularity.commands;

import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import redstonedev.singularity.Singularity;
import redstonedev.singularity.util.ChatUtil;

public class Debug {
    public static void spawn(MinecraftServer server, ServerLevel level, BlockPos pos, ItemInput item, int count) {
        int rounds = ((count - count % 64) / 64) - 1;
        int extra = count % 64;

        for (int i = 0; i < rounds; i++) {
            ItemEntity entity = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(),
                    new ItemStack(item.getItem(), 64));

            level.addFreshEntity(entity);
        }

        if (extra != 0) {
            ItemEntity entity = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(),
                    new ItemStack(item.getItem(), extra));

            level.addFreshEntity(entity);
        }

        ChatUtil.chat(server, Component.translatable("singularity.chat.debug.spawn", count, rounds + (extra == 0 ? 0 : 1)),
                !Singularity.CONFIG.generalOptions.displayDebugSpawns);
    }
}
