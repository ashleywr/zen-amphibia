# Atelier Environment Migration Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace Atelier room/zone-dependent frog terrarium logic with Amphibia-owned frog habitat evaluation over local environment snapshots.

**Architecture:** Add a focused habitat package in Amphibia that converts optional Atelier `EnvironmentSnapshot` data plus lightweight local scans into a `FrogHabitatReading`. Existing frog systems consume that reading instead of `ZoneData`, room IDs, room quality, or zone-scoped ledgers.

**Tech Stack:** Java 21, NeoForge 1.21.1, JUnit 5, Mockito, compileOnly local Zen Atelier jar.

---

## File Structure

- Create `src/main/java/com/sanhiruzu/amphibia/habitat/FrogHabitatReading.java`
  - Immutable value object with clamped suitability component scores.
- Create `src/main/java/com/sanhiruzu/amphibia/habitat/AtelierEnvironmentBridge.java`
  - Optional Atelier adapter. It should call `ZoneAPI.environmentAt` only when `zen_atelier` is loaded and should degrade to `EnvironmentSnapshot.AMBIENT`.
- Create `src/main/java/com/sanhiruzu/amphibia/habitat/FrogHabitatEvaluator.java`
  - Main scoring boundary for frogs, breeding, tadpoles, and climate checks.
- Create `src/test/java/com/sanhiruzu/amphibia/habitat/FrogHabitatReadingTest.java`
  - Pure unit tests for clamping and threshold behavior.
- Create `src/test/java/com/sanhiruzu/amphibia/habitat/FrogHabitatEvaluatorTest.java`
  - Pure unit tests for score composition from constructed data.
- Create `src/test/java/com/sanhiruzu/amphibia/AtelierZoneContractRemovalTest.java`
  - Source/resource regression tests for removed old room/zone contracts.
- Modify `src/main/java/com/sanhiruzu/amphibia/genetics/TerrariumHappinessHandler.java`
  - Replace room reflection and `ZoneAPI.setZoneQualityModifier` with habitat reading.
- Modify `src/main/java/com/sanhiruzu/amphibia/genetics/FrogEggLayingHandler.java`
  - Replace optimal zone check and zone ledger with habitat suitability.
- Modify `src/main/java/com/sanhiruzu/amphibia/genetics/GeneticEvents.java`
  - Replace placed frogspawn zone check and zone ledger with habitat suitability.
- Modify `src/main/java/com/sanhiruzu/amphibia/event/FrogEstivationHandler.java`
  - Replace zone temperature/humidity lookup and optimal zone bypass with habitat climate/water checks.
- Modify `src/main/java/com/sanhiruzu/amphibia/genetics/TadpoleGrowthHandler.java`
  - Replace `ZoneData` growth modifier with local water and density logic.
- Modify `src/main/java/com/sanhiruzu/amphibia/mixin/FluidDrainingBehaviourMixin.java`
  - Remove zone ledger upload.
- Modify `src/main/java/com/sanhiruzu/amphibia/AmphibiaConfig.java`
  - Replace `optimal_breeding_zone_type` config with habitat suitability threshold or update it through a compatibility deprecation path.
- Modify `src/main/resources/assets/zen_amphibia/patchouli_books/field_guide/en_us/entries/getting_started/terrarium.json`
  - Replace room/HUD language with habitat language.
- Modify `src/main/resources/assets/zen_amphibia/patchouli_books/field_guide/en_us/entries/resources/frog_slime.json`
  - Remove "perfect terrarium" room implication if necessary.
- Modify `src/main/resources/assets/zen_amphibia/patchouli_books/field_guide/en_us/entries/genetics/breeding_goals.json`
  - Replace "quality terrarium" wording with "good habitat" wording if necessary.
- Delete or deprecate `src/main/resources/data/zen_amphibia/zones/frog_terrarium.json`
  - Prefer delete if no downstream test requires it.
- Delete or deprecate `src/main/resources/data/zen_amphibia/room_profiles/frog_terrarium.json`
  - Prefer delete if no downstream test requires it.
- Modify `src/main/resources/assets/zen_amphibia/lang/en_us.json`
  - Remove orphaned room/zone translation keys if JSON files are deleted.
