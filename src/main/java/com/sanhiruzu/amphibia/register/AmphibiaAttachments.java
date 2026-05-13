package com.sanhiruzu.amphibia.register;

import com.sanhiruzu.amphibia.genetics.FrogDNA;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class AmphibiaAttachments {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, "amphibia");

    public static final Supplier<AttachmentType<FrogDNA>> FROG_DNA = ATTACHMENTS.register(
            "frog_dna", () -> AttachmentType.builder(FrogDNA::createDefault).serialize(FrogDNA.CODEC).sync(FrogDNA.STREAM_CODEC).copyOnDeath().build()
    );

    public static final Supplier<AttachmentType<FrogDNA>> OFFSPRING_DNA = ATTACHMENTS.register(
            "offspring_dna", () -> AttachmentType.builder(FrogDNA::createDefault).serialize(FrogDNA.CODEC).sync(FrogDNA.STREAM_CODEC).build()
    );

    public static final Supplier<AttachmentType<Float>> FROG_SCALE = ATTACHMENTS.register(
            "frog_scale", () -> AttachmentType.builder(() -> 1.0f).serialize(com.mojang.serialization.Codec.FLOAT).sync(net.minecraft.network.codec.ByteBufCodecs.FLOAT).build()
    );

    public static void register(IEventBus eventBus) {
        ATTACHMENTS.register(eventBus);
    }
}
