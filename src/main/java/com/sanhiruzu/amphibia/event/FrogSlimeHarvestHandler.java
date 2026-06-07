package com.sanhiruzu.amphibia.event;

import com.sanhiruzu.amphibia.genetics.FrogDropHandler;
import com.sanhiruzu.amphibia.genetics.FrogGradeCalculator;
import com.sanhiruzu.amphibia.genetics.FrogGenome;
import com.sanhiruzu.amphibia.genetics.FrogSlimeHarvestConstants;
import com.sanhiruzu.amphibia.genetics.Gene;
import com.sanhiruzu.amphibia.register.AmphibiaAttachments;
import com.sanhiruzu.amphibia.register.AmphibiaDataComponents;
import com.sanhiruzu.amphibia.register.AmphibiaItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = "zen_amphibia")
public class FrogSlimeHarvestHandler {

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Frog frog)) return;
        if (frog.level().isClientSide) return;
        if (frog.isBaby()) return;
        if (frog.tickCount % FrogSlimeHarvestConstants.TICK_INTERVAL != 0) return;
        if (frog.getData(AmphibiaAttachments.SLIME_HARVEST_READY)) return;

        FrogGenome genome = frog.getData(AmphibiaAttachments.FROG_GENOME);
        FrogGradeCalculator.Grade grade = FrogGradeCalculator.calculateGrade(genome.getGene(Gene.SLIME_YIELD));
        if (grade.ordinal() < FrogGradeCalculator.Grade.B.ordinal()) return;

        float happiness = frog.getData(AmphibiaAttachments.FROG_HAPPINESS);
        if (happiness < FrogSlimeHarvestConstants.MIN_HAPPINESS) return;

        if (isOvercrowded(frog)) return;

        float gain = FrogSlimeHarvestConstants.progressPerInterval(grade)
            * FrogSlimeHarvestConstants.happinessMultiplier(happiness);

        float progress = frog.getData(AmphibiaAttachments.SLIME_READINESS_PROGRESS) + gain;
        if (progress >= 1.0f) {
            frog.setData(AmphibiaAttachments.SLIME_READINESS_PROGRESS, 0.0f);
            frog.setData(AmphibiaAttachments.SLIME_HARVEST_READY, true);
        } else {
            frog.setData(AmphibiaAttachments.SLIME_READINESS_PROGRESS, progress);
        }
    }

    @SubscribeEvent
    public static void onFrogInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof Frog frog)) return;
        if (event.getLevel().isClientSide) return;
        if (!event.getItemStack().isEmpty()) return;
        if (!frog.getData(AmphibiaAttachments.SLIME_HARVEST_READY)) return;

        Player player = event.getEntity();
        FrogGenome genome = frog.getData(AmphibiaAttachments.FROG_GENOME);
        float happiness = frog.getData(AmphibiaAttachments.FROG_HAPPINESS);
        int count = calculateYield(frog, genome, happiness);
        ItemStack result = resolveSecretionStack(genome);
        result.setCount(count);

        if (!player.getInventory().add(result)) {
            player.drop(result, false);
        }

        frog.setData(AmphibiaAttachments.SLIME_HARVEST_READY, false);
        frog.setData(AmphibiaAttachments.SLIME_READINESS_PROGRESS, 0.0f);

        frog.playSound(SoundEvents.SLIME_SQUISH_SMALL, 0.7f, 1.1f + frog.getRandom().nextFloat() * 0.2f);
        player.displayClientMessage(
            Component.literal("Collected " + count + " " + result.getHoverName().getString() + ".").withStyle(ChatFormatting.GREEN),
            true
        );

        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }

    private static boolean isOvercrowded(Frog frog) {
        int nearby = frog.level().getEntitiesOfClass(Frog.class,
            frog.getBoundingBox().inflate(FrogSlimeHarvestConstants.OVERCROWD_RADIUS)).size();
        return nearby > FrogSlimeHarvestConstants.OVERCROWD_MAX;
    }

    // Returns the gene that best characterises this frog's profile (Grade A or S, highest wins).
    // First-encountered gene wins ties. Returns null if no gene reaches Grade A.
    public static Gene getDominantGene(FrogGenome genome) {
        Gene dominant = null;
        int best = FrogGradeCalculator.Grade.B.ordinal(); // must strictly exceed B to qualify
        for (Gene gene : Gene.values()) {
            if (gene == Gene.SLIME_YIELD) continue;
            int ord = FrogGradeCalculator.calculateGrade(genome.getGene(gene)).ordinal();
            if (ord > best) {
                best = ord;
                dominant = gene;
            }
        }
        return dominant;
    }

    // Builds the base secretion ItemStack (without count) for a given genome.
    // COLORATION dominant + SLIME_YIELD A/S → pigmented (with FROGLIGHT_TYPE component)
    // COLORATION dominant + SLIME_YIELD B     → white luminous secretion
    // anything else                           → basic frog slime
    public static ItemStack resolveSecretionStack(FrogGenome genome) {
        Gene dominant = getDominantGene(genome);
        if (dominant != Gene.COLORATION) {
            return new ItemStack(AmphibiaItems.FROG_SLIME.get());
        }

        FrogGradeCalculator.Grade yieldGrade = FrogGradeCalculator.calculateGrade(genome.getGene(Gene.SLIME_YIELD));
        if (yieldGrade.ordinal() >= FrogGradeCalculator.Grade.A.ordinal()) {
            Item froglightItem = FrogDropHandler.froglightForGenome(genome);
            ResourceLocation froglightId = BuiltInRegistries.ITEM.getKey(froglightItem);
            ItemStack pigmented = new ItemStack(AmphibiaItems.PIGMENTED_FROG_SECRETION.get());
            pigmented.set(AmphibiaDataComponents.FROGLIGHT_TYPE.get(), froglightId);
            return pigmented;
        }

        return new ItemStack(AmphibiaItems.LUMINOUS_FROG_SECRETION.get());
    }

    private static int calculateYield(Frog frog, FrogGenome genome, float happiness) {
        FrogGradeCalculator.Grade grade = FrogGradeCalculator.calculateGrade(genome.getGene(Gene.SLIME_YIELD));
        return switch (grade) {
            case B -> 1;
            case A -> 1 + (frog.getRandom().nextBoolean() ? 1 : 0);
            case S -> {
                int base = 2;
                if (happiness >= FrogSlimeHarvestConstants.S_BONUS_HAPPINESS
                        && frog.getRandom().nextFloat() < FrogSlimeHarvestConstants.S_BONUS_CHANCE) {
                    base = 3;
                }
                yield base;
            }
            default -> 0;
        };
    }
}
