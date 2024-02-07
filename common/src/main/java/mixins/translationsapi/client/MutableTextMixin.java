package mixins.translationsapi.client;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fr.catcore.server.translations.api.resource.language.SystemDelegatedLanguage;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;

@Mixin(MutableComponent.class)
public class MutableTextMixin {
    @Shadow
    private FormattedCharSequence visualOrderText;

    @Unique
    private Language stapi_cachedLanguageVanilla;

    @Redirect(method = "getVisualOrderText", at = @At(value = "FIELD", target = "Lnet/minecraft/network/chat/MutableComponent;language:Lnet/minecraft/locale/Language;", opcode = Opcodes.GETFIELD))
    private Language markAsAlwaysTrue(MutableComponent mutableText) {
        return null;
    }

    @Inject(method = "getVisualOrderText", at = @At(value = "FIELD", target = "Lnet/minecraft/network/chat/MutableComponent;visualOrderText:Lnet/minecraft/util/FormattedCharSequence;", opcode = Opcodes.PUTFIELD, shift = At.Shift.BEFORE), cancellable = true)
    private void cancelIfEqual(CallbackInfoReturnable<FormattedCharSequence> cir) {
        var language = Language.getInstance();

        if (language instanceof SystemDelegatedLanguage delegatedLanguage
                && delegatedLanguage.getVanilla() != this.stapi_cachedLanguageVanilla) {
            this.stapi_cachedLanguageVanilla = delegatedLanguage.getVanilla();
        } else {
            cir.setReturnValue(this.visualOrderText);
        }
    }
}
