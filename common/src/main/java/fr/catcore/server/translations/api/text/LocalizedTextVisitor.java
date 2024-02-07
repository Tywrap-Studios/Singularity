package fr.catcore.server.translations.api.text;

import java.util.Optional;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

public interface LocalizedTextVisitor {
    void accept(MutableComponent text);

    default void acceptLiteral(String string, Style style) {
        this.accept(Component.literal(string).setStyle(style));
    }

    default <T> FormattedText.ContentConsumer<T> asGeneric(Style style) {
        return string -> {
            this.acceptLiteral(string, style);

            return Optional.empty();
        };
    }

    Component getResult();
}
