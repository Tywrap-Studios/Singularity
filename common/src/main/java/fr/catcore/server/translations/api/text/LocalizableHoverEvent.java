package fr.catcore.server.translations.api.text;

import fr.catcore.server.translations.api.LocalizationTarget;
import net.minecraft.network.chat.HoverEvent;

public interface LocalizableHoverEvent {
    HoverEvent asLocalizedFor(LocalizationTarget target);
}
