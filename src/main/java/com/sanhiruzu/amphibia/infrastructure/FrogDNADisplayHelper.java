package com.sanhiruzu.amphibia.infrastructure;

import com.sanhiruzu.amphibia.genetics.FrogDNA;
import com.sanhiruzu.amphibia.genetics.FrogGradeCalculator;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.animal.frog.Frog;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FrogDNADisplayHelper {

    public static List<Component> getDNATooltip(FrogDNA dna, boolean includeHeader) {
        List<Component> lines = new ArrayList<>();

        if (includeHeader) {
            lines.add(Component.translatable("gui.goggles.amphibia.frog_genetics").withStyle(ChatFormatting.GREEN));
        }

        lines.add(Component.literal("  ").append(Component.translatable("gui.create_kaizen.factory_manager.temperature"))
            .append(Component.literal(": " + dna.heatTolerance().geneA() + "/" + dna.heatTolerance().geneB()).withStyle(ChatFormatting.GOLD)));

        lines.add(Component.literal("  ").append(Component.translatable("gui.create_kaizen.factory_manager.purity"))
            .append(Component.literal(": " + dna.slimeViscosity().geneA() + "/" + dna.slimeViscosity().geneB()).withStyle(ChatFormatting.GREEN)));

        lines.add(Component.literal("  ").append(Component.literal("Growth: "))
            .append(Component.literal(dna.growthRate().geneA() + "/" + dna.growthRate().geneB()).withStyle(ChatFormatting.AQUA)));

        return lines;
    }

    public static List<Component> getFrogDebugInfo(Frog frog, FrogDNA dna) {
        List<Component> lines = new ArrayList<>();

        lines.add(Component.literal("=== FROG DEBUG INFO ===").withStyle(ChatFormatting.LIGHT_PURPLE));

        // Basic info
        lines.add(Component.literal("UUID: ").withStyle(ChatFormatting.GRAY)
            .append(Component.literal(frog.getUUID().toString()).withStyle(ChatFormatting.WHITE)));

        lines.add(Component.literal("Age: ").withStyle(ChatFormatting.GRAY)
            .append(Component.literal(frog.getAge() + " ticks").withStyle(ChatFormatting.YELLOW)));

        lines.add(Component.literal("Size: ").withStyle(ChatFormatting.GRAY)
            .append(Component.literal(String.format("%.2f", frog.getScale())).withStyle(ChatFormatting.YELLOW)));

        // Mutations
        if (dna != null && !dna.mutations().isEmpty()) {
            lines.add(Component.empty());
            lines.add(Component.literal("=== MUTATIONS ===").withStyle(ChatFormatting.LIGHT_PURPLE));
            for (String mutationId : dna.mutations()) {
                com.sanhiruzu.amphibia.genetics.FrogMutation mutation = com.sanhiruzu.amphibia.genetics.FrogMutation.getById(mutationId);
                if (mutation != null) {
                    lines.add(Component.literal("  ").append(Component.literal(mutation.displayName().getString()).withStyle(net.minecraft.network.chat.Style.EMPTY.withColor(net.minecraft.network.chat.TextColor.fromRgb(mutation.color())))));
                }
            }
        }

        // Genetics
        lines.add(Component.empty());
        lines.add(Component.literal("=== GENETICS ===").withStyle(ChatFormatting.GREEN));

        if (dna != null) {
            FrogGradeCalculator.Grade healthGrade = FrogGradeCalculator.calculateGrade(dna.health());
            FrogGradeCalculator.Grade damageGrade = FrogGradeCalculator.calculateGrade(dna.damage());

            lines.add(Component.literal("Heat Tolerance: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(dna.heatTolerance().geneA() + " / " + dna.heatTolerance().geneB()).withStyle(ChatFormatting.GOLD)));

            lines.add(Component.literal("Viscosity: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(dna.slimeViscosity().geneA() + " / " + dna.slimeViscosity().geneB()).withStyle(ChatFormatting.GREEN)));

            lines.add(Component.literal("Growth Rate: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(dna.growthRate().geneA() + " / " + dna.growthRate().geneB()).withStyle(ChatFormatting.AQUA)));

            // Combat traits with grades
            lines.add(Component.empty());
            lines.add(Component.literal("Health: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(dna.health().geneA() + " / " + dna.health().geneB()).withStyle(ChatFormatting.WHITE))
                .append(Component.literal(" [" + healthGrade.label + "]").withStyle(net.minecraft.network.chat.Style.EMPTY.withColor(net.minecraft.network.chat.TextColor.fromRgb(healthGrade.color)))));

            lines.add(Component.literal("Damage: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(dna.damage().geneA() + " / " + dna.damage().geneB()).withStyle(ChatFormatting.WHITE))
                .append(Component.literal(" [" + damageGrade.label + "]").withStyle(net.minecraft.network.chat.Style.EMPTY.withColor(net.minecraft.network.chat.TextColor.fromRgb(damageGrade.color)))));

            // Color genetics
            int colorHash = getDNAColor(dna);
            int r = (colorHash >> 16) & 0xFF;
            int g = (colorHash >> 8) & 0xFF;
            int b = colorHash & 0xFF;
            String colorName = getColorName(r, g, b);

            lines.add(Component.literal("Color: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(colorName + " (RGB:" + r + "," + g + "," + b + ")").withStyle(ChatFormatting.WHITE)));
        } else {
            lines.add(Component.literal("No DNA data").withStyle(ChatFormatting.RED));
        }

        // State
        lines.add(Component.empty());
        lines.add(Component.literal("=== STATE ===").withStyle(ChatFormatting.BLUE));
        lines.add(Component.literal("InLove: ").withStyle(ChatFormatting.GRAY)
            .append(Component.literal(String.valueOf(frog.isInLove())).withStyle(ChatFormatting.YELLOW)));

        lines.add(Component.literal("Position: ").withStyle(ChatFormatting.GRAY)
            .append(Component.literal(String.format("[%.1f, %.1f, %.1f]", frog.getX(), frog.getY(), frog.getZ())).withStyle(ChatFormatting.WHITE)));

        return lines;
    }

    private static String getColorName(int r, int g, int b) {
        if (r > g && r > b) return "Reddish";
        if (g > r && g > b) return "Greenish";
        if (b > r && b > g) return "Bluish";
        if (r > 150 && g > 150 && b < 100) return "Yellowish";
        if (r > 150 && g < 100 && b > 150) return "Magenta";
        if (r < 100 && g > 150 && b > 150) return "Cyan";
        if (r < 100 && g < 100 && b < 100) return "Dark";
        return "Neutral";
    }

    public static List<Component> getDebugDNATooltip(FrogDNA dna) {
        List<Component> lines = new ArrayList<>();

        lines.add(Component.empty());
        lines.add(Component.literal("DEBUG - Frog DNA:").withStyle(ChatFormatting.DARK_RED));
        lines.add(Component.literal("  Heat Tolerance: " + dna.heatTolerance().geneA() + " / " + dna.heatTolerance().geneB()).withStyle(ChatFormatting.GOLD));
        lines.add(Component.literal("  Slime Viscosity: " + dna.slimeViscosity().geneA() + " / " + dna.slimeViscosity().geneB()).withStyle(ChatFormatting.GREEN));
        lines.add(Component.literal("  Growth Rate: " + dna.growthRate().geneA() + " / " + dna.growthRate().geneB()).withStyle(ChatFormatting.AQUA));

        FrogGradeCalculator.Grade healthGrade = FrogGradeCalculator.calculateGrade(dna.health());
        FrogGradeCalculator.Grade damageGrade = FrogGradeCalculator.calculateGrade(dna.damage());

        lines.add(Component.literal("  Health: " + dna.health().geneA() + " / " + dna.health().geneB() + " [" + healthGrade.label + "]").withStyle(ChatFormatting.RED));
        lines.add(Component.literal("  Damage: " + dna.damage().geneA() + " / " + dna.damage().geneB() + " [" + damageGrade.label + "]").withStyle(ChatFormatting.DARK_RED));

        return lines;
    }

    public static int getDNAColor(FrogDNA dna) {
        int hashRed = Math.abs(dna.heatTolerance().hashCode());
        int hashGreen = Math.abs(dna.slimeViscosity().hashCode());
        int hashBlue = Math.abs(dna.growthRate().hashCode());

        int r = 100 + (hashRed % 155);
        int g = 100 + (hashGreen % 155);
        int b = 100 + (hashBlue % 155);

        return (255 << 24) | (r << 16) | (g << 8) | b;
    }

    public static float getScaleFromDNA(FrogDNA dna) {
        int hashScale = Math.abs(dna.growthRate().geneA().hashCode() * 31 + dna.growthRate().geneB().hashCode());
        return 0.5f + ((hashScale * 739) % 100) / 50.0f;
    }
}
