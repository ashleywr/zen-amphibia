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
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, "zen_amphibia");

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

    public static final Supplier<AttachmentType<Float>> FROG_HAPPINESS = ATTACHMENTS.register(
            "frog_happiness", () -> AttachmentType.builder(() -> 0.0f).serialize(com.mojang.serialization.Codec.FLOAT).sync(net.minecraft.network.codec.ByteBufCodecs.FLOAT).build()
    );

    public static final Supplier<AttachmentType<Boolean>> STUNTED_GROWTH = ATTACHMENTS.register(
            "stunted_growth", () -> AttachmentType.builder(() -> false)
                .serialize(com.mojang.serialization.Codec.BOOL)
                .sync(net.minecraft.network.codec.ByteBufCodecs.BOOL)
                .build()
    );

    public static final Supplier<AttachmentType<Boolean>> ACCELERATED_GROWTH = ATTACHMENTS.register(
            "accelerated_growth", () -> AttachmentType.builder(() -> false)
                .serialize(com.mojang.serialization.Codec.BOOL)
                .sync(net.minecraft.network.codec.ByteBufCodecs.BOOL)
                .build()
    );

    public static final Supplier<AttachmentType<Boolean>> FROG_DNA_OVERLAY_ENABLED = ATTACHMENTS.register(
            "frog_dna_overlay_enabled", () -> AttachmentType.builder(() -> false)
                .serialize(com.mojang.serialization.Codec.BOOL)
                .sync(net.minecraft.network.codec.ByteBufCodecs.BOOL)
                .copyOnDeath()
                .build()
    );

    public static final Supplier<AttachmentType<String>> CURRENT_AI_STATUS = ATTACHMENTS.register(
            "current_ai_status", () -> AttachmentType.builder(() -> "idle")
                .serialize(com.mojang.serialization.Codec.STRING)
                .sync(net.minecraft.network.codec.ByteBufCodecs.STRING_UTF8)
                .build()
    );

    public static final Supplier<AttachmentType<Long>> BIRTH_GAME_TIME = ATTACHMENTS.register(
            "birth_game_time", () -> AttachmentType.builder(() -> 0L)
                .serialize(com.mojang.serialization.Codec.LONG)
                .sync(net.minecraft.network.codec.ByteBufCodecs.VAR_LONG)
                .build()
    );

    public static final Supplier<AttachmentType<Boolean>> ESTIVATING = ATTACHMENTS.register(
            "estivating", () -> AttachmentType.builder(() -> false)
                .serialize(com.mojang.serialization.Codec.BOOL)
                .build()
    );

    public static final Supplier<AttachmentType<Long>> LAST_REVIVAL_TICK = ATTACHMENTS.register(
            "last_revival_tick", () -> AttachmentType.builder(() -> 0L)
                .serialize(com.mojang.serialization.Codec.LONG)
                .build()
    );

    public static final Supplier<AttachmentType<Boolean>> SLIME_HARVEST_READY = ATTACHMENTS.register(
            "slime_harvest_ready", () -> AttachmentType.builder(() -> false)
                .serialize(com.mojang.serialization.Codec.BOOL)
                .sync(net.minecraft.network.codec.ByteBufCodecs.BOOL)
                .build()
    );

    public static final Supplier<AttachmentType<Float>> SLIME_READINESS_PROGRESS = ATTACHMENTS.register(
            "slime_readiness_progress", () -> AttachmentType.builder(() -> 0.0f)
                .serialize(com.mojang.serialization.Codec.FLOAT)
                .build()
    );

    public static void register(IEventBus eventBus) {
        ATTACHMENTS.register(eventBus);
    }
}
