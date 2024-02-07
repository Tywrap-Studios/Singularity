package mixins.translationsapi.client;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.catcore.server.translations.api.resource.language.SystemDelegatedLanguage;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.contents.TranslatableContents;

@Mixin(TranslatableContents.class)
public abstract class TranslatableTextContentMixin {
    @Unique
    private Language stapi_cachedLanguage = null;

    @Redirect(
            method = "decompose",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/network/chat/contents/TranslatableContents;decomposedWith:Lnet/minecraft/locale/Language;",
                    opcode = Opcodes.GETFIELD
            )
    )
    private Language markAsAlwaysTrue(TranslatableContents translatableTextContent) {
        return null;
    }

    @Inject(
            method = "decompose",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/network/chat/contents/TranslatableContents;decomposedWith:Lnet/minecraft/locale/Language;",
                    opcode = Opcodes.PUTFIELD,
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private void cancelIfEqual(CallbackInfo ci) {
        var language = Language.getInstance();

        if (language instanceof SystemDelegatedLanguage delegatedLanguage && delegatedLanguage.getVanilla() != this.stapi_cachedLanguage) {
            this.stapi_cachedLanguage = delegatedLanguage.getVanilla();
        } else {
            ci.cancel();
        }
    }
}