- Modify `src/test/java/com/sanhiruzu/amphibia/docs/PatchouliGuideSyncTest.java`
  - Update constant mirror expectations if happiness constants are renamed.

## Task 1: Habitat Reading Value Object

**Files:**
- Create: `src/main/java/com/sanhiruzu/amphibia/habitat/FrogHabitatReading.java`
- Test: `src/test/java/com/sanhiruzu/amphibia/habitat/FrogHabitatReadingTest.java`

- [ ] **Step 1: Write the failing tests**

```java
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
```

- [ ] **Step 2: Run the tests to verify they fail**

Run: `.\gradlew.bat test --tests com.sanhiruzu.amphibia.habitat.FrogHabitatReadingTest`

Expected: FAIL because `FrogHabitatReading` does not exist.

- [ ] **Step 3: Implement `FrogHabitatReading`**

```java
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
```

- [ ] **Step 4: Run the tests to verify they pass**

Run: `.\gradlew.bat test --tests com.sanhiruzu.amphibia.habitat.FrogHabitatReadingTest`

Expected: PASS.

- [ ] **Step 5: Commit**

```powershell
git add -- src/main/java/com/sanhiruzu/amphibia/habitat/FrogHabitatReading.java src/test/java/com/sanhiruzu/amphibia/habitat/FrogHabitatReadingTest.java
git commit -m "Add frog habitat reading value object"
```

## Task 2: Habitat Evaluator With Pure Scoring

**Files:**
- Create: `src/main/java/com/sanhiruzu/amphibia/habitat/FrogHabitatEvaluator.java`
- Test: `src/test/java/com/sanhiruzu/amphibia/habitat/FrogHabitatEvaluatorTest.java`
- Modify: `src/main/java/com/sanhiruzu/amphibia/genetics/FrogHappinessConstants.java`

- [ ] **Step 1: Add or rename constants for habitat scoring**

Modify `FrogHappinessConstants` so the weights no longer say "zone" while preserving old numeric behavior:

```java
// Happiness formula weights (must sum to 1.0)
public static final float HABITAT_COVER_WEIGHT   = 0.30f;
public static final float WATER_RATIO_WEIGHT     = 0.25f;
public static final float CLIMATE_SCORE_WEIGHT   = 0.25f;
public static final float PLANT_SCORE_WEIGHT     = 0.15f;
public static final float SIZE_SCORE_WEIGHT      = 0.05f;

/** @deprecated Use HABITAT_COVER_WEIGHT. */
@Deprecated(forRemoval = false)
public static final float ZONE_QUALITY_WEIGHT = HABITAT_COVER_WEIGHT;
```

- [ ] **Step 2: Write the failing evaluator tests**

```java
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
}
```

- [ ] **Step 3: Run the tests to verify they fail**

Run: `.\gradlew.bat test --tests com.sanhiruzu.amphibia.habitat.FrogHabitatEvaluatorTest`

Expected: FAIL because `FrogHabitatEvaluator` does not exist.

- [ ] **Step 4: Implement pure scoring in `FrogHabitatEvaluator`**

```java
package com.sanhiruzu.amphibia.habitat;

import com.sanhiruzu.amphibia.genetics.FrogGradeCalculator;
import com.sanhiruzu.amphibia.genetics.FrogHappinessConstants;
import com.sanhiruzu.amphibia.genetics.FrogGenome;
import com.sanhiruzu.amphibia.genetics.Gene;

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
```

- [ ] **Step 5: Run the tests to verify they pass**

Run: `.\gradlew.bat test --tests com.sanhiruzu.amphibia.habitat.FrogHabitatEvaluatorTest --tests com.sanhiruzu.amphibia.habitat.FrogHabitatReadingTest`

Expected: PASS.

- [ ] **Step 6: Commit**

```powershell
git add -- src/main/java/com/sanhiruzu/amphibia/genetics/FrogHappinessConstants.java src/main/java/com/sanhiruzu/amphibia/habitat/FrogHabitatEvaluator.java src/test/java/com/sanhiruzu/amphibia/habitat/FrogHabitatEvaluatorTest.java
git commit -m "Add frog habitat scoring"
```

## Task 3: Atelier Environment Bridge

