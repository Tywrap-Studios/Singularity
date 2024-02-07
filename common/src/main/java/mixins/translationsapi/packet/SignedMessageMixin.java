package mixins.translationsapi.packet;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import fr.catcore.server.translations.api.text.LocalizableText;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;

@Pseudo
@Mixin(PlayerChatMessage.class)
public class SignedMessageMixin {
    @SuppressWarnings("unchecked")
    @ModifyArg(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/FriendlyByteBuf;writeOptional(Ljava/util/Optional;Lnet/minecraft/network/FriendlyByteBuf$Writer;)V"), require = 0)
    private <T> Optional<T> stapi$dontLocalize(Optional<T> text) {
        if (text.isPresent()) {
            Component copy = ((Component) text.get()).copy();
            
            ((LocalizableText) copy).setLocalized(true);

            return Optional.of((T) copy);
        }
        return text;
    }
}
