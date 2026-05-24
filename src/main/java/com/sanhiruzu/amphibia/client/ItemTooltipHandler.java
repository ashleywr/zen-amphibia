package com.sanhiruzu.amphibia.client;

import com.sanhiruzu.amphibia.infrastructure.FrogDNADisplayHelper;
import com.sanhiruzu.amphibia.register.AmphibiaDataComponents;
import com.sanhiruzu.amphibia.genetics.FrogGenome;
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

        // Handle Frogport items (from Create mod - we don't own this item class)
        if (stack.is(com.simibubi.create.AllBlocks.PACKAGE_FROGPORT.get().asItem())) {
            FrogGenome genome = stack.get(AmphibiaDataComponents.FROG_DNA.get());

            if (genome != null) {
                if (Screen.hasShiftDown()) {
                    event.getToolTip().addAll(FrogDNADisplayHelper.getDebugDNATooltip(genome));
                } else {
                    event.getToolTip().add(Component.literal("Hold SHIFT for genetics").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
                }
            }
        }
    }
}