**Files:**
- Create: `src/main/java/com/sanhiruzu/amphibia/habitat/AtelierEnvironmentBridge.java`
- Modify: `src/main/java/com/sanhiruzu/amphibia/habitat/FrogHabitatEvaluator.java`
- Test: `src/test/java/com/sanhiruzu/amphibia/habitat/FrogHabitatEvaluatorTest.java`

- [ ] **Step 1: Build local Atelier before changing compile-time API use**

Run: `.\gradlew.bat buildAtelier`

Expected: SUCCESS and a current `..\Atelier\build\libs\zen_atelier-*.jar` exists.

- [ ] **Step 2: Write a failing smoke test for neutral bridge fallback**

Add this test to `FrogHabitatEvaluatorTest`:

```java
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
```

- [ ] **Step 3: Run the test to verify it fails**

Run: `.\gradlew.bat test --tests com.sanhiruzu.amphibia.habitat.FrogHabitatEvaluatorTest`

Expected: FAIL because `neutralFacts()` does not exist.

- [ ] **Step 4: Implement `AtelierEnvironmentBridge`**

```java
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
```

- [ ] **Step 5: Add runtime evaluator entry points**

Add these methods to `FrogHabitatEvaluator`:

```java
public static Facts neutralFacts() {
    return new Facts(Map.of(), false, false, false, Temperature.PLEASANT, 0, 0, 1);
}

public static FrogHabitatReading evaluate(Level level, BlockPos pos, Frog frog) {
    FrogGenome genome = frog == null ? null : frog.getData(AmphibiaAttachments.FROG_GENOME);
    return evaluateFacts(
        AtelierEnvironmentBridge.factsAt(level, pos, DEFAULT_SCAN_RADIUS),
        genome,
        FrogHappinessConstants.BREEDING_HAPPINESS_THRESHOLD
    );
}
```

Also add imports:

```java
import com.sanhiruzu.amphibia.register.AmphibiaAttachments;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.level.Level;
```

- [ ] **Step 6: Run compile and focused tests**

Run: `.\gradlew.bat test --tests com.sanhiruzu.amphibia.habitat.FrogHabitatEvaluatorTest`

Expected: PASS.

Run: `.\gradlew.bat compileJava`

Expected: SUCCESS. If this fails because local Atelier lacks `EnvironmentSnapshot`, run `.\gradlew.bat buildAtelier`, then rerun `compileJava`.

- [ ] **Step 7: Commit**

```powershell
git add -- src/main/java/com/sanhiruzu/amphibia/habitat/AtelierEnvironmentBridge.java src/main/java/com/sanhiruzu/amphibia/habitat/FrogHabitatEvaluator.java src/test/java/com/sanhiruzu/amphibia/habitat/FrogHabitatEvaluatorTest.java
git commit -m "Bridge frog habitat scoring to Atelier environment snapshots"
```

## Task 4: Port Frog Happiness To Habitat Readings

**Files:**
- Modify: `src/main/java/com/sanhiruzu/amphibia/genetics/TerrariumHappinessHandler.java`
- Test: `src/test/java/com/sanhiruzu/amphibia/AtelierZoneContractRemovalTest.java`

- [ ] **Step 1: Write the source regression test for removed room APIs**

```java
package com.sanhiruzu.amphibia;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AtelierZoneContractRemovalTest {
    private static final Path PROJECT_ROOT = Path.of("").toAbsolutePath();

    @Test
    void activeSourceDoesNotCallRemovedRoomLookupApi() throws IOException {
        List<Path> offenders = javaFilesContaining("getRoomAt", "getZoneTypeId", "setZoneQualityModifier");
        assertTrue(offenders.isEmpty(), () -> "Removed room/zone APIs still referenced:\n" + offenders);
    }

    private static List<Path> javaFilesContaining(String... needles) throws IOException {
        try (var walk = Files.walk(PROJECT_ROOT.resolve("src/main/java"))) {
            return walk
                .filter(path -> path.toString().endsWith(".java"))
                .filter(path -> containsAny(path, needles))
                .toList();
        }
    }

    private static boolean containsAny(Path path, String[] needles) {
        try {
            String text = Files.readString(path);
            for (String needle : needles) {
                if (text.contains(needle)) {
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read " + path, e);
        }
    }
}
```

