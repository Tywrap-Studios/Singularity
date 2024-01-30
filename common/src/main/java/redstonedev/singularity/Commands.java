package redstonedev.singularity;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands.CommandSelection;

public class Commands {
    @SuppressWarnings("unused")
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext access, CommandSelection env) {
        dispatcher.register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("singularity")
                        .requires((src) -> src.hasPermission(2))
                        .then(LiteralArgumentBuilder.<CommandSourceStack>literal("clear")
                                .executes(ctx -> {
                                    Clear.clearItems(ctx.getSource().getServer());
                                    return 1;
                                })
                        )
                        .then(LiteralArgumentBuilder.<CommandSourceStack>literal("detect")
                                .executes(ctx -> {
                                    Detect.detectSingularitiesAuto(ctx.getSource().getServer());
                                    return 1;
                                })
                        )
        );
    }
}
