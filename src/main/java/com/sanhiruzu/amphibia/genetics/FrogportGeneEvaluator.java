package com.sanhiruzu.amphibia.genetics;

import com.sanhiruzu.amphibia.register.AmphibiaItems;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.RandomSource;

import java.util.List;
import java.util.Optional;

public class FrogportGeneEvaluator {

    // QUICKNESS drives how fast the frog completes package handoff animations.
    // Create's baseline chase speed is 0.1.
    public static double getAnimationChaseSpeed(FrogGenome genome) {
        FrogGradeCalculator.Grade quickness = FrogGradeCalculator.calculateGrade(genome.getGene(Gene.QUICKNESS));
        return switch (quickness) {
            case D -> 0.10;
            case C -> 0.12;
            case B -> 0.15;
            case A -> 0.18;
            case S -> 0.22;
        };
    }

    // TONGUE_LENGTH defines how comfortably the worker reaches the package target.
    public static int getReachBlocks(FrogGenome genome) {
        FrogGradeCalculator.Grade reach = FrogGradeCalculator.calculateGrade(genome.getGene(Gene.TONGUE_LENGTH));
        return switch (reach) {
            case D -> 2;
            case C -> 3;
            case B -> 4;
            case A -> 5;
            case S -> 6;
        };
    }

    // TEMPERAMENT keeps package work steady. Lower grades can miss residue opportunities.
    public static float getResidueReliability(FrogGenome genome) {
        FrogGradeCalculator.Grade temperament = FrogGradeCalculator.calculateGrade(genome.getGene(Gene.TEMPERAMENT));
        return switch (temperament) {
            case D -> 0.55f;
            case C -> 0.70f;
            case B -> 0.85f;
            case A -> 0.95f;
            case S -> 1.0f;
        };
    }

    // SLIME_YIELD provides package-work residue, but only when a package is dispatched.
    public static Optional<ItemStack> getDispatchResidue(FrogGenome genome, RandomSource random) {
        if (random.nextFloat() > getResidueReliability(genome)) {
            return Optional.empty();
        }

        FrogGradeCalculator.Grade slimeYield = FrogGradeCalculator.calculateGrade(genome.getGene(Gene.SLIME_YIELD));
        return switch (slimeYield) {
            case D, C -> Optional.empty();
            case B -> random.nextFloat() < 0.20f
                ? Optional.of(new ItemStack(AmphibiaItems.FROG_SLIME.get()))
                : Optional.empty();
            case A -> random.nextFloat() < 0.45f
                ? Optional.of(new ItemStack(AmphibiaItems.FROG_SLIME.get()))
                : Optional.empty();
            case S -> Optional.of(new ItemStack(AmphibiaItems.FROG_SLIME.get()));
        };
    }

    public static void addWorkerTooltip(List<Component> tooltip, FrogGenome genome) {
        FrogGradeCalculator.Grade quickness = FrogGradeCalculator.calculateGrade(genome.getGene(Gene.QUICKNESS));
        FrogGradeCalculator.Grade reach = FrogGradeCalculator.calculateGrade(genome.getGene(Gene.TONGUE_LENGTH));
        FrogGradeCalculator.Grade temperament = FrogGradeCalculator.calculateGrade(genome.getGene(Gene.TEMPERAMENT));
        FrogGradeCalculator.Grade slimeYield = FrogGradeCalculator.calculateGrade(genome.getGene(Gene.SLIME_YIELD));

        tooltip.add(Component.literal("Worker Frogport").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.literal(" Throughput: ")
            .withStyle(ChatFormatting.GRAY)
            .append(Component.literal(FrogGradeCalculator.getGradeDescription(quickness)).withStyle(formattingFor(quickness))));
        tooltip.add(Component.literal(" Reach: ")
            .withStyle(ChatFormatting.GRAY)
            .append(Component.literal(getReachBlocks(genome) + " blocks").withStyle(formattingFor(reach))));
        tooltip.add(Component.literal(" Stability: ")
            .withStyle(ChatFormatting.GRAY)
            .append(Component.literal(FrogGradeCalculator.getGradeDescription(temperament)).withStyle(formattingFor(temperament))));
        tooltip.add(Component.literal(" Dispatch residue: ")
            .withStyle(ChatFormatting.GRAY)
            .append(Component.literal(FrogGradeCalculator.getGradeDescription(slimeYield)).withStyle(formattingFor(slimeYield))));
    }

    private static ChatFormatting formattingFor(FrogGradeCalculator.Grade grade) {
        return switch (grade) {
            case D -> ChatFormatting.RED;
            case C -> ChatFormatting.GOLD;
            case B -> ChatFormatting.YELLOW;
            case A -> ChatFormatting.GREEN;
            case S -> ChatFormatting.LIGHT_PURPLE;
        };
    }
}
