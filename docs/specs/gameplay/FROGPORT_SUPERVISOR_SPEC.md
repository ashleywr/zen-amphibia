# Frogport Supervisor Spec

**Status:** Draft  
**Created:** 2026-05-28  
**Scope:** Define the supervisor-style frogport role, its gameplay purpose, its integration boundary with Zen Atelier, and the first implementation target.

## Overview

Amphibia should stop treating the frogport as only a package-output modifier.

Instead, the frogport becomes a frog-assignment platform with at least two long-term station roles:

- `Worker Frogport`: improves package handling, routing, and throughput
- `Supervisor Frogport`: observes nearby factory conditions and reports higher-level production state

This document defines the `Supervisor Frogport` role.

The design goal is to make frogs useful in control rooms and factory management, not just on the production floor. This should complement Create's gauges, stock tickers, requesters, and suppliers rather than replace them.

## Problem Statement

Create's existing logistics tools are powerful, but they are often low-level and finicky for the player to combine into a readable factory dashboard.

Examples:

- Requesters and suppliers expose item movement rules, but not a simple answer to "is this production line healthy?"
- Stock tickers expose quantities, but not trend, bottleneck, or branch-level status
- Gauges show machine-local state, but not system-level interpretation

The mod should let players breed and deploy frogs as factory supervisors that interpret these signals into useful control-room information.

## Design Principles

- The frogport must add capability, not create upkeep burdens
- Supervisor frogs should never make an existing Create build worse
- Existing Create tools remain valid; supervisor frogs sit above them as an interpretation layer
- Amphibia owns frog genetics, role assignment, and player-facing behavior
- Zen Atelier can own environment scanning, signal gathering, and Create integration helpers
- Indoor factory builds should be rewarded, but outdoor use should still function at reduced effectiveness
- The first version should be legible enough that players can understand it from block behavior and tooltips without reading source code

## Core Fantasy

A frog in a frogport is not just a machine part. It is a biological worker assigned to a role.

When mounted on a dedicated supervisor block, the frog becomes a room-scale observer:

- it watches nearby factory conditions
- it summarizes production state
- it feeds dashboards, displays, and alarms
- it gives breeding a direct payoff in factory planning and control-room design

## Role Split

### Worker Frogport

Purpose:
- Improve packaging or routing jobs

Examples of future effects:
- better reach to belts or inventories
- faster work cycles
- smarter routing options

### Supervisor Frogport

Purpose:
- Observe a nearby production area and generate useful management signals

Examples of outputs:
- line healthy
- input starved
- output blocked
- machine idle
- stress failure nearby
- target stock unstable

This spec is only for the supervisor role.

## Block Concept

Add a dedicated block for supervisor use.

Working name:
- `Supervisor Perch`

Behavior:
- A frogport placed on or attached to the perch becomes a `Supervisor Frogport`
- The perch defines the scan origin and optionally the scan shape
- The frog genome modifies how good the supervisor is at the job

This avoids overloading the default Create frogport with every behavior at once.

## System Boundary With Zen Atelier

The clean architecture is:

### Amphibia owns

- frog genetics
- frog-to-role assignment
- supervisor block identity
- gene evaluation for supervisor performance
- player-facing display strings and diagnostics

### Zen Atelier owns

- reading area/room context
- optional Create integration for machine and inventory sensing
- normalized signal collection for nearby factory state
- helper APIs or signals that Amphibia can consume

This keeps Amphibia from hardcoding deep Create logic everywhere while letting Atelier become the general sensing layer.

## Gameplay Goals

The Supervisor Frogport should help players answer these questions:

- Is this room or production line currently operating correctly?
- Is a machine line waiting on inputs?
- Is output backing up?
- Is a stock target being maintained?
- Which branch of the factory is the bottleneck?
- Is this room healthy enough for precise monitoring?

It should make control rooms more rewarding to build, especially when combined with displays, redstone, and status panels.

## Environment Rule

Supervisor frogs work best in controlled indoor spaces.

Reason:
- This supports the terrarium/factory-room fantasy
- It gives Atelier room evaluation a clear purpose
- It discourages "just place it outside anywhere" without hard-disabling the feature

Outdoor penalty should be soft, not binary.

Recommended rule:

- Indoors: full scan radius, fastest updates, highest signal fidelity
- Outdoors: reduced scan radius, slower updates, fewer distinguishable statuses

The frog still functions outdoors. It is just less effective.

## First Implementation Target

The first version should be narrow and useful.

### V1 objective

Build a supervisor block that can observe a nearby area and produce a small set of meaningful factory statuses for display systems.

### V1 status categories

- `Healthy`
- `Idle`
- `Input Starved`
- `Output Blocked`
- `Overstressed`
- `Offline`

Definitions:

- `Healthy`: machines active, inputs present, outputs moving
- `Idle`: line is present but currently not working
- `Input Starved`: machines want items but supply is insufficient
- `Output Blocked`: products cannot leave inventories or transport buffers
- `Overstressed`: Create stress or power state is invalid for operation
- `Offline`: no meaningful activity or no readable systems detected

