package com.sanhiruzu.amphibia.genetics;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;

public record FrogDNA(Trait heatTolerance, Trait slimeViscosity, Trait growthRate, Trait health, Trait damage, List<String> mutations) {
    public FrogDNA(Trait heatTolerance, Trait slimeViscosity, Trait growthRate, Trait health, Trait damage) {
        this(heatTolerance, slimeViscosity, growthRate, health, damage, new ArrayList<>());
    }

    public static final Codec<FrogDNA> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Trait.CODEC.fieldOf("heat_tolerance").forGetter(FrogDNA::heatTolerance),
            Trait.CODEC.fieldOf("slime_viscosity").forGetter(FrogDNA::slimeViscosity),
            Trait.CODEC.fieldOf("growth_rate").forGetter(FrogDNA::growthRate),
            Trait.CODEC.fieldOf("health").forGetter(FrogDNA::health),
            Trait.CODEC.fieldOf("damage").forGetter(FrogDNA::damage),
            Codec.STRING.listOf().fieldOf("mutations").forGetter(FrogDNA::mutations)
    ).apply(instance, FrogDNA::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FrogDNA> STREAM_CODEC = StreamCodec.composite(
            Trait.STREAM_CODEC, FrogDNA::heatTolerance,
            Trait.STREAM_CODEC, FrogDNA::slimeViscosity,
            Trait.STREAM_CODEC, FrogDNA::growthRate,
            Trait.STREAM_CODEC, FrogDNA::health,
            Trait.STREAM_CODEC, FrogDNA::damage,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.collection(ArrayList::new)), FrogDNA::mutations,
            FrogDNA::new
    );

    public static FrogDNA createDefault() {
        java.util.Random rand = new java.util.Random();
        String[] possibleGenes = {"w", "A", "B", "C", "D", "E", "F", "G"};

        List<String> mutations = new ArrayList<>();
        // 0.1% chance for a mutation to spontaneously appear
        if (rand.nextDouble() < 0.001) {
            FrogMutation mutation = FrogMutation.ALL_MUTATIONS[rand.nextInt(FrogMutation.ALL_MUTATIONS.length)];
            mutations.add(mutation.id());
        }

        return new FrogDNA(
            new Trait(possibleGenes[rand.nextInt(possibleGenes.length)], possibleGenes[rand.nextInt(possibleGenes.length)]),
            new Trait(possibleGenes[rand.nextInt(possibleGenes.length)], possibleGenes[rand.nextInt(possibleGenes.length)]),
            new Trait(possibleGenes[rand.nextInt(possibleGenes.length)], possibleGenes[rand.nextInt(possibleGenes.length)]),
            new Trait(possibleGenes[rand.nextInt(possibleGenes.length)], possibleGenes[rand.nextInt(possibleGenes.length)]),
            new Trait(possibleGenes[rand.nextInt(possibleGenes.length)], possibleGenes[rand.nextInt(possibleGenes.length)]),
            mutations
        );
    }

    public static FrogDNA mix(FrogDNA parentA, FrogDNA parentB, RandomSource random) {
        return new FrogDNA(
                Trait.mix(parentA.heatTolerance(), parentB.heatTolerance(), random),
                Trait.mix(parentA.slimeViscosity(), parentB.slimeViscosity(), random),
                Trait.mix(parentA.growthRate(), parentB.growthRate(), random),
                Trait.mix(parentA.health(), parentB.health(), random),
                Trait.mix(parentA.damage(), parentB.damage(), random),
                new ArrayList<>()  // Mutations are not inherited during natural breeding
        );
    }

    public static FrogDNA mixWithMutationPreservation(FrogDNA parentA, FrogDNA parentB, RandomSource random) {
        List<String> inheritedMutations = new ArrayList<>();
        inheritedMutations.addAll(parentA.mutations());
        inheritedMutations.addAll(parentB.mutations());

        return new FrogDNA(
                Trait.mix(parentA.heatTolerance(), parentB.heatTolerance(), random),
                Trait.mix(parentA.slimeViscosity(), parentB.slimeViscosity(), random),
                Trait.mix(parentA.growthRate(), parentB.growthRate(), random),
                Trait.mix(parentA.health(), parentB.health(), random),
                Trait.mix(parentA.damage(), parentB.damage(), random),
                inheritedMutations  // Mutations are preserved through special breeding methods
        );
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
            return new Trait("w", "w"); // 'w' for wild-type
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