- [ ] **Step 2: Run the regression test to verify it fails**

Run: `.\gradlew.bat test --tests com.sanhiruzu.amphibia.AtelierZoneContractRemovalTest`

Expected: FAIL with `TerrariumHappinessHandler.java` listed.

- [ ] **Step 3: Replace `TerrariumHappinessHandler` zone logic**

In `TerrariumHappinessHandler`, remove these imports:

```java
import com.sanhiruzu.atelier.api.ZoneAPI;
import com.sanhiruzu.atelier.space.zone.ZoneData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.neoforged.fml.ModList;
import java.util.Collections;
import java.util.Map;
```

Add:

```java
import com.sanhiruzu.amphibia.habitat.FrogHabitatEvaluator;
import com.sanhiruzu.amphibia.habitat.FrogHabitatReading;
```

Replace `computeHappiness` with:

```java
private static float computeHappiness(Frog frog, Level level) {
    FrogHabitatReading reading = FrogHabitatEvaluator.evaluate(level, frog.blockPosition(), frog);
    return reading.suitability();
}
```

Delete all private methods between `computeHappiness` and `applyJumpSuppression`, including:

```java
getZoneAtPosition
getZoneTypeId
getZoneQuality
getSignalCounts
getVolume
count
computeWaterRatio
computePlantScore
computeSizeScore
computeClimateScore
computeTemperature
computeHumidity
rangeMatch
hasSpatialExtent
getZoneInt
containsPos
countFrogsInZone
computeCrowdingPenalty
syncOvercrowdingToAtelier
```

Remove this line from `updateFrogHappiness`:

```java
syncOvercrowdingToAtelier(frog, level);
```

- [ ] **Step 4: Run the regression test**

Run: `.\gradlew.bat test --tests com.sanhiruzu.amphibia.AtelierZoneContractRemovalTest`

Expected: It may still fail because other files still reference old APIs, but it should no longer list `TerrariumHappinessHandler.java`.

- [ ] **Step 5: Run compile**

Run: `.\gradlew.bat compileJava`

Expected: SUCCESS for `TerrariumHappinessHandler`.

- [ ] **Step 6: Commit**

```powershell
git add -- src/main/java/com/sanhiruzu/amphibia/genetics/TerrariumHappinessHandler.java src/test/java/com/sanhiruzu/amphibia/AtelierZoneContractRemovalTest.java
git commit -m "Use habitat readings for frog happiness"
```

## Task 5: Replace Breeding And Genetic Fluid Zone Checks

**Files:**
- Modify: `src/main/java/com/sanhiruzu/amphibia/genetics/FrogEggLayingHandler.java`
- Modify: `src/main/java/com/sanhiruzu/amphibia/genetics/GeneticEvents.java`
- Modify: `src/main/java/com/sanhiruzu/amphibia/mixin/FluidDrainingBehaviourMixin.java`
- Test: `src/test/java/com/sanhiruzu/amphibia/AtelierZoneContractRemovalTest.java`

- [ ] **Step 1: Extend regression test for zone data store removal**

In `AtelierZoneContractRemovalTest.activeSourceDoesNotCallRemovedRoomLookupApi`, include these needles:

```java
List<Path> offenders = javaFilesContaining(
    "getRoomAt",
    "getZoneTypeId",
    "setZoneQualityModifier",
    "ZoneDataStore",
    "getZoneAt(",
    "isZoneType("
);
```

- [ ] **Step 2: Run the regression test to verify it fails**

Run: `.\gradlew.bat test --tests com.sanhiruzu.amphibia.AtelierZoneContractRemovalTest`

Expected: FAIL with `FrogEggLayingHandler.java`, `GeneticEvents.java`, and `FluidDrainingBehaviourMixin.java` listed.

- [ ] **Step 3: Port `FrogEggLayingHandler`**

Remove imports:

```java
import com.sanhiruzu.atelier.api.ZoneAPI;
import com.sanhiruzu.atelier.space.zone.ZoneData;
import net.neoforged.fml.ModList;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
```

Add imports:

```java
import com.sanhiruzu.amphibia.habitat.FrogHabitatEvaluator;
import com.sanhiruzu.amphibia.habitat.FrogHabitatReading;
```

