package com.sanhiruzu.amphibia.client;

import com.sanhiruzu.amphibia.infrastructure.FrogDNADisplayHelper;
import com.sanhiruzu.amphibia.register.AmphibiaDataComponents;
import com.sanhiruzu.amphibia.genetics.FrogGenome;
import com.sanhiruzu.amphibia.block.FrogChestBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = "zen_amphibia", value = Dist.CLIENT)
public class ItemTooltipHandler {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        if (stack.is(com.sanhiruzu.amphibia.register.AmphibiaItems.WORKER_FROGPORT.get())
                || stack.is(com.sanhiruzu.amphibia.register.AmphibiaItems.FROG_CHEST.get())) {
            FrogGenome genome = stack.get(AmphibiaDataComponents.FROG_DNA.get());

            if (genome != null) {
                if (stack.is(com.sanhiruzu.amphibia.register.AmphibiaItems.FROG_CHEST.get())) {
                    int rows = FrogChestBlockEntity.rowsFromGenome(genome);
                    event.getToolTip().add(Component.translatable("tooltip.zen_amphibia.frog_chest.rows", rows)
                            .withStyle(ChatFormatting.GREEN));
                }
                if (Screen.hasShiftDown()) {
                    event.getToolTip().addAll(FrogDNADisplayHelper.getComprehensiveDNATooltip(genome));
                } else {
                    event.getToolTip().add(Component.translatable("tooltip.zen_amphibia.hold_shift_for_genetics").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
                }
            }
        }
    }
}
