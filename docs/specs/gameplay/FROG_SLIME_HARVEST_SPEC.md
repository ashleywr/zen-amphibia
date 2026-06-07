# Frog Slime Harvest Spec

**Status:** Draft  
**Created:** 2026-06-07  
**Depends on:** [Frog Output Doctrine](FROG_OUTPUT_DOCTRINE.md)  
**Scope:** Replace the death-drop model with a live-harvest cycle tied to frog condition, gene quality, and habitat care.

## Problem With the Current Model

`FrogDropHandler` currently produces slime on `LivingDropsEvent` — only when the frog dies.

This has several problems:

- It teaches the player that frogs are expendable inventory rather than animals worth keeping
- It produces no reason to improve habitat, breeding, or care
- It punishes the player for maintaining a healthy long-lived frog population
- Happiness gates are present in the code but the player never knows to act on them, because there is nothing to observe or interact with
- It directly contradicts the doctrine: care should drive output, not death

The death-drop model needs to be replaced entirely. Slime should come from caring for a living frog, not from killing one.

## Core Concept: Harvest-Ready State

A frog becomes **harvest-ready** over time when its welfare conditions are met.

When a frog is harvest-ready:

- it has built up a coating of secretion that can be collected without harm
- this state is visible through a distinct particle and a tooltip change
- the player can harvest by right-clicking with an empty hand or a collection jar
- harvesting resets a recovery cooldown before the frog can become ready again

If welfare conditions are not met, the frog will not reach readiness — or will reach it more slowly and with a reduced yield.

## Readiness Conditions

The following must all be true for a frog to progress toward readiness:

- Happiness is at or above the minimum threshold (`0.4`)
- The frog is not in a stress state (no overcrowding, no predator proximity)
- The previous harvest recovery cooldown has elapsed

If happiness falls below the threshold mid-cycle, the readiness timer pauses. It does not reset — it resumes when conditions improve. This rewards stable care rather than punishing brief lapses.

## Gene Influence

The relevant gene is `SLIME_YIELD`.

| Grade | Readiness Time | Yield | Recovery Time | Stability |
|-------|---------------|-------|---------------|-----------|
| D     | never ready   | 0     | —             | —         |
| C     | never ready   | 0     | —             | —         |
| B     | ~3 in-game days | 1   | ~2 days       | stable    |
| A     | ~2 in-game days | 1–2 | ~1.5 days     | stable    |
| S     | ~1.5 in-game days | 2–3 | ~1 day    | bonus chance |

Grade S frogs have a small chance to yield one additional unit when harvested at high happiness (≥ `0.9`).

Grades D and C never become harvest-ready regardless of happiness or habitat quality. These frogs still have biological value — line development, ecology, breeding — but slime is not their role.

## Happiness Modifiers

Happiness directly modifies readiness rate and yield within a grade.

| Happiness range | Effect |
|-----------------|--------|
| < 0.4           | Readiness paused |
| 0.4 – 0.6       | Base readiness rate, base yield |
| 0.6 – 0.8       | +15% readiness rate |
| 0.8 – 0.9       | +25% readiness rate, recovery shortened by 20% |
| ≥ 0.9           | Grade S bonus yield chance unlocked; +35% readiness rate |

## Habitat and Welfare Modifiers

These apply on top of happiness:

- **Overcrowding** — more than 4 frogs within 5 blocks adds a stress flag that halves readiness rate and suppresses yield for all affected frogs
- **Predator proximity** — a hostile mob within detection range adds stress for 30 seconds after the threat is gone
- **Recent disturbance** — if the frog witnessed a nearby cull, readiness is suppressed for the stress duration defined in the culling system
- **Ecological fit** — a frog in appropriate biome water conditions gains +10% readiness rate; a frog in a poor-fit environment loses 10%

These keep the output loop in the same design language as habitat and welfare already established in the mod.

## Visible Signals

The player should always be able to tell whether a frog is ready without opening any UI.

**Readiness signals:**
- Subtle bubble or sheen particle effect that appears when the frog is fully ready (distinct from ambient particles)
- Tooltip line changes from `"Condition: [grade]"` to `"Slime: Ready"` when the player hovers over the frog
- Optional: frog sits still briefly just before becoming ready, as a posture change

**Non-ready signals:**
- No special particle
- Tooltip shows normal condition line
- Stress state adds a red `"Stressed"` tooltip line

