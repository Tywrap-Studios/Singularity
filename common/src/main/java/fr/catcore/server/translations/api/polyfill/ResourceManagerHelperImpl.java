package fr.catcore.server.translations.api.polyfill;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import dev.architectury.platform.Mod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.util.Tuple;

public class ResourceManagerHelperImpl implements ResourceManagerHelper {
	private static final Map<PackType, ResourceManagerHelperImpl> registryMap = new HashMap<>();
	private static final Set<Tuple<String, ModNioResourcePack>> builtinResourcePacks = new HashSet<>();
	private static final Logger LOGGER = LoggerFactory.getLogger(ResourceManagerHelperImpl.class);

	private final Set<ResourceLocation> addedListenerIds = new HashSet<>();
	private final Set<IdentifiableResourceReloadListener> addedListeners = new LinkedHashSet<>();

	public static ResourceManagerHelperImpl get(PackType type) {
		return registryMap.computeIfAbsent(type, (t) -> new ResourceManagerHelperImpl());
	}

	public static boolean registerBuiltinResourcePack(ResourceLocation id, String subPath, Mod mod, String displayName, ResourcePackActivationType activationType) {
		String separator = mod.getFilePaths().get(0).getFileSystem().getSeparator();
		
        subPath = subPath.replace("/", separator);
		
        String name = displayName;
		
        ModNioResourcePack resourcePack = ModNioResourcePack.create(id, name, mod, subPath, PackType.CLIENT_RESOURCES, activationType);
		ModNioResourcePack dataPack = ModNioResourcePack.create(id, name, mod, subPath, PackType.SERVER_DATA, activationType);
		
        if (resourcePack == null && dataPack == null) return false;

		if (resourcePack != null) {
			builtinResourcePacks.add(new Tuple<>(name, resourcePack));
		}

		if (dataPack != null) {
			builtinResourcePacks.add(new Tuple<>(name, dataPack));
		}

		return true;
	}

	public static boolean registerBuiltinResourcePack(ResourceLocation id, String subPath, Mod container, ResourcePackActivationType activationType) {
		return registerBuiltinResourcePack(id, subPath, container, id.getNamespace() + "/" + id.getPath(), activationType);
	}

	public static void registerBuiltinResourcePacks(PackType resourceType, Consumer<Pack> consumer, Pack.PackConstructor factory) {
		// Loop through each registered built-in resource packs and add them if valid.
		for (Tuple<String, ModNioResourcePack> entry : builtinResourcePacks) {
			ModNioResourcePack pack = entry.getB();

			// Add the built-in pack only if namespaces for the specified resource type are present.
			if (!pack.getNamespaces(resourceType).isEmpty()) {
				// Make the resource pack profile for built-in pack, should never be always enabled.
				Pack profile = Pack.create(entry.getA(),
						pack.getActivationType() == ResourcePackActivationType.ALWAYS_ENABLED,
						entry::getB, factory, Pack.Position.TOP, new BuiltinModResourcePackSource(pack.getArchitecturyMod().getModId()));
				if (profile != null) {
					consumer.accept(profile);
				}
			}
		}
	}

	public static List<PreparableReloadListener> sort(PackType type, List<PreparableReloadListener> listeners) {
		if (type == null) {
			return listeners;
		}

		ResourceManagerHelperImpl instance = get(type);

		if (instance != null) {
			List<PreparableReloadListener> mutable = new ArrayList<>(listeners);
			
            instance.sort(mutable);
			
            return Collections.unmodifiableList(mutable);
		}

		return listeners;
	}

	protected void sort(List<PreparableReloadListener> listeners) {
		listeners.removeAll(addedListeners);

		List<IdentifiableResourceReloadListener> listenersToAdd = Lists.newArrayList(addedListeners);
		Set<ResourceLocation> resolvedIds = new HashSet<>();

		for (PreparableReloadListener listener : listeners) {
			if (listener instanceof IdentifiableResourceReloadListener) {
				resolvedIds.add(((IdentifiableResourceReloadListener) listener).getFabricId());
			}
		}

		int lastSize = -1;

		while (listeners.size() != lastSize) {
			lastSize = listeners.size();

			Iterator<IdentifiableResourceReloadListener> it = listenersToAdd.iterator();

			while (it.hasNext()) {
				IdentifiableResourceReloadListener listener = it.next();

				if (resolvedIds.containsAll(listener.getFabricDependencies())) {
					resolvedIds.add(listener.getFabricId());
					listeners.add(listener);
					it.remove();
				}
			}
		}

		for (IdentifiableResourceReloadListener listener : listenersToAdd) {
			LOGGER.warn("Could not resolve dependencies for listener: " + listener.getFabricId() + "!");
		}
	}

	@Override
	public void registerReloadListener(IdentifiableResourceReloadListener listener) {
		if (!addedListenerIds.add(listener.getFabricId())) {
			LOGGER.warn("Tried to register resource reload listener " + listener.getFabricId() + " twice!");
			return;
		}

		if (!addedListeners.add(listener)) {
			throw new RuntimeException("Listener with previously unknown ID " + listener.getFabricId() + " already in listener set!");
		}
	}
}
