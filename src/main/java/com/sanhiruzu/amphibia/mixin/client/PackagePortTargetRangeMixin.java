package com.sanhiruzu.amphibia.mixin.client;

import com.sanhiruzu.amphibia.genetics.FrogGenome;
import com.sanhiruzu.amphibia.genetics.FrogportGeneEvaluator;
import com.sanhiruzu.amphibia.register.AmphibiaDataComponents;
import com.sanhiruzu.amphibia.register.AmphibiaItems;
import com.simibubi.create.content.logistics.packagePort.PackagePortTargetSelectionHandler;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PackagePortTargetSelectionHandler.class)
public abstract class PackagePortTargetRangeMixin {
    @Inject(method = "validateDiff", at = @At("HEAD"), cancellable = true)
    private static void amphibia$validateWorkerFrogportReach(Vec3 target, BlockPos placedPos, CallbackInfoReturnable<String> cir) {
        if (PackagePortTargetSelectionHandler.isPostbox) return;

        Vec3 source = Vec3.atBottomCenterOf(placedPos);
        Vec3 diff = target.subtract(source);
        if (diff.y < 0) return;

        ItemStack stack = heldWorkerFrogport();
        FrogGenome genome = stack.get(AmphibiaDataComponents.FROG_DNA.get());
        if (genome == null) return;

        int baseRange = AllConfigs.server().logistics.packagePortRange.get();
        if (diff.length() <= FrogportGeneEvaluator.getEffectivePackageRange(genome, baseRange)) {
            cir.setReturnValue(null);
        }
    }

    private static ItemStack heldWorkerFrogport() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return ItemStack.EMPTY;
        ItemStack mainHand = minecraft.player.getMainHandItem();
        if (mainHand.is(AmphibiaItems.WORKER_FROGPORT.get())) return mainHand;
        ItemStack offHand = minecraft.player.getOffhandItem();
        return offHand.is(AmphibiaItems.WORKER_FROGPORT.get()) ? offHand : ItemStack.EMPTY;
    }
}
