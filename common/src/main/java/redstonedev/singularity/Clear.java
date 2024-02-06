package redstonedev.singularity;

import java.util.List;

import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.item.ItemEntity;

public class Clear {
    public static void clearItems(MinecraftServer server) {
        int cleared = 0;
        List<GlobalPos> positions = Detector.getAllItems(server);
        
        for (ItemEntity item : Detector.detectSingularities(server)) {
            cleared += item.getItem().getCount();
            item.kill();
        }

        Util.chat(server, Component.translatable("singularity.chat.clearingItems.done"), false);
        Util.chat(server, Component.translatable("singularity.chat.clearingItems.count", cleared),
                !Singularity.CONFIG.clearOptions.showCleared);

        Dumps.create(server.getServerDirectory().toPath(), positions);
    }
}
