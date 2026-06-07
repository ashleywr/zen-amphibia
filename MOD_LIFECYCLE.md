# Amphibia Mod Lifecycle

This file tracks where the mod actually is.

Use it to answer:

- what exists
- what is hardened
- what needs smoke testing
- what is only planned
- when the current work is a good full playable slice

The roadmap describes direction. Specs describe design. This file describes release readiness.

## Status Labels

### Planned

Designed or discussed, but not implemented.

### Prototype

Implemented enough to try, but numbers, UX, compatibility, or persistence may still change.

### Implemented

Feature works in normal code paths and has at least build/test coverage or obvious in-game behavior.

### Hardened

Feature has survived focused runtime testing, has player-facing explanation, and is unlikely to need structural changes before the next playable slice.

### Backlog

Valid idea, but not required for the current slice.

## Current Slice Target

### Survival Frog Husbandry Slice

The next good slice should let a new survival player:

1. find wild frogs and crickets
2. bring frogs home with buckets
3. understand which frogs are worth breeding
4. breed frogs across generations
5. get useful frog-native resources
6. see habitat and overcrowding feedback
7. use the guidebook to recover the loop without outside notes

Create support can be present, but this slice should not depend on Create.

## Current Feature Status

| Feature | Status | Notes |
|---|---|---|
| Frog genome storage | Implemented | Frogs, tadpoles, frogspawn, buckets, and worker frogports carry DNA. Needs broader persistence smoke testing across save/load paths. |
| Wild frog genetics | Implemented | Natural/spawned frogs receive randomized genomes. Bucket and tadpole conversion paths preserve existing DNA. |
| Frog bucket capture | Implemented | Empty bucket captures adult frogs and stores genetics. Guidebook entry exists. |
| Frog handling delight touches | Prototype | Frog buckets preserve custom names, show small personality/prospect lines, and play release feedback. Needs in-game feel check. |
| Breeding inheritance | Implemented | Parent DNA mixes into genetic frogspawn and tadpoles. Needs survival loop smoke test with several generations. |
| Breeding motivation UX | Prototype | Frog buckets show top breeding targets. Guide explains why breeding matters. Needs in-game tooltip review. |
| Crickets | Implemented | Entity, spawn egg, frog food tag, and natural biome spawns exist. Needs runtime spawn-density check. |
| Frog Slime feeding loop | Implemented | Tamed frogs produce Frog Slime from crickets. Needs balance check in survival. |
| Frog Slime material economy | Prototype | Slimeball conversion and slime gear exist. Needs recipe/progression pass. |
| Genetic frogspawn | Implemented | Stored DNA, reduced clutch size, and tooltips exist. Needs save/load and hatching smoke test. |
| Tadpole overcrowding | Prototype | Stunting, visual signs, and failure pressure exist. Needs runtime tuning for readability and fairness. |
| Terrarium happiness | Prototype | Atelier integration and happiness effects exist. Needs the checklist in the terrarium integration plan. |
| Genetic froglights | Prototype | Blocks and recipes exist. Natural frog/magma-cube production is still planned. Textures are placeholder. |
| Patchouli guide | Implemented | Covers the main loop, genetics, breeding, crickets, frog buckets, slime, froglights, tadpoles, and overcrowding. Needs final survival-flow readthrough. |
| Worker frogport | Prototype | Owned block/item, Create recipe override, genetics effects, and tooltips exist. Needs Create runtime smoke test before hardening. |
| Create integration exit plan | Planned | Finish plan exists. Not required for the survival-only slice. |
| Warden/combat frogs | Prototype | Combat goals and role helpers exist. Needs design narrowing and survival UX before hardening. |
| Frog professions and hats | Planned | Direction exists in specs and decisions. Not part of the current slice. |
| Frog retirement/release/archive | Planned | Specs exist. Not part of the current slice. |

## Hardened Enough For Current Slice

Nothing should be marked here until it has been tested in a dev client as part of the survival loop.

Candidates to harden first:

- frog bucket capture and release
- wild frog randomized genetics
- cricket natural spawning
- breeding inheritance through frogspawn and tadpoles
- Frog Slime production from tamed frogs
- guidebook recovery path

## Full Slice Exit Checklist

The Survival Frog Husbandry slice is done when:

- [ ] A fresh survival world naturally contains frogs with varied Amphibia genetics.
- [ ] Crickets spawn naturally often enough to find, but not so often they crowd passive spawns.
- [ ] A player can capture a wild adult frog in a bucket, carry it home, place it, and keep its DNA.
- [ ] Frog Bucket tooltips make at least one breeding target obvious for non-perfect frogs.
- [ ] A player can tame frogs with crickets and breed them without creative items.
- [ ] Offspring DNA visibly differs from parents in expected ways.
- [ ] Genetic frogspawn hatches into tadpoles with the stored offspring genome.
- [ ] Tadpoles grow into frogs without losing DNA.
- [ ] Overcrowding feedback appears before the pond becomes punishing.
- [ ] Tamed frogs produce Frog Slime at a rate that feels useful but not complete by itself.
- [ ] The guidebook explains how to recover the loop from discovery to first breeding goal.
- [ ] No current survival-critical path depends on Create.
- [ ] The full build passes.
- [ ] The completed slice is pushed only after the local commit stack is reviewed.

## Runtime Smoke Test Order

1. Create a fresh survival world.
2. Locate crickets in a target biome from `#zen_amphibia:has_crickets`.
3. Locate wild frogs and capture at least two adult frogs with buckets.
4. Compare Frog Bucket breeding target summaries.
5. Build a small pond or terrarium.
6. Release frogs from buckets and confirm DNA remains.
7. Tame/feed frogs with crickets and trigger breeding.
8. Inspect genetic frogspawn, tadpoles, and adult offspring.
9. Overcrowd a small pond enough to confirm visual warning signs.
10. Feed tamed frogs for Frog Slime and check output usefulness.
11. Read the guidebook path from Getting Started through Breeding Goals.

## Current Planning Backlog

These should not block the survival slice:

- Create worker frogport hardening
- supervisor perch
- profession hats
- aptitude migration
- wetland release
- gene archiving
- mutation philosophy
- custom texture pass
- advanced Frog Slime tiers
- natural genetic froglight production

## Maintenance Rule

When a feature moves from planned to prototype, implemented, or hardened, update this file in the same change.

When a runtime smoke test finds a blocker, add it to [docs/planning/TODO.md](docs/planning/TODO.md) unless it is fixed immediately.
