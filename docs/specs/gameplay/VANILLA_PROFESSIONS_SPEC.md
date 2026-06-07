# Vanilla Professions Spec

**Status:** Draft  
**Created:** 2026-05-28  
**Scope:** Define the professions Amphibia should support without external mods, prioritize a small initial roster, and describe the player experience of breeding and deploying those frogs.

## Overview

Amphibia needs a smaller and more grounded vanilla profession set than the broad cross-mod profession map described in [FROG_PROFESSION_SPEC.md](/C:/WorkDir/Minecraft%20Mods/Amphibia/docs/specs/gameplay/FROG_PROFESSION_SPEC.md).

This spec exists to answer a simpler question:

If the player installs only Amphibia, what frog jobs are actually fun, understandable, and technically realistic?

The answer should not be "every possible profession family." It should be a short roster that:

- fits the mod's husbandry identity
- works with believable Minecraft entity behavior
- creates strong breeding goals
- teaches the profession system clearly

## Design Principles

- Vanilla professions should stand on their own without Create or other mods
- The first profession should have a strong player fantasy and low AI risk
- Bounded, local jobs are better than long-range autonomous worker fantasies
- Profession roles should create visible, satisfying payoff for breeding effort
- The player should be able to understand why a frog is promising before deep spreadsheet play
- The first roster should be intentionally small

## Profession Categories

For planning purposes, vanilla professions should be split into three buckets.

### 1. Vanilla-first

These should exist in base Amphibia and help define the core breeding game.

### 2. Integration-first

These may exist conceptually, but should not drive vanilla design.

### 3. Deferred

These may be interesting later, but should not be treated as near-term commitments.

## Recommended Vanilla-First Roster

Start with a very small roster:

- `Warden`
- `Naturalist`

Optional third role later:

- `Foreman`

This is enough to define:

- a combat-oriented line
- a husbandry-oriented line
- optionally a management-oriented line

It is not necessary to launch vanilla Amphibia with full foraging, trade, craft, and arcane roles all at once.

## Profession: Warden

This should be the first fully realized vanilla profession.

### Core fantasy

A Warden frog is a territorial defender.

It is not primarily a roaming party-member AI. It is a trained guard frog that protects a place, a pen, or a bounded home area.

This matters because Minecraft pet-follow AI is often mediocre in open-world travel. Wolves already demonstrate the limits of designing around perfect adventure-companion behavior.

The Warden role should therefore promise:

- dependable local defense

Not:

- perfect full-time combat pet behavior everywhere the player goes

### Core jobs

The Warden should start with local, bounded roles:

- `Pen Defender`
- `Perch Guardian`
- `Pest Hunter`

Optional later mode:

- `Escort`

But escort/follow mode should be treated as an extension, not the initial promise of the profession.

### Player fantasy

The player should feel:

- "I bred a frog line strong enough to protect my frogs and home."

Not:

- "I built a complicated AI companion and hope pathfinding behaves."

### Qualification

Primary qualifier:

- `Power`

Secondary performance:

- `Hardiness`
- `Awareness`

### Phenotype tie-ins

The Warden line should feel visible, not purely hidden.

Recommended phenotype relevance:

- `Size`
  Larger frogs feel more imposing and tougher
- `Coloration`
  May help players visually identify preferred bloodlines

These do not need to dominate balance, but they should contribute to the feeling that a Warden line is a distinct breed, not only a hidden stat outcome.

### Job item

Suggested hat:

- `Warden Band`

The hat should:

- clearly identify the role
- be easy to understand
- signal the frog is on guard duty

### Player progression

The intended Warden breeding loop is:

1. collect and inspect frogs
2. notice a few with promising `Power`
3. breed for stronger `Power`
4. stabilize `Hardiness` and `Awareness`
5. reach `B` qualification
6. craft and assign the `Warden Band`
7. deploy the frog to defend a home area
8. continue breeding for stronger lines

This loop is good because:

- it gives a clear first goal
- it gives a first success threshold
- it offers meaningful optimization afterward

### Tier expectations

The profession should have clear qualitative tiers.

#### `B` Warden

- qualifies to wear the `Warden Band`
- defends a local area from basic threats
- acceptable but not exceptional response speed or durability

#### `A` Warden

- faster threat recognition
- better survival
- more reliable local defense behavior

#### `S` Warden

- elite local defender
- strongest durability and reaction behavior
- may unlock premium role perks or mutation-enhanced specialization later

These differences should be felt in behavior, not just displayed in text.

### AI expectations

The initial Warden should use bounded AI.

Recommended behavior model:

