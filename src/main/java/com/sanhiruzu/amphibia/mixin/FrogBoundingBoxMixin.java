package com.sanhiruzu.amphibia.mixin;

import com.sanhiruzu.amphibia.register.AmphibiaAttachments;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Frog.class, remap = false)
public abstract class FrogBoundingBoxMixin {

    @Inject(method = "getBoundingBox", at = @At("RETURN"), cancellable = true)
    private void amphibia$scaleBoundingBox(CallbackInfoReturnable<AABB> cir) {
        Frog frog = (Frog) (Object) this;
        Float scale = frog.getData(AmphibiaAttachments.FROG_SCALE);
        if (scale != null && Math.abs(scale - 1.0f) > 0.01f) {
            AABB box = cir.getReturnValue();
            double centerX = (box.minX + box.maxX) / 2;
            double centerZ = (box.minZ + box.maxZ) / 2;
            double width = (box.maxX - box.minX) * scale / 2;
            double height = (box.maxY - box.minY) * scale;
            double depth = (box.maxZ - box.minZ) * scale / 2;
            AABB scaledBox = new AABB(
                centerX - width, box.minY,
                centerZ - depth, centerX + width,
                box.minY + height, centerZ + depth
            );
            cir.setReturnValue(scaledBox);
        }
    }
}
