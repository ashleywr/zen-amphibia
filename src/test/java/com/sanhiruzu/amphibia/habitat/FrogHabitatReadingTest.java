package com.sanhiruzu.amphibia.habitat;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FrogHabitatReadingTest {
    @Test
    void clampsAllScoresToUnitRange() {
        FrogHabitatReading reading = new FrogHabitatReading(
            1.4f,
            -0.5f,
            0.25f,
            2.0f,
            -1.0f,
            0.75f,
            9,
            0.5f
        );

        assertEquals(1.0f, reading.suitability());
        assertEquals(0.0f, reading.waterScore());
        assertEquals(0.25f, reading.plantScore());
        assertEquals(1.0f, reading.coverScore());
        assertEquals(0.0f, reading.climateScore());
        assertEquals(0.75f, reading.crowdingScore());
    }

    @Test
    void optimalBreedingUsesSuppliedThreshold() {
        FrogHabitatReading low = FrogHabitatReading.neutral();
        FrogHabitatReading good = new FrogHabitatReading(0.62f, 1f, 1f, 1f, 1f, 1f, 8, 0.5f);

        assertFalse(low.isOptimalBreedingHabitat());
        assertTrue(good.isOptimalBreedingHabitat());
    }
}
