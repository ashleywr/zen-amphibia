package com.sanhiruzu.amphibia.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import com.mojang.serialization.JsonOps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Loads estivation configuration from datapacks.
 * JSON files at: data/amphibia/estivation/config.json
 */
public class EstivationConfigManager extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(EstivationConfigManager.class);
    private static EstivationConfig CURRENT_CONFIG = EstivationConfig.DEFAULT;

    public EstivationConfigManager() {
        super(new GsonBuilder().create(), "estivation");
    }

    @Override
    protected void apply(java.util.Map<ResourceLocation, com.google.gson.JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        EstivationConfig loadedConfig = EstivationConfig.DEFAULT;

        // Try to load from amphibia namespace
        ResourceLocation configPath = ResourceLocation.parse("amphibia:estivation/config");
        if (pObject.containsKey(configPath)) {
            try {
                com.google.gson.JsonElement json = pObject.get(configPath);
                var result = EstivationConfig.CODEC.decode(JsonOps.INSTANCE, json);
                if (result.isSuccess()) {
                    var pair = result.result().orElseThrow();
                    loadedConfig = pair.getFirst();
                } else {
                    LOGGER.warn("Failed to parse estivation config: {}", result.error());
                }
            } catch (Exception e) {
                LOGGER.error("Failed to load estivation config", e);
            }
        }

        CURRENT_CONFIG = loadedConfig;
        LOGGER.info("Loaded estivation config: enabled={}, maxTemp={}, minHumidity={}, cooldown={}",
            loadedConfig.enabled(), loadedConfig.maxTemperature(), loadedConfig.minHumidity(), loadedConfig.cooledownTicksAfterRevival());
    }

    /**
     * Get the current estivation configuration.
     */
    public static EstivationConfig getConfig() {
        return CURRENT_CONFIG;
    }
}
