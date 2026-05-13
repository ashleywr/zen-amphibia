package com.sanhiruzu.amphibia.mixin;

import com.sanhiruzu.amphibia.register.AmphibiaAttachments;
import net.minecraft.world.entity.animal.frog.Frog;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Frog.class, remap = false)
public abstract class FrogEyeHeightMixin {

    @Inject(method = "getEyeHeight", at = @At("RETURN"), cancellable = true)
    private void amphibia$scaleEyeHeight(CallbackInfoReturnable<Float> cir) {
        Frog frog = (Frog) (Object) this;
        Float scale = frog.getData(AmphibiaAttachments.FROG_SCALE);
        if (scale != null && Math.abs(scale - 1.0f) > 0.01f) {
            cir.setReturnValue(cir.getReturnValue() * scale);
        }
    }
}
