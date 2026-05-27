package com.sanhiruzu.amphibia.genetics;

import com.sanhiruzu.amphibia.register.AmphibiaAttachments;
import com.sanhiruzu.amphibia.register.AmphibiaItems;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;

@EventBusSubscriber(modid = "zen_amphibia")
public class FrogDropHandler {

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof Frog frog)) return;
        if (frog.level().isClientSide) return;

        FrogGenome genome = frog.getData(AmphibiaAttachments.FROG_GENOME);
        float happiness = frog.getData(AmphibiaAttachments.FROG_HAPPINESS);

        addFrogSlimeDrops(event, frog, genome, happiness);
    }

    private static void addFrogSlimeDrops(LivingDropsEvent event, Frog frog, FrogGenome genome, float happiness) {
        FrogGradeCalculator.Grade viscosityGrade = FrogGradeCalculator.calculateGrade(genome.getGene(Gene.SLIME_VISCOSITY));

        // Grade B+ frogs express SLIME_VISCOSITY only in a happy enough environment
        if (viscosityGrade.ordinal() >= FrogGradeCalculator.Grade.B.ordinal()
                && happiness >= FrogDropConstants.GRADE_GATE_THRESHOLD) {
            int count = switch (viscosityGrade) {
                case B -> FrogDropConstants.GRADE_B_DROP_COUNT;
                case A -> FrogDropConstants.GRADE_A_DROP_COUNT;
                case S -> FrogDropConstants.GRADE_S_DROP_COUNT;
                default -> 0;
            };
            if (happiness >= FrogDropConstants.BONUS_THRESHOLD) count++;
            drop(event, frog, count);
        }

        // Additional probabilistic drop at higher happiness
        if (happiness >= FrogDropConstants.ENHANCED_DROP_THRESHOLD) {
            float chance = FrogDropConstants.ENHANCED_DROP_BASE_CHANCE
                + (happiness - FrogDropConstants.ENHANCED_DROP_THRESHOLD) * FrogDropConstants.ENHANCED_DROP_SCALE;
            if (frog.getRandom().nextFloat() < chance) {
                drop(event, frog, 1);
            }
        }
    }

    private static void drop(LivingDropsEvent event, Frog frog, int count) {
        event.getDrops().add(new ItemEntity(
            frog.level(),
            frog.getX(), frog.getY(), frog.getZ(),
            new ItemStack(AmphibiaItems.FROG_SLIME.get(), count)
        ));
    }
}
