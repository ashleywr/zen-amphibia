# Gene Migration Spec

**Status:** Implemented initial catalog migration  
**Created:** 2026-05-28  
**Scope:** Define how Amphibia transitions from the current six-gene frog DNA model to a profession-ready aptitude model, prioritizing clean redesign over old-world compatibility.

## Overview

Amphibia already has a working frog genome system:

- genes are stored as allele pairs
- grades are computed from allele values
- genomes persist on entities, items, and blocks
- breeding already mixes alleles correctly

The problem is not the genome machinery. The problem is that the current gene catalog was not designed for a profession system.

This spec defines how to migrate from the current gene set into a profession-focused aptitude model while preserving useful implementation machinery, but not prioritizing backwards compatibility for old worlds.

## Previous State

The original gene enum contained:

- `HEAT_TOLERANCE`
- `SLIME_VISCOSITY`
- `GROWTH_RATE`
- `HEALTH`
- `DAMAGE`
- `SIZE`

These genes already drive:

- frog color derivation
- frog size derivation
- happiness/environment preference
- slime output
- combat stat scaling
- frogport placeholder behavior

## Implemented Catalog

The active `Gene` enum now contains:

- Aptitudes: `POWER`, `HARDINESS`, `QUICKNESS`, `CUNNING`, `AWARENESS`, `TEMPERAMENT`, `AFFINITY`, `ATTUNEMENT`
- Phenotypes: `SIZE`, `COLORATION`, `TONGUE_LENGTH`, `SLIME_YIELD`
- Ecology: `HEAT_TOLERANCE`, `HUMIDITY_TOLERANCE`

The old `SLIME_VISCOSITY`, `GROWTH_RATE`, `HEALTH`, and `DAMAGE` enum entries have been removed from active code.

The underlying genome storage is in good shape:

- `FrogGenome` stores `Map<Gene, Trait>` plus mutation ids
- traits are still simple two-allele pairs
- default and random genome creation are centralized
- grade logic is generic and reusable

So the migration target is not "replace DNA." It is "replace what the DNA means."

## Problem Statement

The profession system needs job-readable breed traits like:

- `Power`
- `Hardiness`
- `Quickness`
- `Cunning`
- `Awareness`
- `Temperament`

The current catalog does not map cleanly to those jobs.

If Amphibia tries to keep reusing the current six genes for professions, several problems appear:

- the profession fantasy stays muddy
- tooltip readability gets worse
- Create integration becomes forced
- future non-Create jobs become harder to justify

But a hard replacement also has costs:

- migration complexity
- display/guide/tooling churn
- rework across existing systems
- clearer handling needed for all old gene meanings

This spec exists to choose a migration model deliberately instead of drifting into a half-reinterpreted mess.

## Migration Goals

- Keep the existing genome storage machinery
- Give professions a clean aptitude model
- Preserve useful biological/ecological traits
- Keep the first profession release implementable without rewriting the entire mod from scratch
- Prefer a cleaner long-term model over preserving transitional baggage

## Migration Options

### Option A: Reinterpret the existing six genes

Example:

- rename `SLIME_VISCOSITY` into `REACH`
- rename `GROWTH_RATE` into `WORK_RATE`
- rename `HEALTH` into `TEMPERAMENT`

#### Pros

- fewer genes
- fewer schema changes
- simpler implementation in the short term

#### Cons

- existing worlds become semantically strange
- frog lines bred for slime or combat suddenly become workforce specialists
- ecological and biological flavor gets hollowed out
- old guidebook/tooltips become misleading

### Option B: Replace the old catalog completely

Remove the current profession-unfriendly genes and move to a brand-new aptitude-driven enum.

#### Pros

- cleanest long-term design
- no compromises in gene naming or meaning

#### Cons

- highest migration risk
- old items, frogports, and frogs need hard conversion
- existing biological systems lose their inputs unless rebuilt immediately
- too disruptive for the current stage of development

### Option C: Add a parallel aptitude layer beside the current biological genes

Keep the current biological/ecological genes and introduce a new aptitude gene set for professions.

#### Pros

- keeps ecological systems intact
- lets profession design become legible without destroying current frog identity
- supports phased rollout

#### Cons

- larger total gene count
- more UI work
- needs careful pacing so players are not overwhelmed

## Recommendation

Use **Option C: a parallel aptitude layer**, introduced in phases, without treating old-world compatibility as a requirement.

This is the most organized and least destructive path.

The current gene catalog should become the **biological layer**.

The new profession-facing genes should become the **aptitude layer**.

This avoids the worst failure mode:

- old frogs bred for one purpose accidentally becoming best-in-slot workers for unrelated reasons

It preserves the strongest parts of the current mod:

- ecological breeding flavor
- slime and habitat systems
- frog color and body variation
- the existing genome implementation patterns

## Target Model

Amphibia should move toward a four-layer genetics design.

### Aptitudes

These are internal breed traits for professions.

- `Power`
- `Hardiness`
- `Quickness`
- `Cunning`
- `Awareness`
- `Temperament`
- `Affinity`
- `Attunement`

These affect:

- profession qualification
- workforce performance
- role specialization

### Phenotypes

These are visible physical traits.

- `Size`
- `Coloration`
- `Tongue Length`
- `Slime Yield`

These affect:

- visual identity
- some obvious physical capabilities
- some natural production roles

### Ecology

These describe habitat fit.

- `Heat Tolerance`
- `Humidity Tolerance`

These affect:

- comfort and happiness expression
- environmental suitability

### Mutations

These remain rare overlays with unusual effects and premium bonuses.

## What Happens to the Current Six Genes

Not all current genes should survive unchanged.

### Keep conceptually

- `HEAT_TOLERANCE`
- `SIZE`

