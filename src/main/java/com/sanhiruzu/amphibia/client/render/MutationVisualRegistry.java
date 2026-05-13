package com.sanhiruzu.amphibia.client.render;

import net.minecraft.core.particles.ParticleTypes;

import java.util.HashMap;
import java.util.Map;

public class MutationVisualRegistry {
    private static final Map<String, MutationVisuals> REGISTRY = new HashMap<>();

    static {
        register("ender", new MutationVisuals(
            0xFF4B0082,  // Deep indigo/purple
            ParticleTypes.PORTAL,
            5,           // spawn particle every 5 ticks
            false
        ));

        register("carbon_compressor", new MutationVisuals(
            0xFFCC3300,  // Dark red/orange
            ParticleTypes.FLAME,
            10,
            false
        ));

        register("fermenter", new MutationVisuals(
            0xFFFFD700,  // Golden
            ParticleTypes.FALLING_HONEY,
            8,
            true         // has glow
        ));

        register("eccentric", new MutationVisuals(
            0xFF87CEEB,  // Sky blue
            ParticleTypes.CLOUD,
            6,
            false
        ));

        register("feline_instinct", new MutationVisuals(
            0xFF8B4789,  // Purple/shadow
            ParticleTypes.MYCELIUM,
            12,
            false
        ));
    }

    public static void register(String mutationId, MutationVisuals visuals) {
        REGISTRY.put(mutationId, visuals);
    }

    public static MutationVisuals get(String mutationId) {
        return REGISTRY.get(mutationId);
    }

    public static boolean has(String mutationId) {
        return REGISTRY.containsKey(mutationId);
    }
}