### V1 outputs

- Create display source text
- comparator/redstone output encoding status severity
- clipboard diagnostic readout

Optional later outputs:

- alert lamp compatibility
- sound/alarm triggers
- historical dashboard pages

## Data Model

The supervisor should not try to understand every machine individually in Amphibia code.

Instead, it should consume normalized area signals such as:

- active machine count
- idle machine count
- blocked output count
- missing input count
- stress failure count
- stock target deviation
- recent throughput samples

These can come from Atelier's room or area evaluation systems plus Create-aware adapters.

The supervisor then interprets those signals into a smaller set of player-facing statuses.

## Gene Design Direction

The current `SLIME_VISCOSITY` concept is a poor fit for this role and likely a poor fit for frogports in general.

Supervisor frogs should instead read genes that describe job competence.

Recommended supervisor-facing traits:

| Trait | Purpose |
|---|---|
| `AWARENESS` | Scan radius and sensor coverage |
| `INTELLIGENCE` | Number of conditions or rule types the frog can distinguish |
| `MEMORY` | Trend detection, smoothing, or rolling averages |
| `TEMPERAMENT` | Signal stability, especially in noisy or outdoor environments |

These can be implemented as new genes, renamed existing genes, or role-specific interpretations of existing genes.

The important point is that the player should be able to infer what the trait means for a supervisor job.

## Recommended Grade Scaling

This table is a design target, not a finalized balance pass.

| Grade | Awareness | Intelligence | Memory | Temperament |
|---|---|---|---|---|
| `D` | Small radius | Distinguishes only coarse good/bad state | Current state only | Unstable outdoors |
| `C` | Modest radius | Distinguishes 2-3 status categories | Short smoothing window | Mild outdoor penalty |
| `B` | Useful room radius | Distinguishes core V1 categories | Can smooth flicker | Acceptable stability |
| `A` | Large room radius | Better bottleneck classification | Basic trend awareness | Good stability indoors/outdoors |
| `S` | Full control-room radius | Richest status interpretation | Rolling history/trend alerts | Very stable, resilient to poor conditions |

## User Experience

The feature should be understandable in-game through three surfaces:

### 1. Frogport tooltip / goggles

Show:
- assigned role: `Supervisor`
- main stats: scan radius, update rate, diagnostic tier
- whether the frog is operating with an outdoor penalty

### 2. Supervisor perch state

Show:
- active/inactive visual feedback
- basic block-state or particle indication when scanning
- optional warning tint when outdoors or degraded

### 3. Display output

Show:
- readable one-line summary
- optional compact metrics such as "2 starved / 1 blocked"

Players should not need to interpret raw counters unless they choose to.

## Why This Complements Gauges

Gauges remain useful because they answer:
- what is happening at this machine right now

Supervisor frogs answer:
- what is happening in this room or production branch overall

That distinction keeps the feature additive:

- gauges are local instrumentation
- supervisors are factory management

## Why This Complements Requesters and Suppliers

Requesters and suppliers are action tools. They define what the system should move.

Supervisor frogs are observation tools. They tell the player whether those logistics rules are accomplishing the intended result.

This is the exact gap that makes a control-room feature worthwhile.

## Suggested V1 Implementation Sequence

1. Add `Supervisor Perch` block and block entity
2. Detect when a frogport is acting as a supervisor instead of a worker
3. Define a small supervisor data object:
   - scan radius
   - update interval
   - readable status
   - optional detail counters
4. Add a first pass of area signal gathering
5. Expose supervisor output through:
   - display source
   - comparator strength
   - clipboard text
6. Add indoor/outdoor effectiveness modifier
7. Add gene-based scaling

## Non-Goals For V1

- Replacing stock tickers
- Full logistics automation authoring from the supervisor block
- Per-machine AI behavior
- Penalties or upkeep systems that can stop vanilla Create behavior from working
- Hard requirement that the build be indoors

## Open Questions

- Should the supervisor observe a radius, a room, or a directional cone?
- Should it lock to an Atelier room type, or work anywhere with bonus performance in valid rooms?
- How much Create-specific sensing belongs in Amphibia versus Atelier?
- Should the first release expose raw metrics, interpreted statuses, or both?
- Which existing genes can be renamed or repurposed without damaging the broader breeding game?
- Does the supervisor role live on a normal Create frogport mounted to a perch, or on a dedicated Amphibia frogport variant?

## Recommendation

For the first implementation:

- Use a dedicated `Supervisor Perch`
- Make the feature work anywhere, with reduced effectiveness outdoors
- Keep V1 focused on status reporting, not control automation
- Treat Zen Atelier as the signal-collection layer
- Move frogport gene design away from `SLIME_VISCOSITY` and toward role-readable job traits

## Relationship To Existing Frogport Design

This spec does not replace the worker frogport concept.

Instead, it reframes the frogport as a platform with multiple jobs:

- `Worker Frogport` for packaging and routing improvements
- `Supervisor Frogport` for factory observation and control-room data

That broader role split should guide future gene redesign and help ensure frogs feel like industrial specialists rather than random passive item generators.
