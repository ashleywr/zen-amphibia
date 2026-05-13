package com.sanhiruzu.amphibia.infrastructure;

import com.sanhiruzu.amphibia.genetics.FrogDNA;
import com.sanhiruzu.amphibia.genetics.FrogGeneRegistry;
import com.sanhiruzu.amphibia.genetics.FrogGradeCalculator;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.animal.frog.Frog;

import java.util.ArrayList;
import java.util.List;

public class FrogDNADisplayHelper {

    public static List<Component> getDNATooltip(FrogDNA dna, boolean includeHeader) {
        List<Component> lines = new ArrayList<>();

        if (includeHeader) {
            lines.add(Component.translatable("gui.goggles.amphibia.frog_genetics").withStyle(ChatFormatting.GREEN));
        }

        var heatTrait = dna.getGene(FrogGeneRegistry.HEAT_TOLERANCE);
        lines.add(Component.literal("  ").append(Component.translatable("gui.create_kaizen.factory_manager.temperature"))
            .append(Component.literal(": " + heatTrait.geneA() + "/" + heatTrait.geneB()).withStyle(ChatFormatting.GOLD)));

        var viscosityTrait = dna.getGene(FrogGeneRegistry.SLIME_VISCOSITY);
        lines.add(Component.literal("  ").append(Component.translatable("gui.create_kaizen.factory_manager.purity"))
            .append(Component.literal(": " + viscosityTrait.geneA() + "/" + viscosityTrait.geneB()).withStyle(ChatFormatting.GREEN)));

        var growthTrait = dna.getGene(FrogGeneRegistry.GROWTH_RATE);
        lines.add(Component.literal("  ").append(Component.literal("Growth: "))
            .append(Component.literal(growthTrait.geneA() + "/" + growthTrait.geneB()).withStyle(ChatFormatting.AQUA)));

        var sizeTrait = dna.getGene(FrogGeneRegistry.SIZE);
        lines.add(Component.literal("  ").append(Component.literal("Size: "))
            .append(Component.literal(sizeTrait.geneA() + "/" + sizeTrait.geneB()).withStyle(ChatFormatting.LIGHT_PURPLE)));

        return lines;
    }

