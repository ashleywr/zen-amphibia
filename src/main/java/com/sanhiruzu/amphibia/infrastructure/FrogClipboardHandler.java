package com.sanhiruzu.amphibia.infrastructure;

import com.sanhiruzu.amphibia.genetics.FrogGenome;
import com.sanhiruzu.amphibia.genetics.Gene;
import com.simibubi.create.content.equipment.clipboard.ClipboardContent;
import com.simibubi.create.content.equipment.clipboard.ClipboardEntry;
import com.simibubi.create.content.equipment.clipboard.ClipboardOverrides;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class FrogClipboardHandler {

    public static void addDNAToClipboard(ItemStack clipboard, FrogGenome genome, String title) {
        if (genome == null) return;

        List<ClipboardEntry> tasks = new ArrayList<>();
        tasks.add(new ClipboardEntry(true, Component.literal(title).withStyle(ChatFormatting.GREEN)));

        var heatTrait = genome.getGene(Gene.HEAT_TOLERANCE);
        tasks.add(new ClipboardEntry(true, Component.literal(" - Heat Tolerance: " + heatTrait.geneA() + "/" + heatTrait.geneB())));

        var viscosityTrait = genome.getGene(Gene.SLIME_VISCOSITY);
        tasks.add(new ClipboardEntry(true, Component.literal(" - Viscosity: " + viscosityTrait.geneA() + "/" + viscosityTrait.geneB())));

        var growthTrait = genome.getGene(Gene.GROWTH_RATE);
        tasks.add(new ClipboardEntry(true, Component.literal(" - Growth Rate: " + growthTrait.geneA() + "/" + growthTrait.geneB())));

        ClipboardContent content = new ClipboardContent(
            ClipboardOverrides.ClipboardType.WRITTEN, List.of(tasks), false);

        clipboard.set(com.simibubi.create.AllDataComponents.CLIPBOARD_CONTENT, content);
    }
}
