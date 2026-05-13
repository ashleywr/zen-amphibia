package com.sanhiruzu.amphibia.mixin;

import com.sanhiruzu.amphibia.genetics.FrogDNA;
import com.sanhiruzu.amphibia.genetics.FrogGeneRegistry;
import com.sanhiruzu.amphibia.genetics.FrogGradeCalculator;
import com.sanhiruzu.amphibia.genetics.FrogMutation;
import com.sanhiruzu.amphibia.infrastructure.FrogDNADisplayHelper;
import com.sanhiruzu.amphibia.register.AmphibiaAttachments;
import com.sanhiruzu.amphibia.register.AmphibiaBlocks;
import com.sanhiruzu.zonectrl.zone.AtmosphereManager;
import com.sanhiruzu.zonectrl.zone.FactoryZone;
import com.sanhiruzu.zonectrl.zone.FactoryZoneManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(value = Frog.class, remap = false)
public abstract class FrogMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void amphibia$checkEstivation(CallbackInfo ci) {
        Frog frog = (Frog) (Object) this;
        Level level = frog.level();
        if (level == null || level.isClientSide) return;

        // Initialize DNA only once on first tick
        boolean geneticsApplied = frog.getData(AmphibiaAttachments.FROG_GENETICS_APPLIED);
        if (!geneticsApplied) {
            FrogDNA dna = frog.getData(AmphibiaAttachments.FROG_DNA);
            if (dna == null) {
                dna = FrogDNA.createDefault();
                frog.setData(AmphibiaAttachments.FROG_DNA, dna);
            }
            applyGeneticsToFrog(frog, dna);
            frog.setData(AmphibiaAttachments.FROG_GENETICS_APPLIED, true);
        }

        // Check breeding conditions every 40 ticks
        if (level.getGameTime() % 40 != 0) return;

        FactoryZoneManager manager = FactoryZoneManager.get(level);
        if (manager == null) return;

        BlockPos pos = frog.blockPosition();
        com.sanhiruzu.zonectrl.api.IAtmosphere atmosphereZone = manager.getAt(pos);
        if (atmosphereZone == null) return;

        float temp = atmosphereZone.getTemperature();
        float hum = atmosphereZone.getHumidity();
        String atmosphere = AtmosphereManager.determineAtmosphere(atmosphereZone);

        if (atmosphere.equals(com.sanhiruzu.amphibia.AmphibiaConfig.OPTIMAL_BREEDING_ATMOSPHERE.get())) {
            if (frog.getAge() == 0 && !frog.isInLove()) {
                boolean hasTannins = false;
                BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
                for (int x = -3; x <= 3; x++) {
                    for (int y = -1; y <= 1; y++) {
                        for (int z = -3; z <= 3; z++) {
                            mutable.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                            BlockState s = level.getBlockState(mutable);
                            if (s.is(net.minecraft.tags.BlockTags.MANGROVE_LOGS_CAN_GROW_THROUGH) || s.is(net.minecraft.world.level.block.Blocks.MUDDY_MANGROVE_ROOTS)) {
                                hasTannins = true;
                                break;
                            }
                        }
                    }
                }

                if (hasTannins) {
                    frog.setInLove(null);
                }
            }
        } else if (temp > 35.0f || hum < 20.0f) {
            if (level.getBlockState(pos).isAir()) {
                level.setBlockAndUpdate(pos, AmphibiaBlocks.MUCUS_COCOON.get().defaultBlockState());
                frog.discard();
            }
        }
    }

    private void applyGeneticsToFrog(Frog frog, FrogDNA dna) {
        // Apply mutations
        for (String mutationId : dna.mutations()) {
            FrogMutation mutation = FrogMutation.getById(mutationId);
            if (mutation != null) {
                mutation.applyToFrog(frog);
            }
        }

        // Apply visual scale based on DNA and scale collision box
        float scale = FrogDNADisplayHelper.getScaleFromDNA(dna);
        frog.setData(AmphibiaAttachments.FROG_SCALE, scale);

        // Scale the entity's eye height to match visual scale
        try {
            frog.refreshDimensions();
        } catch (Exception e) {
            // Ignore if refreshDimensions isn't available
        }

        // Apply health and damage bonuses based on DNA
        FrogGradeCalculator.Grade healthGrade = FrogGradeCalculator.calculateGrade(dna.getGene(FrogGeneRegistry.HEALTH));
        FrogGradeCalculator.Grade damageGrade = FrogGradeCalculator.calculateGrade(dna.getGene(FrogGeneRegistry.DAMAGE));

        double healthBonus = FrogGradeCalculator.getHealthBonus(healthGrade);
        double damageBonus = FrogGradeCalculator.getDamageBonus(damageGrade);

        try {
            var healthAttribute = frog.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH);
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
            var attackDamageAttribute = frog.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE);
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
