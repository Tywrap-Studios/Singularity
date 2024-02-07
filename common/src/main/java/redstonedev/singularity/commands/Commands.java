package redstonedev.singularity.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;

import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands.CommandSelection;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import redstonedev.singularity.Detector;

public class Commands {
        public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext access,
                        CommandSelection env) {
                dispatcher.register(
                                LiteralArgumentBuilder.<CommandSourceStack>literal("singularity")
                                                .requires((src) -> src.hasPermission(2))
                                                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("clear")
                                                                .executes(ctx -> {
                                                                        Clear.clearItems(ctx.getSource().getServer());
                                                                        return 1;
                                                                }))
                                                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("detect")
                                                                .executes(ctx -> {
                                                                        Detector.detectSingularities(
                                                                                        ctx.getSource().getServer());
                                                                        return 1;
                                                                }))
                                                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("debug")
                                                                .then(RequiredArgumentBuilder
                                                                                .<CommandSourceStack, ResourceLocation>argument(
                                                                                                "level",
                                                                                                DimensionArgument
                                                                                                                .dimension())
                                                                                .then(RequiredArgumentBuilder
                                                                                                .<CommandSourceStack, Coordinates>argument(
                                                                                                                "pos",
                                                                                                                Vec3Argument.vec3())
                                                                                                .then(RequiredArgumentBuilder
                                                                                                                .<CommandSourceStack, ItemInput>argument(
                                                                                                                                "item_id",
                                                                                                                                ItemArgument.item(
                                                                                                                                                access))
                                                                                                                .then(RequiredArgumentBuilder
                                                                                                                                .<CommandSourceStack, Integer>argument(
                                                                                                                                                "count",
                                                                                                                                                IntegerArgumentType
                                                                                                                                                                .integer(0))
                                                                                                                                .executes(ctx -> {
                                                                                                                                        ItemInput item = ItemArgument
                                                                                                                                                        .getItem(ctx, "item_id");
                                                                                                                                        int count = IntegerArgumentType
                                                                                                                                                        .getInteger(ctx, "count");
                                                                                                                                        ServerLevel level = DimensionArgument
                                                                                                                                                        .getDimension(ctx,
                                                                                                                                                                        "level");
                                                                                                                                        BlockPos pos = Vec3Argument
                                                                                                                                                        .getCoordinates(ctx,
                                                                                                                                                                        "pos").getBlockPos(ctx.getSource());

                                                                                                                                        Debug.spawn(ctx.getSource()
                                                                                                                                                        .getServer(),
                                                                                                                                                        level,
                                                                                                                                                        pos,
                                                                                                                                                        item,
                                                                                                                                                        count);
                                                                                                                                        return 1;
                                                                                                                                })))))));
        }
}
