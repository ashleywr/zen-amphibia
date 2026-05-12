package com.sanhiruzu.amphibia.block;

import com.sanhiruzu.amphibia.register.AmphibiaBlockEntities;
import com.sanhiruzu.amphibia.genetics.FrogDNA;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class GeneticFrogspawnBlockEntity extends BlockEntity {
    private FrogDNA dna = FrogDNA.createDefault();

    public GeneticFrogspawnBlockEntity(BlockPos pos, BlockState state) {
        super(AmphibiaBlockEntities.GENETIC_FROGSPAWN.get(), pos, state);
    }

    public void setDna(FrogDNA dna) {
        this.dna = dna;
        setChanged();
    }

    public FrogDNA getDna() {
        return dna;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("FrogDNA", FrogDNA.CODEC.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE, dna).getOrThrow());
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("FrogDNA")) {
            dna = FrogDNA.CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE, tag.get("FrogDNA")).getOrThrow();
        }
    }
}
