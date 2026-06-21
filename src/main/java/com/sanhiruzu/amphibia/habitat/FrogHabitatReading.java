package com.sanhiruzu.amphibia.habitat;

import com.sanhiruzu.amphibia.genetics.FrogHappinessConstants;

public record FrogHabitatReading(
    float suitability,
    float waterScore,
    float plantScore,
    float coverScore,
    float climateScore,
    float crowdingScore,
    int nearbyLifecycleCount,
    float breedingThreshold
) {
    public FrogHabitatReading {
        suitability = clamp01(suitability);
        waterScore = clamp01(waterScore);
        plantScore = clamp01(plantScore);
        coverScore = clamp01(coverScore);
        climateScore = clamp01(climateScore);
        crowdingScore = clamp01(crowdingScore);
        nearbyLifecycleCount = Math.max(0, nearbyLifecycleCount);
        breedingThreshold = clamp01(breedingThreshold);
    }

    public static FrogHabitatReading neutral() {
        return new FrogHabitatReading(
            0f,
            0f,
            0f,
            0f,
            0f,
            1f,
            0,
            FrogHappinessConstants.BREEDING_HAPPINESS_THRESHOLD
        );
    }

    public boolean isOptimalBreedingHabitat() {
        return suitability >= breedingThreshold;
    }

    private static float clamp01(float value) {
        if (Float.isNaN(value)) {
            return 0f;
        }
        return Math.max(0f, Math.min(1f, value));
    }
}
