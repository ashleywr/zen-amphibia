# Frog Professions and Aptitude Genetics Spec

**Status:** Draft  
**Created:** 2026-05-28  
**Scope:** Define frog professions, profession hats, qualification rules, integration gating, and the genetics redesign needed to support role-focused breeding.

## Overview

Amphibia needs a profession system that works backward from gameplay jobs rather than forward from abstract frog traits.

The current genetics model was built as a general-purpose frog DNA system. That works for breeding flavor, slime output, visuals, and some stat scaling, but it does not yet give a strong foundation for specialized industrial, social, magical, or crafting roles.

This spec defines:

- what professions frogs should have
- how profession assignment works
- how mod integrations are gated
- how genes should be redesigned to support those jobs

The core idea is:

- `aptitudes` define workforce suitability
- `phenotypes` define visible physical traits
- `ecology` defines environmental fit
- `hats` define profession access
- `stations` define the exact assigned job
- `mutations` remain beneficial special traits, not basic profession gates

## Problem Statement

Right now the breeding system has a working genome model, but the gameplay targets are not clear enough.

Symptoms:

- some genes are flavorful but hard to translate into useful jobs
- the Create frogport has genome plumbing but not a coherent role model
- mutations risk becoming overloaded if they are asked to define whole careers
- new mod integrations could easily turn into one-off compat gimmicks instead of part of a workforce system

The fix is to start from gameplay needs:

- what jobs do players want frogs to perform
- what stations or mods do those jobs belong to
- what aptitudes should make a frog good or bad at those jobs

Then the DNA system can be redesigned to serve those jobs directly.

## Design Principles

- Players should breed toward clear, legible goals
- Frogs should be specialized, not universally optimal
- Profession assignment should be explicit and readable in-world
- Mod integrations should be gated through profession items, not hidden compat rules
- Base Amphibia should still have meaningful professions without external mods
- Mutations should stay exciting and beneficial, not mandatory for baseline labor
- Existing breeding and inspection systems should still matter after the redesign

## Core Model

The profession system should use four layers.

### 1. Aptitudes

These are internal breed traits that determine whether a frog is naturally suited for certain jobs.

Examples:

- `Power`
- `Hardiness`
- `Quickness`
- `Cunning`
- `Awareness`
- `Temperament`
- `Affinity`
- `Attunement`

### 2. Phenotypes

These are visible or strongly world-facing physical traits.

Examples:

- `Size`
- `Coloration`
- `Tongue Length`
- `Slime Yield`

### 3. Ecology

These determine what environments suit the frog best.

Examples:

- `Heat Tolerance`
- `Humidity Tolerance`

### 4. Profession hats

These are crafted or unlocked items that assign frogs to a profession family.

### 5. Workstations

These blocks or assignment contexts decide the frog's exact active task.

### 6. Mutations

These add rare bonuses, quirks, or advanced variants on top of the profession system.

This means:

- a frog can be genetically suited for a profession
- but still needs the correct hat to enter that role
- and still needs the correct station to perform the actual work

## Why Hats

Hats solve several design problems at once.

### Readability

The player can identify role assignment immediately.

Examples:

- `Create Goggles`
- `Foreman Cap`
- `MineColonies Top Hat`
- `Workshop Apron`

### Agency

The player chooses what line to test in what role instead of waiting on pure RNG role unlocks.

### Integration gating

Mods can be represented by profession items rather than fragile invisible compat behavior.

### Role flexibility

A frog line can be bred for an aptitude and then assigned to one of several jobs within that profession family.

## Qualification Model

Hats should not be freely usable by any frog.

The player must first breed a line that reaches a minimum aptitude threshold.

Recommended structure:

- `1 primary qualification trait` per profession
- `1-2 secondary performance traits` per profession

### Qualification rule

- below the minimum threshold: frog cannot meaningfully take the job
- at threshold: frog is qualified for baseline work
- higher grades: frog performs substantially better

Recommended threshold model:

- `B` = qualified
- `A` = strong
- `S` = elite

This teaches the player:

- breeding unlocks the job
- continued breeding optimization improves the job

## Profession Families

Professions should be designed around gameplay families first, then mapped to hats and stations.

Recommended top-level families:

- `Industry`
- `Supervision`
- `Foraging`
- `Craft`
- `Trade`
- `Combat`
- `Arcane`

Not all need to ship at once, but the genetics model should anticipate them.

## Profession Family: Industry

Purpose:
- item movement
- packaging
- routing
- factory-side logistics labor

