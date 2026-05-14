package com.sanhiruzu.amphibia.api;

/**
 * Interface for external mods to integrate with Amphibia.
 * Implement this and register via AmphibiaPluginRegistry.
 */
public interface AmphibiaPlugin {
    /**
     * Called during mod initialization. Use this to register your integrations.
     */
    void onLoad();

    /**
     * Human-readable name of your plugin/mod integration.
     */
    String getPluginName();
}
