package com.sanhiruzu.amphibia.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * Configurable settings for frog estivation (dormant state during harsh conditions).
 * Data-driven via datapacks for full modpack customization.
 */
public record EstivationConfig(
    boolean enabled,
    float maxTemperature,
    float minHumidity,
    long cooledownTicksAfterRevival
) {
    public static final EstivationConfig DEFAULT = new EstivationConfig(
        true,           // enabled
        30.0f,          // maxTemperature - trigger estivation above this (biome temp * 20, e.g. desert=40)
        25.0f,          // minHumidity - trigger estivation below this (dry biomes=20, wet biomes=80)
        6000L           // cooldownTicksAfterRevival - 5 minutes (6000 ticks)
    );

    public static final Codec<EstivationConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.BOOL.fieldOf("enabled").forGetter(EstivationConfig::enabled),
        Codec.FLOAT.fieldOf("max_temperature").forGetter(EstivationConfig::maxTemperature),
        Codec.FLOAT.fieldOf("min_humidity").forGetter(EstivationConfig::minHumidity),
        Codec.LONG.fieldOf("cooldown_ticks_after_revival").forGetter(EstivationConfig::cooledownTicksAfterRevival)
    ).apply(instance, EstivationConfig::new));

    /**
     * Check if a frog should estivate given conditions.
     */
    public boolean shouldEstivate(float temperature, float humidity) {
        if (!enabled) return false;
        return temperature > maxTemperature || humidity < minHumidity;
    }
}
