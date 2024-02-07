package redstonedev.singularity.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class ChatUtil {
    public static void chat(MinecraftServer server, Component msg, boolean operatorOnly) {
        server.sendSystemMessage(Component.literal(ChatFormatting.stripFormatting(msg.getString())));

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
