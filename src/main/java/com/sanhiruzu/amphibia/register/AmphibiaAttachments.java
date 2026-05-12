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
            "frog_dna", () -> AttachmentType.builder(FrogDNA::createDefault).serialize(FrogDNA.CODEC).copyOnDeath().build()
    );

    public static final Supplier<AttachmentType<FrogDNA>> OFFSPRING_DNA = ATTACHMENTS.register(
            "offspring_dna", () -> AttachmentType.builder(FrogDNA::createDefault).serialize(FrogDNA.CODEC).build()
    );

    public static void register(IEventBus eventBus) {
        ATTACHMENTS.register(eventBus);
    }
}
