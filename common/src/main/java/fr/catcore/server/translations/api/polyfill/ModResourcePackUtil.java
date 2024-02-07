package fr.catcore.server.translations.api.polyfill;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import dev.architectury.platform.Mod;
import dev.architectury.platform.Platform;
import net.minecraft.SharedConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.world.level.DataPackConfig;

public final class ModResourcePackUtil {
	private static final Gson GSON = new Gson();

	private ModResourcePackUtil() {
	}

	public static void appendModResourcePacks(List<ModResourcePack> packs, PackType type, @Nullable String subPath) {
		for (Mod mod : Platform.getMods()) {
			ModResourcePack pack = ModNioResourcePack.create(new ResourceLocation("fabric", mod.getModId()), getName(mod), mod, subPath, type, ResourcePackActivationType.ALWAYS_ENABLED);

			if (pack != null) {
				packs.add(pack);
			}
		}
	}

	public static boolean containsDefault(Mod info, String filename) {
		return "pack.mcmeta".equals(filename);
	}

	public static InputStream openDefault(Mod info, PackType type, String filename) {
		switch (filename) {
		case "pack.mcmeta":
			String description = Objects.requireNonNullElse(info.getName(), "");
			String metadata = serializeMetadata(type.getVersion(SharedConstants.getCurrentVersion()), description);
			
            return IOUtils.toInputStream(metadata, Charsets.UTF_8);
		default:
			return null;
		}
	}

	public static String serializeMetadata(int packVersion, String description) {
		JsonObject pack = new JsonObject();

		pack.addProperty("pack_format", packVersion);
		pack.addProperty("description", description);
		
        JsonObject metadata = new JsonObject();
		
        metadata.add("pack", pack);
		
        return GSON.toJson(metadata);
	}

	public static String getName(Mod info) {
		if (info.getName() != null) {
			return info.getName();
		} else {
			return "Mod \"" + info.getModId() + "\"";
		}
	}

	public static DataPackConfig createDefaultDataPackSettings() {
		ModResourcePackCreator modResourcePackCreator = new ModResourcePackCreator(PackType.SERVER_DATA);
		List<Pack> moddedResourcePacks = new ArrayList<>();
		
        modResourcePackCreator.register(moddedResourcePacks::add);

		List<String> enabled = new ArrayList<>(DataPackConfig.DEFAULT.getEnabled());
		List<String> disabled = new ArrayList<>(DataPackConfig.DEFAULT.getDisabled());

		// This ensures that any built-in registered data packs by mods which needs to be enabled by default are
		// as the data pack screen automatically put any data pack as disabled except the Default data pack.
		for (Pack profile : moddedResourcePacks) {
			try (PackResources pack = profile.open()) {
				if (pack instanceof LoaderModResourcePack || (pack instanceof ModNioResourcePack && ((ModNioResourcePack) pack).getActivationType().isEnabledByDefault())) {
					enabled.add(profile.getId());
				} else {
					disabled.add(profile.getId());
				}
			}
		}

		return new DataPackConfig(enabled, disabled);
	}
}
