package fr.catcore.server.translations.api.polyfill;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;

public class ModResourcePackCreator implements RepositorySource {
	public static final PackSource RESOURCE_PACK_SOURCE = text -> Component.translatable("pack.nameAndSource", text, Component.translatable("pack.source.fabricmod"));
	public static final ModResourcePackCreator CLIENT_RESOURCE_PACK_PROVIDER = new ModResourcePackCreator(PackType.CLIENT_RESOURCES);
	private final Pack.PackConstructor factory;
	private final PackType type;

	public ModResourcePackCreator(PackType type) {
		this.type = type;
		this.factory = (name, text, bl, supplier, metadata, initialPosition, source) ->
				new Pack(name, text, bl, supplier, metadata, type, initialPosition, source);
	}

	public void register(Consumer<Pack> consumer) {
		this.loadPacks(consumer, this.factory);
	}

	@Override
	public void loadPacks(Consumer<Pack> consumer, Pack.PackConstructor factory) {
		List<ModResourcePack> packs = new ArrayList<>();
		ModResourcePackUtil.appendModResourcePacks(packs, type, null);

		if (!packs.isEmpty()) {
			Pack resourcePackProfile = Pack.create("Fabric Mods",
					true, () -> new LoaderModResourcePack(this.type, packs), factory, Pack.Position.TOP,
					RESOURCE_PACK_SOURCE);

			if (resourcePackProfile != null) {
				consumer.accept(resourcePackProfile);
			}
		}

		// Register all built-in resource packs provided by mods.
		ResourceManagerHelperImpl.registerBuiltinResourcePacks(this.type, consumer, factory);
	}
}
