package com.sanhiruzu.amphibia.mixin;

import com.sanhiruzu.amphibia.duck.IFrogportDNA;
import com.sanhiruzu.amphibia.genetics.FrogGenome;
import com.sanhiruzu.amphibia.infrastructure.FrogDNADisplayHelper;
import com.simibubi.create.content.logistics.packagePort.frogport.FrogportBlockEntity;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import net.minecraft.network.chat.Component;

import java.util.List;

@Mixin(FrogportBlockEntity.class)
public abstract class FrogportBlockEntityMixin implements IFrogportDNA, IHaveGoggleInformation {

    @Unique
    private FrogGenome amphibia$genome = null;

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (isPlayerSneaking && this.amphibia$genome != null) {
            tooltip.addAll(FrogDNADisplayHelper.getDNATooltip(this.amphibia$genome, true));
            return true;
        }
        return false;
    }

    @Inject(method = "write", at = @At("TAIL"))
    private void amphibia$write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        if (this.amphibia$genome != null) {
            tag.put("AmphibiaGenome", FrogGenome.CODEC.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE, this.amphibia$genome).getOrThrow());
        }
    }

    @Inject(method = "read", at = @At("TAIL"))
    private void amphibia$read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        if (tag.contains("AmphibiaGenome")) {
            FrogGenome.CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE, tag.getCompound("AmphibiaGenome"))
                .resultOrPartial()
                .ifPresent(genome -> this.amphibia$genome = genome);
        }
    }

    @Override
    public FrogGenome amphibia$getGenome() {
        return this.amphibia$genome;
    }

    @Override
    public void amphibia$setGenome(FrogGenome genome) {
        this.amphibia$genome = genome;
    }
}
