package com.sanhiruzu.amphibia.genetics;

import com.sanhiruzu.amphibia.register.AmphibiaAttachments;
import net.minecraft.world.entity.animal.frog.Frog;

public class FrogBreedingHelper {
    public static String getEggLayingStatus(Frog frog) {
        FrogGenome offspringGenome = frog.getData(AmphibiaAttachments.OFFSPRING_GENOME);
        if (offspringGenome == null || offspringGenome.equals(FrogGenome.createDefault())) {
            return null;
        }

        if (frog.isInWater()) {
            return "Laying eggs, water found";
        } else {
            return "Laying eggs, searching for water...";
        }
    }
}