Replace the optimal zone block in `tryLayEggs` with:

```java
FrogHabitatReading habitat = FrogHabitatEvaluator.evaluate(level, spawnPos, frog);
boolean isOptimalHabitat = habitat.isOptimalBreedingHabitat();
```

Replace:

```java
if (isOptimalZone) {
```

with:

```java
if (isOptimalHabitat) {
```

Delete the entire `if (zone != null) { ... ZoneDataStore ... }` ledger upload block from the raw genetic fluid path.

- [ ] **Step 4: Port `GeneticEvents`**

Remove imports:

```java
import com.sanhiruzu.amphibia.AmphibiaConfig;
import com.sanhiruzu.atelier.api.ZoneAPI;
import com.sanhiruzu.atelier.space.zone.ZoneData;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.neoforged.fml.ModList;
```

Add imports:

```java
import com.sanhiruzu.amphibia.habitat.FrogHabitatEvaluator;
import com.sanhiruzu.amphibia.habitat.FrogHabitatReading;
```

Replace the zone lookup block in `onPlaceSpawn` with:

```java
FrogHabitatReading habitat = FrogHabitatEvaluator.evaluate(serverLevel, event.getPos(), mom);
boolean isOptimalHabitat = habitat.isOptimalBreedingHabitat();
```

Replace:

```java
if (isOptimalZone) {
```

with:

```java
if (isOptimalHabitat) {
```

Delete the `if (zone != null) { ... ZoneDataStore ... }` ledger upload block.

- [ ] **Step 5: Remove fluid draining ledger upload**

In `FluidDrainingBehaviourMixin`, remove imports:

```java
import com.sanhiruzu.atelier.api.ZoneAPI;
import com.sanhiruzu.atelier.space.zone.ZoneData;
import net.neoforged.fml.ModList;
import org.spongepowered.asm.mixin.Unique;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import java.util.UUID;
```

Replace the inner genetics handling block with:

```java
if (genetics != null) {
    wildRegistry.remove(currentPos);
}
```

Delete the `amphibia$uploadToLedger` method entirely.

- [ ] **Step 6: Run tests and compile**

Run: `.\gradlew.bat test --tests com.sanhiruzu.amphibia.AtelierZoneContractRemovalTest`

Expected: Still may fail because `FrogEstivationHandler` and `TadpoleGrowthHandler` still reference old APIs.

Run: `.\gradlew.bat compileJava`

Expected: SUCCESS.

- [ ] **Step 7: Commit**

```powershell
git add -- src/main/java/com/sanhiruzu/amphibia/genetics/FrogEggLayingHandler.java src/main/java/com/sanhiruzu/amphibia/genetics/GeneticEvents.java src/main/java/com/sanhiruzu/amphibia/mixin/FluidDrainingBehaviourMixin.java src/test/java/com/sanhiruzu/amphibia/AtelierZoneContractRemovalTest.java
git commit -m "Use habitat suitability for frog breeding outputs"
```

## Task 6: Port Estivation And Tadpole Growth

**Files:**
- Modify: `src/main/java/com/sanhiruzu/amphibia/event/FrogEstivationHandler.java`
- Modify: `src/main/java/com/sanhiruzu/amphibia/genetics/TadpoleGrowthHandler.java`
- Test: `src/test/java/com/sanhiruzu/amphibia/AtelierZoneContractRemovalTest.java`

- [ ] **Step 1: Run the regression test to list remaining old API references**

Run: `.\gradlew.bat test --tests com.sanhiruzu.amphibia.AtelierZoneContractRemovalTest`

Expected: FAIL with `FrogEstivationHandler.java` and `TadpoleGrowthHandler.java` listed.

- [ ] **Step 2: Port `FrogEstivationHandler`**

Remove imports:

```java
import com.sanhiruzu.amphibia.AmphibiaConfig;
import com.sanhiruzu.atelier.api.ZoneAPI;
import com.sanhiruzu.atelier.space.zone.OutdoorZoneData;
import com.sanhiruzu.atelier.space.zone.ZoneData;
import net.neoforged.fml.ModList;
```

Add imports:

