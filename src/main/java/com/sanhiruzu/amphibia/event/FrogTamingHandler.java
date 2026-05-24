package com.sanhiruzu.amphibia.event;

import com.sanhiruzu.amphibia.genetics.FrogGradeCalculator;
import com.sanhiruzu.amphibia.genetics.Gene;
import com.sanhiruzu.amphibia.register.AmphibiaAttachments;
import com.sanhiruzu.amphibia.register.AmphibiaItems;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.player.Player;
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
        if (!event.getItemStack().is(AmphibiaItems.CRICKET.get())) return;

        Player player = event.getEntity();
        String ownerUUID = frog.getPersistentData().getString("zen_amphibia:tamed_by");

        // Check if already tamed
        if (!ownerUUID.isEmpty()) {
            if (ownerUUID.equals(player.getUUID().toString())) {
                player.displayClientMessage(
                    Component.literal("This frog is already your pet!").withStyle(ChatFormatting.GREEN),
                    true
                );
            } else {
                player.displayClientMessage(
                    Component.literal("This frog belongs to someone else!").withStyle(ChatFormatting.RED),
                    true
                );
            }
            return;
        }

        // Check if frog can be tamed (low DAMAGE grade = docile)
        FrogGradeCalculator.Grade damageGrade = FrogGradeCalculator.calculateGrade(
            frog.getData(AmphibiaAttachments.FROG_GENOME).getGene(Gene.DAMAGE)
        );

        if (damageGrade.ordinal() > FrogGradeCalculator.Grade.C.ordinal()) {
            player.displayClientMessage(
                Component.literal("This frog is too aggressive to tame!").withStyle(ChatFormatting.RED),
                true
            );
            return;
        }

        // Tame the frog
        frog.getPersistentData().putString("zen_amphibia:tamed_by", player.getUUID().toString());
        frog.setPersistenceRequired();

        if (!player.isCreative()) {
            event.getItemStack().shrink(1);
        }

        player.displayClientMessage(
            Component.literal("You tamed the frog!").withStyle(ChatFormatting.AQUA),
            true
        );

        event.setCancellationResult(net.minecraft.world.InteractionResult.SUCCESS);
        event.setCanceled(true);
    }
}
