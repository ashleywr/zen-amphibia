# Atelier Environment Migration Design

## Context

Atelier's zone API no longer exposes persistent room or zone objects as the gameplay contract. Its current `ZoneAPI` is a point-of-use environment facade: callers sample `ZoneAPI.environmentAt(level, pos, radius)` when they need local facts, and Atelier handles cached environment scans internally.

Amphibia still treats terrariums as Atelier room or zone data in several places:

- `TerrariumHappinessHandler` queries `SpaceQuery.getRoomAt` and reads zone type, quality, signals, volume, and bounds.
- Breeding, egg laying, estivation, tadpole growth, genetic fluid draining, and genetics events use `ZoneAPI.getZoneAt`, `ZoneAPI.isZoneType`, `ZoneData`, or `ZoneDataStore`.
- Data and guide content describe a `frog_terrarium` Atelier room/zone as the source of frog happiness.

The new design should make frogs evaluate their immediate habitat instead of asking whether they are inside a named terrarium room.

## Goals

- Replace room/zone-dependent terrarium logic with frog habitat evaluation based on local environment snapshots.
- Use Atelier's environment cache when Atelier is present.
- Keep frog-specific scoring and thresholds in Amphibia.
- Preserve existing player-facing mechanics where possible: good water, plants, cover, comfortable climate, and reasonable population density make frogs happier and improve breeding outcomes.
- Keep Amphibia functional without treating Atelier room data as required.

## Non-Goals

- Do not recreate Atelier rooms or zone definitions under a new name.
- Do not make terrarium quality a HUD-known area concept.
- Do not move frog-specific gameplay rules into Atelier in this migration.
- Do not redesign frog genetics, slime harvests, or frogport systems beyond their habitat lookup boundary.

## Architecture

Add a small Amphibia-owned habitat boundary:

- `FrogHabitatEvaluator`
  - Inputs: `Level`, `BlockPos`, optional `Frog`, and a scan radius.
  - Output: an immutable `FrogHabitatReading`.
  - Responsibility: convert local environment facts into frog gameplay scores.

- `FrogHabitatReading`
  - Fields should cover the values consumers actually need:
    - overall suitability, `0.0` to `1.0`
    - water score
    - plant score
    - cover/enclosure score
    - climate score
    - crowding score
    - approximate volume/capacity score if available from scan radius rather than room bounds
    - whether the location qualifies as an optimal breeding habitat

- `AtelierEnvironmentBridge`
  - Responsibility: optional adapter around Atelier's current `ZoneAPI.environmentAt`.
  - It should hide direct API calls and reflection, depending on what compiles cleanly against the local Atelier jar.
  - It returns neutral or locally computed fallback facts if Atelier is not loaded or the API call fails.

The evaluator should be the only place that knows how to translate environment facts into frog habitat quality. Existing handlers should ask for a reading and make decisions from that reading.

## Data Flow

1. A frog-related event fires: happiness tick, breeding, egg laying, estivation check, tadpole growth, or fluid/genetics hook.
2. The handler calls `FrogHabitatEvaluator.evaluate(...)` at the relevant position.
3. The evaluator requests a cached `EnvironmentSnapshot` from Atelier when available.
4. The evaluator scores local facts:
   - Water and dampness: water blocks, damp sources, or Amphibia's humid-source tag.
   - Plants: Amphibia frog plant tag and any Atelier plant-like signals.
   - Cover: `isCovered`, `isIndoorsLike`, and `isEnclosed`.
   - Climate: Atelier temperature band plus Amphibia warming/cooling/humid tags, compared against frog genes when a frog is present.
   - Crowding: nearby frog and tadpole counts from the snapshot or a bounded entity query.
5. The caller uses the reading:
   - Happiness sets or decays frog happiness.
   - Breeding requires sufficient suitability instead of a matching zone type.
   - Egg laying chooses genetic fluid only in optimal habitat, otherwise normal genetic frogspawn.
   - Estivation revives or starts based on local climate and water availability.
   - Tadpole growth uses local water/crowding quality rather than zone bounds.

## Compatibility

Atelier should remain optional at runtime. Amphibia can compile against the local `compileOnly` Atelier jar, but all runtime calls should stay behind a ModList check and an adapter so failures degrade to neutral habitat readings instead of crashing.

The old room and zone JSON files under `data/zen_amphibia/zones` and `data/zen_amphibia/room_profiles` should no longer be part of the active gameplay contract. They can be removed if no tests or downstream integrations need them, or retained temporarily only as inert compatibility data.

Zone-scoped genetics ledgers cannot be preserved as-is because there is no region id. Replace them with one of these narrower behaviors during implementation:

- Prefer no shared ledger for V1 of the migration; keep genetics at the block/entity attachment level.
- If a ledger is still needed, key it by a coarse chunk or section position in Amphibia-owned saved data.

## Player-Facing Language

Rename the concept from "Atelier terrarium room" to "frog habitat" or "terrarium-style habitat." A terrarium can still be a player build style, but frogs should respond to the local conditions rather than a discovered room type.

Guide text should emphasize:

- water nearby
- damp/pleasant climate
- frog-friendly plants
- enough cover or enclosure
- not too many frogs in one small area

References to Atelier's room HUD, room quality, zone type, or persistent terrarium rooms should be removed.

## Error Handling

- If Atelier is missing, return a neutral snapshot and rely on Amphibia's lightweight local checks where needed.
- If Atelier throws or returns unexpected data, log at debug level at most and return neutral facts for that tick.
- Clamp every score to `0.0` to `1.0`.
- Treat unknown signals as absent.
- Avoid storing environment snapshots on entities; they are cached by Atelier and should be queried at event time.

## Testing

Add focused unit tests for `FrogHabitatEvaluator` using constructed readings or snapshots:

- high water, plants, cover, and comfortable climate produce high suitability
- dry/exposed habitat produces low suitability
- overcrowding lowers suitability
- frog heat and humidity genes affect climate match
- missing Atelier or neutral snapshot does not crash and gives low but valid suitability

Add regression tests that scan source/resources for removed old contracts:

- no active code path calls `getRoomAt`
- no active code path requires `ZoneData.getZoneTypeId`
- guide text no longer says frogs depend on an Atelier room HUD

Run `.\gradlew.bat test` after implementation. If Minecraft/NeoForge API usage changes during implementation, run `.\gradlew.bat syncReferenceSources` first and inspect `internal/reference-sources/`.

## Migration Steps

1. Introduce `FrogHabitatReading`, `AtelierEnvironmentBridge`, and `FrogHabitatEvaluator`.
2. Port happiness calculation to use habitat readings.
3. Replace optimal breeding zone checks with habitat suitability checks.
4. Replace estivation temperature/humidity reads with habitat climate reads.
5. Replace tadpole zone growth and overcrowding with local habitat/water/crowding logic.
6. Remove or isolate zone-scoped genetics ledger usage.
7. Remove inactive room/zone JSON or mark it as deprecated compatibility data.
8. Update guide text, config descriptions, docs, and tests.

## Open Implementation Decisions

- Exact scan radius for frog habitat should start conservative, likely `6` to `8`, and only increase if testing shows habitats feel too strict.
- The optimal breeding threshold should initially match existing happiness expectations, likely around `0.5`.
- The genetic ledger behavior should be removed for the first migration unless a current gameplay loop depends on it.
