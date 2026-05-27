package com.sanhiruzu.amphibia.item;

import com.sanhiruzu.amphibia.genetics.FrogGenome;
import com.sanhiruzu.amphibia.infrastructure.FrogDNADisplayHelper;
import com.sanhiruzu.amphibia.register.AmphibiaDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class FrogspawnBlockItem extends BlockItem {
    private final boolean dormant;

    public FrogspawnBlockItem(Block block, Properties properties, boolean dormant) {
        super(block, properties);
        this.dormant = dormant;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(dormant
                ? "tooltip.zen_amphibia.dormant_frogspawn.description"
                : "tooltip.zen_amphibia.genetic_frogspawn.description")
            .withStyle(ChatFormatting.GRAY));

        tooltip.add(Component.translatable(dormant
                ? "tooltip.zen_amphibia.dormant_frogspawn.action"
                : "tooltip.zen_amphibia.genetic_frogspawn.action")
            .withStyle(ChatFormatting.DARK_AQUA));

        FrogGenome genome = stack.get(AmphibiaDataComponents.FROG_DNA.get());
        if (genome != null) {
            if (Screen.hasShiftDown()) {
                tooltip.addAll(FrogDNADisplayHelper.getComprehensiveDNATooltip(genome));
            } else {
                tooltip.add(Component.translatable("tooltip.zen_amphibia.hold_shift_for_genetics")
                    .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            }
        }

        super.appendHoverText(stack, context, tooltip, flag);
    }
}
