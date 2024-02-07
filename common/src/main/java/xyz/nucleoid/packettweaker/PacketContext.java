package xyz.nucleoid.packettweaker;

import java.io.IOException;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public final class PacketContext {
    private static final ThreadLocal<PacketContext> INSTANCE = ThreadLocal.withInitial(PacketContext::new);

    private ServerPlayer target;

    public static PacketContext get() {
        return INSTANCE.get();
    }

    public static void writeWithContext(Packet<?> packet, FriendlyByteBuf buffer, @Nullable ServerGamePacketListenerImpl networkHandler) throws IOException {
        if (networkHandler == null) {
            packet.write(buffer);
            return;
        }

        PacketContext context = PacketContext.get();
        try {
            context.target = networkHandler.player;
            packet.write(buffer);
        } finally {
            context.target = null;
        }
    }
    @Deprecated
    public static void setReadContext(@Nullable ServerGamePacketListenerImpl networkHandler) {
        setContext(networkHandler);
    }

    @ApiStatus.Internal
    public static void setContext(@Nullable ServerGamePacketListenerImpl networkHandler) {
        if (networkHandler == null) {
            return;
        }

        PacketContext context = PacketContext.get();
        context.target = networkHandler.player;
    }

    @ApiStatus.Internal
    public static void setContext(@Nullable ServerPlayer player) {
        PacketContext context = PacketContext.get();
        context.target = player;
    }

    public static void clearReadContext() {
        clearContext();
    }

    public static void clearContext() {
        PacketContext context = PacketContext.get();
        context.target = null;
    }

    @Nullable
    public ServerPlayer getTarget() {
        return this.target;
    }
}
