package fr.catcore.server.translations.api.polyfill;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.repository.PackSource;

public class BuiltinModResourcePackSource implements PackSource {
    private final String modId;

    public BuiltinModResourcePackSource(String modId) {
        this.modId = modId;
    }

    @Override
    public Component decorate(Component packName) {
        return Component
                .translatable("pack.nameAndSource", packName, Component.translatable("pack.source.builtinMod", modId))
                .withStyle(ChatFormatting.GRAY);
    }
}
