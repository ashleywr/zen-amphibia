package com.sanhiruzu.amphibia.event;

import com.sanhiruzu.amphibia.genetics.AmphibiaFrog;
import com.sanhiruzu.amphibia.genetics.FrogGradeCalculator;
import com.sanhiruzu.amphibia.genetics.Gene;
import com.sanhiruzu.amphibia.genetics.FrogGenome;
import com.sanhiruzu.amphibia.register.AmphibiaAttachments;
import com.sanhiruzu.amphibia.register.AmphibiaItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
    private static final String LAST_SLIME_TICK_KEY = "zen_amphibia:last_frog_slime_tick";
    private static final long FROG_SLIME_COOLDOWN_TICKS = 2400L;

    @SubscribeEvent
    public static void onFrogInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof Frog frog)) return;
        if (event.getLevel().isClientSide) return;

        ItemStack item = event.getItemStack();
        Player player = event.getEntity();

        if (item.is(Items.SLIME_BALL)) {
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

        // Check if already tamed
        if (!ownerUUID.isEmpty()) {
            if (ownerUUID.equals(player.getUUID().toString())) {
                tryProduceFrogSlime(event, frog, player);
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

        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }

    private static void tryProduceFrogSlime(PlayerInteractEvent.EntityInteract event, Frog frog, Player player) {
        long gameTime = frog.level().getGameTime();
        long ticksUntilReady = frog.getPersistentData().contains(LAST_SLIME_TICK_KEY)
            ? FROG_SLIME_COOLDOWN_TICKS - (gameTime - frog.getPersistentData().getLong(LAST_SLIME_TICK_KEY))
            : 0;

        if (ticksUntilReady > 0) {
            player.displayClientMessage(
                Component.literal("This frog is not slimy enough yet.").withStyle(ChatFormatting.GRAY),
                true
            );
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            return;
        }

        FrogGenome genome = frog.getData(AmphibiaAttachments.FROG_GENOME);
        int slimeAmount = calculateSlimeAmount(genome);
        ItemStack slime = new ItemStack(AmphibiaItems.FROG_SLIME.get(), slimeAmount);

        if (!player.isCreative()) {
            event.getItemStack().shrink(1);
        }

        if (!player.getInventory().add(slime)) {
            player.drop(slime, false);
        }

        frog.getPersistentData().putLong(LAST_SLIME_TICK_KEY, gameTime);

        // Also put the frog in love mode — crickets are how you breed tamed frogs
        if (frog.getAge() == 0 && !frog.isInLove()) {
            AmphibiaFrog af = AmphibiaFrog.of(frog);
            if (!af.isMature(frog.level().getGameTime())) {
                player.displayClientMessage(
                    Component.literal("This frog is still maturing.").withStyle(ChatFormatting.YELLOW),
                    true
                );
            } else {
                frog.setInLove(player);
            }
        }

        player.displayClientMessage(
            Component.literal("The fed frog produced frog slime.").withStyle(ChatFormatting.GREEN),
            true
        );
        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }

    private static int calculateSlimeAmount(FrogGenome genome) {
        if (genome == null) return 1;

        FrogGradeCalculator.Grade slimeGrade = FrogGradeCalculator.calculateGrade(genome.getGene(Gene.SLIME_VISCOSITY));
        FrogGradeCalculator.Grade sizeGrade = FrogGradeCalculator.calculateGrade(genome.getGene(Gene.SIZE));

        int amount = 1;
        if (slimeGrade.ordinal() >= FrogGradeCalculator.Grade.B.ordinal()) amount++;
        if (slimeGrade == FrogGradeCalculator.Grade.S) amount++;
        if (sizeGrade.ordinal() >= FrogGradeCalculator.Grade.A.ordinal()) amount++;
        return Math.min(amount, 4);
    }
}
