package com.sanhiruzu.amphibia.mixin;

import com.sanhiruzu.amphibia.duck.IFrogportDNA;
import com.sanhiruzu.amphibia.genetics.FrogDNA;
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
import net.minecraft.ChatFormatting;
import java.util.List;

@Mixin(value = FrogportBlockEntity.class, remap = false)
public abstract class FrogportBlockEntityMixin implements IFrogportDNA, IHaveGoggleInformation {

    @Unique
    private FrogDNA amphibia$dna = null;

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (isPlayerSneaking && this.amphibia$dna != null) {
            tooltip.addAll(FrogDNADisplayHelper.getDNATooltip(this.amphibia$dna, true));
            return true;
        }
        return false;
    }

    @Inject(method = "write", at = @At("TAIL"))
    private void amphibia$write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        if (this.amphibia$dna != null) {
            compound.put("AmphibiaDNA", (CompoundTag) FrogDNA.CODEC.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE, this.amphibia$dna).getOrThrow());
        }
    }

    @Inject(method = "read", at = @At("TAIL"))
    private void amphibia$read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        if (compound.contains("AmphibiaDNA")) {
            FrogDNA.CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE, compound.getCompound("AmphibiaDNA"))
                .resultOrPartial()
                .ifPresent(dna -> this.amphibia$dna = dna);
        }
    }

    @Override
    public FrogDNA amphibia$getDna() {
        return this.amphibia$dna;
    }

    @Override
    public void amphibia$setDna(FrogDNA dna) {
        this.amphibia$dna = dna;
    }
}
