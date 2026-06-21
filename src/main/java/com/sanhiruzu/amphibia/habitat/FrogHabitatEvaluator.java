package com.sanhiruzu.amphibia.habitat;

import com.sanhiruzu.amphibia.AmphibiaConfig;
import com.sanhiruzu.amphibia.genetics.FrogGradeCalculator;
import com.sanhiruzu.amphibia.genetics.FrogHappinessConstants;
import com.sanhiruzu.amphibia.genetics.FrogGenome;
import com.sanhiruzu.amphibia.genetics.Gene;
import com.sanhiruzu.amphibia.register.AmphibiaAttachments;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.level.Level;

import java.util.Map;

public final class FrogHabitatEvaluator {
    public static final int DEFAULT_SCAN_RADIUS = 8;

    private FrogHabitatEvaluator() {
    }

    public enum Temperature {
        COLD,
        COOL,
        PLEASANT,
        WARM,
        HOT
    }

    public record Facts(
        Map<String, Integer> signalCounts,
        boolean covered,
        boolean indoorsLike,
        boolean enclosed,
        Temperature temperature,
        int nearbyFrogs,
        int nearbyTadpoles,
        int approximateVolume
    ) {
        public Facts {
            signalCounts = signalCounts == null ? Map.of() : Map.copyOf(signalCounts);
            temperature = temperature == null ? Temperature.PLEASANT : temperature;
            nearbyFrogs = Math.max(0, nearbyFrogs);
            nearbyTadpoles = Math.max(0, nearbyTadpoles);
            approximateVolume = Math.max(1, approximateVolume);
        }
    }

    public static Facts neutralFacts() {
        return new Facts(Map.of(), false, false, false, Temperature.PLEASANT, 0, 0, 1);
    }

    public static FrogHabitatReading evaluate(Level level, BlockPos pos, Frog frog) {
        FrogGenome genome = frog == null ? null : frog.getData(AmphibiaAttachments.FROG_GENOME);
        return evaluateFacts(
            AtelierEnvironmentBridge.factsAt(level, pos, DEFAULT_SCAN_RADIUS),
            genome,
            (float) AmphibiaConfig.OPTIMAL_BREEDING_HABITAT_THRESHOLD.getAsDouble()
        );
    }

    public static FrogHabitatReading evaluateFacts(Facts facts, FrogGenome genome, float breedingThreshold) {
        float waterScore = computeWaterScore(facts);
        float plantScore = computePlantScore(facts);
        float coverScore = computeCoverScore(facts);
        float climateScore = computeClimateScore(facts, genome, waterScore);
        int lifecycleCount = facts.nearbyFrogs() + facts.nearbyTadpoles();
        float crowdingScore = computeCrowdingScore(lifecycleCount, facts.approximateVolume());
        float sizeScore = computeSizeScore(facts.approximateVolume());

        float raw = (coverScore * FrogHappinessConstants.HABITAT_COVER_WEIGHT)
            + (waterScore * FrogHappinessConstants.WATER_RATIO_WEIGHT)
            + (climateScore * FrogHappinessConstants.CLIMATE_SCORE_WEIGHT)
            + (plantScore * FrogHappinessConstants.PLANT_SCORE_WEIGHT)
            + (sizeScore * FrogHappinessConstants.SIZE_SCORE_WEIGHT);

        return new FrogHabitatReading(
            raw * crowdingScore,
            waterScore,
            plantScore,
            coverScore,
            climateScore,
            crowdingScore,
            lifecycleCount,
            breedingThreshold
        );
    }

    private static float computeWaterScore(Facts facts) {
        int water = count(facts, "water_coverage")
            + count(facts, "water")
            + count(facts, "damp_source")
            + count(facts, "humid_source");
        float denominator = Math.max(4f, facts.approximateVolume() * FrogHappinessConstants.WATER_RATIO_DENOMINATOR_FACTOR);
        return Math.min(1f, water / denominator);
    }

    private static float computePlantScore(Facts facts) {
        int plants = count(facts, "frog_plant")
            + count(facts, "plant")
            + count(facts, "foliage")
            + count(facts, "crop");
        return Math.min(1f, plants / (float) FrogHappinessConstants.PLANT_FULL_SCORE_COUNT);
    }

    private static float computeCoverScore(Facts facts) {
        float score = 0f;
        if (facts.covered()) score += 0.35f;
        if (facts.indoorsLike()) score += 0.35f;
        if (facts.enclosed()) score += 0.30f;
        return score;
    }

    private static float computeClimateScore(Facts facts, FrogGenome genome, float waterScore) {
        float climate = switch (facts.temperature()) {
            case COLD -> 0.15f;
            case COOL -> 0.45f;
            case PLEASANT -> 0.9f;
            case WARM -> 0.75f;
            case HOT -> 0.25f;
        };

        float humidity = Math.min(1f, waterScore + count(facts, "damp_source") * 0.05f + count(facts, "humid_source") * 0.05f);
        if (genome == null) {
            return (climate + humidity) / 2f;
        }

        FrogGradeCalculator.Grade heatGrade = FrogGradeCalculator.calculateGrade(genome.getGene(Gene.HEAT_TOLERANCE));
        FrogGradeCalculator.Grade humidGrade = FrogGradeCalculator.calculateGrade(genome.getGene(Gene.HUMIDITY_TOLERANCE));
        float tempMatch = rangeMatch(climate, FrogGradeCalculator.getPreferredTemperatureRange(heatGrade));
        float humidMatch = rangeMatch(humidity, FrogGradeCalculator.getPreferredHumidityRange(humidGrade));
        return (tempMatch + humidMatch) / 2f;
    }

    private static float computeCrowdingScore(int lifecycleCount, int approximateVolume) {
        int capacity = Math.max(1, approximateVolume / FrogHappinessConstants.OVERCROWDING_FROG_CAPACITY_PER_VOLUME);
        if (lifecycleCount <= capacity) return 1f;
        float excess = (float) (lifecycleCount - capacity) / capacity;
        return Math.max(1f - FrogHappinessConstants.OVERCROWDING_PENALTY_MAX, 1f - excess);
    }

    private static float computeSizeScore(int approximateVolume) {
        if (approximateVolume < 8) return 0.2f;
        if (approximateVolume < 30) return 0.4f;
        if (approximateVolume < 60) return 0.6f;
        if (approximateVolume < 120) return 0.8f;
        return 1f;
    }

    private static float rangeMatch(float value, float[] range) {
        if (value >= range[0] && value <= range[1]) return 1f;
        float distance = Math.min(Math.abs(value - range[0]), Math.abs(value - range[1]));
        return Math.max(0f, 1f - distance * 4f);
    }

    private static int count(Facts facts, String signal) {
        return facts.signalCounts().getOrDefault(signal, 0);
    }
}
