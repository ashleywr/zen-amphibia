package com.sanhiruzu.amphibia.block;

import com.sanhiruzu.amphibia.register.AmphibiaBlockEntities;
import com.sanhiruzu.amphibia.genetics.FrogGenome;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class GeneticFrogspawnBlockEntity extends BlockEntity {
    private FrogGenome genome = FrogGenome.createDefault();

    public GeneticFrogspawnBlockEntity(BlockPos pos, BlockState state) {
        super(AmphibiaBlockEntities.GENETIC_FROGSPAWN.get(), pos, state);
    }

    public void setGenome(FrogGenome genome) {
        this.genome = genome;
        setChanged();
    }

    public FrogGenome getGenome() {
        return genome;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("FrogGenome", FrogGenome.CODEC.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE, genome).getOrThrow());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("FrogGenome")) {
            genome = FrogGenome.CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE, tag.get("FrogGenome")).getOrThrow();
        }
    }
}