Suggested hat:
- `Create Goggles`

Suggested stations:
- worker frogport
- package handler perch
- future stock-balancing or live-cargo stations

Suggested jobs:
- package worker
- courier
- cargo handler

Primary qualifier:
- `Tongue Length`

Secondary performance:
- `Quickness`
- `Cunning`

Why these matter:

- `Tongue Length` determines whether the frog can physically handle logistics work well
- `Quickness` determines throughput
- `Cunning` improves reliability and routing precision

## Profession Family: Supervision

Purpose:
- factory monitoring
- control-room status reporting
- system interpretation

Suggested hat:
- `Foreman Cap`

Suggested stations:
- supervisor perch
- control-room observer block

Suggested jobs:
- line monitor
- bottleneck reporter
- alarm or dashboard source

Primary qualifier:
- `Awareness`

Secondary performance:
- `Cunning`
- `Temperament`

This profession is detailed further in [FROGPORT_SUPERVISOR_SPEC.md](/C:/WorkDir/Minecraft%20Mods/Amphibia/docs/specs/gameplay/FROGPORT_SUPERVISOR_SPEC.md).

## Profession Family: Foraging

Purpose:
- environmental item finding
- natural resource production
- frog-native material collection

Suggested hat:
- `Forager Band`

Suggested stations:
- resource terrarium
- hunting/perching station
- wetland collector block

Suggested jobs:
- slime gatherer
- froglight specialist
- natural item finder

Primary qualifier:
- `Awareness`

Secondary performance:
- `Quickness`
- `Temperament`

Why this matters:

- this keeps a non-Create profession family at the heart of base Amphibia

## Profession Family: Craft

Purpose:
- workshop support
- recipe assistance
- production-line craftsmanship

Suggested hat:
- `Workshop Apron`

Suggested stations:
- tinkering table
- frog-assisted bench
- crafting supervisor station

Suggested jobs:
- assembler
- quality improver
- specialty crafter

Primary qualifier:
- `Quickness`

Secondary performance:
- `Cunning`
- `Temperament`

This family should support both Amphibia-native crafting and future modded crafting integrations.

## Profession Family: Trade

Purpose:
- villager interaction
- social exchange
- stock brokerage
- colony-side supply roles

Suggested hat:
- `Broker Ribbon`
- or integration-specific `MineColonies Top Hat`

Suggested stations:
- villager exchange post
- colony office
- trade counter

Suggested jobs:
- naturalist liaison
- stock broker
- messenger

Primary qualifier:
- `Affinity`

Secondary performance:
- `Cunning`
- `Temperament`

This family is a strong fit for villager and MineColonies integration.

## Profession Family: Combat

Purpose:
- defense
- escort
- hostile creature suppression

Suggested hat:
- `Warden Band`

Suggested stations:
- guard perch
- patrol post

Suggested jobs:
- pen defender
- escort frog
- pest hunter

Primary qualifier:
- `Power`

Secondary performance:
- `Hardiness`
- `Awareness`

This family should likely remain more niche than industry or foraging.

## Profession Family: Arcane

Purpose:
- strange biology
- magic-adjacent behavior
- mutation expression
- interaction with magical systems or dimensions

Suggested hat:
- `Arcane Hood`

Suggested stations:
- ritual pool
- mutation lab
- occult perch

Suggested jobs:
- mutation catalyst
- anomaly monitor
- magical reactor assistant

Primary qualifier:
- `Attunement`

Secondary performance:
- `Cunning`
- `Temperament`

Arcane roles are where mutations should feel especially relevant, but mutations should still enhance rather than fully gate the role.

## Integration Gating

Mod integrations should be unlocked through hats that represent certified profession families.

### Why this is useful

- players can understand at a glance which frogs interact with which systems
- compat content becomes modular and scalable
- base Amphibia professions remain coherent even when the mod is installed alone

### Recommended rule

- hats gate profession families into external systems
- stations define the exact job inside that system

Examples:

- `Create Goggles` unlock Create-side industrial jobs
- `MineColonies Top Hat` unlock colony-side labor or administration jobs
- future magic-mod hood unlocks arcane compat jobs

This prevents "all frogs can suddenly do everything with every mod" design sprawl.

## Mutation Role

Mutations should not be required for ordinary profession access.

Instead, mutations should act as rare premium modifiers.

Examples:

- `Ender` mutation improves range, unusual routing, or anomaly handling
- `Creeper` mutation improves demolition, mining, or explosive defense jobs
- future mutations improve specialty outputs or unlock elite variants of a role

