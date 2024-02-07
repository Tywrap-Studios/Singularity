package fr.catcore.server.translations.fabric;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import fr.catcore.server.translations.ServerTranslationsInitializer;
import fr.catcore.server.translations.api.ServerTranslations;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

public class ServerTranslationsFabric implements IdentifiableResourceReloadListener, ModInitializer {
    public static final ServerTranslations INSTANCE = ServerTranslations.INSTANCE;

    @Override
    public void onInitialize() {
        INSTANCE.onInitialize();
        ServerTranslationsInitializer.init();
    }

    @Override
    public String getName() {
        return INSTANCE.getName();
    }

    @Override
    public ResourceLocation getFabricId() {
        return INSTANCE.getFabricId();
    }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier synchronizer, ResourceManager manager,
            ProfilerFiller prepareProfiler, ProfilerFiller applyProfiler, Executor prepareExecutor,
            Executor applyExecutor) {
        return INSTANCE.reload(synchronizer, manager, prepareProfiler, applyProfiler, prepareExecutor, applyExecutor);
    }
}