- linked to a home perch, pen, or defended radius
- patrols or idles locally
- attacks configured hostile targets entering that area
- returns to its home zone after combat

This keeps the role technically realistic and easier to polish.

### Territory anchor

The Warden needs a clear home or defended anchor.

Recommended approach:

- use a vanilla `Bell` as the temporary prototype anchor
- later replace it with a dedicated Amphibia `Warden Perch` or `Guard Perch`

Why `Bell` works as a prototype:

- players already read it as a village or alert center
- it is easy to understand as a defended focal point
- it gives the role a simple bounded-area reference without waiting for a custom block

Why it should not be the final solution:

- the fantasy is only approximate
- Amphibia will likely want frog-specific visuals and assignment behavior
- a custom perch will better support long-term profession UX

### What to avoid

- designing the Warden around long-distance follower AI first
- requiring perfect pathfinding over varied terrain
- treating it like a party-member RPG companion

Those are the exact places vanilla Minecraft pet AI is weakest.

## Profession: Naturalist

This should be the second vanilla-first profession.

### Core fantasy

A Naturalist frog supports husbandry, habitat, and breeding systems.

This role exists so the profession system is not only combat or automation.

### Core jobs

Possible early jobs:

- `Breeding Attendant`
- `Habitat Calmer`
- `Nursery Watcher`

The exact mechanics can be specified later, but the job family should reinforce:

- frog care
- habitat quality
- breeding success
- reduced pen stress

### Qualification

Suggested primary qualifier:

- `Temperament`

Suggested secondary performance:

- `Awareness`
- `Affinity`

### Why this matters

This role fits Amphibia's identity better than forcing a shaky free-roaming forager early.

It also naturally complements:

- retirement systems
- culling distress
- terrarium happiness
- nursery and pond management

### Status

Naturalist should be recognized as a vanilla-first role, but not necessarily implemented before Warden.

It can follow once the profession and hat framework is proven.

## Optional Vanilla Role: Foreman

Foreman may exist in vanilla Amphibia, but only if you want a base-game management role before Create-specific supervision.

### Reason to delay

The strongest Foreman fantasy currently overlaps with Create and Zen Atelier control-room ideas.

Without those systems, a vanilla Foreman risks being underpowered or unclear.

### Recommendation

- keep Foreman conceptually valid
- do not treat it as part of the first vanilla roster by default

## Roles That Should Not Drive Vanilla V1

These may still exist later, but they should not define the first base-Amphibia profession experience.

### Forager

Reason:

- local item-gathering AI is possible, but the fantasy still feels less solid than Warden or Naturalist right now
- it risks overcommitting to autonomous worker behavior before the profession framework is proven

### Trader

Reason:

- better as a later villager or MineColonies-facing expansion

### Crafter

Reason:

- likely stronger once there are more Amphibia-native workstations to justify it

### Arcane

Reason:

- mutation and magic systems need more maturity before this becomes satisfying

## Recommended Vanilla Profession Order

### First

- `Warden`

### Second

- `Naturalist`

### Third if needed

- `Foreman`

This is enough to establish:

- combat breeding
- husbandry breeding
- later stationed management

Without pretending the mod already supports a whole zoo of mature careers.

## Relationship to the Genetics Model

The vanilla profession set should drive the first practical test of the four-layer genetics model:

- `Aptitudes`
  Example: `Power`, `Hardiness`, `Awareness`, `Temperament`, `Affinity`
- `Phenotypes`
  Example: `Size`, `Coloration`
- `Ecology`
  Example: `Heat Tolerance`, `Humidity Tolerance`
- `Mutations`
  Premium overlays, not profession gates

The Warden role is especially useful because it pressure-tests whether these trait names actually feel like breeding traits instead of generic stats.

## Player Experience Test

The vanilla profession system is working if a player can naturally think:

- "I want a stronger guard line."
- "This frog looks promising."
- "I finally bred one that qualifies for the band."
- "Now it really does protect my base better."

If the player instead feels:

- "I am manipulating hidden numbers to make AI maybe work,"

then the design has failed.

## Open Questions

- What exact block or anchor defines a Warden's defended territory?
- Should Warden targeting be broad hostile defense or a smaller curated mob list at first?
- Should Naturalist affect breeding directly, habitat happiness, or both?
- How much should phenotype matter mechanically versus visually for vanilla professions?
- Should a Warden ever have a true player-follow mode in vanilla, or only a bounded escort mode?

## Recommendation

Treat `Warden` as the first real vanilla profession and define it as a **territorial defender**, not a high-expectation travel companion.

Follow it with `Naturalist` as the first husbandry-support role.

Keep the rest of the profession map as future structure, not near-term obligation.
