package fr.catcore.server.translations.api.polyfill;

import dev.architectury.platform.Mod;
import net.minecraft.server.packs.PackResources;

public interface ModResourcePack extends PackResources {
	Mod getArchitecturyMod();
}
