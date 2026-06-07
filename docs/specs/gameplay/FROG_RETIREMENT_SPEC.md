# Frog Retirement and Population Control Spec

**Status:** Draft  
**Created:** 2026-05-28  
**Scope:** Define how Amphibia handles excess frogs, breeder turnover, and non-lethal retirement so the player is not pushed into manual culling as the default workflow.

## Overview

Amphibia needs a deliberate retirement model for frogs.

If breeding works well, players will quickly produce more frogs than they want to house, inspect, or keep as active stock. A good genetics game needs a way to sort, keep, repurpose, or remove excess individuals without making "kill the extras" the main answer.

This spec defines the retirement philosophy and the preferred sink paths for frogs that are no longer part of the player's active breeding or workforce lines.

## Problem Statement

Without retirement paths, frog husbandry will create several bad outcomes:

- pens fill with low-value surplus frogs
- players feel punished for exploring breeding
- the optimal answer becomes killing or abandoning frogs
- useful line management becomes tedious inventory and entity cleanup

Forestry solved a similar problem structurally by making queens temporary and drones disposable surplus. Amphibia should solve it differently, because frogs are individual animals with stronger identity than bee drones.

The goal is not to eliminate population pressure. The goal is to channel that pressure into meaningful progression actions.

## Design Principles

- Killing frogs should never be the intended primary sink
- Non-lethal retirement should be mechanically smart, not just morally nice
- The player should feel rewarded for sorting lines, not punished for breeding
- Good frogs should have more retirement value than bad frogs
- Some retirement paths should preserve lineage value
- Some retirement paths should permanently convert frogs into infrastructure
- Population control should happen through systems, not only through entity caps or decay

## Retirement Categories

Excess frogs should fall into one of four categories:

1. `Keep`
- breeder stock
- active workforce frogs
- display or collection frogs

2. `Convert`
- frogs turned into infrastructure or long-term utility

3. `Release`
- frogs returned to the world to influence future wild genetics

4. `Archive`
- frogs retired into research, records, or genetic preservation

These are the intended management tools. Direct killing exists, but should be strictly inferior for long-term progression.

## Intended Core Loop

The retirement loop should look like this:

1. breed frogs
2. inspect genes and mutations
3. keep a few breeder or worker candidates
4. convert some qualified frogs into jobs or blocks
5. release some frogs back into habitat
6. archive exceptional lines for future use

The player should feel like a breeder-manager, not a slaughterhouse operator.

## Retirement Path 1: Worker Conversion

This is the most industrial sink and should be one of the strongest.

Examples:

- frog bucket + frogport recipe consumes the frog and creates a permanent frogport worker
- future supervisor perch assignment may permanently consume or station a frog
- other job blocks may absorb the frog as assigned infrastructure

Why this works:

- converts an entity-management problem into block infrastructure
- makes good frogs feel invested into the factory
- creates a meaningful sink for qualified lines

Design rule:

- conversion should be permanent enough to matter
- the resulting block should preserve or display the frog's genetics in some form

## Retirement Path 2: Wetland Release

This should be the primary ecological retirement path.

### Core concept

The player releases a frog into a valid natural or semi-natural habitat.

That frog is removed from active ownership and contributes genetically to a local wetland gene pool.

### Expected benefits

- future wild frogs or frogspawn in that area can inherit some released traits
- local release contributes to lineage discovery or biome adaptation goals
- releasing frogs becomes a way to seed useful wild populations

### Why it works

- it is non-lethal
- it gives excess frogs long-tail value
- it supports the fantasy that breeding changes ecosystems over time

### Design rule

- released frogs should not instantly duplicate the player's exact line everywhere
- release should bias local future genetics, not fully replace natural randomness

## Retirement Path 3: Gene Archiving

This is the knowledge-preservation path.

### Core concept

The player retires a frog by extracting or recording its lineage.

Possible forms:

- genetic imprint
- archive vial
- lineage entry in a bestiary or codex
- preserved frogspawn sample

### Expected benefits

- stores notable genes or mutation discoveries
- contributes to progression, guidebook unlocks, or analysis tools
- gives exceptional frogs value even if they are not needed as live breeders

