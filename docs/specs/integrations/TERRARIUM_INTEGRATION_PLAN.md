# Terrarium Habitat Integration Plan

**Status:** Superseded by the Atelier environment migration.

The original version of this plan described Amphibia creating or consuming named Atelier terrarium rooms. Atelier no longer exposes rooms or zones as the gameplay contract. Amphibia now treats a terrarium as a player-built habitat style: frogs sample their local environment and compute happiness from water, plants, cover, climate, and crowding.

Use the current design and implementation plan instead:

- `docs/superpowers/specs/2026-06-21-atelier-environment-migration-design.md`
- `docs/superpowers/plans/2026-06-21-atelier-environment-migration.md`

The retained gameplay goals are:

- happy frogs reward good habitat construction
- water, frog plants, cover, climate match, and population density determine suitability
- breeding and drops continue to benefit from high happiness
- Atelier, when present, provides cached local environment snapshots rather than persistent room identity
