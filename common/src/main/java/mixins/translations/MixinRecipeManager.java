package mixins.translations;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeManager;

@Mixin(RecipeManager.class)
public class MixinRecipeManager {
    @ModifyArg(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;)V", ordinal = 0, remap = false), index = 0)
    private String translated_loaded(String string, Object p0) {
        return Component.translatable("text.translated_server.loaded.recipe", p0).getString();
    }
}
