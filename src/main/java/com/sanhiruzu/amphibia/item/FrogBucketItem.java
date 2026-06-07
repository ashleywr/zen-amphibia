package com.sanhiruzu.amphibia.item;

import com.sanhiruzu.amphibia.genetics.FrogGenome;
import com.sanhiruzu.amphibia.infrastructure.FrogDNADisplayHelper;
import com.sanhiruzu.amphibia.register.AmphibiaAttachments;
import com.sanhiruzu.amphibia.register.AmphibiaDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;

import java.util.List;

public class FrogBucketItem extends BucketItem {
    public FrogBucketItem(Properties properties) {
        super(Fluids.EMPTY, properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, net.minecraft.world.entity.LivingEntity target, InteractionHand hand) {
        if (target instanceof Frog frog && !frog.isBaby()) {
            Level level = target.level();
            if (!level.isClientSide) {
                ItemStack bucket = new ItemStack(this);
                FrogGenome genome = frog.getData(AmphibiaAttachments.FROG_GENOME);
                bucket.set(AmphibiaDataComponents.FROG_DNA.get(), genome);
                if (frog.hasCustomName()) {
                    Component name = frog.getCustomName();
                    if (name != null) {
                        CustomData.update(DataComponents.CUSTOM_DATA, bucket,
                            tag -> tag.putString("AmphibiaFrogName", name.getString()));
                    }
                }

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
    public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        FrogGenome genome = stack.get(AmphibiaDataComponents.FROG_DNA.get());

        if (genome != null) {
            tooltip.addAll(FrogDNADisplayHelper.getBucketPersonalityTooltip(genome));
            tooltip.addAll(FrogDNADisplayHelper.getBreedingSummaryTooltip(genome));
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
            CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
            if (customData != null && customData.contains("AmphibiaFrogName")) {
                frog.setCustomName(Component.literal(customData.copyTag().getString("AmphibiaFrogName")));
            }
            frog.setPersistenceRequired();
            frog.finalizeSpawn((net.minecraft.world.level.ServerLevelAccessor) level, level.getCurrentDifficultyAt(pos), net.minecraft.world.entity.MobSpawnType.BUCKET, null);
            level.addFreshEntity(frog);
            level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, net.minecraft.sounds.SoundSource.PLAYERS, 0.8f, 1.2f);
            frog.playSound(SoundEvents.FROG_AMBIENT, 0.6f, 1.1f);
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.SPLASH,
                    pos.getX() + 0.5, pos.getY() + 0.15, pos.getZ() + 0.5,
                    8, 0.25, 0.05, 0.25, 0.02);
                if (genome != null && genome.getScale() > 1.25f) {
                    serverLevel.sendParticles(ParticleTypes.FALLING_WATER,
                        pos.getX() + 0.5, pos.getY() + 0.35, pos.getZ() + 0.5,
                        4, 0.2, 0.05, 0.2, 0.0);
                }
            }
        }
    }
}
