package com.sanhiruzu.amphibia.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FrogspawnBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class DormantFrogspawnBlock extends FrogspawnBlock implements EntityBlock {
    public DormantFrogspawnBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DormantFrogspawnBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        ItemStack handStack = player.getItemInHand(InteractionHand.MAIN_HAND);

        // Check if player is holding glass bottle
        if (handStack.is(Items.GLASS_BOTTLE)) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof DormantFrogspawnBlockEntity dormantBE) {
                // Create bottled frogspawn item with genome
                ItemStack bottledItem = new ItemStack(com.sanhiruzu.amphibia.register.AmphibiaItems.BOTTLED_FROGSPAWN.get());
                bottledItem.set(com.sanhiruzu.amphibia.register.AmphibiaDataComponents.FROG_DNA.get(), dormantBE.getGenome());

                // Remove block
                level.removeBlock(pos, false);

                // Consume bottle
                if (handStack.getCount() == 1) {
                    player.setItemInHand(InteractionHand.MAIN_HAND, bottledItem);
                } else {
                    handStack.shrink(1);
                    if (!player.getInventory().add(bottledItem)) {
                        player.drop(bottledItem, false);
                    }
                }

                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }
}
