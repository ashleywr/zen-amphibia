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

	public FrogGradeCalculator.Grade calculateGeneGrade(Gene gene, FrogGenome genome) {
		return FrogGradeCalculator.calculateGrade(genome.getGene(gene));
	}
}
