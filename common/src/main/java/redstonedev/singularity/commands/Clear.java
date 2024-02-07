package redstonedev.singularity.commands;

import java.util.List;

import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.item.ItemEntity;
import redstonedev.singularity.Detector;
import redstonedev.singularity.Singularity;
import redstonedev.singularity.util.Dumps;
import redstonedev.singularity.util.ChatUtil;

public class Clear {
    public static void clearItems(MinecraftServer server) {
        int cleared = 0;
        List<GlobalPos> positions = Detector.getAllItems(server);
        
        for (ItemEntity item : Detector.detectSingularities(server)) {
            cleared += item.getItem().getCount();
            item.kill();
        }

        ChatUtil.chat(server, Component.translatable("singularity.chat.clearingItems.done"), false);
        ChatUtil.chat(server, Component.translatable("singularity.chat.clearingItems.count", cleared),
                !Singularity.CONFIG.clearOptions.showCleared);

        Dumps.create(server.getServerDirectory().toPath(), positions);
    }
}
