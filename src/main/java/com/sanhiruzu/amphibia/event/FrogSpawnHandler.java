package com.sanhiruzu.amphibia.event;

import com.sanhiruzu.amphibia.entity.goal.GrazeKelpGoal;
import com.sanhiruzu.amphibia.entity.goal.FrogBiteGoal;
import com.sanhiruzu.amphibia.entity.goal.FrogTongueGoal;
import com.sanhiruzu.amphibia.entity.goal.FrogHostileTargetGoal;
import com.sanhiruzu.amphibia.genetics.FrogGenome;
import com.sanhiruzu.amphibia.genetics.Gene;
import com.sanhiruzu.amphibia.genetics.FrogGradeCalculator;
import com.sanhiruzu.amphibia.genetics.FrogMutation;
import com.sanhiruzu.amphibia.genetics.FrogCombatCapability;
import com.sanhiruzu.amphibia.genetics.FrogAttackType;
import com.sanhiruzu.amphibia.register.AmphibiaAttachments;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@EventBusSubscriber(modid = "zen_amphibia")
public class FrogSpawnHandler {

    @SubscribeEvent
    public static void onFrogJoinLevel(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Frog frog) {
            if (frog.level().isClientSide) return;

            // Check if genetics have already been applied (e.g., from NBT on load)
            boolean geneticsApplied = frog.getData(AmphibiaAttachments.FROG_GENETICS_APPLIED);
            if (geneticsApplied) return;

            // Get or create genome
            FrogGenome genome = frog.getData(AmphibiaAttachments.FROG_GENOME);
            if (genome == null) {
                genome = FrogGenome.createDefault();
                frog.setData(AmphibiaAttachments.FROG_GENOME, genome);
            }

            // Apply genetics immediately, before first render
            applyGeneticsToFrog(frog, genome);
            frog.setData(AmphibiaAttachments.FROG_GENETICS_APPLIED, true);
        } else if (event.getEntity() instanceof Tadpole tadpole) {
            if (tadpole.level().isClientSide) return;

            // Inject grazing AI goal for tadpoles
            tadpole.goalSelector.addGoal(4, new GrazeKelpGoal(tadpole, 8));
        }
    }

    public static void applyGeneticsToFrog(Frog frog, FrogGenome genome) {
        // Apply mutations
        for (String mutationId : genome.mutations()) {
            FrogMutation mutation = FrogMutation.getById(mutationId);
            if (mutation != null) {
                mutation.applyToFrog(frog);
            }
        }

        // Apply scale based on genome (visual scaling handled by FrogScaleLayer)
        float scale = genome.getScale();
        frog.setData(AmphibiaAttachments.FROG_SCALE, scale);

        // Apply health and damage bonuses based on genome
        FrogGradeCalculator.Grade healthGrade = FrogGradeCalculator.calculateGrade(genome.getGene(Gene.HEALTH));
        FrogGradeCalculator.Grade damageGrade = FrogGradeCalculator.calculateGrade(genome.getGene(Gene.DAMAGE));

        double healthBonus = FrogGradeCalculator.getHealthBonus(healthGrade);
        double damageBonus = FrogGradeCalculator.getDamageBonus(damageGrade);

        try {
            var healthAttribute = frog.getAttribute(Attributes.MAX_HEALTH);
            if (healthAttribute != null) {
                double baseHealth = 10;
                double targetHealth = baseHealth + healthBonus;
                healthAttribute.setBaseValue(targetHealth);
                frog.setHealth((float) targetHealth);
            }
        } catch (Exception e) {
            // Health attribute not available, silently continue
        }

        try {
            var attackDamageAttribute = frog.getAttribute(Attributes.ATTACK_DAMAGE);
            if (attackDamageAttribute != null) {
                double baseAttackDamage = 1.0;
                double targetDamage = baseAttackDamage + damageBonus;
                attackDamageAttribute.setBaseValue(targetDamage);
            }
        } catch (Exception e) {
            // Attack damage attribute not available, silently continue
        }

        // Apply combat goals based on genetics
        FrogAttackType attackType = FrogCombatCapability.getAttackType(genome);
        if (attackType != FrogAttackType.NONE) {
            // Add targeting goal
            frog.targetSelector.addGoal(2, new FrogHostileTargetGoal(frog));

            // Add attack goals based on type
            if (attackType == FrogAttackType.BITE || attackType == FrogAttackType.BOTH) {
                frog.goalSelector.addGoal(3, new FrogBiteGoal(frog, damageBonus));
            }
            if (attackType == FrogAttackType.TONGUE || attackType == FrogAttackType.BOTH) {
                frog.goalSelector.addGoal(4, new FrogTongueGoal(frog, damageBonus));
            }
        }
    }
}
