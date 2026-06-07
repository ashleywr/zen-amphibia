# TODO

This file tracks active design and implementation follow-up.

## Active Design TODOs

- Decide how hats are assigned:
  - directly equipped on frogs
  - stored in bucket data
  - assigned through a station
- Decide whether profession qualification is:
  - one primary grade threshold
  - or weighted multi-gene scoring
- Decide the first wetland release UX:
  - direct contextual action
  - or crafted release item
- Decide what gene archiving preserves:
  - full genome
  - summary profile
  - mutation unlocks
  - or all three

## Approved Direction To Implement First

- Create a first profession loop around:
  - `Create Goggles`
  - `Foreman Cap`
  - `Worker Frogport`
  - `Supervisor Perch`
- Support that loop with the first aptitude set:
  - `REACH`
  - `WORK_RATE`
  - `FOCUS`
  - `AWARENESS`
  - `INTELLIGENCE`
  - `TEMPERAMENT`

## Code Follow-Up

- Use [MOD_LIFECYCLE.md](/C:/WorkDir/Minecraft%20Mods/Amphibia/MOD_LIFECYCLE.md) to track implemented, prototype, hardened, planned, and backlog status.
- Run the Survival Frog Husbandry smoke test and move passing systems toward hardened status.
- Finish the Create integration exit pass:
  - runtime smoke test worker frogport crafting, placement, targeting, dispatch, DNA persistence, and reach bonus
  - fix only blocking compatibility issues found by the smoke test
  - add focused checks for recipe output, duplicate recipe absence, DNA item support, and worker tooltip summary
- Decide whether the existing `FrogportGeneEvaluator` should be repurposed or replaced after the aptitude redesign.

## Documentation Follow-Up

- Once profession direction stabilizes, add a short summary section to the roadmap.
- Keep this planning hub updated whenever a new spec is added or a major direction changes.
- Write the first `Frog Slime Harvest Spec` using [Frog Output Doctrine](/C:/WorkDir/Minecraft%20Mods/Amphibia/docs/specs/gameplay/FROG_OUTPUT_DOCTRINE.md) as the design constraint.

## Missing Specs To Write

- `Profession Hat UX Spec`
  Define how hats are crafted, assigned, stored, swapped, and displayed on frogs or stations.
- `Wetland Release Spec`
  Define release rules, habitat validation, local gene-pool scope, and player feedback.
- `Gene Archiving Spec`
  Define what archiving preserves and how archived genetics are viewed or reused.
- `Mutation Philosophy Spec`
  Define what mutations are for after professions move to hats and aptitudes.