Design rule:

- a profession hat should provide baseline access
- genes provide qualification and competence
- mutations provide rare bonuses on top

This keeps mutations exciting without making the whole profession system RNG-locked.

## Working Backward to Gene Design

The original six-gene list was not built around professions. The implementation has now moved to the layered model below: aptitudes, phenotypes, ecology, and mutations.

## Recommended Genetics Redesign

The best redesign is to separate the genome into four layers:

- `Aptitudes`
- `Phenotypes`
- `Ecology`
- `Mutations`

### Aptitudes

These are the breeder-facing internal traits that govern professions.

- `Power`
- `Hardiness`
- `Quickness`
- `Cunning`
- `Awareness`
- `Temperament`
- `Affinity`
- `Attunement`

These should be shared across professions instead of inventing a bespoke stat for every job.

### Phenotypes

These are external physical traits players can see or infer more easily.

- `Size`
- `Coloration`
- `Tongue Length`
- `Slime Yield`

### Ecology

These govern habitat fit and environmental comfort.

- `Heat Tolerance`
- `Humidity Tolerance`

### Mutations

These remain rare overlays with unusual effects or bonuses.

## Recommended Release Sequence

Do not redesign the entire genome in one pass.

### Phase 1

Add enough traits for the first two profession families:

- `Power`
- `Hardiness`
- `Quickness`
- `Cunning`
- `Awareness`
- `Temperament`
- `Tongue Length`

These support:

- Create worker jobs
- Create supervisor jobs

### Phase 2

Add non-Create core professions:

- `Affinity`
- refine `Size`
- refine `Slime Yield`
- refine ecological fit traits

These support:

- foraging
- craft

### Phase 3

Add broader arcane and specialized profession support:

- `Attunement`
- any extra phenotype or ecology traits the earlier roles prove necessary

These support:

- trade
- combat
- magic

## Compatibility With Existing DNA

The existing genome system should not be discarded lightly because it already supports:

- item and block DNA persistence
- breeding inheritance
- grade calculation
- display and tooltip plumbing

The better move is to evolve the meaning of the gene catalog while keeping the genome machinery.

Recommended approach:

- keep `FrogGenome`, allele pairs, and grade logic
- reinterpret the gene catalog around the four-layer model
- add new aptitude, phenotype, and ecology traits in controlled phases

## Profession Assignment Flow

The intended player loop should be:

1. catch or breed frogs
2. inspect visible genetics
3. identify a line with promising aptitude and phenotype
4. breed until the primary qualifier reaches `B` or higher
5. craft the relevant profession hat
6. assign the hat to the qualified frog
7. place the frog at the appropriate station
8. continue breeding for `A` and `S` performance

This is the core breeding-to-workforce bridge the current design is missing.

## User Experience Rules

The profession system must stay legible.

### Tooltips should show

- profession hat name
- primary qualifier
- whether the frog qualifies
- key secondary strengths
- any visible phenotype that matters to the role

### Failed assignment should show

- what aptitude is too low
- what grade is required

### Station UI should show

- current worker role
- active efficiency tier
- if relevant, whether the frog is distressed, unqualified, or outdoors-degraded

## First Implementation Recommendation

The first profession release should stay tightly scoped.

Implement first:

- `Create Goggles`
- `Foreman Cap`
- `Worker Frogport`
- `Supervisor Perch`

And support them with the first trait set:

- `Power`
- `Hardiness`
- `Quickness`
- `Cunning`
- `Awareness`
- `Temperament`
- `Tongue Length`

This gives the system:

- a clear hat model
- a clear qualification model
- a strong Create integration path
- a reason to redesign the existing frogport genetics around actual jobs

## Open Questions

- Should hats be equipped directly on frog entities, stored in bucket data, or assigned at a station?
- Should hats be swappable freely, or require removal/reassignment cost?
- How much of the old gene set should be preserved versus renamed?
- Should profession qualification require one grade threshold or a weighted combined score?
- Should some hats be Amphibia-native and some explicitly mod-branded, or should all integrations use branded hats?
- When and how should the guidebook teach profession breeding?

## Recommendation

Amphibia should redesign professions from gameplay needs outward.

The strongest structure is:

- professions define the player goal
- aptitudes define breeding targets
- phenotypes provide visible identity
- ecology shapes habitat fit
- hats define role access
- stations define exact work
- mutations define rare premium variants

That gives the breeding system a clear purpose and prevents future Create, MineColonies, trade, craft, or magic integrations from becoming disconnected one-off mechanics.
