package com.sanhiruzu.amphibia.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.sanhiruzu.amphibia.compat.create.FrogportCompat;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChainConveyorBlock.class)
public abstract class ChainConveyorBlockMixin {
    @ModifyExpressionValue(
        method = "useItemOn",
        at = @At(value = "INVOKE", target = "Lcom/tterrag/registrate/util/entry/BlockEntry;isIn(Lnet/minecraft/world/item/ItemStack;)Z")
    )
    private boolean amphibia$workerFrogportInteractsWithChains(boolean original, ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return original || FrogportCompat.isWorkerFrogport(stack);
    }
}
