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

    private FrogState(Frog frog, FrogGenome genome, FrogGenome offspringGenome, boolean hasEgg,
                      boolean inLove, boolean inWater, float scale, int age, boolean estivating,
                      String eggLayingStatus) {
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
    }

    public static FrogState fromFrog(Frog frog) {
        FrogGenome genome = frog.getData(AmphibiaAttachments.FROG_GENOME);
        FrogGenome offspringGenome = frog.getData(AmphibiaAttachments.OFFSPRING_GENOME);
        boolean estivating = frog.getPersistentData().getBoolean("zen_amphibia:estivating");
        String eggLayingStatus = FrogBreedingHelper.getEggLayingStatus(frog);
        boolean hasEgg = offspringGenome != null && !offspringGenome.equals(FrogGenome.createDefault());

        return new FrogState(
            frog,
            genome,
            offspringGenome,
            hasEgg,
            frog.isInLove(),
            frog.isInWater(),
            frog.getData(AmphibiaAttachments.FROG_SCALE),
            frog.getAge(),
            estivating,
            eggLayingStatus
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
