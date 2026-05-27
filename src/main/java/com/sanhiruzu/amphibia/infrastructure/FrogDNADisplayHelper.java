package com.sanhiruzu.amphibia.infrastructure;

import com.sanhiruzu.amphibia.genetics.FrogGenome;
import com.sanhiruzu.amphibia.genetics.Gene;
import com.sanhiruzu.amphibia.genetics.FrogGradeCalculator;
import com.sanhiruzu.amphibia.genetics.FrogMutation;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

import java.util.ArrayList;
import java.util.List;

public class FrogDNADisplayHelper {

    /** Compact 2-gene overlay summary, optionally preceded by a header line. */
    public static List<Component> getDNATooltip(FrogGenome genome, boolean includeHeader) {
        List<Component> lines = new ArrayList<>();

        if (includeHeader) {
            lines.add(Component.translatable("gui.goggles.zen_amphibia.frog_genetics").withStyle(ChatFormatting.GREEN));
        }

        var heatTrait = genome.getGene(Gene.HEAT_TOLERANCE);
        lines.add(Component.literal("  ").append(Component.translatable("gui.goggles.zen_amphibia.heat_tolerance"))
            .append(Component.literal(": " + heatTrait.geneA() + "/" + heatTrait.geneB()).withStyle(Gene.HEAT_TOLERANCE.color)));

        var viscosityTrait = genome.getGene(Gene.SLIME_VISCOSITY);
        lines.add(Component.literal("  ").append(Component.translatable("gui.goggles.zen_amphibia.slime_viscosity"))
            .append(Component.literal(": " + viscosityTrait.geneA() + "/" + viscosityTrait.geneB()).withStyle(Gene.SLIME_VISCOSITY.color)));

        var growthTrait = genome.getGene(Gene.GROWTH_RATE);
        lines.add(Component.literal("  ").append(Component.literal("Growth: "))
            .append(Component.literal(growthTrait.geneA() + "/" + growthTrait.geneB()).withStyle(Gene.GROWTH_RATE.color)));

        var sizeTrait = genome.getGene(Gene.SIZE);
        lines.add(Component.literal("  ").append(Component.literal("Size: "))
            .append(Component.literal(sizeTrait.geneA() + "/" + sizeTrait.geneB()).withStyle(Gene.SIZE.color)));

        return lines;
    }

    /** Full mutations + genetics breakdown, used for item shift-tooltips. */
    public static List<Component> getComprehensiveDNATooltip(FrogGenome genome) {
        List<Component> lines = new ArrayList<>();
        addMutationsSection(lines, genome);
        addGeneticsSection(lines, genome);
        return lines;
    }

    /** Genome tint color for item/entity rendering. */
    public static int getGenomeColor(FrogGenome genome) {
        return genome.getColor();
    }

    static void addMutationsSection(List<Component> lines, FrogGenome genome) {
        if (genome == null || genome.mutations().isEmpty()) return;
        lines.add(Component.empty());
        lines.add(Component.literal("=== MUTATIONS ===").withStyle(ChatFormatting.LIGHT_PURPLE));
        for (String mutationId : genome.mutations()) {
            FrogMutation mutation = FrogMutation.getById(mutationId);
            if (mutation != null) {
                lines.add(Component.literal("  ").append(
                    Component.literal(mutation.displayName().getString())
                        .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(mutation.color())))));
            }
        }
    }

    static void addGeneticsSection(List<Component> lines, FrogGenome genome) {
        lines.add(Component.empty());
        lines.add(Component.literal("=== GENETICS ===").withStyle(ChatFormatting.GREEN));

        if (genome == null) {
            lines.add(Component.literal("No genome data").withStyle(ChatFormatting.RED));
            return;
        }

        FrogGradeCalculator.Grade healthGrade = FrogGradeCalculator.calculateGrade(genome.getGene(Gene.HEALTH));
        FrogGradeCalculator.Grade damageGrade = FrogGradeCalculator.calculateGrade(genome.getGene(Gene.DAMAGE));

        var heatTrait = genome.getGene(Gene.HEAT_TOLERANCE);
        lines.add(Component.literal("Heat Tolerance: ").withStyle(ChatFormatting.GRAY)
            .append(Component.literal(heatTrait.geneA() + " / " + heatTrait.geneB()).withStyle(Gene.HEAT_TOLERANCE.color)));

        var viscosityTrait = genome.getGene(Gene.SLIME_VISCOSITY);
        lines.add(Component.literal("Viscosity: ").withStyle(ChatFormatting.GRAY)
            .append(Component.literal(viscosityTrait.geneA() + " / " + viscosityTrait.geneB()).withStyle(Gene.SLIME_VISCOSITY.color)));

        var growthTrait = genome.getGene(Gene.GROWTH_RATE);
        lines.add(Component.literal("Growth Rate: ").withStyle(ChatFormatting.GRAY)
            .append(Component.literal(growthTrait.geneA() + " / " + growthTrait.geneB()).withStyle(Gene.GROWTH_RATE.color)));

        var sizeTrait = genome.getGene(Gene.SIZE);
        lines.add(Component.literal("Size: ").withStyle(ChatFormatting.GRAY)
            .append(Component.literal(sizeTrait.geneA() + " / " + sizeTrait.geneB()).withStyle(Gene.SIZE.color)));

        lines.add(Component.empty());
        var healthTrait = genome.getGene(Gene.HEALTH);
        lines.add(Component.literal("Health: ").withStyle(ChatFormatting.GRAY)
            .append(Component.literal(healthTrait.geneA() + " / " + healthTrait.geneB()).withStyle(ChatFormatting.WHITE))
            .append(Component.literal(" [" + healthGrade.label + "]")
                .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(healthGrade.color)))));

        var damageTrait = genome.getGene(Gene.DAMAGE);
        lines.add(Component.literal("Damage: ").withStyle(ChatFormatting.GRAY)
            .append(Component.literal(damageTrait.geneA() + " / " + damageTrait.geneB()).withStyle(ChatFormatting.WHITE))
            .append(Component.literal(" [" + damageGrade.label + "]")
                .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(damageGrade.color)))));

        int colorHash = getGenomeColor(genome);
        int r = (colorHash >> 16) & 0xFF;
        int g = (colorHash >> 8) & 0xFF;
        int b = colorHash & 0xFF;
        lines.add(Component.literal("Color: ").withStyle(ChatFormatting.GRAY)
            .append(Component.literal(colorName(r, g, b) + " (RGB:" + r + "," + g + "," + b + ")").withStyle(ChatFormatting.WHITE)));
    }

    private static String colorName(int r, int g, int b) {
        if (r > g && r > b) return "Reddish";
        if (g > r && g > b) return "Greenish";
        if (b > r && b > g) return "Bluish";
        if (r > 150 && g > 150 && b < 100) return "Yellowish";
        if (r > 150 && g < 100 && b > 150) return "Magenta";
        if (r < 100 && g > 150 && b > 150) return "Cyan";
        if (r < 100 && g < 100 && b < 100) return "Dark";
        return "Neutral";
    }
}
