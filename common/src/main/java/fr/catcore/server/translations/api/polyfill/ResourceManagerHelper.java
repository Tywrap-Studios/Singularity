package fr.catcore.server.translations.api.polyfill;

import dev.architectury.platform.Mod;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

public interface ResourceManagerHelper {
	@Deprecated
	default void addReloadListener(IdentifiableResourceReloadListener listener) {
		registerReloadListener(listener);
	}

	void registerReloadListener(IdentifiableResourceReloadListener listener);

	static ResourceManagerHelper get(PackType type) {
		return ResourceManagerHelperImpl.get(type);
	}

	static boolean registerBuiltinResourcePack(ResourceLocation id, Mod container, ResourcePackActivationType activationType) {
		return ResourceManagerHelperImpl.registerBuiltinResourcePack(id, "resourcepacks/" + id.getPath(), container, activationType);
	}
    
	static boolean registerBuiltinResourcePack(ResourceLocation id, Mod container, Component displayName, ResourcePackActivationType activationType) {
		return ResourceManagerHelperImpl.registerBuiltinResourcePack(id, "resourcepacks/" + id.getPath(), container, displayName.getString(), activationType);
	}

	@Deprecated
	static boolean registerBuiltinResourcePack(ResourceLocation id, Mod container, String displayName, ResourcePackActivationType activationType) {
		return ResourceManagerHelperImpl.registerBuiltinResourcePack(id, "resourcepacks/" + id.getPath(), container, displayName, activationType);
	}

	@Deprecated
	static boolean registerBuiltinResourcePack(ResourceLocation id, String subPath, Mod container, boolean enabledByDefault) {
		return ResourceManagerHelperImpl.registerBuiltinResourcePack(id, subPath, container,
				enabledByDefault ? ResourcePackActivationType.DEFAULT_ENABLED : ResourcePackActivationType.NORMAL);
	}
}
