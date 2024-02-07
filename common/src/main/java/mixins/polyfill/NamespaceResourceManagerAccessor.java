package mixins.polyfill;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.FallbackResourceManager;

@Mixin(FallbackResourceManager.class)
public interface NamespaceResourceManagerAccessor {
	@Accessor("type")
	PackType getType();

	@Invoker("getMetadataLocation")
	static ResourceLocation polyfill$accessor_getMetadataPath(ResourceLocation id) {
		throw new UnsupportedOperationException("Invoker injection failed.");
	}
}
