package com.sanhiruzu.amphibia.genetics;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record FrogGenome(Map<Gene, Trait> genes, List<String> mutations) {
	public FrogGenome(Map<Gene, Trait> genes) {
		this(genes, new ArrayList<>());
	}

	public Trait getGene(Gene gene) {
		return genes.getOrDefault(gene, Trait.defaultTrait());
	}

	public static final Codec<FrogGenome> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.unboundedMap(Codec.STRING, Trait.CODEC)
				.fieldOf("genes")
				.xmap(
					stringMap -> {
						Map<Gene, Trait> geneMap = new HashMap<>();
						stringMap.forEach((key, trait) -> {
							Gene gene = Gene.fromString(key);
							if (gene != null) {
								geneMap.put(gene, trait);
							}
						});
						return geneMap;
					},
					geneMap -> {
						Map<String, Trait> stringMap = new HashMap<>();
						geneMap.forEach((gene, trait) -> stringMap.put(gene.displayName, trait));
						return stringMap;
					}
				)
				.forGetter(FrogGenome::genes),
			Codec.STRING.listOf().fieldOf("mutations").forGetter(FrogGenome::mutations)
	).apply(instance, FrogGenome::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, FrogGenome> STREAM_CODEC = new StreamCodec<RegistryFriendlyByteBuf, FrogGenome>() {
		@Override
		public FrogGenome decode(RegistryFriendlyByteBuf buf) {
			int geneCount = buf.readInt();
			Map<Gene, Trait> genes = new HashMap<>();
			for (int i = 0; i < geneCount; i++) {
				int geneIndex = buf.readInt();
				Gene gene = Gene.values()[geneIndex];
				Trait trait = Trait.STREAM_CODEC.decode(buf);
				genes.put(gene, trait);
			}
			List<String> mutations = ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.collection(ArrayList::new)).decode(buf);
			return new FrogGenome(genes, mutations);
		}

		@Override
		public void encode(RegistryFriendlyByteBuf buf, FrogGenome genome) {
			buf.writeInt(genome.genes.size());
			for (Map.Entry<Gene, Trait> entry : genome.genes.entrySet()) {
				buf.writeInt(entry.getKey().index);
				Trait.STREAM_CODEC.encode(buf, entry.getValue());
			}
			ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.collection(ArrayList::new)).encode(buf, new ArrayList<>(genome.mutations));
		}
	};

	public static FrogGenome createDefault() {
		java.util.Random rand = new java.util.Random();
		Map<Gene, Trait> genes = new HashMap<>();

		for (Gene gene : Gene.values()) {
			genes.put(gene, randomTrait(rand));
		}

		List<String> mutations = new ArrayList<>();
		if (rand.nextDouble() < 0.001) {
			FrogMutation mutation = FrogMutation.ALL_MUTATIONS[rand.nextInt(FrogMutation.ALL_MUTATIONS.length)];
			mutations.add(mutation.id());
		}

		return new FrogGenome(genes, mutations);
	}

	private static Trait randomTrait(java.util.Random rand) {
		return new Trait(selectWeightedAllele(rand), selectWeightedAllele(rand));
	}

	private static String selectWeightedAllele(java.util.Random rand) {
		int roll = rand.nextInt(100);
		if (roll < 60) return "N";
		if (roll < 75) return "A";
		if (roll < 90) return "B";
		if (roll < 95) return "C";
		return "D";
	}

	public int getColor() {
		int hashRed = Math.abs(getGene(Gene.HEAT_TOLERANCE).hashCode());
		int hashGreen = Math.abs(getGene(Gene.SLIME_VISCOSITY).hashCode());
		int hashBlue = Math.abs(getGene(Gene.GROWTH_RATE).hashCode());

		int r = 100 + (hashRed % 155);
		int g = 100 + (hashGreen % 155);
		int b = 100 + (hashBlue % 155);

		return (255 << 24) | (r << 16) | (g << 8) | b;
	}

	public float getScale() {
		int hashScale = Math.abs(getGene(Gene.SIZE).geneA().hashCode() * 31 + getGene(Gene.SIZE).geneB().hashCode());
		return 0.5f + ((hashScale * 739) % 100) / 50.0f;
	}

	public record Trait(String geneA, String geneB) {
		public static final Codec<Trait> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.STRING.fieldOf("gene_a").forGetter(Trait::geneA),
				Codec.STRING.fieldOf("gene_b").forGetter(Trait::geneB)
		).apply(instance, Trait::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, Trait> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.STRING_UTF8, Trait::geneA,
				ByteBufCodecs.STRING_UTF8, Trait::geneB,
				Trait::new
		);

		public static Trait defaultTrait() {
			return new Trait("w", "w");
		}

		public static Trait mix(Trait a, Trait b, RandomSource random) {
			String geneFromA = random.nextBoolean() ? a.geneA() : a.geneB();
			String geneFromB = random.nextBoolean() ? b.geneA() : b.geneB();
			return new Trait(geneFromA, geneFromB);
		}

		public boolean isDominantExpressing(String dominantGene) {
			return geneA.equals(dominantGene) || geneB.equals(dominantGene);
		}
	}
}
