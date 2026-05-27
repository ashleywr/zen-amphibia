package com.sanhiruzu.amphibia.block;

import com.sanhiruzu.amphibia.register.AmphibiaBlockEntities;
import com.sanhiruzu.amphibia.genetics.FrogGenome;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DormantFrogspawnBlockEntity extends BlockEntity {
    private FrogGenome genome = FrogGenome.createDefault();
    private long spawnedAt = 0;

    public DormantFrogspawnBlockEntity(BlockPos pos, BlockState state) {
        super(AmphibiaBlockEntities.DORMANT_FROGSPAWN.get(), pos, state);
    }

    @Override
    public void setLevel(net.minecraft.world.level.Level level) {
        super.setLevel(level);
        if (spawnedAt == 0) {
            spawnedAt = level.getGameTime();
        }
    }

    public long getSpawnedAt() {
        return spawnedAt;
    }

    public void setGenome(FrogGenome genome) {
        this.genome = genome;
        setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    public FrogGenome getGenome() {
        return genome;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("FrogGenome", FrogGenome.CODEC.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE, genome).getOrThrow());
        tag.putLong("SpawnedAt", spawnedAt);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("FrogGenome")) {
            genome = FrogGenome.CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE, tag.get("FrogGenome")).getOrThrow();
        }
        if (tag.contains("SpawnedAt")) {
            spawnedAt = tag.getLong("SpawnedAt");
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveCustomOnly(registries);
    }
}
