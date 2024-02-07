package mixins.translationsapi.text;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import fr.catcore.server.translations.api.LocalizationTarget;
import fr.catcore.server.translations.api.nbt.StackNbtLocalizer;
import fr.catcore.server.translations.api.text.LocalizableHoverEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.world.item.ItemStack;

@Mixin(HoverEvent.class)
public abstract class HoverEventMixin<T> implements LocalizableHoverEvent {
    @Shadow
    public abstract HoverEvent.Action<T> getAction();

    @SuppressWarnings("hiding")
    @Shadow
    @Nullable
    public abstract <T> T getValue(HoverEvent.Action<T> action);

    @Override
    public HoverEvent asLocalizedFor(LocalizationTarget target) {
        var action = this.getAction();
        var value = this.getValue(action);

        if (action == HoverEvent.Action.SHOW_ITEM) {
            ItemStack itemStack = ((HoverEvent.ItemStackInfo) value).getItemStack();
            CompoundTag localized = StackNbtLocalizer.localize(itemStack, itemStack.getTag(), target);
            
            itemStack.setTag(localized);

            return new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackInfo(itemStack));
        }

        return (HoverEvent) (Object) this;
    }
}
