package com.sanhiruzu.amphibia.genetics;

import com.sanhiruzu.amphibia.register.AmphibiaAttachments;
import net.minecraft.world.entity.animal.frog.Frog;

public class FrogState {
    public final Frog entity;
    public final FrogGenome genome;
    public final FrogGenome offspringGenome;
    public final boolean hasEgg;
    public final boolean inLove;
    public final boolean inWater;
    public final float scale;
    public final int age;
    public final boolean estivating;
    public final String eggLayingStatus;
    public final String aiStatus;
    public final float maturityProgress;

    private FrogState(Frog frog, FrogGenome genome, FrogGenome offspringGenome, boolean hasEgg,
                      boolean inLove, boolean inWater, float scale, int age, boolean estivating,
                      String eggLayingStatus, String aiStatus, float maturityProgress) {
        this.entity = frog;
        this.genome = genome;
        this.offspringGenome = offspringGenome;
        this.hasEgg = hasEgg;
        this.inLove = inLove;
        this.inWater = inWater;
        this.scale = scale;
        this.age = age;
        this.estivating = estivating;
        this.eggLayingStatus = eggLayingStatus;
        this.aiStatus = aiStatus;
        this.maturityProgress = maturityProgress;
    }

    public static FrogState fromFrog(Frog frog) {
        AmphibiaFrog af = AmphibiaFrog.of(frog);
        FrogGenome genome = af.getGenome();
        FrogGenome offspringGenome = af.getOffspringGenome();
        boolean estivating = af.isEstivating();
        String eggLayingStatus = FrogBreedingHelper.getEggLayingStatus(frog);
        boolean hasEgg = af.hasOffspringGenome();

        long birthTick = frog.getData(AmphibiaAttachments.BIRTH_GAME_TIME);
        float maturityProgress = birthTick == 0L ? 1.0f
            : Math.min(1.0f, (float)(frog.level().getGameTime() - birthTick) / AmphibiaFrog.MATURATION_TICKS);

        return new FrogState(
            frog,
            genome,
            offspringGenome,
            hasEgg,
            frog.isInLove(),
            frog.isInWater(),
            af.getScale(),
            frog.getAge(),
            estivating,
            eggLayingStatus,
            frog.getData(AmphibiaAttachments.CURRENT_AI_STATUS),
            maturityProgress
        );
    }

    @Override
    public String toString() {
        return "FrogState{" +
            "entity=" + entity.getName().getString() +
            ", genome=" + genome +
            ", offspringGenome=" + offspringGenome +
            ", hasEgg=" + hasEgg +
            ", inLove=" + inLove +
            ", inWater=" + inWater +
            ", scale=" + scale +
            ", age=" + age +
            ", estivating=" + estivating +
            ", eggLayingStatus='" + eggLayingStatus + '\'' +
            '}';
    }
}
