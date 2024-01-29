package redstonedev.singularity;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import redstonedev.singularity.config.SingularityConfig;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("CallToPrintStackTrace")
public class Singularity {
	public static final String MOD_ID = "singularity";
	public static SingularityConfig CONFIG;

	public static void init() {
		AutoConfig.register(SingularityConfig.class, Toml4jConfigSerializer::new);
		CONFIG = AutoConfig.getConfigHolder(SingularityConfig.class).getConfig();

		CommandRegistrationEvent.EVENT.register((disp, acc, env) -> {
			disp.register(LiteralArgumentBuilder.<CommandSourceStack>literal("singularity").requires((src) -> src.hasPermission(2)).executes(ctx -> {
				clearItems(ctx.getSource().getServer());
				return 1;
			}));
		});

		LifecycleEvent.SERVER_STARTED.register((server) -> {
			if (!CONFIG.clearOptions.enableClearing) return;

			Thread thread = new Thread(() -> {
				try {
					while (true) {
						if (CONFIG.clearOptions.clearInterval < 60) {
							CONFIG.clearOptions.clearInterval = 60;
						}

						Thread.sleep((CONFIG.clearOptions.clearInterval - 30) * 1000L);
						chat(server, Component.translatable("singularity.chat.clearingItems.30"));

						Thread.sleep(20 * 1000L);
						chat(server, Component.translatable("singularity.chat.clearingItems.10"));

						Thread.sleep(10 * 1000L);
						chat(server, Component.translatable("singularity.chat.clearingItems"));

						clearItems(server);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			});

			thread.start();
		});
	}

	private static void chat(MinecraftServer server, Component msg) {
		server.sendSystemMessage(msg);

		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			player.sendSystemMessage(msg);
		}
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	public static void clearItems(MinecraftServer server) {
		int cleared = 0;
		List<BlockPos> positions = new ArrayList<>();

		for (ServerLevel level : server.getAllLevels()) {
			for (Entity entity : level.getAllEntities()) {
				if (entity instanceof ItemEntity) {
					cleared++;
					positions.add(new BlockPos(entity.getBlockX(), entity.getBlockY(), entity.getBlockZ()));
					entity.kill();
				}
			}
		}

		chat(server, Component.translatable("singularity.chat.clearingItems.done"));

		if (CONFIG.clearOptions.showCleared) {
			chat(server, Component.translatable("singularity.chat.clearingItems.count", cleared));
		} else {
			server.sendSystemMessage(Component.translatable("singularity.chat.clearingItems.count", cleared));
		}

		Path path = server.getServerDirectory().toPath().resolve("singularity").resolve("dump-" + LocalDate.now() + "-" + LocalTime.now().toString().replaceAll(":", "-") + ".txt");
		StringBuilder data = new StringBuilder();

		for (BlockPos pos : positions) {
			data.append("(").append(pos.getX()).append(",").append(pos.getY()).append(",").append(pos.getZ()).append(")\n");
		}

		String dataStr = data.toString();

		if (!path.getParent().toFile().exists()) {
			path.getParent().toFile().mkdirs();
		}

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile()));

			writer.write(dataStr);
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
