package com.sanhiruzu.amphibia.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.sanhiruzu.amphibia.compat.create.FrogportCompat;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorInteractionHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChainConveyorInteractionHandler.class)
public abstract class ChainConveyorInteractionHandlerMixin {
    @ModifyExpressionValue(
        method = "isActive",
        at = @At(value = "INVOKE", target = "Lcom/tterrag/registrate/util/entry/BlockEntry;isIn(Lnet/minecraft/world/item/ItemStack;)Z")
    )
    private static boolean amphibia$workerFrogportActivatesChainSelection(boolean original) {
        return original || FrogportCompat.isWorkerFrogport(mainHandItem());
    }

    @ModifyExpressionValue(
        method = "onUse",
        at = @At(value = "INVOKE", target = "Lcom/tterrag/registrate/util/entry/BlockEntry;isIn(Lnet/minecraft/world/item/ItemStack;)Z")
    )
    private static boolean amphibia$workerFrogportTargetsChain(boolean original) {
        return original || FrogportCompat.isWorkerFrogport(mainHandItem());
    }

    private static ItemStack mainHandItem() {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.player == null ? ItemStack.EMPTY : minecraft.player.getMainHandItem();
    }
}
