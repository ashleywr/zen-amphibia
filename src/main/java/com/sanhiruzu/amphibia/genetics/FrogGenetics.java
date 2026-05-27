package com.sanhiruzu.amphibia.genetics;

import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrogGenetics {
	private FrogGenetics() {
		// Utility class
	}

	public static FrogGenome breed(FrogGenome parent1, FrogGenome parent2, RandomSource random) {
		Map<Gene, FrogGenome.Trait> offspringGenes = new HashMap<>();

		for (Gene gene : Gene.values()) {
			FrogGenome.Trait trait1 = parent1.getGene(gene);
			FrogGenome.Trait trait2 = parent2.getGene(gene);

			String alleleFromParent1 = random.nextBoolean() ? trait1.geneA() : trait1.geneB();
			String alleleFromParent2 = random.nextBoolean() ? trait2.geneA() : trait2.geneB();

			boolean parent1IsPrimary = random.nextBoolean();
			FrogGenome.Trait offspringTrait = parent1IsPrimary
				? new FrogGenome.Trait(alleleFromParent1, alleleFromParent2)
				: new FrogGenome.Trait(alleleFromParent2, alleleFromParent1);

			offspringGenes.put(gene, offspringTrait);
		}

		return new FrogGenome(offspringGenes, new ArrayList<>());
	}

	public static FrogGenome breedWithMutationPreservation(FrogGenome parent1, FrogGenome parent2, RandomSource random) {
		FrogGenome offspring = breed(parent1, parent2, random);

		List<String> inheritedMutations = new ArrayList<>();
		inheritedMutations.addAll(parent1.mutations());
		inheritedMutations.addAll(parent2.mutations());

		return new FrogGenome(offspring.genes(), inheritedMutations);
	}

	public static FrogGenome breed(FrogGenome p1, FrogGenome p2, RandomSource random, float happinessBonus) {
		FrogGenome base = breed(p1, p2, random);
		if (happinessBonus > 0 && random.nextFloat() < happinessBonus * FrogHappinessConstants.BREEDING_HAPPINESS_CHANCE_FACTOR) {
			Gene target = Gene.values()[random.nextInt(Gene.values().length)];
			FrogGenome.Trait t = base.getGene(target);
			boolean upgradeA = random.nextBoolean();
			String upgraded = upgradeAllele(upgradeA ? t.geneA() : t.geneB());
			FrogGenome.Trait newTrait = upgradeA
				? new FrogGenome.Trait(upgraded, t.geneB())
				: new FrogGenome.Trait(t.geneA(), upgraded);
			Map<Gene, FrogGenome.Trait> newGenes = new HashMap<>(base.genes());
			newGenes.put(target, newTrait);
			return new FrogGenome(newGenes, base.mutations());
		}
		return base;
	}

	private static String upgradeAllele(String a) {
		return switch (a) {
			case "w" -> "A";
			case "A" -> "B";
			case "B" -> "C";
			case "C" -> "D";
			default -> a;
		};
	}

	public FrogGradeCalculator.Grade calculateGeneGrade(Gene gene, FrogGenome genome) {
		return FrogGradeCalculator.calculateGrade(genome.getGene(gene));
	}
}
