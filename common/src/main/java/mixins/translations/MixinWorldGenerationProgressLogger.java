package mixins.translations;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.progress.LoggerChunkProgressListener;

@Mixin(LoggerChunkProgressListener.class)
public class MixinWorldGenerationProgressLogger {
    @ModifyArg(method = "stop", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;)V", ordinal = 0, remap = false), index = 0)
    private String translated_time(String string, Object p0) {
        return Component.translatable("text.translated_server.time", p0).getString();
    }
}
