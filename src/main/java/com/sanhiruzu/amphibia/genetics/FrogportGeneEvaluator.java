package com.sanhiruzu.amphibia.genetics;

import com.sanhiruzu.amphibia.register.AmphibiaItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.util.RandomSource;

import java.util.Optional;

public class FrogportGeneEvaluator {

    // QUICKNESS -> output interval in ticks (lower = faster throughput)
    // Baseline 20t, scales down to 7t at S-grade
    public static int getOutputInterval(FrogGenome genome) {
        FrogGradeCalculator.Grade grade = FrogGradeCalculator.calculateGrade(genome.getGene(Gene.QUICKNESS));
        return switch (grade) {
            case D -> 20;
            case C -> 17;
            case B -> 14;
            case A -> 10;
            case S -> 7;
        };
    }

    // SLIME_YIELD -> optional bonus item per package output
    // D/C: no bonus; B: slime_ball (25%); A: slime_ball (60%); S: frog_slime (guaranteed)
    public static Optional<ItemStack> getSlimeBonus(FrogGenome genome, RandomSource random) {
        FrogGradeCalculator.Grade grade = FrogGradeCalculator.calculateGrade(genome.getGene(Gene.SLIME_YIELD));
        return switch (grade) {
            case D, C -> Optional.empty();
            case B -> random.nextFloat() < 0.25f
                ? Optional.of(new ItemStack(Items.SLIME_BALL))
                : Optional.empty();
            case A -> random.nextFloat() < 0.60f
                ? Optional.of(new ItemStack(Items.SLIME_BALL))
                : Optional.empty();
            case S -> Optional.of(new ItemStack(AmphibiaItems.FROG_SLIME.get()));
        };
    }
}
