package com.sanhiruzu.amphibia.block;

import com.mojang.serialization.MapCodec;
import com.sanhiruzu.amphibia.register.AmphibiaBlockEntities;
import com.sanhiruzu.amphibia.register.AmphibiaDataComponents;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class FrogChestBlock extends ChestBlock {
    public static final MapCodec<FrogChestBlock> CODEC = simpleCodec(FrogChestBlock::new);

    public FrogChestBlock(BlockBehaviour.Properties properties) {
        super(properties, () -> AmphibiaBlockEntities.FROG_CHEST.get());
    }

    @Override
    public MapCodec<? extends FrogChestBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockState getStateForPlacement(net.minecraft.world.item.context.BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        return state == null ? null : state.setValue(TYPE, ChestType.SINGLE);
    }

    @Override
    public DoubleBlockCombiner.NeighborCombineResult<? extends ChestBlockEntity> combine(
            BlockState state,
            Level level,
            BlockPos pos,
            boolean override
    ) {
        if (!override && ChestBlock.isChestBlockedAt(level, pos)) {
            return DoubleBlockCombiner.Combiner::acceptNone;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof FrogChestBlockEntity frogChest) {
            return new DoubleBlockCombiner.NeighborCombineResult.Single<>(frogChest);
        }
        return DoubleBlockCombiner.Combiner::acceptNone;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FrogChestBlockEntity(pos, state);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        List<ItemStack> drops = super.getDrops(state, params);
        BlockEntity blockEntity = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockEntity instanceof FrogChestBlockEntity frogChest) {
            for (ItemStack drop : drops) {
                if (drop.is(this.asItem())) {
                    drop.set(AmphibiaDataComponents.FROG_DNA.get(), frogChest.getGenome());
                }
            }
        }
        return drops;
    }
}
