package com.sanhiruzu.amphibia.event;

import com.sanhiruzu.amphibia.genetics.AmphibiaFrog;
import com.sanhiruzu.amphibia.entity.goal.GrazeKelpGoal;
import com.sanhiruzu.amphibia.entity.goal.FrogBiteGoal;
import com.sanhiruzu.amphibia.entity.goal.FrogTongueGoal;
import com.sanhiruzu.amphibia.entity.goal.FrogHostileTargetGoal;
import com.sanhiruzu.amphibia.profession.WardenRoleHelper;
import com.sanhiruzu.amphibia.genetics.FrogGenome;
import com.sanhiruzu.amphibia.genetics.Gene;
import javax.annotation.Nullable;
import com.sanhiruzu.amphibia.genetics.FrogGradeCalculator;
import com.sanhiruzu.amphibia.genetics.FrogMutation;
import com.sanhiruzu.amphibia.genetics.FrogCombatCapability;
import com.sanhiruzu.amphibia.genetics.FrogAttackType;
import com.sanhiruzu.amphibia.register.AmphibiaAttachments;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.MobSpawnType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;

@EventBusSubscriber(modid = "zen_amphibia")
public class FrogSpawnHandler {

    public static final ThreadLocal<FrogGenome> PENDING_GENOME = new ThreadLocal<>();
    public static final ThreadLocal<Long> PENDING_BIRTH_TICK = new ThreadLocal<>();

    @SubscribeEvent
    public static void onFrogFinalizeSpawn(FinalizeSpawnEvent event) {
        if (!(event.getEntity() instanceof Frog frog)) return;
        if (event.getSpawnType() == MobSpawnType.BUCKET) return;
        if (PENDING_GENOME.get() != null) return;

        frog.setData(AmphibiaAttachments.FROG_GENOME, FrogGenome.createRandom(event.getLevel().getRandom()));
        frog.setData(AmphibiaAttachments.FROG_GENETICS_APPLIED, false);
    }

    @SubscribeEvent
    public static void onFrogJoinLevel(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Frog frog) {
            if (frog.level().isClientSide) return;

            // Tadpole→frog conversion: genome and birth tick captured in TadpoleMixin
            FrogGenome pendingGenome = PENDING_GENOME.get();
            Long pendingBirth = PENDING_BIRTH_TICK.get();
            if (pendingGenome != null) {
                PENDING_GENOME.remove();
                PENDING_BIRTH_TICK.remove();
                frog.setData(AmphibiaAttachments.FROG_GENOME, pendingGenome);
                frog.setData(AmphibiaAttachments.BIRTH_GAME_TIME, pendingBirth != null ? pendingBirth : 0L);
                frog.setData(AmphibiaAttachments.FROG_GENETICS_APPLIED, false);
            }

            // Get genome; naturally finalized frogs are randomized before joining.
            FrogGenome genome = frog.getData(AmphibiaAttachments.FROG_GENOME);

            // Check if genetics have already been applied (e.g., from NBT on load)
            boolean geneticsApplied = frog.getData(AmphibiaAttachments.FROG_GENETICS_APPLIED);
            if (geneticsApplied) {
                restoreFrogBehavior(frog, genome);
            } else {
                // Apply genetics immediately, before first render
                applyGeneticsToFrog(frog, genome);
                frog.setData(AmphibiaAttachments.FROG_GENETICS_APPLIED, true);
            }
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

        // Apply combat stats based on aptitude genes.
        FrogGradeCalculator.Grade healthGrade = FrogGradeCalculator.calculateGrade(genome.getGene(Gene.HARDINESS));
        FrogGradeCalculator.Grade damageGrade = FrogGradeCalculator.calculateGrade(genome.getGene(Gene.POWER));

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
        ensureCombatGoals(frog, genome, damageBonus);
        WardenRoleHelper.applyAnchorRestriction(frog);
    }

    private static void restoreFrogBehavior(Frog frog, FrogGenome genome) {
        for (String mutationId : genome.mutations()) {
            FrogMutation mutation = FrogMutation.getById(mutationId);
            if (mutation != null) {
                mutation.applyToFrog(frog);
            }
        }

        double damageBonus = FrogCombatCapability.getDamageBonus(genome);
        ensureCombatGoals(frog, genome, damageBonus);
        WardenRoleHelper.applyAnchorRestriction(frog);
    }

    private static void ensureCombatGoals(Frog frog, FrogGenome genome, double damageBonus) {
        FrogAttackType attackType = FrogCombatCapability.getAttackType(genome);
        if (attackType != FrogAttackType.NONE) {
            if (!WardenRoleHelper.hasGoal(frog.targetSelector, FrogHostileTargetGoal.class)) {
                frog.targetSelector.addGoal(2, new FrogHostileTargetGoal(frog));
            }

            if (!WardenRoleHelper.hasGoal(frog.goalSelector, MoveTowardsRestrictionGoal.class)) {
                frog.goalSelector.addGoal(2, new MoveTowardsRestrictionGoal(frog, 1.0));
            }

            // Add attack goals based on type
            if ((attackType == FrogAttackType.BITE || attackType == FrogAttackType.BOTH)
                && !WardenRoleHelper.hasGoal(frog.goalSelector, FrogBiteGoal.class)) {
                frog.goalSelector.addGoal(3, new FrogBiteGoal(frog, damageBonus));
            }
            if ((attackType == FrogAttackType.TONGUE || attackType == FrogAttackType.BOTH)
                && !WardenRoleHelper.hasGoal(frog.goalSelector, FrogTongueGoal.class)) {
                frog.goalSelector.addGoal(4, new FrogTongueGoal(frog, damageBonus));
            }
        }
    }
}