    public static List<Component> getFrogDebugInfo(Frog frog, FrogDNA dna) {
        List<Component> lines = new ArrayList<>();

        lines.add(Component.literal("=== FROG DEBUG INFO ===").withStyle(ChatFormatting.LIGHT_PURPLE));

        lines.add(Component.literal("UUID: ").withStyle(ChatFormatting.GRAY)
            .append(Component.literal(frog.getUUID().toString()).withStyle(ChatFormatting.WHITE)));

        lines.add(Component.literal("Age: ").withStyle(ChatFormatting.GRAY)
            .append(Component.literal(frog.getAge() + " ticks").withStyle(ChatFormatting.YELLOW)));

        lines.add(Component.literal("Size: ").withStyle(ChatFormatting.GRAY)
            .append(Component.literal(String.format("%.2f", frog.getScale())).withStyle(ChatFormatting.YELLOW)));

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

        lines.add(Component.empty());
        lines.add(Component.literal("=== GENETICS ===").withStyle(ChatFormatting.GREEN));

        if (dna != null) {
            FrogGradeCalculator.Grade healthGrade = FrogGradeCalculator.calculateGrade(dna.getGene(FrogGeneRegistry.HEALTH));
            FrogGradeCalculator.Grade damageGrade = FrogGradeCalculator.calculateGrade(dna.getGene(FrogGeneRegistry.DAMAGE));

            var heatTrait = dna.getGene(FrogGeneRegistry.HEAT_TOLERANCE);
            lines.add(Component.literal("Heat Tolerance: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(heatTrait.geneA() + " / " + heatTrait.geneB()).withStyle(ChatFormatting.GOLD)));

            var viscosityTrait = dna.getGene(FrogGeneRegistry.SLIME_VISCOSITY);
            lines.add(Component.literal("Viscosity: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(viscosityTrait.geneA() + " / " + viscosityTrait.geneB()).withStyle(ChatFormatting.GREEN)));

            var growthTrait = dna.getGene(FrogGeneRegistry.GROWTH_RATE);
            lines.add(Component.literal("Growth Rate: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(growthTrait.geneA() + " / " + growthTrait.geneB()).withStyle(ChatFormatting.AQUA)));

            var sizeTrait = dna.getGene(FrogGeneRegistry.SIZE);
            lines.add(Component.literal("Size: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(sizeTrait.geneA() + " / " + sizeTrait.geneB()).withStyle(ChatFormatting.LIGHT_PURPLE)));

            lines.add(Component.empty());
            var healthTrait = dna.getGene(FrogGeneRegistry.HEALTH);
            lines.add(Component.literal("Health: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(healthTrait.geneA() + " / " + healthTrait.geneB()).withStyle(ChatFormatting.WHITE))
                .append(Component.literal(" [" + healthGrade.label + "]").withStyle(net.minecraft.network.chat.Style.EMPTY.withColor(net.minecraft.network.chat.TextColor.fromRgb(healthGrade.color)))));

            var damageTrait = dna.getGene(FrogGeneRegistry.DAMAGE);
            lines.add(Component.literal("Damage: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(damageTrait.geneA() + " / " + damageTrait.geneB()).withStyle(ChatFormatting.WHITE))
                .append(Component.literal(" [" + damageGrade.label + "]").withStyle(net.minecraft.network.chat.Style.EMPTY.withColor(net.minecraft.network.chat.TextColor.fromRgb(damageGrade.color)))));

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

    public static List<Component> getComprehensiveDNATooltip(FrogDNA dna) {
        List<Component> lines = new ArrayList<>();

        if (dna != null && !dna.mutations().isEmpty()) {
            lines.add(Component.literal("=== MUTATIONS ===").withStyle(ChatFormatting.LIGHT_PURPLE));
            for (String mutationId : dna.mutations()) {
                com.sanhiruzu.amphibia.genetics.FrogMutation mutation = com.sanhiruzu.amphibia.genetics.FrogMutation.getById(mutationId);
                if (mutation != null) {
                    lines.add(Component.literal("  ").append(Component.literal(mutation.displayName().getString()).withStyle(net.minecraft.network.chat.Style.EMPTY.withColor(net.minecraft.network.chat.TextColor.fromRgb(mutation.color())))));
                }
            }
            lines.add(Component.empty());
        }

        lines.add(Component.literal("=== GENETICS ===").withStyle(ChatFormatting.GREEN));

        if (dna != null) {
            FrogGradeCalculator.Grade healthGrade = FrogGradeCalculator.calculateGrade(dna.getGene(FrogGeneRegistry.HEALTH));
            FrogGradeCalculator.Grade damageGrade = FrogGradeCalculator.calculateGrade(dna.getGene(FrogGeneRegistry.DAMAGE));

            var heatTrait = dna.getGene(FrogGeneRegistry.HEAT_TOLERANCE);
            lines.add(Component.literal("Heat Tolerance: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(heatTrait.geneA() + " / " + heatTrait.geneB()).withStyle(ChatFormatting.GOLD)));

            var viscosityTrait = dna.getGene(FrogGeneRegistry.SLIME_VISCOSITY);
            lines.add(Component.literal("Viscosity: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(viscosityTrait.geneA() + " / " + viscosityTrait.geneB()).withStyle(ChatFormatting.GREEN)));

            var growthTrait = dna.getGene(FrogGeneRegistry.GROWTH_RATE);
            lines.add(Component.literal("Growth Rate: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(growthTrait.geneA() + " / " + growthTrait.geneB()).withStyle(ChatFormatting.AQUA)));

            var sizeTrait = dna.getGene(FrogGeneRegistry.SIZE);
            lines.add(Component.literal("Size: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(sizeTrait.geneA() + " / " + sizeTrait.geneB()).withStyle(ChatFormatting.LIGHT_PURPLE)));

            lines.add(Component.empty());
            var healthTrait = dna.getGene(FrogGeneRegistry.HEALTH);
            lines.add(Component.literal("Health: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(healthTrait.geneA() + " / " + healthTrait.geneB()).withStyle(ChatFormatting.WHITE))
                .append(Component.literal(" [" + healthGrade.label + "]").withStyle(net.minecraft.network.chat.Style.EMPTY.withColor(net.minecraft.network.chat.TextColor.fromRgb(healthGrade.color)))));

            var damageTrait = dna.getGene(FrogGeneRegistry.DAMAGE);
            lines.add(Component.literal("Damage: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(damageTrait.geneA() + " / " + damageTrait.geneB()).withStyle(ChatFormatting.WHITE))
                .append(Component.literal(" [" + damageGrade.label + "]").withStyle(net.minecraft.network.chat.Style.EMPTY.withColor(net.minecraft.network.chat.TextColor.fromRgb(damageGrade.color)))));

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

        return lines;
    }

    public static List<Component> getDebugDNATooltip(FrogDNA dna) {
        return getComprehensiveDNATooltip(dna);
    }

    public static int getDNAColor(FrogDNA dna) {
        int hashRed = Math.abs(dna.getGene(FrogGeneRegistry.HEAT_TOLERANCE).hashCode());
        int hashGreen = Math.abs(dna.getGene(FrogGeneRegistry.SLIME_VISCOSITY).hashCode());
        int hashBlue = Math.abs(dna.getGene(FrogGeneRegistry.GROWTH_RATE).hashCode());

        int r = 100 + (hashRed % 155);
        int g = 100 + (hashGreen % 155);
        int b = 100 + (hashBlue % 155);

        return (255 << 24) | (r << 16) | (g << 8) | b;
    }

    public static float getScaleFromDNA(FrogDNA dna) {
        int hashScale = Math.abs(dna.getGene(FrogGeneRegistry.SIZE).geneA().hashCode() * 31 + dna.getGene(FrogGeneRegistry.SIZE).geneB().hashCode());
        return 0.5f + ((hashScale * 739) % 100) / 50.0f;
    }
}
