package fr.catcore.server.translations.api.polyfill;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.architectury.platform.Mod;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;

public class ModNioResourcePack implements ModResourcePack {
	private static final Logger LOGGER = LoggerFactory.getLogger(ModNioResourcePack.class);
	private static final Pattern RESOURCE_PACK_PATH = Pattern.compile("[a-z0-9-_.]+");
	private static final FileSystem DEFAULT_FS = FileSystems.getDefault();

	private final ResourceLocation id;
	private final String name;
    private final Mod mod;
	private final List<Path> basePaths;
	private final PackType type;
	private final AutoCloseable closer;
	private final ResourcePackActivationType activationType;
	private final Map<PackType, Set<String>> namespaces;

	public static ModNioResourcePack create(ResourceLocation id, String name, Mod mod, String subPath, PackType type, ResourcePackActivationType activationType) {
		List<Path> rootPaths = mod.getFilePaths();
		List<Path> paths;

		if (subPath == null) {
			paths = rootPaths;
		} else {
			paths = new ArrayList<>(rootPaths.size());

			for (Path path : rootPaths) {
				path = path.toAbsolutePath().normalize();
				Path childPath = path.resolve(subPath.replace("/", path.getFileSystem().getSeparator())).normalize();

				if (!childPath.startsWith(path) || !exists(childPath)) {
					continue;
				}

				paths.add(childPath);
			}
		}

		if (paths.isEmpty()) return null;

		ModNioResourcePack ret = new ModNioResourcePack(id, name, mod, paths, type, null, activationType);

		return ret.getNamespaces(type).isEmpty() ? null : ret;
	}

	private ModNioResourcePack(ResourceLocation id, String name, Mod mod, List<Path> paths, PackType type, AutoCloseable closer, ResourcePackActivationType activationType) {
		this.id = id;
		this.name = name;
		this.mod = mod;
		this.basePaths = paths;
		this.type = type;
		this.closer = closer;
		this.activationType = activationType;
		this.namespaces = readNamespaces(paths, mod.getModId());
	}

	static Map<PackType, Set<String>> readNamespaces(List<Path> paths, String modId) {
		Map<PackType, Set<String>> ret = new EnumMap<>(PackType.class);

		for (PackType type : PackType.values()) {
			Set<String> namespaces = null;

			for (Path path : paths) {
				Path dir = path.resolve(type.getDirectory());
                
				if (!Files.isDirectory(dir)) continue;

				String separator = path.getFileSystem().getSeparator();

				try (DirectoryStream<Path> ds = Files.newDirectoryStream(dir)) {
					for (Path p : ds) {
						if (!Files.isDirectory(p)) continue;

						String s = p.getFileName().toString();
						// s may contain trailing slashes, remove them
						s = s.replace(separator, "");

						if (!RESOURCE_PACK_PATH.matcher(s).matches()) {
							LOGGER.warn("Fabric NioResourcePack: ignored invalid namespace: {} in mod ID {}", s, modId);
							continue;
						}

						if (namespaces == null) namespaces = new HashSet<>();

						namespaces.add(s);
					}
				} catch (IOException e) {
					LOGGER.warn("getNamespaces in mod " + modId + " failed!", e);
				}
			}

			ret.put(type, namespaces != null ? namespaces : Collections.emptySet());
		}

		return ret;
	}

	private Path getPath(String filename) {
		if (hasAbsentNs(filename)) return null;

		for (Path basePath : basePaths) {
			Path childPath = basePath.resolve(filename.replace("/", basePath.getFileSystem().getSeparator())).toAbsolutePath().normalize();

			if (childPath.startsWith(basePath) && exists(childPath)) {
				return childPath;
			}
		}

		return null;
	}

	private static final String resPrefix = PackType.CLIENT_RESOURCES.getDirectory() + "/";
	private static final String dataPrefix = PackType.SERVER_DATA.getDirectory() + "/";

