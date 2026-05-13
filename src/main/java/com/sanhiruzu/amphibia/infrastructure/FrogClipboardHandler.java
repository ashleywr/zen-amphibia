package com.sanhiruzu.amphibia.infrastructure;

import com.sanhiruzu.amphibia.genetics.FrogDNA;
import com.sanhiruzu.amphibia.genetics.FrogGeneRegistry;
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

        var heatTrait = dna.getGene(FrogGeneRegistry.HEAT_TOLERANCE);
        tasks.add(new ClipboardEntry(true, Component.literal(" - Heat Tolerance: " + heatTrait.geneA() + "/" + heatTrait.geneB())));

        var viscosityTrait = dna.getGene(FrogGeneRegistry.SLIME_VISCOSITY);
        tasks.add(new ClipboardEntry(true, Component.literal(" - Viscosity: " + viscosityTrait.geneA() + "/" + viscosityTrait.geneB())));

        var growthTrait = dna.getGene(FrogGeneRegistry.GROWTH_RATE);
        tasks.add(new ClipboardEntry(true, Component.literal(" - Growth Rate: " + growthTrait.geneA() + "/" + growthTrait.geneB())));

        ClipboardContent content = new ClipboardContent(
            ClipboardOverrides.ClipboardType.WRITTEN, List.of(tasks), false);

        clipboard.set(com.simibubi.create.AllDataComponents.CLIPBOARD_CONTENT, content);
    }
}
