package com.sanhiruzu.amphibia.item;

import com.sanhiruzu.amphibia.genetics.FrogGenome;
import com.sanhiruzu.amphibia.infrastructure.FrogDNADisplayHelper;
import com.sanhiruzu.amphibia.register.AmphibiaAttachments;
import com.sanhiruzu.amphibia.register.AmphibiaDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("NullableProblems")
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
                FrogGenome genome = frog.getData(AmphibiaAttachments.FROG_GENOME);
                bucket.set(AmphibiaDataComponents.FROG_DNA.get(), genome);

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

    @Override
    public void appendHoverText(@NotNull ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        FrogGenome genome = stack.get(AmphibiaDataComponents.FROG_DNA.get());

        if (genome != null) {
            if (net.minecraft.client.gui.screens.Screen.hasShiftDown()) {
                tooltip.addAll(FrogDNADisplayHelper.getComprehensiveDNATooltip(genome));
            } else {
                tooltip.add(Component.literal("Hold SHIFT for genetics").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            }
        }

        super.appendHoverText(stack, context, tooltip, flag);
    }

    public static void onPlaceFrog(Level level, ItemStack stack, BlockPos pos) {
        if (level.isClientSide) return;

        Frog frog = EntityType.FROG.create(level);
        if (frog != null) {
            frog.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            FrogGenome genome = stack.get(AmphibiaDataComponents.FROG_DNA.get());
            if (genome != null) {
                frog.setData(AmphibiaAttachments.FROG_GENOME, genome);
            }
            frog.setPersistenceRequired();
            frog.finalizeSpawn((net.minecraft.world.level.ServerLevelAccessor) level, level.getCurrentDifficultyAt(pos), net.minecraft.world.entity.MobSpawnType.BUCKET, null);
            level.addFreshEntity(frog);
        }
    }
}
