package com.sanhiruzu.amphibia.item;

import com.sanhiruzu.amphibia.genetics.FrogGenome;
import com.sanhiruzu.amphibia.infrastructure.FrogDNADisplayHelper;
import com.sanhiruzu.amphibia.register.AmphibiaDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.InteractionResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("NullableProblems")
public class BottledFrogspawnItem extends Item {
    public BottledFrogspawnItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockState blockState = context.getLevel().getBlockState(context.getClickedPos());

        if (blockState.getBlock() instanceof CauldronBlock && blockState.is(Blocks.WATER_CAULDRON)) {
            BlockPos cauldronPos = context.getClickedPos();
            BlockPos firePos = cauldronPos.below();
            BlockState fireState = context.getLevel().getBlockState(firePos);

            if (fireState.is(Blocks.CAMPFIRE) && fireState.getValue(net.minecraft.world.level.block.CampfireBlock.LIT)) {
                if (!context.getLevel().isClientSide) {
                    ItemStack stack = context.getItemInHand();
                    ItemEntity itemEntity = new ItemEntity(context.getLevel(), cauldronPos.getX() + 0.5, cauldronPos.getY() + 0.5, cauldronPos.getZ() + 0.5, stack.copy());

                    CompoundTag tag = itemEntity.getPersistentData();
                    tag.putInt("IncubationTicks", 0);
                    tag.putInt("CatalystLapis", 0);
                    tag.putInt("CatalystSlime", 0);

                    context.getLevel().addFreshEntity(itemEntity);
                    stack.shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        FrogGenome genome = stack.get(AmphibiaDataComponents.FROG_DNA.get());

        if (genome != null) {
            if (net.minecraft.client.gui.screens.Screen.hasShiftDown()) {
                tooltip.addAll(FrogDNADisplayHelper.getComprehensiveDNATooltip(genome));
            } else {
                tooltip.add(Component.literal("Hold SHIFT for genetics").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            }
        } else {
            tooltip.add(Component.literal("[Status: Viable Embryo]").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }

        super.appendHoverText(stack, context, tooltip, flag);
    }
}
