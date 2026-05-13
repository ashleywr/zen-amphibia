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
        String[] possibleGenes = {"w", "A", "B", "C", "D", "E", "F", "G"};

        Map<String, Trait> genes = new HashMap<>();
        genes.put(FrogGeneRegistry.HEAT_TOLERANCE, randomTrait(possibleGenes, rand));
        genes.put(FrogGeneRegistry.SLIME_VISCOSITY, randomTrait(possibleGenes, rand));
        genes.put(FrogGeneRegistry.GROWTH_RATE, randomTrait(possibleGenes, rand));
        genes.put(FrogGeneRegistry.HEALTH, randomTrait(possibleGenes, rand));
        genes.put(FrogGeneRegistry.DAMAGE, randomTrait(possibleGenes, rand));
        genes.put(FrogGeneRegistry.SIZE, randomTrait(possibleGenes, rand));

        List<String> mutations = new ArrayList<>();
        // 0.1% chance for a mutation to spontaneously appear
        if (rand.nextDouble() < 0.001) {
            FrogMutation mutation = FrogMutation.ALL_MUTATIONS[rand.nextInt(FrogMutation.ALL_MUTATIONS.length)];
            mutations.add(mutation.id());
        }

        return new FrogDNA(genes, mutations);
    }

    private static Trait randomTrait(String[] possibleGenes, java.util.Random rand) {
        return new Trait(
            possibleGenes[rand.nextInt(possibleGenes.length)],
            possibleGenes[rand.nextInt(possibleGenes.length)]
        );
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
