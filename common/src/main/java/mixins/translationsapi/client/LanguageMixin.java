package mixins.translationsapi.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import fr.catcore.server.translations.api.ServerTranslations;
import fr.catcore.server.translations.api.resource.language.ServerLanguageDefinition;
import fr.catcore.server.translations.api.resource.language.SystemDelegatedLanguage;
import net.minecraft.client.Minecraft;
import net.minecraft.locale.Language;

@Mixin(Language.class)
public class LanguageMixin {
    @ModifyVariable(method = "inject", at = @At("HEAD"), argsOnly = true)
    private static Language modifyLanguage(Language language) {
        String languageCode = Minecraft.getInstance().getLanguageManager().getSelected().getCode();
        ServerLanguageDefinition languageDefinition = ServerTranslations.INSTANCE.getLanguageDefinition(languageCode);
        
        ServerTranslations.INSTANCE.setSystemLanguage(languageDefinition);

        SystemDelegatedLanguage delegated = SystemDelegatedLanguage.INSTANCE;
        
        delegated.setVanilla(language);

        return delegated;
    }
}
