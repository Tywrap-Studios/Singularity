package fr.catcore.server.translations.api.resource.language;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.common.base.Supplier;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import fr.catcore.server.translations.api.ServerTranslations;
import net.minecraft.locale.Language;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

public final class LanguageReader {
    public static TranslationMap read(InputStream stream) {
        TranslationMap map = new TranslationMap();
        Language.loadFromJson(stream, map::put);

        return map;
    }

    public static TranslationMap readLegacy(InputStream input) {
        TranslationMap map = new TranslationMap();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));

        reader.lines().forEach(line -> {
            if (line.startsWith("\n") || line.startsWith("#") || line.startsWith("/")) {
                return;
            }

            String key = line.split("=")[0];
            int values = line.split("=").length;
            StringBuilder value = new StringBuilder();

            for (int i = 1; i < values; i++) {
                value.append(line.split("=")[i]);
            }

            map.put(key, value.toString());
        });

        return map;
    }

    public static TranslationMap loadVanillaTranslations() {
        try (InputStream input = Language.class
                .getResourceAsStream("/assets/minecraft/lang/" + ServerLanguageDefinition.DEFAULT_CODE + ".json")) {
            return LanguageReader.read(input);
        } catch (IOException e) {
            ServerTranslations.LOGGER.warn("Failed to load default language", e);

            return new TranslationMap();
        }
    }

    public static Multimap<String, Supplier<TranslationMap>> collectTranslationSuppliers(ResourceManager manager) {
        Multimap<String, Supplier<TranslationMap>> translationSuppliers = HashMultimap.create();

        for (ResourceLocation path : manager.listResources("lang", path -> path.getPath().endsWith(".json")).keySet()) {
            String code = getLanguageCodeForPath(path);

            translationSuppliers.put(code, () -> {
                TranslationMap map = new TranslationMap();

                try {
                    for (Resource resource : manager.getResourceStack(path)) {
                        map.putAll(read(resource.open()));
                    }
                } catch (RuntimeException | IOException e) {
                    ServerTranslations.LOGGER.warn("Failed to load language resource at {}", path, e);
                }

                return map;
            });
        }

        return translationSuppliers;
    }

    private static String getLanguageCodeForPath(ResourceLocation file) {
        String path = file.getPath();

        path = path.substring("lang".length() + 1, path.length() - ".json".length());

        return ServerTranslations.INSTANCE.getCodeAlias(path);
    }
}
