package com.sanhiruzu.amphibia.item;

import com.sanhiruzu.amphibia.genetics.FrogDNA;
import com.sanhiruzu.amphibia.register.AmphibiaAttachments;
import com.sanhiruzu.amphibia.register.AmphibiaDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

public class FrogBucketItem extends BucketItem {
    public FrogBucketItem(Properties properties) {
        super(Fluids.EMPTY, properties);
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack stack, @NotNull Player player, net.minecraft.world.entity.@NotNull LivingEntity target, @NotNull InteractionHand hand) {
        if (target instanceof Frog frog && !frog.isBaby()) {
            Level level = target.level();
            if (!level.isClientSide) {
                ItemStack bucket = new ItemStack(this);
                FrogDNA dna = frog.getData(AmphibiaAttachments.FROG_DNA);
                bucket.set(AmphibiaDataComponents.FROG_DNA, dna);

                frog.playSound(SoundEvents.BUCKET_FILL_FISH, 1.0f, 1.0f);
                frog.discard();

                if (stack.getCount() == 1) {
                    player.setItemInHand(hand, bucket);
                } else {
                    stack.shrink(1);
                    if (!player.getInventory().add(bucket)) {
                        player.drop(bucket, false);
                    }
                }
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public static void onPlaceFrog(Level level, ItemStack stack, BlockPos pos) {
        if (level.isClientSide) return;
        
        Entity entity = EntityType.FROG.spawn((net.minecraft.server.level.ServerLevel) level, stack, null, pos, net.minecraft.world.entity.MobSpawnType.BUCKET, true, false);
        if (entity instanceof Frog frog) {
            FrogDNA dna = stack.get(AmphibiaDataComponents.FROG_DNA);
            if (dna != null) {
                frog.setData(AmphibiaAttachments.FROG_DNA, dna);
            }
            frog.setPersistenceRequired();
        }
    }
}
