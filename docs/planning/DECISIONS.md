# Decisions

This file records short, stable design decisions.

Update this when a design choice is clear enough that future sessions should treat it as current direction unless explicitly changed.

## Current Decisions

### Frog professions are job-driven

- The breeding and DNA system should be designed backward from gameplay jobs.
- The profession system is the main bridge between frog genetics and external integrations.

### Gene migration should use a parallel aptitude layer

- Keep the existing genome machinery.
- Move toward four genetics layers: aptitudes, phenotypes, ecology, and mutations.
- Keep only the traits that still serve the new design.
- Add profession-facing aptitude traits in phases.
- Do not prioritize backwards compatibility for old worlds if it compromises a cleaner redesign.

### Hats gate profession access

- Hats are the visible profession assignment layer.
- Genes gate qualification to use those hats effectively.
- Stations determine the exact active task.

### Mutations are premium bonuses, not baseline class gates

- Mutations should remain beneficial and exciting.
- Baseline profession access should not depend on rare RNG mutations.

### Create integration starts with worker and supervisor roles

- `Worker Frogport` handles logistics labor.
- `Supervisor Frogport` handles monitoring and control-room output.

### Vanilla-first professions should stay small

- `Warden` is the first fully realized vanilla profession.
- `Warden` should be designed as a territorial defender, not a high-expectation travel companion.
- `Naturalist` is the next strongest vanilla-first role after Warden.

### Supervisor frogs should complement, not replace, Create tools

- They sit above gauges, stock tickers, requesters, and suppliers as an interpretation layer.

### Frog retirement should be non-lethal by default

- Strong retirement paths are `Convert`, `Release`, and `Archive`.
- Direct culling may remain possible but should be a weak option.

### Witnessed culling should distress nearby frogs

- Nearby frogs should temporarily lose happiness and breeding confidence when they witness culling.
- This is a local husbandry penalty, not a global reputation system.

### Mod integrations can be gated by profession hats

- Example direction:
  - `Create Goggles`
  - `MineColonies Top Hat`
- Integrations should remain understandable from visuals alone.

### Frog outputs are husbandry byproducts, not throughput-first production

- A frog line should still feel meaningful even if its direct item output were removed.
- Output systems should reinforce care, habitat, life stage, and line quality.
- Create and alchemy compatibility should process or consume frog outputs downstream rather than defining the base frog loop.
