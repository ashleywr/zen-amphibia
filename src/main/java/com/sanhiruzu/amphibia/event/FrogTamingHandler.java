package com.sanhiruzu.amphibia.event;

import com.sanhiruzu.amphibia.genetics.AmphibiaFrog;
import com.sanhiruzu.amphibia.genetics.FrogGradeCalculator;
import com.sanhiruzu.amphibia.genetics.FrogHappinessConstants;
import com.sanhiruzu.amphibia.genetics.Gene;
import com.sanhiruzu.amphibia.register.AmphibiaAttachments;
import com.sanhiruzu.amphibia.register.AmphibiaItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = "zen_amphibia")
public class FrogTamingHandler {

    @SubscribeEvent
    public static void onFrogInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof Frog frog)) return;
        if (event.getLevel().isClientSide) return;

        ItemStack item = event.getItemStack();
        Player player = event.getEntity();

        if (item.is(net.minecraft.world.item.Items.SLIME_BALL)) {
            player.displayClientMessage(
                Component.literal("The frog is offended you confused it with a slime.").withStyle(ChatFormatting.YELLOW),
                true
            );
            event.setCancellationResult(InteractionResult.FAIL);
            event.setCanceled(true);
            return;
        }

        if (item.is(AmphibiaItems.FROG_SLIME.get())) {
            player.displayClientMessage(
                Component.literal("Frogs produce slime. They don't eat it.").withStyle(ChatFormatting.YELLOW),
                true
            );
            event.setCancellationResult(InteractionResult.FAIL);
            event.setCanceled(true);
            return;
        }

        if (!item.is(AmphibiaItems.CRICKET.get())) return;

        String ownerUUID = frog.getPersistentData().getString("zen_amphibia:tamed_by");

        if (!ownerUUID.isEmpty()) {
            if (ownerUUID.equals(player.getUUID().toString())) {
                tryBreed(event, frog, player);
            } else {
                player.displayClientMessage(
                    Component.literal("This frog belongs to someone else!").withStyle(ChatFormatting.RED),
                    true
                );
            }
            return;
        }

        FrogGradeCalculator.Grade damageGrade = FrogGradeCalculator.calculateGrade(
            frog.getData(AmphibiaAttachments.FROG_GENOME).getGene(Gene.POWER)
        );

        if (damageGrade.ordinal() > FrogGradeCalculator.Grade.C.ordinal()) {
            player.displayClientMessage(
                Component.literal("This frog is too aggressive to tame!").withStyle(ChatFormatting.RED),
                true
            );
            return;
        }

        frog.getPersistentData().putString("zen_amphibia:tamed_by", player.getUUID().toString());
        frog.setPersistenceRequired();

        if (!player.isCreative()) {
            event.getItemStack().shrink(1);
        }

        player.displayClientMessage(
            Component.literal("You tamed the frog!").withStyle(ChatFormatting.AQUA),
            true
        );

        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }

    private static void tryBreed(PlayerInteractEvent.EntityInteract event, Frog frog, Player player) {
        if (frog.getAge() != 0 || frog.isInLove()) {
            player.displayClientMessage(
                Component.literal("The frog is not ready to breed right now.").withStyle(ChatFormatting.GRAY),
                true
            );
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            return;
        }

        AmphibiaFrog af = AmphibiaFrog.of(frog);
        if (!af.isMature(frog.level().getGameTime())) {
            player.displayClientMessage(
                Component.literal("This frog is still maturing.").withStyle(ChatFormatting.YELLOW),
                true
            );
        } else {
            float happiness = frog.getData(AmphibiaAttachments.FROG_HAPPINESS);
            if (happiness < FrogHappinessConstants.BREEDING_HAPPINESS_THRESHOLD) {
                player.displayClientMessage(
                    Component.literal("This frog needs a more comfortable home to breed.")
                        .withStyle(ChatFormatting.YELLOW),
                    true
                );
            } else {
                frog.setInLove(player);
                player.displayClientMessage(
                    Component.literal("The frog is ready to breed.").withStyle(ChatFormatting.GREEN),
                    true
                );
            }
        }

        if (!player.isCreative()) {
            event.getItemStack().shrink(1);
        }
        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }
}
