package redstonedev.singularity.forge;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import dev.architectury.platform.forge.EventBuses;
import fr.catcore.server.translations.ServerTranslationsInitializer;
import fr.catcore.server.translations.api.ServerTranslations;
import fr.catcore.server.translations.api.polyfill.IdentifiableResourceReloadListener;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import redstonedev.singularity.Singularity;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import redstonedev.singularity.config.SingularityConfig;

// I would make another class but then forge yells at me :(
@Mod(Singularity.MOD_ID)
public class SingularityForge implements IdentifiableResourceReloadListener {
    public static final ServerTranslations INSTANCE = ServerTranslations.INSTANCE;

    public SingularityForge() {
        EventBuses.registerModEventBus(Singularity.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        
        Singularity.init();
        INSTANCE.onInitialize();
        ServerTranslationsInitializer.init();

        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                        (client, parent) -> AutoConfig.getConfigScreen(SingularityConfig.class, parent).get()
                )
        );
    }

    @SubscribeEvent
    public void onReload(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(this);
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