package fr.catcore.server.translations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import com.google.common.base.Supplier;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import dev.architectury.platform.Mod;
import dev.architectury.platform.Platform;
import fr.catcore.server.translations.api.ServerTranslations;
import fr.catcore.server.translations.api.polyfill.SemanticVersion;
import fr.catcore.server.translations.api.polyfill.VersionParsingException;
import fr.catcore.server.translations.api.resource.language.LanguageReader;
import fr.catcore.server.translations.api.resource.language.ServerLanguageDefinition;
import fr.catcore.server.translations.api.resource.language.TranslationMap;
import net.minecraft.locale.Language;

public class TranslationGatherer {
    private static final URL LANGUAGE_LIST;

    static {
        URL languageList;
        
        try {
            languageList = new URL("https://github.com/arthurbambou/Server-Translations/raw/1.18/src/main/resources/data/server_translations/language_list.json");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            languageList = null;
        }
        
        LANGUAGE_LIST = languageList;
    }

    public static void init() {
        try {
            VanillaAssets assets = VanillaAssets.get();
            Iterable<ServerLanguageDefinition> languages = ServerTranslations.INSTANCE.getAllLanguages();
            
            for (ServerLanguageDefinition language : languages) {
                Supplier<TranslationMap> supplier = () -> loadVanillaLanguage(assets, language);
            
                ServerTranslations.INSTANCE.addTranslations(language.code(), supplier);
            }
            
            getModTranslationFromGithub();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    private static TranslationMap loadVanillaLanguage(VanillaAssets assets, ServerLanguageDefinition language) {
        try {
            Mod vanilla = Platform.getMod("minecraft");
            SemanticVersion minecraftVersion = SemanticVersion.parse(vanilla.getVersion());

            if (minecraftVersion.compareTo(SemanticVersion.parse("1.13-Snapshot.17.48.a")) >= 0) {
                InputStream stream;
                if (ServerLanguageDefinition.DEFAULT.equals(language)) {
                    stream = Language.class.getResourceAsStream("/assets/minecraft/lang/en_us.json");
                } else {
                    stream = assets.openStream("minecraft/lang/" + language.code() + ".json");
                }

                try {
                    return LanguageReader.read(stream);
                } finally {
                    stream.close();
                }
            } else {
                InputStream stream;
                if (minecraftVersion.compareTo(SemanticVersion.parse("1.11-Snapshot.16.32.a")) >= 0) {
                    if (ServerLanguageDefinition.DEFAULT.equals(language)) {
                        stream = Language.class.getResourceAsStream("/assets/minecraft/lang/en_us.lang");
                    } else {
                        stream = assets.openStream("minecraft/lang/" + language.code() + ".lang");
                    }
                } else {
                    if (ServerLanguageDefinition.DEFAULT.equals(language)) {
                        stream = Language.class.getResourceAsStream("/assets/minecraft/lang/en_US.lang");
                    } else {
                        stream = assets.openStream("minecraft/lang/" + language.code().split("_")[0] + "_" + language.code().split("_")[1].toUpperCase(Locale.ROOT) + ".lang");
                    }
                }

                try {
                    return LanguageReader.readLegacy(stream);
                } finally {
                    stream.close();
                }
            }
        } catch (VersionParsingException | IOException e) {
            ServerTranslations.LOGGER.warn("Failed to load vanilla language", e);
            
            return null;
        }
    }

    private static void getModTranslationFromGithub() {
        try (BufferedReader read = new BufferedReader(new InputStreamReader(LANGUAGE_LIST.openStream()))) {
            JsonObject jsonObject = JsonParser.parseReader(read).getAsJsonObject();
            JsonArray languageArray = jsonObject.getAsJsonArray("languages");

            for (JsonElement entry : languageArray) {
                String code = entry.getAsString();
                URL languageURL = getLanguageURL(code);

                if (languageURL == null) continue;

                ServerTranslations.INSTANCE.addTranslations(code, () -> {
                    try {
                        return LanguageReader.read(languageURL.openStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return null;
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static URL getLanguageURL(String code) {
        try {
            return new URL("https://github.com/arthurbambou/Server-Translations/raw/1.18/src/main/resources/data/server_translations/lang/" + code + ".json");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
