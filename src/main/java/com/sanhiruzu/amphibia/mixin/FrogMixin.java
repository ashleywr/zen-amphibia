package com.sanhiruzu.amphibia.mixin;

import com.sanhiruzu.amphibia.genetics.FrogGenetics;
import com.sanhiruzu.amphibia.genetics.FrogGenome;
import com.sanhiruzu.amphibia.register.AmphibiaAttachments;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.frog.Frog;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Frog.class)
public abstract class FrogMixin {

    @Inject(method = "spawnChildFromBreeding", at = @At("HEAD"))
    private void amphibia$mixGenomesOnBreed(ServerLevel level, Animal partner, CallbackInfo ci) {
        if (!(partner instanceof Frog partnerFrog)) return;
        Frog self = (Frog) (Object) this;
        FrogGenome selfGenome = self.getData(AmphibiaAttachments.FROG_GENOME);
        FrogGenome partnerGenome = partnerFrog.getData(AmphibiaAttachments.FROG_GENOME);
        float avgHappiness = (self.getData(AmphibiaAttachments.FROG_HAPPINESS) + partnerFrog.getData(AmphibiaAttachments.FROG_HAPPINESS)) / 2f;
        FrogGenome childGenome = FrogGenetics.breed(selfGenome, partnerGenome, self.getRandom(), avgHappiness);
        self.setData(AmphibiaAttachments.OFFSPRING_GENOME, childGenome);
        partnerFrog.setData(AmphibiaAttachments.OFFSPRING_GENOME, childGenome);
    }
}
