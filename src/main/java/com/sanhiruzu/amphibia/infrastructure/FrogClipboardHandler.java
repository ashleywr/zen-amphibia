package com.sanhiruzu.amphibia.infrastructure;

import com.sanhiruzu.amphibia.genetics.FrogGenome;
import com.sanhiruzu.amphibia.genetics.FrogGradeCalculator;
import com.sanhiruzu.amphibia.genetics.FrogMutation;
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

        if (!genome.mutations().isEmpty()) {
            tasks.add(new ClipboardEntry(true, Component.literal(" - Mutations:")));
            for (String mutationId : genome.mutations()) {
                FrogMutation mutation = FrogMutation.getById(mutationId);
                String name = mutation != null ? mutation.displayName().getString() : mutationId;
                tasks.add(new ClipboardEntry(true, Component.literal("   * " + name)));
            }
        }

        Gene.Layer currentLayer = null;
        for (Gene gene : Gene.values()) {
            if (gene.layer != currentLayer) {
                currentLayer = gene.layer;
                tasks.add(new ClipboardEntry(true, Component.literal(" - " + currentLayer.displayName + ":")));
            }
            var trait = genome.getGene(gene);
            FrogGradeCalculator.Grade grade = FrogGradeCalculator.calculateGrade(trait);
            tasks.add(new ClipboardEntry(true, Component.literal(
                "   * " + gene.displayName + ": " + trait.geneA() + "/" + trait.geneB() + " [" + grade.label + "]"
            )));
        }

        ClipboardContent content = new ClipboardContent(
            ClipboardOverrides.ClipboardType.WRITTEN, List.of(tasks), false);

        clipboard.set(com.simibubi.create.AllDataComponents.CLIPBOARD_CONTENT, content);
    }
}
