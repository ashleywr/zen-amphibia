package com.sanhiruzu.amphibia.genetics;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;

public record FrogDNA(Trait heatTolerance, Trait slimeViscosity, Trait growthRate) {
    public static final Codec<FrogDNA> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Trait.CODEC.fieldOf("heat_tolerance").forGetter(FrogDNA::heatTolerance),
            Trait.CODEC.fieldOf("slime_viscosity").forGetter(FrogDNA::slimeViscosity),
            Trait.CODEC.fieldOf("growth_rate").forGetter(FrogDNA::growthRate)
    ).apply(instance, FrogDNA::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FrogDNA> STREAM_CODEC = StreamCodec.composite(
            Trait.STREAM_CODEC, FrogDNA::heatTolerance,
            Trait.STREAM_CODEC, FrogDNA::slimeViscosity,
            Trait.STREAM_CODEC, FrogDNA::growthRate,
            FrogDNA::new
    );

    public static FrogDNA createDefault() {
        return new FrogDNA(Trait.defaultTrait(), Trait.defaultTrait(), Trait.defaultTrait());
    }

    public static FrogDNA mix(FrogDNA parentA, FrogDNA parentB, RandomSource random) {
        return new FrogDNA(
                Trait.mix(parentA.heatTolerance(), parentB.heatTolerance(), random),
                Trait.mix(parentA.slimeViscosity(), parentB.slimeViscosity(), random),
                Trait.mix(parentA.growthRate(), parentB.growthRate(), random)
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
