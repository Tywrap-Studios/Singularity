package fr.catcore.server.translations.api.text;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class LocalizedTextBuilder implements LocalizedTextVisitor {
    private MutableComponent result;

    @Override
    public void accept(MutableComponent text) {
        if (this.result == null) {
            this.result = text;
        } else {
            this.result = this.result.append(text);
        }
    }

    public Component getResult() {
        return this.result;
    }
}
