package com.sanhiruzu.amphibia.event;

import com.sanhiruzu.amphibia.genetics.FrogDNA;
import com.sanhiruzu.amphibia.genetics.FrogGeneRegistry;
import com.sanhiruzu.amphibia.genetics.FrogGradeCalculator;
import com.sanhiruzu.amphibia.genetics.FrogMutation;
import com.sanhiruzu.amphibia.infrastructure.FrogDNADisplayHelper;
import com.sanhiruzu.amphibia.register.AmphibiaAttachments;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@EventBusSubscriber(modid = "amphibia")
public class FrogSpawnHandler {

    @SubscribeEvent
    public static void onFrogJoinLevel(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof Frog frog)) return;
        if (frog.level().isClientSide) return;

        // Check if genetics have already been applied (e.g., from NBT on load)
        boolean geneticsApplied = frog.getData(AmphibiaAttachments.FROG_GENETICS_APPLIED);
        if (geneticsApplied) return;

        // Get or create DNA
        FrogDNA dna = frog.getData(AmphibiaAttachments.FROG_DNA);
        if (dna == null) {
            dna = FrogDNA.createDefault();
            frog.setData(AmphibiaAttachments.FROG_DNA, dna);
        }

        // Apply genetics immediately, before first render
        applyGeneticsToFrog(frog, dna);
        frog.setData(AmphibiaAttachments.FROG_GENETICS_APPLIED, true);
    }

    private static void applyGeneticsToFrog(Frog frog, FrogDNA dna) {
        // Apply mutations
        for (String mutationId : dna.mutations()) {
            FrogMutation mutation = FrogMutation.getById(mutationId);
            if (mutation != null) {
                mutation.applyToFrog(frog);
            }
        }

        // Apply scale based on DNA
        float scale = FrogDNADisplayHelper.getScaleFromDNA(dna);
        frog.setData(AmphibiaAttachments.FROG_SCALE, scale);

        var scaleAttribute = frog.getAttribute(Attributes.SCALE);
        if (scaleAttribute != null) {
            scaleAttribute.setBaseValue(scale);
            frog.refreshDimensions();
        }

        // Apply health and damage bonuses based on DNA
        FrogGradeCalculator.Grade healthGrade = FrogGradeCalculator.calculateGrade(dna.getGene(FrogGeneRegistry.HEALTH));
        FrogGradeCalculator.Grade damageGrade = FrogGradeCalculator.calculateGrade(dna.getGene(FrogGeneRegistry.DAMAGE));

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
    }
}
