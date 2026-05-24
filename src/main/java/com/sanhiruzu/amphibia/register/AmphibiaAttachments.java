package com.sanhiruzu.amphibia.register;

import com.sanhiruzu.amphibia.genetics.FrogGenome;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public class AmphibiaAttachments {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, "amphibia");

    public static final Supplier<AttachmentType<FrogGenome>> FROG_GENOME = ATTACHMENTS.register(
            "frog_genome", () -> AttachmentType.builder(FrogGenome::createDefault).serialize(FrogGenome.CODEC).sync(FrogGenome.STREAM_CODEC).copyOnDeath().build()
    );

    public static final Supplier<AttachmentType<Boolean>> FROG_GENETICS_APPLIED = ATTACHMENTS.register(
            "frog_genetics_applied", () -> AttachmentType.builder(() -> false).serialize(com.mojang.serialization.Codec.BOOL).build()
    );

    public static final Supplier<AttachmentType<FrogGenome>> OFFSPRING_GENOME = ATTACHMENTS.register(
            "offspring_genome", () -> AttachmentType.builder(FrogGenome::createDefault).serialize(FrogGenome.CODEC).sync(FrogGenome.STREAM_CODEC).build()
    );

    public static final Supplier<AttachmentType<Float>> FROG_SCALE = ATTACHMENTS.register(
            "frog_scale", () -> AttachmentType.builder(() -> 1.0f).serialize(com.mojang.serialization.Codec.FLOAT).sync(net.minecraft.network.codec.ByteBufCodecs.FLOAT).build()
    );

    public static final Supplier<AttachmentType<Boolean>> STUNTED_GROWTH = ATTACHMENTS.register(
            "stunted_growth", () -> AttachmentType.builder(() -> false).serialize(com.mojang.serialization.Codec.BOOL).build()
    );

    public static final Supplier<AttachmentType<Boolean>> ACCELERATED_GROWTH = ATTACHMENTS.register(
            "accelerated_growth", () -> AttachmentType.builder(() -> false).serialize(com.mojang.serialization.Codec.BOOL).build()
    );

    public static final Supplier<AttachmentType<Boolean>> FROG_DNA_OVERLAY_ENABLED = ATTACHMENTS.register(
            "frog_dna_overlay_enabled", () -> AttachmentType.builder(() -> false)
                .serialize(com.mojang.serialization.Codec.BOOL)
                .sync(net.minecraft.network.codec.ByteBufCodecs.BOOL)
                .copyOnDeath()
                .build()
    );

    public static void register(IEventBus eventBus) {
        ATTACHMENTS.register(eventBus);
    }
}
