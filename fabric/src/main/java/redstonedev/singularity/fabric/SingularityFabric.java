package redstonedev.singularity.fabric;

import net.fabricmc.api.ModInitializer;
import redstonedev.singularity.Singularity;

public class SingularityFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Singularity.init();
    }
}