package com.sanhiruzu.amphibia.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FrogspawnBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DormantFrogspawnBlock extends FrogspawnBlock implements EntityBlock {
    private static final VoxelShape OUTLINE_SHAPE = box(0.0, 0.0, 0.0, 16.0, 14.0, 16.0);

    public DormantFrogspawnBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DormantFrogspawnBlockEntity(pos, state);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return OUTLINE_SHAPE;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack handStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (handStack.is(Items.GLASS_BOTTLE)) {
            if (level.isClientSide) return ItemInteractionResult.SUCCESS;

            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof DormantFrogspawnBlockEntity dormantBE) {
                ItemStack bottledItem = new ItemStack(com.sanhiruzu.amphibia.register.AmphibiaItems.BOTTLED_FROGSPAWN.get());
                bottledItem.set(com.sanhiruzu.amphibia.register.AmphibiaDataComponents.FROG_DNA.get(), dormantBE.getGenome());

                level.removeBlock(pos, false);

                if (handStack.getCount() == 1) {
                    player.setItemInHand(hand, bottledItem);
                } else {
                    handStack.shrink(1);
                    if (!player.getInventory().add(bottledItem)) {
                        player.drop(bottledItem, false);
                    }
                }

                return ItemInteractionResult.SUCCESS;
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return InteractionResult.PASS;
    }
}
