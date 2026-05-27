package com.sanhiruzu.amphibia.genetics;

import com.sanhiruzu.amphibia.register.AmphibiaAttachments;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.Collections;
import java.util.Map;

@EventBusSubscriber(modid = "zen_amphibia")
public class TerrariumHappinessHandler {
    private static final int HAPPINESS_UPDATE_INTERVAL = FrogHappinessConstants.HAPPINESS_UPDATE_INTERVAL;
    private static final float HAPPINESS_DECAY_PER_INTERVAL = FrogHappinessConstants.HAPPINESS_DECAY_PER_INTERVAL;

    // Baseline terrarium temperature before any warming/cooling blocks (0 = freezing, 1 = max heat).
    private static final float TEMP_BASELINE = 0.4f;
    // Each net warming/cooling block shifts temperature by this amount (capped at ±0.4).
    private static final float TEMP_PER_BLOCK = 1f / 16f;
    // Baseline humidity before water or humid sources.
    private static final float HUMID_BASELINE = 0.15f;

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        long gameTime = event.getServer().getTickCount();
        if (gameTime % HAPPINESS_UPDATE_INTERVAL != 0) return;

        for (ServerLevel level : event.getServer().getAllLevels()) {
            for (Frog frog : level.getEntities(EntityType.FROG, Frog::isAlive)) {
                updateFrogHappiness(frog, level);
            }
        }
    }

    private static void updateFrogHappiness(Frog frog, Level level) {
        if (level.isClientSide) return;

        float newHappiness = computeHappiness(frog, level);

        if (newHappiness > 0) {
            frog.setData(AmphibiaAttachments.FROG_HAPPINESS, newHappiness);
        } else {
            float current = frog.getData(AmphibiaAttachments.FROG_HAPPINESS);
            frog.setData(AmphibiaAttachments.FROG_HAPPINESS, Math.max(0, current - HAPPINESS_DECAY_PER_INTERVAL));
        }
    }

    private static float computeHappiness(Frog frog, Level level) {
        try {
            Object zoneData = getZoneAtPosition(level, frog.blockPosition());
            if (zoneData == null) return 0;

            String zoneTypeId = getZoneTypeId(zoneData);
            if (zoneTypeId == null || !zoneTypeId.contains("terrarium")) return 0;

            Map<String, ?> signals = getSignalCounts(zoneData);
            int volume = getVolume(zoneData);
            float zoneQuality = getZoneQuality(zoneData);

            float waterRatio = computeWaterRatio(signals, volume);
            float plantScore = computePlantScore(signals);
            float sizeScore = computeSizeScore(volume);
            float climateScore = computeClimateScore(frog, signals, waterRatio);

            return (zoneQuality * FrogHappinessConstants.ZONE_QUALITY_WEIGHT)
                    + (waterRatio  * FrogHappinessConstants.WATER_RATIO_WEIGHT)
                    + (plantScore  * FrogHappinessConstants.PLANT_SCORE_WEIGHT)
                    + (sizeScore   * FrogHappinessConstants.SIZE_SCORE_WEIGHT)
                    + (climateScore * FrogHappinessConstants.CLIMATE_SCORE_WEIGHT);
        } catch (Exception e) {
            return 0;
        }
    }

    // --- Zone data accessors (reflection, Atelier is compileOnly) ---

    private static Object getZoneAtPosition(Level level, BlockPos pos) {
        try {
            Class<?> spaceQueryClass = Class.forName("com.sanhiruzu.atelier.space.SpaceQuery");
            var method = spaceQueryClass.getMethod("getRoomAt", Level.class, BlockPos.class);
            return method.invoke(null, level, pos);
        } catch (Exception e) {
            return null;
        }
    }

    private static String getZoneTypeId(Object zoneData) {
        try {
            Object result = zoneData.getClass().getMethod("getZoneTypeId").invoke(zoneData);
            return result != null ? result.toString() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private static float getZoneQuality(Object zoneData) {
        try {
            Number result = (Number) zoneData.getClass().getMethod("getQuality").invoke(zoneData);
            return result != null ? result.floatValue() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, ?> getSignalCounts(Object zoneData) {
        try {
            return (Map<String, ?>) zoneData.getClass().getMethod("getSignalCounts").invoke(zoneData);
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    private static int getVolume(Object zoneData) {
        try {
            Number result = (Number) zoneData.getClass().getMethod("getVolume").invoke(zoneData);
            return result != null ? result.intValue() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    // --- Score computations ---

    private static int count(Map<String, ?> signals, String key) {
        Object val = signals.get(key);
        return val instanceof Number n ? n.intValue() : 0;
    }

    private static float computeWaterRatio(Map<String, ?> signals, int volume) {
        float waterCount = count(signals, "water_coverage");
        float denominator = volume * FrogHappinessConstants.WATER_RATIO_DENOMINATOR_FACTOR;
        return denominator > 0 ? Math.min(1.0f, waterCount / denominator) : 0;
    }

    private static float computePlantScore(Map<String, ?> signals) {
        return Math.min(1.0f, count(signals, "frog_plant") / (float) FrogHappinessConstants.PLANT_FULL_SCORE_COUNT);
    }

    private static float computeSizeScore(int volume) {
        if (volume < 8) return 0.2f;
        if (volume < 30) return 0.4f;
        if (volume < 60) return 0.6f;
        if (volume < 120) return 0.8f;
        return 1.0f;
    }

    private static float computeClimateScore(Frog frog, Map<String, ?> signals, float waterRatio) {
        FrogGenome genome = frog.getData(AmphibiaAttachments.FROG_GENOME);

        float terrariumTemp = computeTemperature(signals);
        float terrariumHumidity = computeHumidity(signals, waterRatio);

        FrogGradeCalculator.Grade heatGrade = FrogGradeCalculator.calculateGrade(genome.getGene(Gene.HEAT_TOLERANCE));
        FrogGradeCalculator.Grade humidGrade = FrogGradeCalculator.calculateGrade(genome.getGene(Gene.SLIME_VISCOSITY));

        float tempMatch = rangeMatch(terrariumTemp, FrogGradeCalculator.getPreferredTemperatureRange(heatGrade));
        float humidMatch = rangeMatch(terrariumHumidity, FrogGradeCalculator.getPreferredHumidityRange(humidGrade));

        return (tempMatch + humidMatch) / 2.0f;
    }

    private static float computeTemperature(Map<String, ?> signals) {
        int net = count(signals, "warming_block") - count(signals, "cooling_block");
        float shift = Math.max(-0.4f, Math.min(0.4f, net * TEMP_PER_BLOCK));
        return Math.max(0f, Math.min(1f, TEMP_BASELINE + shift));
    }

    private static float computeHumidity(Map<String, ?> signals, float waterRatio) {
        float humidBonus = Math.min(0.3f, count(signals, "humid_source") / 8.0f);
        return Math.min(1f, HUMID_BASELINE + waterRatio * 0.5f + humidBonus);
    }

    // Returns 1.0 when value is within [min, max], falling linearly to 0 at ±0.25 outside the range.
    private static float rangeMatch(float value, float[] range) {
        if (value >= range[0] && value <= range[1]) return 1.0f;
        float dist = Math.min(Math.abs(value - range[0]), Math.abs(value - range[1]));
        return Math.max(0f, 1f - dist * 4f);
    }
}
