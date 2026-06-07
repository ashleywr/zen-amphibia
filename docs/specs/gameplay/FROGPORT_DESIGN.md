# Frogport Gene Behavior — Design Notes

## What Exists (Already Implemented)

The frog-to-frogport conversion pipeline is complete:
- Crafting `create:package_frogport` with a frog bucket consumes the frog and stores its `FrogGenome` on the block entity
- `FrogportBlockEntityMixin` persists the genome via NBT, color-tints the renderer, and shows DNA in the goggle tooltip
- `FrogportGeneEvaluator.java` maps gene grades to behavior values (framework ready)
- A `@Inject(method = "tick")` hook is wired into Create's `FrogportBlockEntity`

What the tick injection currently does: outputs slime balls/frog_slime items below the frogport on a 40-tick timer based on `SLIME_YIELD` grade. **This is acknowledged as gameplay-incoherent** - it is a random slime farm, not a packaging mechanic.

## The Design Problem

`SLIME_YIELD` outputting slime on a fixed timer has no connection to the packaging workflow. It needs to either:
- React to actual packaging events
- Feed into the packaging process as an input
- Do something else entirely

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
| SLIME_YIELD | Temporary slime bonus output; final packaging use still TBD |
| QUICKNESS | Output interval multiplier - D=20t, C=17t, B=14t, A=10t, S=7t |
| TONGUE_LENGTH | Candidate reach/logistics extension trait |
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