	private boolean hasAbsentNs(String filename) {
		int prefixLen;
		PackType type;

		if (filename.startsWith(resPrefix)) {
			prefixLen = resPrefix.length();
			type = PackType.CLIENT_RESOURCES;
		} else if (filename.startsWith(dataPrefix)) {
			prefixLen = dataPrefix.length();
			type = PackType.SERVER_DATA;
		} else {
			return false;
		}

		int nsEnd = filename.indexOf('/', prefixLen);
		if (nsEnd < 0) return false;

		return !namespaces.get(type).contains(filename.substring(prefixLen, nsEnd));
	}

	private InputStream openFile(String filename) throws IOException {
		InputStream stream;

		Path path = getPath(filename);

		if (path != null && Files.isRegularFile(path)) {
			return Files.newInputStream(path);
		}

		stream = ModResourcePackUtil.openDefault(this.mod, this.type, filename);

		if (stream != null) {
			return stream;
		}

		// ReloadableResourceManagerImpl gets away with FileNotFoundException.
		throw new FileNotFoundException("\"" + filename + "\" in Fabric mod \"" + mod.getModId() + "\"");
	}

	@Override
	public InputStream getRootResource(String fileName) throws IOException {
		if (fileName.contains("/") || fileName.contains("\\")) {
			throw new IllegalArgumentException("Root resources can only be filenames, not paths (no / allowed!)");
		}

		return this.openFile(fileName);
	}

	@Override
	public InputStream getResource(PackType type, ResourceLocation id) throws IOException {
		return openFile(getFilename(type, id));
	}

	@Override
	public Collection<ResourceLocation> getResources(PackType type, String namespace, String path, Predicate<ResourceLocation> predicate) {
		if (!namespaces.getOrDefault(type, Collections.emptySet()).contains(namespace)) {
			return Collections.emptyList();
		}

		List<ResourceLocation> ids = new ArrayList<>();

		for (Path basePath : basePaths) {
			String separator = basePath.getFileSystem().getSeparator();
			Path nsPath = basePath.resolve(type.getDirectory()).resolve(namespace);
			Path searchPath = nsPath.resolve(path.replace("/", separator)).normalize();
			if (!exists(searchPath)) continue;

			try {
				Files.walkFileTree(searchPath, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
						String fileName = file.getFileName().toString();

						if (fileName.endsWith(".mcmeta")) return FileVisitResult.CONTINUE;

						try {
							ResourceLocation id = new ResourceLocation(namespace, nsPath.relativize(file).toString().replace(separator, "/"));
							
                            if (predicate.test(id)) ids.add(id);
						} catch (ResourceLocationException e) {
							LOGGER.error(e.getMessage());
						}

						return FileVisitResult.CONTINUE;
					}
				});
			} catch (IOException e) {
				LOGGER.warn("findResources at " + path + " in namespace " + namespace + ", mod " + mod.getModId() + " failed!", e);
			}
		}

		return ids;
	}

	@Override
	public boolean hasResource(PackType type, ResourceLocation id) {
		String filename = getFilename(type, id);

		if (ModResourcePackUtil.containsDefault(mod, filename)) {
			return true;
		}

		Path path = getPath(filename);
        
		return path != null && Files.isRegularFile(path);
	}

	@Override
	public Set<String> getNamespaces(PackType type) {
		return namespaces.getOrDefault(type, Collections.emptySet());
	}

	@Override
	public <T> T getMetadataSection(MetadataSectionSerializer<T> metaReader) throws IOException {
		try (InputStream is = openFile("pack.mcmeta")) {
			return AbstractPackResources.getMetadataFromStream(metaReader, is);
		}
	}

	@Override
	public void close() {
		if (closer != null) {
			try {
				closer.close();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public Mod getArchitecturyMod() {
		return mod;
	}

	public ResourcePackActivationType getActivationType() {
		return this.activationType;
	}

	@Override
	public String getName() {
		return name;
	}

	public ResourceLocation getId() {
		return id;
	}

	private static boolean exists(Path path) {
		// NIO Files.exists is notoriously slow when checking the file system
		return path.getFileSystem() == DEFAULT_FS ? path.toFile().exists() : Files.exists(path);
	}

	private static String getFilename(PackType type, ResourceLocation id) {
		return String.format(Locale.ROOT, "%s/%s/%s", type.getDirectory(), id.getNamespace(), id.getPath());
	}
}

