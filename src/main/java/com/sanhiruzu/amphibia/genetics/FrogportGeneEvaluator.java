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
    public static final double BASE_ANIMATION_CHASE_SPEED = 0.10;

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

    public static int getThroughputPercent(FrogGenome genome) {
        double speed = getAnimationChaseSpeed(genome);
        return (int) Math.round(((speed / BASE_ANIMATION_CHASE_SPEED) - 1.0) * 100.0);
    }

    // TONGUE_LENGTH extends Create's configured package port placement range.
    public static int getReachBonusBlocks(FrogGenome genome) {
        FrogGradeCalculator.Grade reach = FrogGradeCalculator.calculateGrade(genome.getGene(Gene.TONGUE_LENGTH));
        return switch (reach) {
            case D, C -> 0;
            case B -> 1;
            case A -> 2;
            case S -> 3;
        };
    }

    public static int getEffectivePackageRange(FrogGenome genome, int baseRange) {
        return baseRange + getReachBonusBlocks(genome);
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

    public static float getDispatchResidueChance(FrogGenome genome) {
        FrogGradeCalculator.Grade slimeYield = FrogGradeCalculator.calculateGrade(genome.getGene(Gene.SLIME_YIELD));
        return switch (slimeYield) {
            case D, C -> 0.0f;
            case B -> 0.20f;
            case A -> 0.45f;
            case S -> 1.0f;
        };
    }

    public static float getExpectedResidueChance(FrogGenome genome) {
        return getResidueReliability(genome) * getDispatchResidueChance(genome);
    }

    // SLIME_YIELD provides package-work residue, but only when a package is dispatched.
    public static Optional<ItemStack> getDispatchResidue(FrogGenome genome, RandomSource random) {
        if (random.nextFloat() > getResidueReliability(genome)) {
            return Optional.empty();
        }

        FrogGradeCalculator.Grade slimeYield = FrogGradeCalculator.calculateGrade(genome.getGene(Gene.SLIME_YIELD));
        if (random.nextFloat() >= getDispatchResidueChance(genome)) {
            return Optional.empty();
        }

        int count = slimeYield == FrogGradeCalculator.Grade.S && random.nextFloat() < 0.20f ? 2 : 1;
        return Optional.of(new ItemStack(AmphibiaItems.FROG_SLIME.get(), count));
    }

    public static void addWorkerTooltip(List<Component> tooltip, FrogGenome genome) {
        addWorkerSummaryTooltip(tooltip, genome, true);
    }

    public static void addWorkerSummaryTooltip(List<Component> tooltip, FrogGenome genome, boolean includeHeader) {
        FrogGradeCalculator.Grade quickness = FrogGradeCalculator.calculateGrade(genome.getGene(Gene.QUICKNESS));
        FrogGradeCalculator.Grade reach = FrogGradeCalculator.calculateGrade(genome.getGene(Gene.TONGUE_LENGTH));
        FrogGradeCalculator.Grade temperament = FrogGradeCalculator.calculateGrade(genome.getGene(Gene.TEMPERAMENT));
        FrogGradeCalculator.Grade slimeYield = FrogGradeCalculator.calculateGrade(genome.getGene(Gene.SLIME_YIELD));

        if (includeHeader) {
            tooltip.add(Component.literal("Worker Frogport").withStyle(ChatFormatting.GOLD));
        }
        tooltip.add(Component.literal(" Work rate: ")
            .withStyle(ChatFormatting.GRAY)
            .append(Component.literal("+" + getThroughputPercent(genome) + "%").withStyle(formattingFor(quickness))));
        tooltip.add(Component.literal(" Reach: ")
            .withStyle(ChatFormatting.GRAY)
            .append(Component.literal("+" + getReachBonusBlocks(genome) + " blocks").withStyle(formattingFor(reach))));
        tooltip.add(Component.literal(" Reliability: ")
            .withStyle(ChatFormatting.GRAY)
            .append(Component.literal(percent(getResidueReliability(genome))).withStyle(formattingFor(temperament))));
        tooltip.add(Component.literal(" Slime residue: ")
            .withStyle(ChatFormatting.GRAY)
            .append(Component.literal(percent(getExpectedResidueChance(genome)) + " per dispatch").withStyle(formattingFor(slimeYield))));
    }

    private static String percent(float value) {
        return Math.round(value * 100.0f) + "%";
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
