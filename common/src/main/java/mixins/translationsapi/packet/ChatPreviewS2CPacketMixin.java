package mixins.translationsapi.packet;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import fr.catcore.server.translations.api.text.LocalizableText;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundChatPreviewPacket;

@Pseudo
@Mixin(ClientboundChatPreviewPacket.class)
public class ChatPreviewS2CPacketMixin {
    @SuppressWarnings("unchecked")
    @ModifyArg(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/FriendlyByteBuf;writeNullable(Ljava/lang/Object;Lnet/minecraft/network/FriendlyByteBuf$Writer;)V"), require = 0)
    private <T> T stapi$dontLocalize(T text) {
        if (text != null) {
            Component copy = ((Component) text).copy();

            ((LocalizableText) copy).setLocalized(true);
            
            return (T) copy;
        }
        
        return text;
    }
}
