package com.sanhiruzu.amphibia.event;

import com.sanhiruzu.amphibia.profession.WardenRoleHelper;
import com.sanhiruzu.amphibia.register.AmphibiaItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = "zen_amphibia")
public class FrogWardenHandler {

    @SubscribeEvent
    public static void onFrogInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof Frog frog)) return;
        if (event.getLevel().isClientSide) return;
        if (!event.getItemStack().is(AmphibiaItems.WARDEN_BAND.get())) return;

        Player player = event.getEntity();

        if (!WardenRoleHelper.canClaim(player, frog)) {
            player.displayClientMessage(
                Component.literal("This frog already belongs to someone else.").withStyle(ChatFormatting.RED),
                true
            );
            event.setCancellationResult(InteractionResult.FAIL);
            event.setCanceled(true);
            return;
        }

        if (!WardenRoleHelper.canBecomeWarden(frog)) {
            player.displayClientMessage(
                Component.literal("This frog line is not strong enough to serve as a Warden yet.").withStyle(ChatFormatting.YELLOW),
                true
            );
            event.setCancellationResult(InteractionResult.FAIL);
            event.setCanceled(true);
            return;
        }

        boolean wasWarden = WardenRoleHelper.isWarden(frog);
        WardenRoleHelper.claimFor(player, frog);
        WardenRoleHelper.markAsWarden(frog);
        WardenRoleHelper.setSelectedWarden(player, frog);
        WardenRoleHelper.applyAnchorRestriction(frog);

        if (!player.isCreative() && !wasWarden) {
            event.getItemStack().shrink(1);
        }

        player.displayClientMessage(
            Component.literal("Warden selected. Use the band on a bell to assign its territory.").withStyle(ChatFormatting.AQUA),
            true
        );
        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onBellInteract(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide) return;
        if (!event.getItemStack().is(AmphibiaItems.WARDEN_BAND.get())) return;
        if (!event.getLevel().getBlockState(event.getPos()).is(Blocks.BELL)) return;
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        Player player = event.getEntity();
        WardenRoleHelper.getSelectedWarden(level, player).ifPresentOrElse(frog -> {
            if (frog.distanceToSqr(event.getPos().getCenter()) > WardenRoleHelper.WARDEN_ASSIGNMENT_RANGE * WardenRoleHelper.WARDEN_ASSIGNMENT_RANGE) {
                player.displayClientMessage(
                    Component.literal("Bring the selected Warden closer before assigning a bell.").withStyle(ChatFormatting.YELLOW),
                    true
                );
                event.setCancellationResult(InteractionResult.FAIL);
                event.setCanceled(true);
                return;
            }

            BlockPos anchor = event.getPos();
            WardenRoleHelper.assignAnchor(frog, anchor);
            player.displayClientMessage(
                Component.literal("Warden territory assigned to bell at " + anchor.getX() + ", " + anchor.getY() + ", " + anchor.getZ() + ".")
                    .withStyle(ChatFormatting.GREEN),
                true
            );
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        }, () -> {
            player.displayClientMessage(
                Component.literal("Select a Warden frog with the band first.").withStyle(ChatFormatting.YELLOW),
                true
            );
            event.setCancellationResult(InteractionResult.FAIL);
            event.setCanceled(true);
        });
    }
}
