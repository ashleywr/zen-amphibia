# Frog Output Doctrine

**Status:** Draft  
**Created:** 2026-06-07  
**Scope:** Define how frog-derived materials and alchemy outputs should fit Amphibia without collapsing the mod into throughput-first livestock production.

## Overview

Amphibia should not become "productive bees but frogs."

Frogs are individual husbandry animals with genetics, habitat needs, life stages, and visible behavior. Outputs should exist, but they should be a secondary result of raising frogs well, not the main reason frogs matter.

This doctrine sets the constraints for:

- Frog Slime redesign
- future secretion or alchemy items
- Create processing compatibility
- external mod ingredient compatibility
- future frog professions that produce or refine biological materials

## Problem Statement

If frog outputs are designed as passive production first, several bad things happen:

- the player is taught to optimize throughput before care
- breeding collapses into yield-chasing instead of line development
- habitat and life-stage systems become flavor instead of structure
- frogs stop feeling like animals and start feeling like item printers
- Create or alchemy integrations begin to define the mod instead of extending it

The mod already has enough DNA and breeding depth that this failure mode is plausible. It needs a clear guardrail before more output systems are added.

## Core Rule

**A frog line should still feel meaningful even if its direct item output were removed.**

If that is not true for a feature, the design is drifting in the wrong direction.

## Design Principles

- Frogs are husbandry-first, ecology-first creatures
- Outputs are byproducts of care, not the primary identity of the animal
- Good habitat, feeding, recovery, and breeding lines should matter more than enclosure count
- Harvesting should feel like interacting with a living organism, not collecting from a machine
- Life stages, temperament, and environmental fit should remain relevant even when outputs exist
- Create and alchemy compatibility should consume frog outputs downstream, not redefine the frog loop upstream

## Intended Frog Loop

The baseline loop should remain:

1. find or breed frogs
2. inspect line quality
3. improve habitat and feeding
4. raise frogs through healthy life stages
5. harvest occasional useful biological outputs
6. breed toward better care outcomes, professions, or specialties

The player should feel like a breeder-keeper, not a factory manager who happens to use frogs as blocks.

## What Frog Outputs Are For

Frog outputs should support three roles:

1. `Husbandry payoff`
   - proof that breeding and care are worth doing
2. `Biological feedstock`
   - renewable material for crafting, alchemy, or processing
3. `Line specialization signal`
   - evidence that different frogs are good at different things

They should not primarily serve as:

- passive background income
- mass entity throughput
- a universal answer for every mod integration

## Harvesting Rules

The preferred model is a visible harvest state, not a passive timer drop.

Recommended structure:

- feeding, habitat quality, and recovery determine whether a frog becomes harvest-ready
- the ready state is visible through particles, posture, sound, or tooltip text
- the player harvests by interaction or by collecting from a habitat-side output point
- quality frogs improve readiness, yield, or stability
- overuse, stress, or poor habitat should reduce output quality or reliability

This keeps the resource loop tied to care and observation.

## Output Families

Output families are acceptable, but they must remain biologically flavored and subordinate to the frog loop.

Good long-term families:

- adhesive or viscous secretion
- luminous secretion
- caustic or toxic secretion
- stabilizing or preservative secretion
- mutagenic or unusual secretion

These should be treated as secretions, extracts, residues, or compounds derived from frog biology, not as arbitrary loot classes.

## Frog Slime Direction

`Frog Slime` should remain the first and simplest biological output, but it should not stay a generic passive drop forever.

Recommended direction:

- start as a manually harvested husbandry product
- make its production visibly tied to frog condition
- let genes influence amount, readiness, and stability
- keep it as an intermediate material rather than the final reward

This preserves current progression while leaving room for later output families.

## Genetics and Output

Genetics should shape outputs through suitability, not through raw throughput alone.

Useful levers:

- readiness chance
- cooldown or recovery time
- output amount
- output stability or purity
- compatibility with a specific job or habitat

Less useful direction:

- only increasing flat drops per minute

The question should be "what kind of frog is this line good at being," not only "how much does it print."

## Habitat and Welfare

Output systems should reinforce existing care systems.

Expected relationships:

- healthy habitat improves readiness and stability
- overcrowding harms output and breeding confidence
- distress can suppress output
- ecological fit should matter in outdoor or semi-natural setups
- named or display frogs can still matter even if they are not optimized producers

This keeps welfare and output in the same design language.

## Create Compatibility

Create should industrialize frog byproducts, not replace the husbandry loop.

Good Create roles:

- processing secretions into refined materials
- routing or buffering harvested outputs
- operating frog-specific infrastructure like worker frogports
- helping maintain habitat conditions indirectly

Bad Create roles:

- turning frogs into passive item engines with no care loop
- bypassing line quality, habitat, or recovery
- making the optimal frog setup identical to any other Create machine bank

## Alchemy Compatibility

Alchemy-facing compatibility should consume ingredient categories instead of defining frog behavior directly.

Preferred pattern:

- Amphibia owns production and identity
- compat mods consume tagged ingredient classes

Examples:

- `c:alchemy_adhesives`
- `c:alchemy_toxins`
- `c:alchemy_luminants`
- `c:alchemy_stabilizers`
- `c:alchemy_mutagens`

This keeps compatibility modular and prevents one mod's recipe model from deciding how frogs work.

## Anti-Patterns

Avoid these patterns unless a later spec gives a strong reason:

- passive timer-only drops as the main output model
- "place frogs in a box and wait" as optimal play
- every frog line existing mainly to output a SKU
- removing the need to inspect, breed, or care for frogs
- using Create integration to erase animal behavior
- flattening all outputs into generic industrial goo

## Evaluation Check

Future frog-output features should pass these checks:

- Does the frog still matter as an animal, not just as a source?
- Does the player benefit from understanding the frog's line and condition?
- Does care meaningfully affect the result?
- Is the output a byproduct of a believable frog process?
- Would the feature still make sense without Create installed?
- Does compat sit downstream of Amphibia's identity instead of replacing it?

If the answer is "no" on several of these, the design needs revision.

## First Follow-Up Work

This doctrine implies the next concrete work should be:

1. rewrite Frog Slime around a harvest-ready state
2. define which genes and conditions affect readiness, yield, and recovery
3. decide whether first harvest is direct interaction or habitat-side collection
4. keep future secretion families behind a later spec instead of adding many placeholder items now

## Recommendation

Amphibia should treat frog outputs as biological byproducts of care, breeding, and ecology.

That keeps the mod distinct from throughput-focused animal production systems, while still leaving room for alchemy, Create, and future compat content to grow around a strong frog identity.
