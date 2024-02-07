package mixins.translationsapi.text;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import fr.catcore.server.translations.api.LocalizationTarget;
import fr.catcore.server.translations.api.text.LocalizableHoverEvent;
import fr.catcore.server.translations.api.text.LocalizableText;
import fr.catcore.server.translations.api.text.LocalizableTextContent;
import fr.catcore.server.translations.api.text.LocalizedTextBuilder;
import fr.catcore.server.translations.api.text.LocalizedTextVisitor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

@Mixin(MutableComponent.class)
public abstract class MutableTextMixin implements LocalizableText {
    private boolean sta$isLocalized = false;

    @Shadow
    public abstract List<Component> getSiblings();

    @Shadow
    public abstract Style getStyle();

    @Shadow
    public abstract ComponentContents getContents();

    @Shadow @Final private List<Component> siblings;

    @Override
    public void visitText(LocalizedTextVisitor visitor, LocalizationTarget target, Style style) {
        Style selfStyle = this.getStyle().applyTo(style);
        HoverEvent hoverEvent = selfStyle.getHoverEvent();

        if (hoverEvent != null) {
            LocalizableHoverEvent localizableHoverEvent = (LocalizableHoverEvent) hoverEvent;
            
            selfStyle = selfStyle.withHoverEvent(localizableHoverEvent.asLocalizedFor(target));
        }

        LocalizedTextBuilder vis = new LocalizedTextBuilder();

        if (this.getContents() instanceof LocalizableTextContent localizableTextContent) {
            localizableTextContent.visitSelfLocalized(vis, target, this, selfStyle);
        } else {
            vis.accept(this.plainCopy().setStyle(selfStyle));
        }

        for (Component sibling : this.siblings) {
            ((LocalizableText) sibling).visitText(vis, target, Style.EMPTY);
        }

        visitor.accept((MutableComponent) vis.getResult());
    }

    @Override
    public boolean isLocalized() {
        return this.sta$isLocalized;
    }

    @Override
    public void setLocalized(boolean value) {
        this.sta$isLocalized = value;
    }
}