```java
import com.sanhiruzu.amphibia.habitat.FrogHabitatEvaluator;
import com.sanhiruzu.amphibia.habitat.FrogHabitatReading;
```

Remove this early return:

```java
if (!ModList.get().isLoaded("zen_atelier")) return;
```

Replace the tick-time zone section with:

```java
BlockPos pos = frog.blockPosition();
FrogHabitatReading habitat = FrogHabitatEvaluator.evaluate(level, pos, frog);
float[] tempHum = readTempHumidity(level, pos, habitat);
float temp = tempHum[0];
float hum = tempHum[1];

if (habitat.isOptimalBreedingHabitat()) {
    tryInduceLoveFromTannins(frog, level, pos);
} else {
    EstivationConfig config = EstivationConfigManager.getConfig();
    if (config.shouldEstivate(temp, hum)) {
        if (gameTime - af.getLastRevivalTick() > config.cooledownTicksAfterRevival()) {
            af.startEstivation();
        }
    }
}
```

Replace `checkShouldRevive` body's zone section with:

```java
BlockPos pos = frog.blockPosition();
FrogHabitatReading habitat = FrogHabitatEvaluator.evaluate(level, pos, frog);
float[] tempHum = readTempHumidity(level, pos, habitat);
```

Replace `readTempHumidity` with:

```java
private static float[] readTempHumidity(Level level, BlockPos pos, FrogHabitatReading habitat) {
    var biome = level.getBiome(pos);
    float biomeTemp = biome.value().getBaseTemperature() * 20.0f;
    float biomeHum = biome.value().hasPrecipitation() ? 80.0f : 20.0f;

    float habitatCooling = (1.0f - habitat.climateScore()) * 6.0f;
    float waterHumidity = habitat.waterScore() * 40.0f;
    float temp = Math.max(0.0f, biomeTemp - habitatCooling);
    float hum = Math.min(100.0f, biomeHum + waterHumidity);
    return new float[]{temp, hum};
}
```

- [ ] **Step 3: Port `TadpoleGrowthHandler`**

Remove imports:

```java
import com.sanhiruzu.atelier.api.ZoneAPI;
import com.sanhiruzu.atelier.space.zone.ZoneData;
import net.neoforged.fml.ModList;
```

Delete methods:

```java
applyZoneGrowthModifier
countTadpolesInZone
```

Replace the Atelier branch in `onTadpoleTick` with:

```java
applyLocalWaterGrowthModifier(tadpole, level, pos);
```

Rename `applyOpenWaterGrowthModifier` to `applyLocalWaterGrowthModifier` and replace its body with:

```java
private static void applyLocalWaterGrowthModifier(Tadpole tadpole, Level level, BlockPos pos) {
    TadpoleZoneEcology.WaterVolumeResult volumeResult = TadpoleZoneEcology.calculateWaterVolume(pos, level);
    if (volumeResult.isOpenWater) {
        if (level.random.nextDouble() < 0.0083) {
            tadpole.discard();
        }
        return;
    }

    int nearbyTadpoles = level.getEntities(
        EntityType.TADPOLE,
        new AABB(pos).inflate(DENSITY_RADIUS, DENSITY_VERTICAL_RADIUS, DENSITY_RADIUS),
        nearby -> nearby.isAlive() && nearby.isInWater()
    ).size();
    if (nearbyTadpoles == 0) nearbyTadpoles = 1;

    double biomassRatio = (double) volumeResult.blockCount / nearbyTadpoles;
    if (biomassRatio < 10) {
        applyStuntedGrowth(tadpole);
    } else if (biomassRatio > 50) {
        applyAcceleratedGrowth(tadpole);
    }
}
```

- [ ] **Step 4: Run regression test and compile**

Run: `.\gradlew.bat test --tests com.sanhiruzu.amphibia.AtelierZoneContractRemovalTest`

Expected: PASS.

Run: `.\gradlew.bat compileJava`

Expected: SUCCESS.

- [ ] **Step 5: Commit**

```powershell
git add -- src/main/java/com/sanhiruzu/amphibia/event/FrogEstivationHandler.java src/main/java/com/sanhiruzu/amphibia/genetics/TadpoleGrowthHandler.java
git commit -m "Remove zone dependency from climate and tadpole growth"
```