### Why it works

- supports collection and completionist play
- reduces fear of "losing" a great line forever
- encourages players to engage with breeding instead of hoarding every specimen

### Design rule

- archiving should preserve information and maybe future access
- it should not completely replace the need to maintain live lines

## Retirement Path 4: Sanctuary / Display Retirement

This is the soft housing solution.

### Core concept

The player can place frogs into a non-breeding, low-maintenance sanctuary state.

Examples:

- decorative frog sanctuary block or enclosure
- museum terrarium
- breeder's hall of fame

### Expected benefits

- preserves beloved or rare frogs without keeping them in the production loop
- removes breeding pressure from emotional favorites
- gives builders a reason to keep a few non-functional specimens

### Why it works

- not every retirement path should be utilitarian
- it respects that frogs are more personal than bee drones

### Design rule

- sanctuary frogs should not count as active breeders
- sanctuary should be flavor-first, not the main optimization sink

## Retirement Path 5: Trade / Donation

This is the social or integration-driven sink.

### Core concept

The player hands frogs over to another system or faction.

Possible future examples:

- villager naturalist exchange
- MineColonies animal office or curator request
- research guild or lab submission

### Expected benefits

- rewards players for raising qualified or themed lines
- creates integration-specific demand for frogs
- turns extra stock into progression or reputation

### Why it works

- gives excess frogs a market role
- helps connect genetics gameplay to other mods

### Design rule

- donation targets should prefer specific jobs, lineages, or visible traits
- this path should not be the only retirement method

## Why Direct Culling Should Be Weak

Killing frogs can remain possible, but it should be the least interesting answer.

If direct culling is too rewarding, the system teaches the wrong lesson:

- breed many frogs
- kill most of them
- keep only one output-efficient line

That undermines both the ecological tone and the long-term husbandry loop.

Recommended rule:

- killing a frog gives little or no unique progression value
- non-lethal retirement gives better strategic returns

This keeps the kind option aligned with the smart option.

## Culling Distress

Direct culling should also create short-term local consequences for nearby frogs.

This should work similarly in design spirit to villager distrust systems, but with animal behavior rather than social reputation.

### Core concept

If frogs witness nearby frog killing, they become distressed.

Distress is a temporary local state representing fear, stress, and habitat insecurity.

### Why this exists

- teaches the player in-world that culling is poor husbandry
- discourages slaughter inside breeding pens
- reinforces the happiness system already present in Amphibia
- makes the smart answer "release, archive, or convert" instead of "kill in bulk"

### Expected effects

Nearby frogs that witness culling may:

- flee or avoid the killer briefly
- lose happiness
- refuse breeding for a time
- show visible panic behavior
- perform worse in sensitive systems if distress is severe

### Design rule

- the effect must be local, readable, and temporary
- it should punish repeated bad management, not one accidental event forever
- recovery should happen naturally through time and good habitat conditions

## Suggested Distress Rules

This is a design target, not a locked implementation.

### Trigger

A frog dies within a nearby radius from:

- direct player damage
- tamed/owned entity damage
- deliberate industrial damage sources, if tracked

Natural deaths or clearly unrelated hostile events should either not trigger distress or trigger a much weaker form.

### Affected frogs

Frogs in a local radius around the death site.

Priority targets:

- adult frogs in breeding pens
- nearby tamed or managed frogs
- possibly nearby tadpoles for visual panic only

### Effects

- apply temporary happiness penalty
- suppress breeding attempts during the distress window
- trigger short flee behavior or panic movement
- optionally reduce workforce effectiveness for employed frogs during active distress

### Recovery

- distress expires after time
- high-quality terrarium or habitat conditions speed recovery
- repeated witnessed deaths refresh or stack the penalty up to a cap

## Relationship to Happiness

Distress should not be a completely separate opaque system if it can reuse the existing happiness framework.

Recommended integration:

- witnessed culling applies a temporary negative modifier to happiness
- low happiness then naturally reduces breeding and other positive systems
- habitat quality gradually restores the frog population

