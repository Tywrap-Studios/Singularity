package mixins.translationsapi;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.catcore.server.translations.api.resource.language.SystemDelegatedLanguage;
import net.minecraft.locale.Language;

@Mixin(Language.class)
public class LanguageMixin {
    @Shadow
    private static Language instance;

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void stapi$init(CallbackInfo ci) {
        SystemDelegatedLanguage.INSTANCE.setVanilla(instance);
        instance = SystemDelegatedLanguage.INSTANCE;
    }
}