## Task 7: Update Config, Data, Guide, And Resource Tests

**Files:**
- Modify: `src/main/java/com/sanhiruzu/amphibia/AmphibiaConfig.java`
- Modify: `src/main/resources/assets/zen_amphibia/patchouli_books/field_guide/en_us/entries/getting_started/terrarium.json`
- Modify: `src/main/resources/assets/zen_amphibia/patchouli_books/field_guide/en_us/entries/resources/frog_slime.json`
- Modify: `src/main/resources/assets/zen_amphibia/patchouli_books/field_guide/en_us/entries/genetics/breeding_goals.json`
- Delete: `src/main/resources/data/zen_amphibia/zones/frog_terrarium.json`
- Delete: `src/main/resources/data/zen_amphibia/room_profiles/frog_terrarium.json`
- Modify: `src/main/resources/assets/zen_amphibia/lang/en_us.json`
- Modify: `src/test/java/com/sanhiruzu/amphibia/docs/PatchouliGuideSyncTest.java`
- Test: `src/test/java/com/sanhiruzu/amphibia/AtelierZoneContractRemovalTest.java`

- [ ] **Step 1: Add guide/data regression tests**

Add this test to `AtelierZoneContractRemovalTest`:

```java
@Test
void resourcesDoNotAdvertiseAtelierTerrariumRooms() throws IOException {
    List<Path> offenders = resourceFilesContaining(
        "room HUD",
        "zone quality",
        "zone type",
        "Atelier room",
        "getZoneTypeId",
        "frog_terrarium"
    );
    assertTrue(offenders.isEmpty(), () -> "Old terrarium room language remains:\n" + offenders);
}

private static List<Path> resourceFilesContaining(String... needles) throws IOException {
    try (var walk = Files.walk(PROJECT_ROOT.resolve("src/main/resources"))) {
        return walk
            .filter(path -> path.toString().endsWith(".json"))
            .filter(path -> containsAny(path, needles))
            .toList();
    }
}
```

- [ ] **Step 2: Run the regression test to verify it fails**

Run: `.\gradlew.bat test --tests com.sanhiruzu.amphibia.AtelierZoneContractRemovalTest`

Expected: FAIL with terrarium zone/room JSON and guide JSON listed.

- [ ] **Step 3: Update config description**

Replace `OPTIMAL_BREEDING_ZONE_TYPE` with:

```java
public static final ModConfigSpec.DoubleValue OPTIMAL_BREEDING_HABITAT_THRESHOLD;
```

Replace its builder entry with:

```java
OPTIMAL_BREEDING_HABITAT_THRESHOLD = builder
    .comment("Minimum frog habitat suitability required for frogs to produce raw genetic fluid instead of normal genetic frogspawn.")
    .defineInRange("optimal_breeding_habitat_threshold", 0.5D, 0.0D, 1.0D);
```

Then update `FrogHabitatEvaluator.evaluate` to pass:

```java
(float) com.sanhiruzu.amphibia.AmphibiaConfig.OPTIMAL_BREEDING_HABITAT_THRESHOLD.getAsDouble()
```

instead of `FrogHappinessConstants.BREEDING_HAPPINESS_THRESHOLD`.

- [ ] **Step 4: Delete old room/zone data files**

Run:

```powershell
Remove-Item -LiteralPath 'src/main/resources/data/zen_amphibia/zones/frog_terrarium.json'
Remove-Item -LiteralPath 'src/main/resources/data/zen_amphibia/room_profiles/frog_terrarium.json'
```

Expected: Both files are removed from the working tree.

- [ ] **Step 5: Remove orphaned translations**

Delete these keys from `src/main/resources/assets/zen_amphibia/lang/en_us.json`:

```json
"room_type.zen_amphibia.frog_terrarium": "Frog Terrarium",
"zone.zen_amphibia.frog_terrarium": "Frog Terrarium"
```

- [ ] **Step 6: Update guide text**

In `getting_started/terrarium.json`, replace room/HUD-specific text with habitat text. Keep the existing percentage constants mirrored in the guide. The "What Makes a Good Terrarium" page should say:

