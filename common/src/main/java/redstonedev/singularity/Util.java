package redstonedev.singularity;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class Util {
    public static void chat(MinecraftServer server, Component msg, boolean operatorOnly) {
        server.sendSystemMessage(msg);

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (operatorOnly) {
                if (player.hasPermissions(2)) {
                    player.sendSystemMessage(msg);
                }
            } else {
                player.sendSystemMessage(msg);
            }
        }
    }
}
