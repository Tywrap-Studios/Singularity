package redstonedev.singularity;

import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import redstonedev.singularity.commands.Commands;
import redstonedev.singularity.config.SingularityConfig;

public class Singularity {
    public static final String MOD_ID = "singularity";
    public static SingularityConfig CONFIG;

    public static void init() {
        AutoConfig.register(SingularityConfig.class, Toml4jConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(SingularityConfig.class).getConfig();

        CommandRegistrationEvent.EVENT.register(Commands::register);
        LifecycleEvent.SERVER_STARTED.register(AutoClear::start);
    }
}
