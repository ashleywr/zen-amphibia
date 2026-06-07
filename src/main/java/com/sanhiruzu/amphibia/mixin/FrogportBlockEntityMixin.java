package com.sanhiruzu.amphibia.mixin;

import com.sanhiruzu.amphibia.duck.IFrogportDNA;
import com.sanhiruzu.amphibia.genetics.FrogGenome;
import com.sanhiruzu.amphibia.genetics.FrogGradeCalculator;
import com.sanhiruzu.amphibia.genetics.FrogportGeneEvaluator;
import com.sanhiruzu.amphibia.infrastructure.FrogDNADisplayHelper;
import com.simibubi.create.content.logistics.packagePort.frogport.FrogportBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Optional;

@Mixin(FrogportBlockEntity.class)
public abstract class FrogportBlockEntityMixin implements IFrogportDNA, IHaveGoggleInformation {

    @Unique
    private FrogGenome amphibia$genome = null;

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (isPlayerSneaking && this.amphibia$genome != null) {
            tooltip.addAll(FrogDNADisplayHelper.getDNATooltip(this.amphibia$genome, true));
            if (amphibia$genome != null) {
                FrogGradeCalculator.Grade slimeGrade = FrogGradeCalculator.calculateGrade(amphibia$genome.getGene(com.sanhiruzu.amphibia.genetics.Gene.SLIME_YIELD));
                tooltip.add(Component.literal("§6Slime Output: §r" + FrogGradeCalculator.getGradeDescription(slimeGrade)));
            }
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

    @Inject(method = "tick", at = @At("TAIL"), require = 0)
    private void amphibia$onTick(CallbackInfo ci) {
        if (this.amphibia$genome == null) return;

        FrogportBlockEntity frogport = (FrogportBlockEntity) (Object) this;
        Level level = frogport.getLevel();
        if (level == null || level.isClientSide) return;

        // Output slime bonus periodically based on SLIME_YIELD grade
        // Use game time and block position to create a deterministic pattern
        long gameTime = level.getGameTime();
        int posHash = frogport.getBlockPos().hashCode() & 0x3F;
        int offset = posHash % 40;

        if ((gameTime - offset) % 40 == 0) {
            Optional<ItemStack> bonus = FrogportGeneEvaluator.getSlimeBonus(amphibia$genome, level.random);
            if (bonus.isPresent()) {
                // Output the bonus item below the frogport
                BlockPos pos = frogport.getBlockPos();
                ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() - 0.5, pos.getZ() + 0.5, bonus.get());
                itemEntity.setDefaultPickUpDelay();
                level.addFreshEntity(itemEntity);
            }
        }
    }

}
