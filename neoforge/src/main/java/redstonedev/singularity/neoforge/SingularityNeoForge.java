package redstonedev.singularity.neoforge;

import me.shedaniel.autoconfig.AutoConfig;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import redstonedev.singularity.Singularity;
import redstonedev.singularity.config.SingularityConfig;

@Mod(Singularity.MOD_ID)
public class SingularityNeoForge {
    public SingularityNeoForge() {
        EventBuses.registerModEventBus(Singularity.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        Singularity.init();

        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                        (client, parent) -> AutoConfig.getConfigScreen(SingularityConfig.class, parent).get()
                )
        );
    }
}