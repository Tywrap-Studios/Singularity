package fr.catcore.server.translations.api.polyfill;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import mixins.polyfill.NamespaceResourceManagerAccessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.ResourcePackFileNotFoundException;
import net.minecraft.server.packs.resources.FallbackResourceManager;

public abstract class GroupResourcePack implements PackResources {
	protected final PackType type;
	protected final List<ModResourcePack> packs;
	protected final Map<String, List<ModResourcePack>> namespacedPacks = new Object2ObjectOpenHashMap<>();

	public GroupResourcePack(PackType type, List<ModResourcePack> packs) {
		this.type = type;
		this.packs = packs;
		this.packs.forEach(pack -> pack.getNamespaces(this.type)
				.forEach(namespace -> this.namespacedPacks.computeIfAbsent(namespace, value -> new ArrayList<>())
						.add(pack)));
	}

	@Override
	public InputStream getResource(PackType type, ResourceLocation id) throws IOException {
		List<ModResourcePack> packs = this.namespacedPacks.get(id.getNamespace());

		if (packs != null) {
			for (int i = packs.size() - 1; i >= 0; i--) {
				PackResources pack = packs.get(i);

				if (pack.hasResource(type, id)) {
					return pack.getResource(type, id);
				}
			}
		}

		throw new ResourcePackFileNotFoundException(null,
				String.format("%s/%s/%s", type.getDirectory(), id.getNamespace(), id.getPath()));
	}

	@Override
	public Collection<ResourceLocation> getResources(PackType type, String namespace, String prefix, Predicate<ResourceLocation> predicate) {
		List<ModResourcePack> packs = this.namespacedPacks.get(namespace);

		if (packs == null) {
			return Collections.emptyList();
		}

		Set<ResourceLocation> resources = new HashSet<>();

		for (int i = packs.size() - 1; i >= 0; i--) {
			PackResources pack = packs.get(i);
			Collection<ResourceLocation> modResources = pack.getResources(type, namespace, prefix, predicate);

			resources.addAll(modResources);
		}

		return resources;
	}

	@Override
	public boolean hasResource(PackType type, ResourceLocation id) {
		List<ModResourcePack> packs = this.namespacedPacks.get(id.getNamespace());

		if (packs == null) {
			return false;
		}

		for (int i = packs.size() - 1; i >= 0; i--) {
			PackResources pack = packs.get(i);

			if (pack.hasResource(type, id)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public Set<String> getNamespaces(PackType type) {
		return this.namespacedPacks.keySet();
	}

	public void appendResources(NamespaceResourceManagerAccessor manager, ResourceLocation id, List<FallbackResourceManager.SinglePackResourceThunkSupplier> resources) {
		List<ModResourcePack> packs = this.namespacedPacks.get(id.getNamespace());

		if (packs == null) {
			return;
		}

		ResourceLocation metadataId = NamespaceResourceManagerAccessor.polyfill$accessor_getMetadataPath(id);

		for (ModResourcePack pack : packs) {
			if (pack.hasResource(manager.getType(), id)) {
				final FallbackResourceManager.SinglePackResourceThunkSupplier entry = ((FallbackResourceManager) manager).new SinglePackResourceThunkSupplier(id, metadataId, pack);
				((FabricNamespaceResourceManagerEntry) entry).setFabricPackSource(ModResourcePackCreator.RESOURCE_PACK_SOURCE);
				resources.add(entry);
			}
		}
	}

	public String getFullName() {
		return this.getName() + " (" + this.packs.stream().map(PackResources::getName).collect(Collectors.joining(", ")) + ")";
	}

	@Override
	public void close() {
		this.packs.forEach(PackResources::close);
	}
}
