package mixins.translations;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.advancements.AdvancementList;
import net.minecraft.network.chat.Component;

@Mixin(AdvancementList.class)
public class MixinAdvancementManager {
    @ModifyArg(method = "add", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;)V", ordinal = 0, remap = false), index = 0)
    private String translated_loaded(String string, Object p0) {
        return Component.translatable("text.translated_server.loaded.advancement", p0).getString();
    }
}
