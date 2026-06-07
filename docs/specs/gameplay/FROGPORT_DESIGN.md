# Frogport Gene Behavior — Design Notes

## What Exists (Already Implemented)

The frog-to-frogport conversion pipeline is complete:
- Crafting Create's package frogport recipe with a frog bucket now creates `zen_amphibia:worker_frogport` and stores its `FrogGenome` on the block entity
- `FrogportBlockEntityMixin` persists the genome via NBT, color-tints the renderer, and shows DNA in the goggle tooltip
- `FrogportGeneEvaluator.java` maps gene grades to behavior values (framework ready)
- The worker hooks are tied to actual package dispatch and Create package-port targeting

What the first worker implementation does:
- `QUICKNESS` increases package handoff animation speed
- `TONGUE_LENGTH` extends Create package-port placement reach
- `TEMPERAMENT` improves dispatch reliability
- `SLIME_YIELD` produces Frog Slime residue only when a package is dispatched
- Worker item and goggle tooltips show concrete work rate, reach, reliability, and residue chance

## The Design Problem

The first worker implementation is package-linked, but still shallow. It needs stronger job identity and a more interesting reason to specialize worker lines.

Remaining design questions:
- Should slime residue become a package coating, a machine input, or a processing material?
- Should high-reach workers unlock broader inventory/package targeting beyond placement range?
- Should reliability affect failed dispatches, package handling quality, or only bonus output consistency?
- How should worker lines differ from future supervisor lines?

## Design Options (Pending Decision)

### Option A — Slime coats packages on output
When the frogport ejects a package, it wraps it in slime, creating a "slimed package" variant. Slime grade determines coat quality. Downstream recipes or logistics treat slimed packages differently.
- **Pro**: Output tied to actual packaging events, creates a new item tier
- **Con**: Needs "slimed package" item or NBT variant, downstream recipes

### Option B — Slime is a self-supplied machine input
The frog secretes slime into an internal buffer. The packager below consumes slime to run faster or produce bonus output. Grade determines buffer refill rate. No buffer = no bonus — frog feels like a resource provider inside the machine.
- **Pro**: Frog feels like a worker, creates interesting resource loop
- **Con**: Requires modifying packager behavior, more complex

### Option C — Drop slime, change behavior entirely
`TONGUE_LENGTH` or a future logistics trait determines *reach* - specialized frogs can pull items from adjacent inventories that vanilla frogports cannot reach, or package a wider item range.
- **Pro**: Thematic, no random item dropping, pure logistics improvement
- **Con**: Harder to implement (requires inventory scanning injection)

### Option D — Contextual slime: only when packaging biological items
Slime drops only when the packager processes organic items (food, plants, etc.). Frog is a specialist, not a generic slime farm.
- **Pro**: Adds item specialization gameplay
- **Con**: Limits usefulness, requires reading packager contents

## Gene → Behavior Mapping (Agreed)

| Gene | Behavior |
|---|---|
| SLIME_YIELD | Frog Slime residue chance on package dispatch |
| QUICKNESS | Package handoff animation speed |
| TONGUE_LENGTH | Package-port placement reach bonus |
| TEMPERAMENT | Dispatch reliability |
| CUNNING | Candidate routing/smart instruction trait |
| AWARENESS | Candidate supervisor/control-room trait |
| SIZE | Physical phenotype; may affect storage/output scale later |

## Design Principles

- A frog spent into a frogport should always be strictly better than a vanilla frogport
- Genome quality determines *how much* better
- Breeding specialized frog lines should have a concrete production payoff
- Frogs are consumed (not borrowed) — they become blocks with different states

## Key Files

| File | Purpose |
|---|---|
| `src/.../genetics/FrogportGeneEvaluator.java` | Maps genome grades to behavior values |
| `src/.../mixin/FrogportBlockEntityMixin.java` | Tick injection + NBT + goggle tooltip |
| `src/.../genetics/FrogGradeCalculator.java` | Grade computation (D–S) from allele pairs |
| `src/.../register/AmphibiaItems.java` | `FROG_SLIME` item (the premium output at S-grade) |

## Broader Vision (From Design Conversation)

- Terrariums become production line infrastructure, not just happiness farms
- Breeding loop → genetics R&D pipeline → specialized frogport workers
- Eventually: frog sorter that reads DNA from buckets and routes to different production lines
- Create: Kaizen mod handles complex packager interactions; Amphibia owns the genetics side
