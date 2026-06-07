package com.sanhiruzu.amphibia;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SurvivalWorldgenDataTest {
    private static final Path PROJECT_ROOT = Path.of("").toAbsolutePath();

    @Test
    void cricketBiomeModifierAddsNaturalCricketSpawns() throws IOException {
        Path modifier = PROJECT_ROOT.resolve("src/main/resources/data/zen_amphibia/neoforge/biome_modifier/add_crickets.json");
        String json = Files.readString(modifier);

        assertTrue(json.contains("\"type\": \"neoforge:add_spawns\""));
        assertTrue(json.contains("\"biomes\": \"#zen_amphibia:has_crickets\""));
        assertTrue(json.contains("\"type\": \"zen_amphibia:cricket\""));
        assertTrue(json.contains("\"minCount\": 2"));
        assertTrue(json.contains("\"maxCount\": 5"));
    }

    @Test
    void cricketBiomeTagIncludesCommonSurvivalHabitats() throws IOException {
        Path tag = PROJECT_ROOT.resolve("src/main/resources/data/zen_amphibia/tags/worldgen/biome/has_crickets.json");
        String json = Files.readString(tag);

        assertTrue(json.contains("#c:is_temperate/overworld"));
        assertTrue(json.contains("#c:is_wet/overworld"));
        assertTrue(json.contains("#c:is_sparse_vegetation/overworld"));
    }
}
