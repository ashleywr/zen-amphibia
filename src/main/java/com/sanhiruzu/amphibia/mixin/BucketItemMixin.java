package com.sanhiruzu.amphibia.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.sanhiruzu.amphibia.genetics.FrogGenome;
import com.sanhiruzu.amphibia.genetics.WildGeneticsRegistry;
import com.sanhiruzu.amphibia.register.AmphibiaFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BucketItem.class)
public abstract class BucketItemMixin {

    @Shadow
    @Final
    public Fluid content;

    @Unique
    private static final ThreadLocal<FrogGenome> TEMP_GENOME = new ThreadLocal<>();

    @Inject(
        method = "use",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/BucketItem;emptyContents(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/BlockHitResult;Lnet/minecraft/world/item/ItemStack;)Z"
        )
    )
    private void amphibia$onBucketEmpty(Level level, Player player, InteractionHand hand,
                                         CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir,
                                         @Local(ordinal = 0) ItemStack stack,
                                         @Local(ordinal = 1) BlockPos blockpos1) {
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            if (this.content.isSame(AmphibiaFluids.RAW_GENETIC_FLUID.get())) {
                FrogGenome genome = stack.get(com.sanhiruzu.amphibia.register.AmphibiaDataComponents.FROG_DNA.get());
                if (genome != null) {
                    CompoundTag genomeTag = (CompoundTag) FrogGenome.CODEC.encodeStart(NbtOps.INSTANCE, genome).getOrThrow();
                    WildGeneticsRegistry.get(serverLevel).put(blockpos1, genomeTag);
                }
            }
        }
    }

    @Inject(
        method = "use",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/BucketPickup;pickupBlock(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/item/ItemStack;")
    )
    private void amphibia$onBucketFill(Level level, Player player, InteractionHand hand,
                                        CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir,
                                        @Local(ordinal = 0) BlockPos pos, @Local BlockState state) {
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            if (state.is(AmphibiaFluids.RAW_GENETIC_FLUID_BLOCK.get())) {
                CompoundTag genetics = WildGeneticsRegistry.get(serverLevel).remove(pos);
                if (genetics != null) {
                    FrogGenome genome = FrogGenome.CODEC.parse(NbtOps.INSTANCE, genetics).result().orElse(null);
                    if (genome != null) {
                        TEMP_GENOME.set(genome);
                    }
                }
            }
        }
    }

    @Inject(method = "use", at = @At("RETURN"))
    private void amphibia$afterBucketFill(Level level, Player player, InteractionHand hand,
                                           CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        try {
            FrogGenome genome = TEMP_GENOME.get();
            if (genome != null) {
                ItemStack result = cir.getReturnValue().getObject();
                if (result.is(AmphibiaFluids.RAW_GENETIC_FLUID_BUCKET.get())) {
                    result.set(com.sanhiruzu.amphibia.register.AmphibiaDataComponents.FROG_DNA.get(), genome);
                }
            }
        } finally {
            TEMP_GENOME.remove();
        }
    }
}
