package mixins.packettweaker;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import xyz.nucleoid.packettweaker.PlayerProvidingPacketListener;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin implements PlayerProvidingPacketListener {
    @Shadow public ServerPlayer player;

    @Override
    public @Nullable ServerPlayer getPlayerForPacketTweaker() {
        return this.player;
    }
}
