package com.sanhiruzu.amphibia.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.sanhiruzu.amphibia.duck.IFrogportDNA;
import com.sanhiruzu.amphibia.genetics.FrogGenome;
import com.sanhiruzu.amphibia.genetics.FrogportGeneEvaluator;
import com.simibubi.create.content.logistics.packagePort.PackagePortBlockEntity;
import com.simibubi.create.content.logistics.packagePort.PackagePortPlacementPacket;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PackagePortPlacementPacket.class)
public abstract class PackagePortPlacementPacketMixin {
    @ModifyExpressionValue(
        method = "handle",
        at = @At(value = "INVOKE", target = "Ljava/lang/Integer;intValue()I")
    )
    private int amphibia$allowWorkerFrogportReach(int original, ServerPlayer player, @Local PackagePortBlockEntity packagePort) {
        if (!(packagePort instanceof IFrogportDNA duck)) return original;

        FrogGenome genome = duck.amphibia$getGenome();
        return genome == null ? original : FrogportGeneEvaluator.getEffectivePackageRange(genome, original);
    }
}
