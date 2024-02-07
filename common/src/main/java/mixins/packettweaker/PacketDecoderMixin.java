package mixins.packettweaker;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketDecoder;
import xyz.nucleoid.packettweaker.PacketContext;
import xyz.nucleoid.packettweaker.PlayerProvidingPacketListener;
import xyz.nucleoid.packettweaker.impl.ConnectionHolder;

@Mixin(PacketDecoder.class)
public class PacketDecoderMixin implements ConnectionHolder {
    @Unique
    private Connection connection;

    @Override
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    @Inject(method = "decode", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ConnectionProtocol;createPacket(Lnet/minecraft/network/PacketFlow;ILnet/minecraft/network/FriendlyByteBuf;)Lnet/minecraft/network/protocol/Packet;", shift = At.Shift.BEFORE))
    private void packetTweaker_setPacketContext(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list, CallbackInfo ci) {
        if (this.connection != null) {
            PacketContext.setContext(PlayerProvidingPacketListener.getPlayer(this.connection.getPacketListener()));
        }
    }

    @Inject(method = "decode", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ConnectionProtocol;createPacket(Lnet/minecraft/network/PacketFlow;ILnet/minecraft/network/FriendlyByteBuf;)Lnet/minecraft/network/protocol/Packet;", shift = At.Shift.AFTER))
    private void packetTweaker_clearPacketContext(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list, CallbackInfo ci) {
        PacketContext.clearContext();
    }
}
