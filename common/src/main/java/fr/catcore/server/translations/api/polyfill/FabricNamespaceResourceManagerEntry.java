package fr.catcore.server.translations.api.polyfill;

import net.minecraft.server.packs.repository.PackSource;

public interface FabricNamespaceResourceManagerEntry {
	void setFabricPackSource(PackSource packSource);
}
