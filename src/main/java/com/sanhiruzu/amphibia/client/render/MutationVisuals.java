package com.sanhiruzu.amphibia.client.render;

import net.minecraft.core.particles.ParticleOptions;

public record MutationVisuals(
    int colorTint,
    ParticleOptions particleType,
    int particleInterval,
    boolean hasGlow
) {}
