package redstonedev.singularity;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import redstonedev.singularity.commands.Clear;
import redstonedev.singularity.util.ChatUtil;

public class AutoClear {
    @SuppressWarnings("CallToPrintStackTrace")
    public static void start(MinecraftServer server) {
        if (!Singularity.CONFIG.clearOptions.enableClearing) return;

        Thread thread = new Thread(() -> {
            try {
                while (true) {
                    if (Singularity.CONFIG.clearOptions.clearInterval < 60) {
                        Singularity.CONFIG.clearOptions.clearInterval = 60;
                    }

                    Thread.sleep((Singularity.CONFIG.clearOptions.clearInterval - 30) * 1000L);
                    ChatUtil.chat(server, Component.translatable("singularity.chat.clearingItems.30"), false);

                    Thread.sleep(20 * 1000L);
                    ChatUtil.chat(server, Component.translatable("singularity.chat.clearingItems.10"), false);

                    Thread.sleep(10 * 1000L);
                    ChatUtil.chat(server, Component.translatable("singularity.chat.clearingItems"), false);

                    Clear.clearItems(server);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();
    }
}