**Harvest feedback:**
- Right-click plays a soft wet sound (reuse or extend `SoundEvents.SLIME_SQUISH_SMALL`)
- Item appears in player's inventory or drops in world if full
- Frog plays a brief shimmy or idle animation if available

## Harvest Interaction

The player harvests by right-clicking a harvest-ready frog with:

- **Empty hand** — yields slime, enters recovery
- **Collection jar or bottle** — preferred item for habitat-side automation (defined later if a jar item is added)

Right-clicking a frog that is not ready does nothing — no message, no penalty. This keeps the interaction clean.

## Recovery

After harvest the frog enters a recovery period before becoming ready again.

- Recovery duration is grade-dependent (see table above)
- The recovery timer only runs while happiness is above the minimum threshold
- Harvesting before full recovery is not possible through the base interaction — the player cannot over-harvest by right-clicking faster
- Create automation via frogport should not bypass recovery

## Frogport Relationship

The current `FrogportGeneEvaluator` generates slime passively every 40 ticks based on `SLIME_VISCOSITY` grade. This was a prototype behavior.

The long-term model is:

- **Frogport does not generate slime** — it facilitates collection from frogs that are already ready
- A frogport with a valid genome target can detect nearby ready frogs and trigger a collection interaction on their behalf
- The gene grade of the housed frog influences the frogport's collection frequency or routing behavior, not its raw output quantity
- Slime enters the Create routing system through the frogport's output face, downstream of the frog's natural secretion cycle

The SLIME_VISCOSITY gene in `FrogportGeneEvaluator` and the SLIME_YIELD gene in `FrogDropHandler` should be reconciled into a single gene with a single name before harvest is fully implemented. The name `SLIME_YIELD` is preferred — it describes what the gene determines from the player's perspective.

The existing frogport slime output behavior in `FrogportGeneEvaluator` should be treated as **deprecated** and removed when the live harvest system is implemented.

## Death Drops

Slime should no longer drop from frog death.

Remove `addFrogSlimeDrops` from `FrogDropHandler.onLivingDrops`.

The death-drop model contradicts the doctrine, undermines the incentive to keep frogs alive, and creates a backdoor bypass for players who want slime without engaging with the harvest cycle.

If any death drop is retained for flavour purposes, it should be a single unit only and should not be grade-gated.

## Downstream Usage

Nothing in this spec changes what frog slime is used for. The existing crafting chain — smelt to ingot, craft tools and armor — remains. The slime boots fall-bounce behavior in `FrogSlimeEquipmentHandler` is untouched.

This spec only changes where slime comes from and how the player gets it.

## What This Spec Does Not Cover

- A habitat-side collection item (a jar, trough, or wall-mounted tray) that passively collects from ready frogs without player interaction — this is a follow-up spec if frogport automation does not cover the use case
- Secretion purity or quality as an item data component — that belongs to a later output family spec if frog slime ever needs to vary in quality as a distinct item rather than varying in yield count
- SLIME_YIELD interaction with future alchemy ingredient tags — that belongs to the alchemy compat spec

## Implementation Path

1. Add a `HARVEST_READY` boolean attachment to frogs (`AmphibiaAttachments`)
2. Add a readiness timer driven by happiness, gene grade, and stress state — tick this in an existing server-side event or a lightweight AI goal
3. Add particle effect for the ready state — client-side, keyed off the attachment flag
4. Add tooltip line logic to `FrogDNADisplayHelper` or a dedicated client handler
5. Add right-click handler on ready frog — check attachment, spawn item, set cooldown, play sound
6. Remove `addFrogSlimeDrops` from `FrogDropHandler`
7. Reconcile `SLIME_VISCOSITY` → `SLIME_YIELD` in `FrogportGeneEvaluator` and remove passive slime output

Steps 1–5 can ship before step 7. The frogport prototype can coexist until the full frogport collection model is defined.

## Evaluation

Using the doctrine evaluation check:

- Does the frog still matter as an animal? **Yes** — readiness requires care and observation
- Does the player benefit from understanding the frog's line and condition? **Yes** — grade and happiness are both visible
- Does care meaningfully affect the result? **Yes** — happiness, habitat, and ecology all factor in
- Is the output a byproduct of a believable frog process? **Yes** — secretion cycle, not passive drop timer
- Would the feature still make sense without Create installed? **Yes** — direct interaction works standalone
- Does compat sit downstream of Amphibia's identity? **Yes** — frogport facilitates collection, not production
