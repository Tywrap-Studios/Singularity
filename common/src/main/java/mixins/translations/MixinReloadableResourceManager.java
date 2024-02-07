package mixins.translations;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.resources.ReloadableResourceManager;

@Mixin(ReloadableResourceManager.class)
public class MixinReloadableResourceManager {
    @ModifyArg(method = "createReload", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;)V", ordinal = 0, remap = false), index = 0)
    private String translated_loading(String string, Object args) {
        return Component.translatable("text.translated_server.loading.datapacks", args).getString();
    }

}
