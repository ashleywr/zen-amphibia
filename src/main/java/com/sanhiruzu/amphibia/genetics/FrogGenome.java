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

@SuppressWarnings("NullableProblems")
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
		Map<Gene, Trait> genes = new HashMap<>();

		for (Gene gene : Gene.values()) {
			genes.put(gene, Trait.defaultTrait());
		}

		return new FrogGenome(genes, new ArrayList<>());
	}

	public static FrogGenome createRandom(RandomSource random) {
		Map<Gene, Trait> genes = new HashMap<>();

		for (Gene gene : Gene.values()) {
			genes.put(gene, randomTrait(random));
		}

		return new FrogGenome(genes, new ArrayList<>());
	}

	private static Trait randomTrait(RandomSource rand) {
		return new Trait(selectWeightedAllele(rand), selectWeightedAllele(rand));
	}

	private static String selectWeightedAllele(RandomSource rand) {
		int roll = rand.nextInt(100);
		if (roll < 60) return "w";      // Wild type (60%)
		if (roll < 75) return "A";      // Mild variant (15%)
		if (roll < 90) return "B";      // Mild variant (15%)
		if (roll < 95) return "C";      // Rare (5%)
		return "D";                     // Rare (5%)
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
		long h1 = Math.abs((long) getGene(Gene.SIZE).geneA().hashCode() * 31L + getGene(Gene.SIZE).geneB().hashCode());
		long h2 = Math.abs(h1 * 1031L + 7L);
		float s1 = (int) (h1 * 739L % 100L) / 99.0f;
		float s2 = (int) (h2 * 739L % 100L) / 99.0f;
		// Average of two samples → triangular distribution biased toward the midpoint,
		// keeping most frogs near 1.1 and making extreme sizes rare.
		return 0.75f + (s1 + s2) * 0.5f * 0.75f;  // range [0.75, 1.50]
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
