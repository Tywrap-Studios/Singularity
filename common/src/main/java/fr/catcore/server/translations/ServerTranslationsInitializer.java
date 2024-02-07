package fr.catcore.server.translations;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import fr.catcore.server.translations.api.ServerTranslations;
import fr.catcore.server.translations.api.resource.language.ServerLanguageDefinition;
import fr.catcore.server.translations.config.ConfigManager;
import net.minecraft.network.chat.Component;

public class ServerTranslationsInitializer {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        LOGGER.info("Initializing ServerTranslations.");
        
        TranslationGatherer.init();
        
        LOGGER.info("Initialized ServerTranslations.");

        String systemCode = ConfigManager.getLanguageCodeFromConfig();
        ServerLanguageDefinition language = ServerTranslations.INSTANCE.getLanguageDefinition(systemCode);
        ServerTranslations.INSTANCE.setSystemLanguage(language);

        LOGGER.info(Component.translatable("text.translated_server.language.set", language.code(), language.name(), language.region()).getString());

        ServerTranslations.INSTANCE.registerReloadListener(TranslationGatherer::init);
        ServerTranslations.INSTANCE.registerReloadListener(() -> {
            ServerLanguageDefinition lang = ServerTranslations.INSTANCE.getLanguageDefinition(systemCode);
            ServerTranslations.INSTANCE.setSystemLanguage(lang);
        });
    }
}
