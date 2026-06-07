# Create Integration Finish Plan

## Scope

This plan defines the remaining Create-side work needed before Amphibia can move focus back to core frog systems.

The goal is not to finish every possible Create idea. The goal is to make the current worker frogport loop coherent, testable, documented, and good enough to stop treating Create integration as the active center of development.

## Current State

Implemented:

- Create's package frogport recipe is overridden to output `zen_amphibia:worker_frogport`.
- `worker_frogport` is an Amphibia-owned block and item.
- The worker frogport stores frog DNA from the frog bucket used in crafting.
- The worker frogport uses Create's frogport block entity type through NeoForge's valid-block event.
- Create item checks that special-case package frogports also accept `worker_frogport`.
- Worker genetics affect package handoff animation speed, package-port placement reach, dispatch reliability, and Frog Slime residue chance on package dispatch.
- Tooltips and goggles show a readable worker summary.

Known limitation:

- The newest Create compatibility mixins compile but still need in-game smoke testing.

## Finish Criteria

Create integration is done enough when all of these are true:

1. A player can craft the normal package frogport recipe with an Amphibia frog bucket and reliably receive `zen_amphibia:worker_frogport`.
2. Placing that item always creates the Amphibia-owned block, not `create:package_frogport`.
3. DNA survives item crafting, placement, breaking, and re-placement.
4. Create's normal frogport placement UX works while holding `worker_frogport`.
5. Worker traits have at least three visible effects: work rate, reach, and slime residue or reliability.
6. The behavior is explained in one player-facing guide page or equivalent tooltip coverage.
7. The code has a small regression test or smoke checklist for recipe output and DNA persistence.
8. No passive frogport resource generation remains.
9. Remaining Create ideas are moved to backlog instead of blocking core frog work.

## Required Work

### 1. Runtime Smoke Test

Run a dev client and verify:

- recipe output is `zen_amphibia:worker_frogport`
- item tooltip shows worker stats when DNA exists
- placed block ID is `zen_amphibia:worker_frogport`
- block entity keeps `AmphibiaGenome`
- breaking and replacing keeps DNA through item NBT
- chain conveyor targeting works with `worker_frogport`
- package dispatch triggers animation speed and residue behavior
- reach bonus is accepted by both client preview and server save

This is the highest-priority remaining task because the latest mixins patch Create runtime flows.

### 2. Add Focused Regression Coverage

Add automated checks where practical:

- recipe JSON output is Amphibia's worker frogport
- no standalone duplicate `zen_amphibia:worker_frogport` recipe exists
- worker frogport item can carry `FROG_DNA`
- tooltip summary generation reports work rate, reach, reliability, and residue

Do not overbuild tests around Create internals that are better covered by the runtime smoke checklist.

### 3. Tighten Player Feedback

Add or confirm player-readable feedback:

- worker frogport tooltip summary
- goggle summary on placed worker
- short guidebook entry once behavior stabilizes
- clear distinction from a normal Create frogport

Avoid adding a full UI until there is a real need for configuration or worker management.

### 4. Balance First-Pass Numbers

Use conservative starting numbers:

- `QUICKNESS`: noticeable but not instant
- `TONGUE_LENGTH`: small bonus over Create's configured range
- `TEMPERAMENT`: reliability, not raw output
- `SLIME_YIELD`: package-linked residue only

The first pass should make good workers feel better without making normal Create logistics obsolete.

### 5. Document the Final Worker Contract

Update these files after the smoke test:

- [Frogport Design Notes](./FROGPORT_DESIGN.md)
- [Amphibia Roadmap](../../../AMPHIBIA_ROADMAP.md)
- [Planning TODO](../../planning/TODO.md)

The final docs should say:

- worker frogports are the Create integration baseline
- supervisor perch is future work
- deeper Create automation belongs in backlog

## Backlog After Exit

These are explicitly not required before returning to core frog work:

- slimed package item variants
- advanced routing intelligence
- full packager behavior modification
- supervisor perch implementation
- profession hat framework
- aptitude migration from old gene names
- Create ponder scenes
- custom frogport model/texture pass

## Recommended Order

1. Runtime smoke test the current implementation.
2. Fix only blocking Create compatibility issues.
3. Add focused regression coverage.
4. Add guidebook/player-facing text.
5. Update roadmap and TODO.
6. Push the Create integration commits once the smoke test passes.
7. Return to core frog work.

## Exit Decision

After the smoke test and documentation updates, treat Create integration as complete for the current milestone.

Future Create work should require a specific feature request or a failed compatibility report, not general polish.