```json
"text": "Happiness is scored from five things: $(b)cover and enclosure$() (30%), $(b)water coverage$() (25%), $(b)climate match$() (25%), $(b)frog plants$() (15%), and $(b)habitat size$() (5%). Climate match compares local warmth and humidity against the frog's $(l:zen_amphibia:genetics/reading_genes)Heat Tolerance and Humidity Tolerance$(/l) grades."
```

Replace the overcrowding page sentence:

```json
"text": "Too many frogs in one small habitat penalize happiness. Spread large lines across multiple pools or terrarium-style habitats."
```

In `frog_slime.json`, replace "perfect terrarium" with:

```json
"good habitat"
```

In `breeding_goals.json`, replace "quality terrarium" with:

```json
"good habitat"
```

- [ ] **Step 7: Update guide sync test for renamed constant**

In `PatchouliGuideSyncTest.happinessConstantsAreMirroredInGuide`, replace:

```java
assertMentionedAsPct(raw, FrogHappinessConstants.ZONE_QUALITY_WEIGHT,   "ZONE_QUALITY_WEIGHT");
```

with:

```java
assertMentionedAsPct(raw, FrogHappinessConstants.HABITAT_COVER_WEIGHT, "HABITAT_COVER_WEIGHT");
```

- [ ] **Step 8: Run resource and docs tests**

Run: `.\gradlew.bat test --tests com.sanhiruzu.amphibia.AtelierZoneContractRemovalTest --tests com.sanhiruzu.amphibia.TranslationCompletenessTest --tests com.sanhiruzu.amphibia.docs.PatchouliGuideSyncTest`

Expected: PASS.

- [ ] **Step 9: Commit**

```powershell
git add -- src/main/java/com/sanhiruzu/amphibia/AmphibiaConfig.java src/main/java/com/sanhiruzu/amphibia/habitat/FrogHabitatEvaluator.java src/main/resources/assets/zen_amphibia/patchouli_books/field_guide/en_us/entries/getting_started/terrarium.json src/main/resources/assets/zen_amphibia/patchouli_books/field_guide/en_us/entries/resources/frog_slime.json src/main/resources/assets/zen_amphibia/patchouli_books/field_guide/en_us/entries/genetics/breeding_goals.json src/main/resources/assets/zen_amphibia/lang/en_us.json src/test/java/com/sanhiruzu/amphibia/docs/PatchouliGuideSyncTest.java src/test/java/com/sanhiruzu/amphibia/AtelierZoneContractRemovalTest.java
git add -u -- src/main/resources/data/zen_amphibia/zones/frog_terrarium.json src/main/resources/data/zen_amphibia/room_profiles/frog_terrarium.json
git commit -m "Update terrarium docs for habitat evaluation"
```

## Task 8: Full Verification

**Files:**
- No planned edits.

- [ ] **Step 1: Run full tests**

Run: `.\gradlew.bat test`

Expected: SUCCESS.

- [ ] **Step 2: Run Java compilation**

Run: `.\gradlew.bat compileJava`

Expected: SUCCESS with no `-Werror` failures.

- [ ] **Step 3: Search for old room and zone contracts**

Run:

```powershell
rg -n "getRoomAt|ZoneData|getZoneAt|isZoneType|ZoneDataStore|zone quality|room HUD|frog_terrarium" src docs DATAPACK_CUSTOMIZATION.md MOD_LIFECYCLE.md AMPHIBIA_ROADMAP.md
```

Expected: No active gameplay source references. Documentation references are acceptable only if they explicitly describe removed/deprecated history; otherwise update the docs and rerun.

- [ ] **Step 4: Inspect git status**

Run: `git status --short`

Expected: Only intentional migration changes are present. Pre-existing unrelated changes to `build.gradle` and `src/main/resources/assets/zen_amphibia/blockstates/frog_chest.json` may still be present and must not be reverted or staged unless the user requests it.

- [ ] **Step 5: Final commit if docs needed cleanup**

If Step 3 required extra documentation cleanup:

```powershell
git add -- DATAPACK_CUSTOMIZATION.md MOD_LIFECYCLE.md AMPHIBIA_ROADMAP.md docs
git commit -m "Clean up old terrarium zone references"
```

Expected: Commit succeeds and contains no AI-assistant attribution.
