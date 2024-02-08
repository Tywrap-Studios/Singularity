package redstonedev.singularity.forge;

import dev.architectury.platform.forge.EventBuses;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
import redstonedev.singularity.Singularity;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import redstonedev.singularity.config.SingularityConfig;

@Mod(Singularity.MOD_ID)
public class SingularityForge {
    public SingularityForge() {
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