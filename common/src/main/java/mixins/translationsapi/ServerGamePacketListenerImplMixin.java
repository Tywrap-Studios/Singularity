package mixins.translationsapi;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.catcore.server.translations.api.LocalizationTarget;
import net.minecraft.network.protocol.game.ServerboundClientInformationPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin implements LocalizationTarget {
    @Unique
    private String stapi$language;

    @Inject(method = "handleClientInformation", at = @At("TAIL"))
    private void stapi$setLanguage(ServerboundClientInformationPacket packet, CallbackInfo ci) {
        this.stapi$language = packet.language();
    }

    @Override
    public String getLanguageCode() {
        return this.stapi$language;
    }
}