This keeps the system coherent:

- good rooms calm frogs
- bad husbandry stresses frogs
- breeding results reflect both genetics and care

## Relationship to Retirement Paths

Distress is not itself a retirement mechanic.

It exists to make the bad option self-defeating and to push players toward the intended retirement paths:

- `Convert`
- `Release`
- `Archive`
- `Donate`

That means the player still has agency, but the system clearly communicates which choice is sustainable.

## Population Pressure Still Matters

This spec does not remove the need for population management.

Population pressure should still exist through:

- breeding stock space
- habitat quality
- active pen crowding penalties
- line management effort

The difference is that the player is given intelligent exits instead of only punishment.

## Relationship to Existing Population Systems

The current mod already has useful early pressure:

- frogs stop laying eggs when local population is high
- tadpoles show overcrowding symptoms
- severe overcrowding can cause failure to thrive

These systems are good early brakes.

Retirement systems should be the positive answer that follows those warnings:

- "this pond is too full"
- therefore release, convert, archive, or donate some frogs
- do not start killing stock in view of the remaining line

## Relative Value of Retirement Paths

Not all sinks should be equal.

Recommended value hierarchy:

1. `Convert`
   Best for qualified workforce frogs
2. `Archive`
   Best for rare or strategically important genetics
3. `Release`
   Best for broad excess stock and ecological progression
4. `Donate`
   Best for integration-specific demand
5. `Sanctuary`
   Best for sentiment or display
6. `Cull`
   Always available, rarely optimal

This hierarchy encourages meaningful decisions instead of one dominant sink.

## How Good Frogs Should Retire

Different quality frogs should have different best exits.

### Poor or generic frogs

Best exits:
- release
- donate if requested
- cull only as a last resort

### Promising but non-elite frogs

Best exits:
- workforce conversion
- release into local line improvement

### Excellent frogs

Best exits:
- keep as breeders
- archive
- assign to premium jobs
- sanctuary if sentimental

This creates a sorting game rather than a binary keep/kill game.

## First Implementation Recommendation

The first retirement feature set should stay small and high value.

### V1 recommendation

Implement:

- `Wetland Release`
- `Gene Archiving`
- maintain existing `Worker Conversion` through frogport crafting

Do not implement all retirement paths at once.

### Why these first

- release solves the ecological surplus problem
- archiving solves the fear of losing a rare line
- conversion already has a foothold in the mod

Together, these give three distinct reasons not to cull frogs.

## Suggested V1 User Experience

### Release

- player uses a frog bucket or frog interaction on valid habitat
- game confirms release and shows a short genetic contribution message
- local wetland pool updates invisibly or through light feedback

### Archive

- player uses an analysis or preservation item on a frog
- archive stores key traits, mutations, or bloodline identity
- player can inspect the archive later

### Convert

- crafting or station assignment consumes the frog
- resulting block or station retains the frog identity and stats

### Cull

- still possible
- visibly distresses nearby frogs
- immediately signals to the player that this is a poor stock-management strategy

## Open Questions

- Should release require a crafted item, or just a valid biome and action?
- How local is the wetland gene pool: chunk, region, biome pocket, or room?
- What exactly does archiving preserve: full genome, summary stats, mutation unlock, or all three?
- Should sanctuary retirement exist before job hats and broader integration exist?
- Should named frogs be harder to retire accidentally?
- Should released frogs contribute more if they are healthy or adapted to the local habitat?
- Which damage sources count as intentional culling for distress purposes?
- Should workforce frogs merely lose happiness, or can some job roles fully pause while distressed?

## Recommendation

Amphibia should not copy Forestry's disposal pattern directly.

Forestry made surplus drones disposable because bees were designed around asymmetric breeder roles. Frogs are better treated as individual stock animals with multiple dignified exits.

The strongest retirement model for Amphibia is:

- active breeders are few
- qualified frogs get converted into workforce infrastructure
- excess frogs get released into a local gene pool
- exceptional frogs can be archived instead of hoarded forever

That gives the player a reason to keep breeding aggressively without turning the system into entity clutter or mandatory culling.
