package mixins.translationsapi.text;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import fr.catcore.server.translations.api.LocalizationTarget;
import fr.catcore.server.translations.api.ServerTranslations;
import fr.catcore.server.translations.api.resource.language.TranslationAccess;
import fr.catcore.server.translations.api.text.LocalizableText;
import fr.catcore.server.translations.api.text.LocalizableTextContent;
import fr.catcore.server.translations.api.text.LocalizedTextBuilder;
import fr.catcore.server.translations.api.text.LocalizedTextVisitor;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.TranslatableContents;

@Mixin(TranslatableContents.class)
public abstract class TranslatableTextContentMixin implements ComponentContents, LocalizableTextContent {
    @Shadow
    @Final
    private static FormattedText TEXT_PERCENT;

    @Shadow
    @Final
    private Object[] args;

    @Shadow
    protected abstract FormattedText getArgument(int index);

    @Shadow
    @Final
    private static Pattern FORMAT_PATTERN;

    @Shadow
    @Final
    private String key;

    @Nullable
    private List<FormattedText> buildTranslations(@Nullable LocalizationTarget target) {
        TranslationAccess translations = this.getTranslationsFor(target);
        String translation = translations.getOrNull(this.key);
        
        if (translation == null) {
            return null;
        }

        List<FormattedText> result = new ArrayList<>();

        // Copy from vanilla TranslatableText#setTranslation to not mutate for thread-safety
        Matcher argumentMatcher = FORMAT_PATTERN.matcher(translation);

        int currentCharIndex = 0;
        int currentArgumentIndex = 0;

        while (argumentMatcher.find(currentCharIndex)) {
            int argumentStart = argumentMatcher.start();
            int argumentEnd = argumentMatcher.end();

            if (argumentStart > currentCharIndex) {
                String literal = translation.substring(currentCharIndex, argumentStart);
                
                if (literal.indexOf('%') != -1) {
                    return null;
                }

                result.add(FormattedText.of(literal));
            }

            String formatType = argumentMatcher.group(2);
            String literal = translation.substring(argumentStart, argumentEnd);

            if ("%".equals(formatType) && "%%".equals(literal)) {
                result.add(TEXT_PERCENT);
            } else {
                if (!"s".equals(formatType)) {
                    return null;
                }
            
                String matchedArgumentIndex = argumentMatcher.group(1);
                int argumentIndex = matchedArgumentIndex != null ? Integer.parseInt(matchedArgumentIndex) - 1 : currentArgumentIndex++;
            
                if (argumentIndex < this.args.length) {
                    result.add(this.getArgument(argumentIndex));
                }
            }
            
            currentCharIndex = argumentEnd;
        }

        if (currentCharIndex < translation.length()) {
            String remaining = translation.substring(currentCharIndex);
            
            if (remaining.indexOf('%') != -1) {
                return null;
            }
            
            result.add(FormattedText.of(remaining));
        }

        return result;
    }

    private TranslationAccess getTranslationsFor(@Nullable LocalizationTarget target) {
        if (target != null) {
            return target.getLanguage().remote();
        } else {
            return ServerTranslations.INSTANCE.getSystemLanguage().local();
        }
    }

    @Override
    public void visitSelfLocalized(LocalizedTextVisitor visitor, LocalizationTarget target, Component text, Style style) {
        List<FormattedText> translations = this.buildTranslations(target);

        if (translations != null) {
            this.visitSelfTranslated(visitor, target, style, translations);
        } else {
            this.visitSelfUntranslated(visitor, text, style);
        }
    }

    private void visitSelfTranslated(LocalizedTextVisitor visitor, LocalizationTarget target, Style style, List<FormattedText> translations) {
        visitor.acceptLiteral("", style);

        for (FormattedText translation : translations) {
            if (translation instanceof MutableComponent mutableText) {
                LocalizedTextBuilder visitor2 = new LocalizedTextBuilder();
                
                ((LocalizableText) mutableText).visitText(visitor2, target, mutableText.getStyle());

                visitor.accept((MutableComponent) visitor2.getResult());
            } else {
                translation.visit(visitor.asGeneric(style));
            }
        }
    }

    private void visitSelfUntranslated(LocalizedTextVisitor visitor, Component text, Style style) {
        visitor.accept(text.plainCopy().setStyle(style));
    }
}
