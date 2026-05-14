package com.sanhiruzu.amphibia.genetics;

import com.sanhiruzu.amphibia.register.AmphibiaAttachments;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.animal.frog.Frog;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = "amphibia")
public class CreeperFrogHandler {

	private static final float BASE_EXPLOSION_POWER = 1.0f;

	@SubscribeEvent
	public static void onFrogTick(EntityTickEvent.Post event) {
		if (!(event.getEntity() instanceof Frog frog)) return;
		FrogGenome genome = validateCreeperFrog(frog);
		if (genome == null) return;

		FrogGradeCalculator.Grade damageGrade = FrogGradeCalculator.calculateGrade(genome.getGene(Gene.DAMAGE));
		float healthPercent = frog.getHealth() / frog.getMaxHealth();

		// Low tiers (D/C): explode when taking damage (handled in hurt event)
		// Mid tiers (B/A): semi-controlled, trigger on very low health
		// High tiers (S): fully controlled (player interaction only)

		if (damageGrade == FrogGradeCalculator.Grade.B || damageGrade == FrogGradeCalculator.Grade.A) {
			if (healthPercent < 0.25f) {
				triggerExplosion(frog, genome, damageGrade);
			}
		}
	}

	@SubscribeEvent
	public static void onFrogDamage(LivingDamageEvent.Post event) {
		if (!(event.getEntity() instanceof Frog frog)) return;
		FrogGenome genome = validateCreeperFrog(frog);
		if (genome == null) return;

		FrogGradeCalculator.Grade damageGrade = FrogGradeCalculator.calculateGrade(genome.getGene(Gene.DAMAGE));

		// Low tiers (D/C): explode randomly when taking damage
		if (damageGrade == FrogGradeCalculator.Grade.D || damageGrade == FrogGradeCalculator.Grade.C) {
			if (frog.level().random.nextFloat() < 0.3f) {
				triggerExplosion(frog, genome, damageGrade);
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerInteractEntity(PlayerInteractEvent.EntityInteract event) {
		if (!(event.getTarget() instanceof Frog frog)) return;
		if (frog.level().isClientSide) return;
		if (!event.getEntity().isShiftKeyDown()) return;
		if (!FrogMutation.hasCreeperMutation(frog)) return;

		FrogGenome genome = frog.getData(AmphibiaAttachments.FROG_GENOME);
		if (genome == null) return;

		FrogGradeCalculator.Grade damageGrade = FrogGradeCalculator.calculateGrade(genome.getGene(Gene.DAMAGE));

		// High tiers (B/A/S): player can trigger explosion with sneak + interact
		if (damageGrade == FrogGradeCalculator.Grade.B || damageGrade == FrogGradeCalculator.Grade.A || damageGrade == FrogGradeCalculator.Grade.S) {
			triggerExplosion(frog, genome, damageGrade);
			event.setCancellationResult(net.minecraft.world.InteractionResult.SUCCESS);
		}
	}

	private static void triggerExplosion(Frog frog, FrogGenome genome, FrogGradeCalculator.Grade damageGrade) {
		float explosionPower = getExplosionPower(damageGrade);

		frog.playSound(SoundEvents.CREEPER_PRIMED, 1.0f, 1.0f);
		frog.level().explode(frog, frog.getX(), frog.getY(), frog.getZ(), explosionPower, false, null);

		// S-tier survives the blast; lower tiers take damage or die
		if (damageGrade != FrogGradeCalculator.Grade.S) {
			frog.hurt(frog.damageSources().generic(), explosionPower * 2.0f);
		}
	}

	private static float getExplosionPower(FrogGradeCalculator.Grade grade) {
		return switch (grade) {
			case D -> BASE_EXPLOSION_POWER;
			case C -> 1.5f;
			case B -> 2.0f;
			case A -> 2.5f;
			case S -> 3.0f;
		};
	}

	private static FrogGenome validateCreeperFrog(Frog frog) {
		if (frog.level().isClientSide) return null;
		if (!FrogMutation.hasCreeperMutation(frog)) return null;
		return frog.getData(AmphibiaAttachments.FROG_GENOME);
	}
}
