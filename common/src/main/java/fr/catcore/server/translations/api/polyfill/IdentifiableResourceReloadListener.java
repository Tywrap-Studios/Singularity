package fr.catcore.server.translations.api.polyfill;

import java.util.Collection;
import java.util.Collections;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;

public interface IdentifiableResourceReloadListener extends PreparableReloadListener {
	ResourceLocation getFabricId();

	default Collection<ResourceLocation> getFabricDependencies() {
		return Collections.emptyList();
	}
}
