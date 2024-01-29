package redstonedev.singularity.fabric;

import redstonedev.singularity.Singularity;
import net.fabricmc.api.ModInitializer;

public class SingularityFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Singularity.init();
    }
}