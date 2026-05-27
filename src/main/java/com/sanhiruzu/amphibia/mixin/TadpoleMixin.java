package com.sanhiruzu.amphibia.mixin;

import com.sanhiruzu.amphibia.event.FrogSpawnHandler;
import com.sanhiruzu.amphibia.genetics.FrogGenome;
import com.sanhiruzu.amphibia.register.AmphibiaAttachments;
import net.minecraft.world.entity.animal.frog.Tadpole;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Tadpole.class)
public abstract class TadpoleMixin {
    @Shadow
    private int age;

    @Inject(method = "ageUp()V", at = @At("HEAD"))
    private void amphibia$captureBeforeConversion(CallbackInfo ci) {
        Tadpole tadpole = (Tadpole) (Object) this;
        if (tadpole.level().isClientSide) return;
        FrogGenome genome = tadpole.getData(AmphibiaAttachments.FROG_GENOME);
        FrogSpawnHandler.PENDING_GENOME.set(genome);
        FrogSpawnHandler.PENDING_BIRTH_TICK.set(tadpole.level().getGameTime());
    }

    @Inject(method = "aiStep", at = @At("TAIL"))
    private void amphibia$applyGrowthPressure(CallbackInfo ci) {
        Tadpole tadpole = (Tadpole) (Object) this;
        if (tadpole.level().isClientSide) return;

        if (tadpole.getData(AmphibiaAttachments.STUNTED_GROWTH) && tadpole.tickCount % 2 == 0) {
            this.age = Math.max(0, this.age - 1);
        } else if (tadpole.getData(AmphibiaAttachments.ACCELERATED_GROWTH)) {
            this.age++;
        }
    }
}
