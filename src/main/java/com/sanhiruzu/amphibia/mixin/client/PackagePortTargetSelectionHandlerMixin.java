package com.sanhiruzu.amphibia.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.sanhiruzu.amphibia.compat.create.FrogportCompat;
import com.simibubi.create.content.logistics.packagePort.PackagePortTargetSelectionHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PackagePortTargetSelectionHandler.class)
public abstract class PackagePortTargetSelectionHandlerMixin {
    @ModifyExpressionValue(
        method = "tick",
        at = @At(value = "INVOKE", target = "Lcom/tterrag/registrate/util/entry/BlockEntry;isIn(Lnet/minecraft/world/item/ItemStack;)Z")
    )
    private static boolean amphibia$workerFrogportMaintainsTargetSelection(boolean original) {
        Minecraft minecraft = Minecraft.getInstance();
        ItemStack mainHandItem = minecraft.player == null ? ItemStack.EMPTY : minecraft.player.getMainHandItem();
        return original || FrogportCompat.isWorkerFrogport(mainHandItem);
    }
}
