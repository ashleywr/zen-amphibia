package com.sanhiruzu.amphibia.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.sanhiruzu.amphibia.genetics.FrogGenome;
import com.sanhiruzu.amphibia.genetics.FrogMutation;
import com.sanhiruzu.amphibia.register.AmphibiaAttachments;
import com.sanhiruzu.amphibia.register.AmphibiaDataComponents;
import com.sanhiruzu.amphibia.register.AmphibiaItems;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class AmphibiaCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("zen_amphibia")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("spawn")
                .then(Commands.literal("frog")
                    .executes(ctx -> spawnFrog(ctx.getSource(), 1, null))
                    .then(Commands.argument("count", IntegerArgumentType.integer(1, 100))
                        .executes(ctx -> spawnFrog(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "count"), null))
                        .then(Commands.argument("mutation", StringArgumentType.word())
                            .executes(ctx -> spawnFrog(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "count"), StringArgumentType.getString(ctx, "mutation")))
                        )
                    )
                )
                .then(Commands.literal("tadpole")
                    .executes(ctx -> spawnTadpole(ctx.getSource(), 1))
                    .then(Commands.argument("count", IntegerArgumentType.integer(1, 100))
                        .executes(ctx -> spawnTadpole(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "count")))
                    )
                )
                .then(Commands.literal("bottled_frogspawn")
                    .executes(ctx -> spawnBottledFrogspawn(ctx.getSource(), 1))
                    .then(Commands.argument("count", IntegerArgumentType.integer(1, 64))
                        .executes(ctx -> spawnBottledFrogspawn(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "count")))
                    )
                )
            )
            .then(Commands.literal("overlay")
                .executes(ctx -> toggleOverlay(ctx.getSource()))
            )
        );
    }

    private static int spawnFrog(CommandSourceStack source, int count, String mutationId) {
        if (source.getEntity() == null) {
            source.sendFailure(Component.literal("Must be executed by a player"));
            return 0;
        }

        Level level = source.getLevel();
        var pos = source.getEntity().position();

        for (int i = 0; i < count; i++) {
            Frog frog = EntityType.FROG.create(level);
            if (frog != null) {
                frog.moveTo(pos.x + (i * 0.5), pos.y, pos.z + (i * 0.5), 0, 0);

                if (mutationId != null) {
                    FrogMutation mutation = FrogMutation.getById(mutationId);
                    if (mutation != null) {
                        mutation.applyToFrog(frog);
                    }
                }

                level.addFreshEntity(frog);
            }
        }

        source.sendSuccess(() -> Component.literal("Spawned " + count + " frog(s)" + (mutationId != null ? " with mutation: " + mutationId : "")), true);
        return count;
    }

    private static int spawnTadpole(CommandSourceStack source, int count) {
        if (source.getEntity() == null) {
            source.sendFailure(Component.literal("Must be executed by a player"));
            return 0;
        }

        Level level = source.getLevel();
        var pos = source.getEntity().position();

        for (int i = 0; i < count; i++) {
            Tadpole tadpole = EntityType.TADPOLE.create(level);
            if (tadpole != null) {
                tadpole.moveTo(pos.x + (i * 0.5), pos.y, pos.z + (i * 0.5), 0, 0);
                level.addFreshEntity(tadpole);
            }
        }

        source.sendSuccess(() -> Component.literal("Spawned " + count + " tadpole(s)"), true);
        return count;
    }

    private static int spawnBottledFrogspawn(CommandSourceStack source, int count) {
        if (source.getEntity() == null) {
            source.sendFailure(Component.literal("Must be executed by a player"));
            return 0;
        }

        Level level = source.getLevel();
        var pos = source.getEntity().position();

        for (int i = 0; i < count; i++) {
            ItemStack stack = new ItemStack(AmphibiaItems.BOTTLED_FROGSPAWN.get());
            stack.set(AmphibiaDataComponents.FROG_DNA.get(), FrogGenome.createDefault());

            var itemEntity = new net.minecraft.world.entity.item.ItemEntity(level, pos.x + (i * 0.5), pos.y, pos.z + (i * 0.5), stack);
            level.addFreshEntity(itemEntity);
        }

        source.sendSuccess(() -> Component.literal("Spawned " + count + " bottled frogspawn item(s)"), true);
        return count;
    }

    private static int toggleOverlay(CommandSourceStack source) {
        if (!(source.getEntity() instanceof Player player)) {
            source.sendFailure(Component.literal("Must be executed by a player"));
            return 0;
        }
        boolean current = player.getData(AmphibiaAttachments.FROG_DNA_OVERLAY_ENABLED);
        boolean newState = !current;
        player.setData(AmphibiaAttachments.FROG_DNA_OVERLAY_ENABLED, newState);
        source.sendSuccess(() -> Component.literal("Frog DNA overlay: " + (newState ? "ON" : "OFF"))
            .withStyle(newState ? ChatFormatting.GREEN : ChatFormatting.RED), true);
        return 1;
    }
}
