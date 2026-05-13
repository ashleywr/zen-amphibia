package com.sanhiruzu.amphibia.infrastructure;

import com.sanhiruzu.amphibia.genetics.FrogDNA;
import com.simibubi.create.content.equipment.clipboard.ClipboardContent;
import com.simibubi.create.content.equipment.clipboard.ClipboardEntry;
import com.simibubi.create.content.equipment.clipboard.ClipboardOverrides;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class FrogClipboardHandler {

    public static void addDNAToClipboard(ItemStack clipboard, FrogDNA dna, String title) {
        if (dna == null) return;

        List<ClipboardEntry> tasks = new ArrayList<>();
        tasks.add(new ClipboardEntry(true, Component.literal(title).withStyle(ChatFormatting.GREEN)));
        tasks.add(new ClipboardEntry(true, Component.literal(" - Heat Tolerance: " + dna.heatTolerance().geneA() + "/" + dna.heatTolerance().geneB())));
        tasks.add(new ClipboardEntry(true, Component.literal(" - Viscosity: " + dna.slimeViscosity().geneA() + "/" + dna.slimeViscosity().geneB())));
        tasks.add(new ClipboardEntry(true, Component.literal(" - Growth Rate: " + dna.growthRate().geneA() + "/" + dna.growthRate().geneB())));

        ClipboardContent content = new ClipboardContent(
            ClipboardOverrides.ClipboardType.WRITTEN, List.of(tasks), false);

        clipboard.set(com.simibubi.create.AllDataComponents.CLIPBOARD_CONTENT, content);
    }
}
