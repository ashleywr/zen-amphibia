package com.sanhiruzu.amphibia.habitat;

import com.sanhiruzu.atelier.api.EnvironmentSnapshot;
import com.sanhiruzu.atelier.api.EnvironmentTemperatureBand;
import com.sanhiruzu.atelier.api.ZoneAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;

import java.util.Map;

public final class AtelierEnvironmentBridge {
    private AtelierEnvironmentBridge() {
    }

    public static FrogHabitatEvaluator.Facts factsAt(Level level, BlockPos pos, int radius) {
        if (level == null || pos == null || !ModList.get().isLoaded("zen_atelier")) {
            return FrogHabitatEvaluator.neutralFacts();
        }

        try {
            EnvironmentSnapshot snapshot = ZoneAPI.environmentAt(level, pos, radius);
            return fromSnapshot(snapshot, radius);
        } catch (RuntimeException | LinkageError ignored) {
            return FrogHabitatEvaluator.neutralFacts();
        }
    }

    static FrogHabitatEvaluator.Facts fromSnapshot(EnvironmentSnapshot snapshot, int radius) {
        if (snapshot == null) {
            return FrogHabitatEvaluator.neutralFacts();
        }

        int cubeWidth = Math.max(1, radius * 2 + 1);
        int approximateVolume = cubeWidth * cubeWidth * cubeWidth;
        Map<String, Integer> counts = snapshot.signalCounts();
        int frogs = snapshot.nearbyEntityCounts().getOrDefault(EntityType.FROG, 0);
        int tadpoles = snapshot.nearbyEntityCounts().getOrDefault(EntityType.TADPOLE, 0);

        return new FrogHabitatEvaluator.Facts(
            counts,
            snapshot.isCovered(),
            snapshot.isIndoorsLike(),
            snapshot.isEnclosed(),
            mapTemperature(snapshot.temperatureBand()),
            frogs,
            tadpoles,
            approximateVolume
        );
    }

    private static FrogHabitatEvaluator.Temperature mapTemperature(EnvironmentTemperatureBand band) {
        return switch (band == null ? EnvironmentTemperatureBand.PLEASANT : band) {
            case COLD -> FrogHabitatEvaluator.Temperature.COLD;
            case COOL -> FrogHabitatEvaluator.Temperature.COOL;
            case PLEASANT -> FrogHabitatEvaluator.Temperature.PLEASANT;
            case WARM -> FrogHabitatEvaluator.Temperature.WARM;
            case HOT -> FrogHabitatEvaluator.Temperature.HOT;
        };
    }
}
