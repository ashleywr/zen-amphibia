package com.sanhiruzu.amphibia.genetics;

import com.sanhiruzu.amphibia.register.AmphibiaAttachments;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.animal.frog.Frog;

/**
 * Thin mutable wrapper around a vanilla Frog that exposes all mod-specific state
 * through a single, typed API. Obtain via {@link #of(Frog)}.
 * <p>
 * Server-side only — never push or construct on the client.
 */
public final class AmphibiaFrog {

    private final Frog frog;

    private AmphibiaFrog(Frog frog) {
        this.frog = frog;
    }

    public static AmphibiaFrog of(Frog frog) {
        return new AmphibiaFrog(frog);
    }

    public Frog entity() {
        return frog;
    }

    // ─── Genome ──────────────────────────────────────────────────────────────

    public FrogGenome getGenome() {
        return frog.getData(AmphibiaAttachments.FROG_GENOME);
    }

    public void setGenome(FrogGenome genome) {
        frog.setData(AmphibiaAttachments.FROG_GENOME, genome);
    }

    public boolean isGeneticsApplied() {
        return frog.getData(AmphibiaAttachments.FROG_GENETICS_APPLIED);
    }

    public void markGeneticsApplied() {
        frog.setData(AmphibiaAttachments.FROG_GENETICS_APPLIED, true);
    }

    // ─── Offspring / breeding ────────────────────────────────────────────────

    public FrogGenome getOffspringGenome() {
        return frog.getData(AmphibiaAttachments.OFFSPRING_GENOME);
    }

    public void setOffspringGenome(FrogGenome genome) {
        frog.setData(AmphibiaAttachments.OFFSPRING_GENOME, genome);
    }

    public boolean hasOffspringGenome() {
        FrogGenome og = getOffspringGenome();
        return og != null && !og.equals(FrogGenome.createDefault());
    }

    public void clearOffspringGenome() {
        frog.setData(AmphibiaAttachments.OFFSPRING_GENOME, FrogGenome.createDefault());
    }

    // ─── Scale ───────────────────────────────────────────────────────────────

    public float getScale() {
        return frog.getData(AmphibiaAttachments.FROG_SCALE);
    }

    public void setScale(float scale) {
        frog.setData(AmphibiaAttachments.FROG_SCALE, scale);
    }

    // ─── Happiness ───────────────────────────────────────────────────────────

    public float getHappiness() {
        return frog.getData(AmphibiaAttachments.FROG_HAPPINESS);
    }

    public void setHappiness(float happiness) {
        frog.setData(AmphibiaAttachments.FROG_HAPPINESS, happiness);
    }

    // ─── Growth flags ─────────────────────────────────────────────────────────

    public boolean isStuntedGrowth() {
        return frog.getData(AmphibiaAttachments.STUNTED_GROWTH);
    }

    public boolean isAcceleratedGrowth() {
        return frog.getData(AmphibiaAttachments.ACCELERATED_GROWTH);
    }

    // ─── Estivation ──────────────────────────────────────────────────────────

    public boolean isEstivating() {
        return frog.getData(AmphibiaAttachments.ESTIVATING);
    }

    public long getLastRevivalTick() {
        return frog.getData(AmphibiaAttachments.LAST_REVIVAL_TICK);
    }

    public void startEstivation() {
        frog.setData(AmphibiaAttachments.ESTIVATING, true);
        frog.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, -1, 3, false, false, false));
    }

    public void revive(long gameTime) {
        frog.setData(AmphibiaAttachments.ESTIVATING, false);
        frog.setData(AmphibiaAttachments.LAST_REVIVAL_TICK, gameTime);
        frog.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
    }

    public void refreshEstivationEffect() {
        frog.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, -1, 3, false, false, false));
    }

    // ─── Maturation ──────────────────────────────────────────────────────────

    public static final long MATURATION_TICKS = 48_000L;

    public long getBirthGameTime() {
        return frog.getData(AmphibiaAttachments.BIRTH_GAME_TIME);
    }

    public boolean isMature(long gameTime) {
        long birthTick = getBirthGameTime();
        return birthTick == 0L || (gameTime - birthTick) >= MATURATION_TICKS;
    }

    public float getMaturityProgress(long gameTime) {
        long birthTick = getBirthGameTime();
        if (birthTick == 0L) return 1.0f;
        return Math.min(1.0f, (float)(gameTime - birthTick) / MATURATION_TICKS);
    }
}