These already have clear non-profession meaning.

### Convert in meaning

- `SLIME_VISCOSITY` should stop being the general catch-all industry trait and likely become a phenotype or production-biology trait such as `Slime Yield`
- `GROWTH_RATE` should stop carrying the whole idea of throughput by itself
- `HEALTH` should likely evolve into a breeder-facing trait such as `Hardiness`
- `DAMAGE` should likely evolve into a breeder-facing trait such as `Power`

### Add explicitly

- `HUMIDITY_TOLERANCE`
- `TONGUE_LENGTH`
- the core aptitude trait set

Right now humidity preference is indirectly derived from `SLIME_VISCOSITY`, which will become increasingly awkward.

This is a good opportunity to split:

- moisture ecology
- industrial aptitude

## Data Model Strategy

The migration should preserve useful code structure, not old saved meaning.

Recommended strategy:

### 1. Keep `FrogGenome` structure

Do not replace:

- allele pair storage
- mutation list storage
- grade computation model

This is stable and already integrated.

### 2. Replace the gene catalog deliberately

The gene enum can be reshaped aggressively if needed.

That means:

- remove or rename genes whose meaning no longer serves the design
- add aptitude, phenotype, and ecology traits directly
- split workforce, visible, and environmental concerns cleanly

### 3. Accept old-world breakage if necessary

Because this project is still mid-development, clean design is more valuable than preserving transitional save compatibility.

If old worlds need to be invalidated or treated as unsupported after the migration, that is acceptable.

### 4. Keep migration logic only if it helps code organization

Do not build compatibility scaffolding just for the sake of it.

Only add transitional handling when it meaningfully reduces implementation risk.

## Save Compatibility

Backwards compatibility is not a primary goal for this migration.

Recommended stance:

- existing code patterns should be preserved where useful
- existing world data does not need to remain semantically valid if it blocks a cleaner design
- if necessary, old saves can be treated as pre-redesign and unsupported

This is acceptable because the project is still in active development and not in a public compatibility-heavy release phase.

## Frogport Compatibility

Existing frogports carrying old genome meanings do not need special preservation logic unless implementation convenience justifies it.

Recommended rule:

- redesign frogport behavior against the new aptitude model
- do not keep placeholder behavior alive solely to support transitional genomes

This keeps the worker and supervisor implementation cleaner.

## Breeding Behavior After Migration

Once aptitude genes exist, breeding should mix them exactly like any other gene.

No special inheritance rules are needed for the first migration phase.

This is one of the strongest reasons to preserve the existing `FrogGenome` machinery:

- the inheritance engine already works
- only the gene catalog and interpretation need to grow

## UI and Display Implications

The biggest downside of the parallel-layer model is information load.

This must be managed deliberately.

Recommended UI rules:

### Surface only the relevant traits most of the time

- wild/basic tooltip: short phenotype and ecology summary
- profession hat preview: show only relevant aptitude traits
- advanced inspection: full genome breakdown

### Separate genome sections clearly

Do not mix them into one flat undifferentiated list once aptitude genes arrive.

Recommended headings:

- `Aptitudes`
- `Phenotypes`
- `Ecology`
- `Aptitudes`
- `Mutations`

### Job assignment screens should filter aggressively

A profession assignment interaction should show:

- primary qualifier
- secondary performance traits
- qualification result

Players should not need to parse the entire genome to answer "can this frog wear these goggles?"

## Release Phasing

The migration should be staged.

### Phase 1: Reshape the gene catalog

- define the first aptitude set
- define the first phenotype set
- define the ecology set
- remove or rename genes that no longer fit

### Phase 2: First profession implementation

- `Create Goggles`
- `Foreman Cap`
- `Worker Frogport`
- `Supervisor Perch`

These use:

- `Quickness`
- `Cunning`
- `Awareness`
- `Temperament`
- `Tongue Length`

### Phase 3: Refine phenotype and ecology

- introduce `Humidity Tolerance`
- separate `Slime Yield` from industry aptitude
- refine `Coloration` and other visible signals

### Phase 4: Expand other profession families

- foraging
- craft
- trade
- combat
- arcane

This keeps the migration organized and prevents speculative overbuilding.

## Code Implications

The first implementation pass will likely need changes in:

- `Gene.java`
- `FrogGenome.java`
- tooltip/display helpers
- profession/hat assignment logic
- frogport worker/supervisor evaluators
- any code that assumes the gene set is only six entries

Special care is needed for:

- random genome generation
- default genome generation
- display ordering
- guidebook sync tests

## Guidebook and Teaching

The migration should be visible in the field guide.

The guide should teach:

- aptitudes, phenotypes, ecology, and mutations are different layers
- professions care mainly about aptitudes
- the profession redesign changes frog genetics intentionally
- older internal assumptions may no longer apply

This avoids the player feeling like the genetics system changed silently under them.

## Open Questions

- Should `Coloration` remain derived, or become a first-class stored trait?
- Should `Tongue Length` be a phenotype only, or also a profession-critical trait?
- Should combat remain partly influenced by phenotype, or be mostly aptitude-driven?
- Does `Gene` remain one enum containing all four layers, or should the code later distinguish categories explicitly?

## Recommendation

Adopt a **phased parallel-layer migration**:

- keep the current genome machinery
- keep only the traits that still serve the design
- add profession aptitude traits directly
- make phenotype and ecology explicit instead of leaving them implicit
- allow the gene catalog to change without save-compat constraints
- make new specialist roles depend on the new aptitude traits

This is the most organized path and the best fit for the profession system already defined in [FROG_PROFESSION_SPEC.md](/C:/WorkDir/Minecraft%20Mods/Amphibia/docs/specs/gameplay/FROG_PROFESSION_SPEC.md).
