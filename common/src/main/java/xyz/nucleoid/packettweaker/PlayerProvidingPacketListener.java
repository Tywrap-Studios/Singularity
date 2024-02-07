package xyz.nucleoid.packettweaker;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.PacketListener;
import net.minecraft.server.level.ServerPlayer;

public interface PlayerProvidingPacketListener {
    @Nullable
    default ServerPlayer getPlayerForPacketTweaker() {
        return null;
    }

    @Nullable
    static ServerPlayer getPlayer(PacketListener listener) {
        return ((PlayerProvidingPacketListener) listener).getPlayerForPacketTweaker();
    }
}
