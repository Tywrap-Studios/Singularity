package mixins.packettweaker;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.network.PacketListener;
import xyz.nucleoid.packettweaker.PlayerProvidingPacketListener;

@Mixin(PacketListener.class)
public interface PacketListenerMixin extends PlayerProvidingPacketListener {
    
}
