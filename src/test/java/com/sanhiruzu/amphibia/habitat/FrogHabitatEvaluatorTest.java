package com.sanhiruzu.amphibia.habitat;

import com.sanhiruzu.amphibia.genetics.FrogHappinessConstants;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FrogHabitatEvaluatorTest {
    @Test
    void richHabitatScoresAboveBreedingThreshold() {
        FrogHabitatReading reading = FrogHabitatEvaluator.evaluateFacts(
            new FrogHabitatEvaluator.Facts(
                Map.of(
                    "water_coverage", 12,
                    "frog_plant", 8,
                    "damp_source", 3
                ),
                true,
                true,
                true,
                FrogHabitatEvaluator.Temperature.PLEASANT,
                2,
                0,
                80
            ),
            null,
            FrogHappinessConstants.BREEDING_HAPPINESS_THRESHOLD
        );

        assertTrue(reading.suitability() >= FrogHappinessConstants.BREEDING_HAPPINESS_THRESHOLD);
        assertTrue(reading.isOptimalBreedingHabitat());
        assertEquals(1.0f, reading.plantScore());
    }

    @Test
    void dryExposedHabitatScoresLowButDoesNotCrash() {
        FrogHabitatReading reading = FrogHabitatEvaluator.evaluateFacts(
            new FrogHabitatEvaluator.Facts(
                Map.of(),
                false,
                false,
                false,
                FrogHabitatEvaluator.Temperature.HOT,
                0,
                0,
                80
            ),
            null,
            FrogHappinessConstants.BREEDING_HAPPINESS_THRESHOLD
        );

        assertEquals(0.0f, reading.waterScore());
        assertTrue(reading.suitability() < FrogHappinessConstants.BREEDING_HAPPINESS_THRESHOLD);
    }

    @Test
    void crowdingLowersOtherwiseGoodHabitat() {
        FrogHabitatReading reading = FrogHabitatEvaluator.evaluateFacts(
            new FrogHabitatEvaluator.Facts(
                Map.of("water_coverage", 12, "frog_plant", 8),
                true,
                true,
                true,
                FrogHabitatEvaluator.Temperature.PLEASANT,
                12,
                0,
                80
            ),
            null,
            FrogHappinessConstants.BREEDING_HAPPINESS_THRESHOLD
        );

        assertTrue(reading.crowdingScore() < 1.0f);
        assertEquals(12, reading.nearbyLifecycleCount());
    }

    @Test
    void neutralFactsFromMissingSnapshotStayValid() {
        FrogHabitatReading reading = FrogHabitatEvaluator.evaluateFacts(
            FrogHabitatEvaluator.neutralFacts(),
            null,
            FrogHappinessConstants.BREEDING_HAPPINESS_THRESHOLD
        );

        assertEquals(0.0f, reading.waterScore());
        assertTrue(reading.suitability() >= 0.0f);
        assertTrue(reading.suitability() <= 1.0f);
    }
}
