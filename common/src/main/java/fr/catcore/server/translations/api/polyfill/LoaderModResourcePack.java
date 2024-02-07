package fr.catcore.server.translations.api.polyfill;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

import com.google.common.base.Charsets;

import net.minecraft.SharedConstants;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;

public class LoaderModResourcePack extends GroupResourcePack {
    public LoaderModResourcePack(PackType type, List<ModResourcePack> packs) {
        super(type, packs);
    }

    @Override
    public InputStream getRootResource(String fileName) throws IOException {
        if ("pack.mcmeta".equals(fileName)) {
            String description = "Mod resources.";
            String pack = String.format("{\"pack\":{\"pack_format\":"
                    + type.getVersion(SharedConstants.getCurrentVersion()) + ",\"description\":\"%s\"}}", description);
            return IOUtils.toInputStream(pack, Charsets.UTF_8);
        }

        // ReloadableResourceManagerImpl gets away with FileNotFoundException.
        throw new FileNotFoundException("\"" + fileName + "\" in Fabric mod resource pack");
    }

    @Override
    public <T> @Nullable T getMetadataSection(MetadataSectionSerializer<T> metaReader) throws IOException {
        try {
            InputStream inputStream = this.getRootResource("pack.mcmeta");
            Throwable error = null;
            T metadata;

            try {
                metadata = AbstractPackResources.getMetadataFromStream(metaReader, inputStream);
            } catch (Throwable e) {
                error = e;
                throw e;
            } finally {
                if (inputStream != null) {
                    if (error != null) {
                        try {
                            inputStream.close();
                        } catch (Throwable e) {
                            error.addSuppressed(e);
                        }
                    } else {
                        inputStream.close();
                    }
                }
            }

            return metadata;
        } catch (FileNotFoundException | RuntimeException e) {
            return null;
        }
    }

    @Override
    public String getName() {
        return "Mods";
    }
}
