package fr.catcore.server.translations.api.text;

import fr.catcore.server.translations.api.LocalizationTarget;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

public interface LocalizableTextContent {
    void visitSelfLocalized(LocalizedTextVisitor visitor, LocalizationTarget target, Component text, Style style);
}
