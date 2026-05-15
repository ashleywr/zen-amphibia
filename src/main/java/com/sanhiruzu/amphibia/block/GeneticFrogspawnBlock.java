package com.sanhiruzu.amphibia.block;

import com.sanhiruzu.amphibia.register.AmphibiaAttachments;
import com.sanhiruzu.amphibia.genetics.FrogGenome;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FrogspawnBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class GeneticFrogspawnBlock extends FrogspawnBlock implements EntityBlock {
    public GeneticFrogspawnBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GeneticFrogspawnBlockEntity(pos, state);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // We replicate vanilla hatching logic but inject our genome
        // Vanilla: destroy block and spawn 2-5 tadpoles

        FrogGenome genome = FrogGenome.createDefault();
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof GeneticFrogspawnBlockEntity gbe) {
            genome = gbe.getGenome();
        }

        level.removeBlock(pos, false);
        int i = random.nextInt(2, 6);

        for (int j = 1; j <= i; ++j) {
            Tadpole tadpole = EntityType.TADPOLE.create(level);
            if (tadpole != null) {
                tadpole.setData(AmphibiaAttachments.FROG_GENOME, genome);
                tadpole.setPersistenceRequired();
                double d0 = (double)pos.getX() + random.nextDouble();
                double d1 = (double)pos.getZ() + random.nextDouble();
                tadpole.moveTo(d0, pos.getY(), d1, random.nextFloat() * 360.0F, 0.0F);
                level.addFreshEntity(tadpole);
            }
        }
    }
}
