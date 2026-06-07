package com.sanhiruzu.amphibia.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.sanhiruzu.amphibia.compat.create.FrogportCompat;
import com.simibubi.create.foundation.events.InputEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.InputEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(InputEvents.class)
public abstract class InputEventsMixin {
    @ModifyExpressionValue(
        method = "onClickInput",
        at = @At(value = "INVOKE", target = "Lcom/tterrag/registrate/util/entry/BlockEntry;isIn(Lnet/minecraft/world/item/ItemStack;)Z")
    )
    private static boolean amphibia$workerFrogportSkipsPackageFallback(boolean original, InputEvent.InteractionKeyMappingTriggered event) {
        Minecraft minecraft = Minecraft.getInstance();
        ItemStack itemInHand = minecraft.player == null ? ItemStack.EMPTY : minecraft.player.getItemInHand(event.getHand());
        return original || FrogportCompat.isWorkerFrogport(itemInHand);
    }
}
