package mixins.translationsapi.packet;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import fr.catcore.server.translations.api.LocalizationTarget;
import fr.catcore.server.translations.api.text.LocalizableText;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntityType;

@Mixin(ClientboundBlockEntityDataPacket.class)
public class BlockEntityUpdateS2CPacketMixin {
    @Shadow @Final private BlockEntityType<?> type;

    @ModifyArg(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/FriendlyByteBuf;writeNbt(Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/network/FriendlyByteBuf;"))
    private CompoundTag translateNbt(CompoundTag nbtCompound) {
        LocalizationTarget target = LocalizationTarget.forPacket();

        if (this.type == BlockEntityType.SIGN && target != null) {
            CompoundTag nbt = nbtCompound.copy();

            nbt.putString("Text1", this.parseText(nbt.getString("Text1"), target));
            nbt.putString("Text2", this.parseText(nbt.getString("Text2"), target));
            nbt.putString("Text3", this.parseText(nbt.getString("Text3"), target));
            nbt.putString("Text4", this.parseText(nbt.getString("Text4"), target));
            
            return nbt;
        }
        
        return nbtCompound;
    }

    @Unique
    private String parseText(String text, LocalizationTarget target) {
        LocalizableText parsed = (LocalizableText) Component.Serializer.fromJsonLenient(text);

        if (parsed != null) {
            return Component.Serializer.toJson(parsed.asLocalizedFor(target));
        } else {
            return text;
        }
    }
}
