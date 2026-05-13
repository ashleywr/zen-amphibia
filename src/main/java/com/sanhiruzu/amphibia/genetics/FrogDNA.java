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

public record FrogDNA(Map<String, Trait> genes, List<String> mutations) {
    public FrogDNA(Map<String, Trait> genes) {
        this(genes, new ArrayList<>());
    }

    public Trait getGene(String geneId) {
        return genes.getOrDefault(geneId, Trait.defaultTrait());
    }

    public static final Codec<FrogDNA> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(Codec.STRING, Trait.CODEC).fieldOf("genes").forGetter(FrogDNA::genes),
            Codec.STRING.listOf().fieldOf("mutations").forGetter(FrogDNA::mutations)
    ).apply(instance, FrogDNA::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FrogDNA> STREAM_CODEC = new StreamCodec<RegistryFriendlyByteBuf, FrogDNA>() {
        @Override
        public FrogDNA decode(RegistryFriendlyByteBuf buf) {
            int geneCount = buf.readInt();
            Map<String, Trait> genes = new HashMap<>();
            for (int i = 0; i < geneCount; i++) {
                String geneId = ByteBufCodecs.STRING_UTF8.decode(buf);
                Trait trait = Trait.STREAM_CODEC.decode(buf);
                genes.put(geneId, trait);
            }
            java.util.List<String> mutations = ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.collection(ArrayList::new)).decode(buf);
            return new FrogDNA(genes, mutations);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, FrogDNA dna) {
            buf.writeInt(dna.genes.size());
            for (Map.Entry<String, Trait> entry : dna.genes.entrySet()) {
                ByteBufCodecs.STRING_UTF8.encode(buf, entry.getKey());
                Trait.STREAM_CODEC.encode(buf, entry.getValue());
            }
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.collection(ArrayList::new)).encode(buf, new ArrayList<>(dna.mutations));
        }
    };

    public static FrogDNA createDefault() {
        java.util.Random rand = new java.util.Random();

        Map<String, Trait> genes = new HashMap<>();
        genes.put(FrogGeneRegistry.HEAT_TOLERANCE, randomTrait(rand));
        genes.put(FrogGeneRegistry.SLIME_VISCOSITY, randomTrait(rand));
        genes.put(FrogGeneRegistry.GROWTH_RATE, randomTrait(rand));
        genes.put(FrogGeneRegistry.HEALTH, randomTrait(rand));
        genes.put(FrogGeneRegistry.DAMAGE, randomTrait(rand));
        genes.put(FrogGeneRegistry.SIZE, randomTrait(rand));

        List<String> mutations = new ArrayList<>();
        if (rand.nextDouble() < 0.001) {
            FrogMutation mutation = FrogMutation.ALL_MUTATIONS[rand.nextInt(FrogMutation.ALL_MUTATIONS.length)];
            mutations.add(mutation.id());
        }

        return new FrogDNA(genes, mutations);
    }

    private static Trait randomTrait(java.util.Random rand) {
        return new Trait(selectWeightedAllele(rand), selectWeightedAllele(rand));
    }

    private static String selectWeightedAllele(java.util.Random rand) {
        // Weighted allele pool for proper Mendelian inheritance
        // 60% normal (N), 15% each of mild variants (A,B), 5% each rare (C,D)
        int roll = rand.nextInt(100);
        if (roll < 60) return "N";      // Normal (60%)
        if (roll < 75) return "A";      // Mild variant (15%)
        if (roll < 90) return "B";      // Mild variant (15%)
        if (roll < 95) return "C";      // Rare (5%)
        return "D";                     // Rare (5%)
    }

    public static FrogDNA mix(FrogDNA parentA, FrogDNA parentB, RandomSource random) {
        Map<String, Trait> mixedGenes = new HashMap<>();

        mixedGenes.put(FrogGeneRegistry.HEAT_TOLERANCE,
            Trait.mix(parentA.getGene(FrogGeneRegistry.HEAT_TOLERANCE), parentB.getGene(FrogGeneRegistry.HEAT_TOLERANCE), random));
        mixedGenes.put(FrogGeneRegistry.SLIME_VISCOSITY,
            Trait.mix(parentA.getGene(FrogGeneRegistry.SLIME_VISCOSITY), parentB.getGene(FrogGeneRegistry.SLIME_VISCOSITY), random));
        mixedGenes.put(FrogGeneRegistry.GROWTH_RATE,
            Trait.mix(parentA.getGene(FrogGeneRegistry.GROWTH_RATE), parentB.getGene(FrogGeneRegistry.GROWTH_RATE), random));
        mixedGenes.put(FrogGeneRegistry.HEALTH,
            Trait.mix(parentA.getGene(FrogGeneRegistry.HEALTH), parentB.getGene(FrogGeneRegistry.HEALTH), random));
        mixedGenes.put(FrogGeneRegistry.DAMAGE,
            Trait.mix(parentA.getGene(FrogGeneRegistry.DAMAGE), parentB.getGene(FrogGeneRegistry.DAMAGE), random));
        mixedGenes.put(FrogGeneRegistry.SIZE,
            Trait.mix(parentA.getGene(FrogGeneRegistry.SIZE), parentB.getGene(FrogGeneRegistry.SIZE), random));

        return new FrogDNA(mixedGenes, new ArrayList<>());
    }

    public static FrogDNA mixWithMutationPreservation(FrogDNA parentA, FrogDNA parentB, RandomSource random) {
        Map<String, Trait> mixedGenes = new HashMap<>();

        mixedGenes.put(FrogGeneRegistry.HEAT_TOLERANCE,
            Trait.mix(parentA.getGene(FrogGeneRegistry.HEAT_TOLERANCE), parentB.getGene(FrogGeneRegistry.HEAT_TOLERANCE), random));
        mixedGenes.put(FrogGeneRegistry.SLIME_VISCOSITY,
            Trait.mix(parentA.getGene(FrogGeneRegistry.SLIME_VISCOSITY), parentB.getGene(FrogGeneRegistry.SLIME_VISCOSITY), random));
        mixedGenes.put(FrogGeneRegistry.GROWTH_RATE,
            Trait.mix(parentA.getGene(FrogGeneRegistry.GROWTH_RATE), parentB.getGene(FrogGeneRegistry.GROWTH_RATE), random));
        mixedGenes.put(FrogGeneRegistry.HEALTH,
            Trait.mix(parentA.getGene(FrogGeneRegistry.HEALTH), parentB.getGene(FrogGeneRegistry.HEALTH), random));
        mixedGenes.put(FrogGeneRegistry.DAMAGE,
            Trait.mix(parentA.getGene(FrogGeneRegistry.DAMAGE), parentB.getGene(FrogGeneRegistry.DAMAGE), random));
        mixedGenes.put(FrogGeneRegistry.SIZE,
            Trait.mix(parentA.getGene(FrogGeneRegistry.SIZE), parentB.getGene(FrogGeneRegistry.SIZE), random));

        List<String> inheritedMutations = new ArrayList<>();
        inheritedMutations.addAll(parentA.mutations);
        inheritedMutations.addAll(parentB.mutations);

        return new FrogDNA(mixedGenes, inheritedMutations);
    }

    public int getColor() {
        int hashRed = Math.abs(getGene(FrogGeneRegistry.HEAT_TOLERANCE).hashCode());
        int hashGreen = Math.abs(getGene(FrogGeneRegistry.SLIME_VISCOSITY).hashCode());
        int hashBlue = Math.abs(getGene(FrogGeneRegistry.GROWTH_RATE).hashCode());

        int r = 100 + (hashRed % 155);
        int g = 100 + (hashGreen % 155);
        int b = 100 + (hashBlue % 155);

        return (255 << 24) | (r << 16) | (g << 8) | b;
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
