# Terrarium Rooms + Frog Happiness Integration Plan

**Status:** Approved, Ready for Implementation  
**Created:** 2026-05-25  
**Scope:** Add frog happiness system tied to Atelier terrarium room quality

## Overview

Integrate frogs into the Atelier room system with a happiness mechanic that drives rewards:
- Create a `zen_atelier:terrarium` room type based on water and plant signals
- Implement frog happiness (0.0–1.0) computed from zone quality and environmental factors
- Gate SLIME_VISCOSITY gene expression on happiness threshold
- Boost breeding mutations and drops based on happiness

## Changes Summary

### Atelier Mod (7 changes)

| File | Change | Lines |
|------|--------|-------|
| `Signals.java` | Add `frog_plant` predicate | +10 |
| `ZoneEvaluator.java` | Add water neighbor counting pass | +9 |
| `RoomTypeQualityProfile.java` | Add `TERRARIUM` static constant | +15 |
| `ZoneRegistry.java` | Wire terrarium profile lookup | +2 |
| `zones/terrarium_zone.json` | New zone definition | +9 |
| `room_profiles/terrarium.json` | New room profile | +18 |
| `lang/en_us.json` | Add 2 translation keys | +2 |

**Key Detail:** Water coverage signal injected directly into `signalCounts` in ZoneEvaluator (not via Signals predicates, since water is non-solid).

### Amphibia Mod (5 modified + 2 new)

| File | Change | Impact |
|------|--------|--------|
| `AmphibiaAttachments.java` | Add `FROG_HAPPINESS` Float attachment | +6 lines |
| `TerrariumHappinessHandler.java` | **NEW** — happiness computation event handler | ~80 lines |
| `FrogDropHandler.java` | **NEW** — SLIME_VISCOSITY gate + enhanced drops | ~90 lines |
| `FrogGenetics.java` | Add happiness-boosted breed overload | +25 lines |
| `FrogMixin.java` | Pass avg happiness to breed method | +4 lines (modify) |
| `Amphibia.java` | Register new handlers | +5 lines |

## Happiness Formula

Computed every 40 ticks for frogs in terrarium zones:

```
happiness = zoneQuality × 0.40
          + waterRatio × 0.35
          + plantScore × 0.20
          + sizeScore × 0.05
```

- **zoneQuality:** from `RoomData.getQuality()` (0.0–1.0)
- **waterRatio:** `signalCounts["water_coverage"] / (volume × 0.5)`, capped at 1.0
- **plantScore:** `signalCounts["frog_plant"] / 8`, capped at 1.0
- **sizeScore:** step function (8 blocks=0.2 → 120 blocks=1.0)

Decays by 0.05/interval when outside terrarium (min 0.0).

## Reward Mechanics

### 1. SLIME_VISCOSITY Gene Expression Gate
- **Grade B+** at `happiness ≥ 0.4` → drops slime balls (B=1, A=2, S=3; +1 if happiness ≥ 0.9)
- **Grade B+** at `happiness < 0.4` → gene present but NOT expressed, no drops

### 2. Enhanced Drops
- **FROG_SLIME** item: `happiness ≥ 0.6` → scaled drop chance (30–100%)

### 3. Breeding Mutations
- Up to 15% chance per locus to upgrade a random allele one tier (w→A→B→C→D)
- Chance weighted by average parent happiness

## Implementation Sequence

1. **Atelier changes** (in order):
   - Signals.java → add frog_plant predicate
   - ZoneEvaluator.java → add water counting
   - RoomTypeQualityProfile.java → add TERRARIUM profile
   - ZoneRegistry.java → wire terrarium lookup
   - JSON files + lang keys

2. **Amphibia changes** (in order):
   - AmphibiaAttachments.java → add FROG_HAPPINESS
   - Create TerrariumHappinessHandler.java
   - Create FrogDropHandler.java
   - FrogGenetics.java → add breed overload
   - FrogMixin.java → pass happiness in breeding
   - Amphibia.java → register handlers

3. **Build & test**:
   - `./gradlew buildAtelier` (or project's build script)
   - Run dev client, verify terrarium detection and happiness computation

## Testing Checklist

- [ ] Zone detection: `/atelier inspect` shows `zen_atelier:terrarium` for water-rich room
- [ ] Happiness storage: `/data get entity @e[type=frog] data.Attachments` shows `frog_happiness`
- [ ] Happiness decay: Remove water, confirm happiness drops over ~40 ticks
- [ ] SLIME gate: Grade-A frog with high happiness drops slime; same frog at low happiness doesn't
- [ ] Breeding bonus: Two high-happiness frogs breed, offspring genome has allele upgrades
- [ ] Enhanced drops: High-happiness frog drops FROG_SLIME on kill

## Notes

- `RoomTypeQualityProfile.TERRARIUM` uses theme weight 0.50 (vs 0.15–0.30 for other types) to emphasize water/plant signals
- Water neighbor counting uses a deduplicated set to avoid double-counting blocks touching multiple interior positions
- Happiness is NOT persisted (reset on death) since it reflects environmental context, not genetics
- Soft dependency on Atelier is maintained with `ModList.get().isLoaded()` checks
