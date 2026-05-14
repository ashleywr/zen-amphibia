package com.sanhiruzu.amphibia.api;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Registry for Amphibia plugins. External mods can register their integrations here.
 */
public class AmphibiaPluginRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger(AmphibiaPluginRegistry.class);
    private static final List<AmphibiaPlugin> PLUGINS = new ArrayList<>();

    /**
     * Register a plugin to be loaded during mod initialization.
     */
    public static void register(AmphibiaPlugin plugin) {
        PLUGINS.add(plugin);
        LOGGER.info("Registered Amphibia plugin: {}", plugin.getPluginName());
    }

    /**
     * Get all registered plugins.
     */
    public static List<AmphibiaPlugin> getPlugins() {
        return new ArrayList<>(PLUGINS);
    }

    /**
     * Initialize all registered plugins.
     */
    public static void loadAll() {
        LOGGER.info("Loading {} Amphibia plugin(s)", PLUGINS.size());
        for (AmphibiaPlugin plugin : PLUGINS) {
            try {
                plugin.onLoad();
                LOGGER.info("Loaded plugin: {}", plugin.getPluginName());
            } catch (Exception e) {
                LOGGER.error("Failed to load plugin: {}", plugin.getPluginName(), e);
            }
        }
    }
}
