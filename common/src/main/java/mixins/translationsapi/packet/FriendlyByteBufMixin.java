package mixins.translationsapi.packet;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import fr.catcore.server.translations.api.LocalizationTarget;
import fr.catcore.server.translations.api.nbt.StackNbtLocalizer;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.IdMap;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

@Mixin(FriendlyByteBuf.class)
public abstract class FriendlyByteBufMixin extends ByteBuf {
    @Unique
    private ItemStack stapi_cachedStack;

    @Shadow
    public abstract ByteBuf writeBoolean(boolean b);

    @Shadow
    public abstract ByteBuf writeByte(int b);

    @Shadow
    public abstract <T> void writeId(IdMap<T> idMap, T object);

    @Shadow
    public abstract FriendlyByteBuf writeNbt(@Nullable CompoundTag compoundTag);

    /**
     * @reason Sponge hates me
     * @author RedstoneWizard08
     */
    @Overwrite
    public FriendlyByteBuf writeItem(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            this.writeBoolean(false);
        } else {
            this.writeBoolean(true);
            Item item = itemStack.getItem();
            this.writeId(Registry.ITEM, item);
            this.writeByte(itemStack.getCount());
            CompoundTag compoundTag = null;
            if (item.canBeDepleted() || item.shouldOverrideMultiplayerNbt()) {
                compoundTag = itemStack.getTag();
            }
            this.stapi_cachedStack = itemStack;

            LocalizationTarget target = LocalizationTarget.forPacket();

            if (target != null) {
                compoundTag = StackNbtLocalizer.localize(this.stapi_cachedStack, compoundTag, target);
            }
        
            this.stapi_cachedStack = null;

            this.writeNbt(compoundTag);
        }

        return (FriendlyByteBuf) (ByteBuf) this;
    }

    @Inject(method = "readItem", at = @At(value = "RETURN", ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD)
    private void readItemStack(CallbackInfoReturnable<ItemStack> ci, Item item, int count, ItemStack stack) {
        CompoundTag tag = StackNbtLocalizer.unlocalize(stack.getTag());

        stack.setTag(tag);
    }
}
