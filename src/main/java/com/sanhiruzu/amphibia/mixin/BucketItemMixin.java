package com.sanhiruzu.amphibia.mixin;

import com.sanhiruzu.amphibia.genetics.FrogGenome;
import com.sanhiruzu.amphibia.register.AmphibiaFluids;
import com.sanhiruzu.zonectrl.zone.WildGeneticsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = BucketItem.class, remap = false)
public abstract class BucketItemMixin {

    @Shadow @Final private Fluid content;

    private static final ThreadLocal<FrogGenome> TEMP_GENOME = new ThreadLocal<>();

    @Inject(
        method = "emptyContents",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"),
        locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void amphibia$onBucketEmpty(Player player, Level level, BlockPos pos, BlockHitResult hitResult, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            if (this.content.isSame(AmphibiaFluids.RAW_GENETIC_FLUID.get())) {
                FrogGenome genome = stack.get(com.sanhiruzu.amphibia.register.AmphibiaDataComponents.FROG_DNA.get());
                if (genome != null) {
                    CompoundTag genomeTag = (CompoundTag) FrogGenome.CODEC.encodeStart(NbtOps.INSTANCE, genome).getOrThrow();
                    WildGeneticsRegistry.get(serverLevel).put(pos, genomeTag);
                }
            }
        }
    }

    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemUtils;createFilledResult(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void amphibia$onBucketFill(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir, 
                                      ItemStack itemStack, BlockHitResult blockHitResult, BlockPos pos) {
        
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            BlockState state = level.getBlockState(pos);
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
    private void amphibia$afterBucketFill(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        FrogGenome genome = TEMP_GENOME.get();
        if (genome != null) {
            TEMP_GENOME.remove();
            ItemStack result = cir.getReturnValue().getObject();
            if (result.is(AmphibiaFluids.RAW_GENETIC_FLUID_BUCKET.get())) {
                result.set(com.sanhiruzu.amphibia.register.AmphibiaDataComponents.FROG_DNA.get(), genome);
            }
        }
    }
}
