package fr.catcore.server.translations.api.resource.language;

import fr.catcore.server.translations.api.ServerTranslations;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

public final class SystemDelegatedLanguage extends Language {
    public static final SystemDelegatedLanguage INSTANCE = new SystemDelegatedLanguage();

    private Language vanilla = Language.getInstance();

    private SystemDelegatedLanguage() {
    }

    public void setVanilla(Language language) {
        this.vanilla = language;
    }

    public Language getVanilla() {
        return this.vanilla;
    }

    @Override
    public String getOrDefault(String key) {
        String override = this.getSystemLanguage().local().getOrNull(key);
        
        if (override != null) {
            return override;
        }

        return this.vanilla.getOrDefault(key);
    }

    @Override
    public boolean has(String key) {
        return this.vanilla.has(key) || this.getSystemLanguage().local().contains(key);
    }

    @Override
    public boolean isDefaultRightToLeft() {
        return this.getSystemLanguage().definition().rightToLeft();
    }

    private ServerLanguage getSystemLanguage() {
        return ServerTranslations.INSTANCE.getSystemLanguage();
    }

    @Override
    public FormattedCharSequence getVisualOrder(FormattedText text) {
        return this.vanilla.getVisualOrder(text);
    }
}
