# Amphibia Roadmap

Working design target: Amphibia is a frog genetics and husbandry mod. It should stand alone as a fun breeding/resource loop, while becoming deeper and more automatable when Create is installed.

Core loop:

`collect frogs -> inspect genes -> breed lines -> hatch tadpoles -> select frogs -> feed/use frogs -> produce frog-native resources -> improve lines`

## Implemented

- Cricket entity, spawn egg, renderer, and frog food entity tag.
- Randomized genomes for vanilla frog spawn eggs.
- Parent DNA mixing for frog breeding.
- Genetic frogspawn and dormant frogspawn with stored DNA.
- Tadpoles inherit DNA and transfer it to adult frogs.
- Frogspawn tooltips, block overlays, and client-side block entity DNA sync.
- Frog scale and shadow scale from genetics.
- Population controls:
  - genetic frogspawn hatches 1-2 tadpoles
  - frogs stop laying eggs when local frog/tadpole/frogspawn population is high
  - crowded tadpoles become stunted, tinted, and bubbly
  - severely crowded tadpole patches show witch/smoke particles over water
  - severely overcrowded unnamed tadpoles can fail to thrive
- Frog Slime item.
- Tamed frogs produce Frog Slime when fed crickets.
- Frog Slime amount scales with `SLIME_VISCOSITY` and `SIZE`.
- `2 Frog Slime -> 1 Slimeball`.
- Genetic froglight blocks:
  - Verdant Genetic Froglight
  - Azure Genetic Froglight
  - Rose Genetic Froglight
  - Amber Genetic Froglight
  - Violet Genetic Froglight
  - Pearl Genetic Froglight
  - Umbral Genetic Froglight
- Manual genetic froglight recipes using Frog Slime, Glowstone Dust, Magma Cream, and dye.
- Patchouli Amphibia Field Guide:
  - getting started
  - crickets
  - DNA basics
  - alleles and carriers
  - breeding goals
  - gene reading
  - frogspawn DNA
  - tadpoles
  - overcrowded water
  - Frog Slime
  - genetic froglights
- Patchouli guide sync test for genes and mutations.
- Config-controlled first-login Patchouli guide grant.
- Patchouli guide recovery recipe: Book + Frog Slime.
- Worker Frogport genetics:
  - `QUICKNESS` speeds package handoff animation.
  - `TONGUE_LENGTH` defines displayed worker reach.
  - `TEMPERAMENT` stabilizes dispatch residue chances.
  - `SLIME_YIELD` can produce Frog Slime residue only when a package is dispatched.

## Near-Term Features

### Genetic Froglight Production

Hook vanilla frog-eats-small-magma-cube behavior so genetic frogs can produce Amphibia froglights.

Design:

- Keep vanilla froglights for normal frogs and baseline compatibility.
- If a frog has Amphibia genetics, map `FrogGenome.getColor()` to the nearest genetic froglight palette.
- Rare mutations can override the palette later.
- This should make color breeding produce decorative building rewards.

Palette:

- green -> Verdant
- blue/cyan -> Azure
- red/pink -> Rose
- yellow/orange -> Amber
- purple/magenta -> Violet
- pale/white -> Pearl
- dark/black -> Umbral

### Custom Textures

Current Frog Slime and genetic froglights use placeholder vanilla textures/models.

Needed:

- Green Frog Slime item texture.
- Froglight-style block textures for each genetic froglight.
- Optional animated/emissive-looking style if resource pack support is added later.

### Guidebook / Bestiary Expansion

Players need in-world explanation for systems that are intentionally discoverable. A first Patchouli guide exists; expand it as systems mature.

Maintenance rule:

- When adding a gene or mutation, update the Patchouli Field Guide in the same change.
- `PatchouliGuideSyncTest` checks that every gene display name and mutation display name/id appears in the guide content.

Pages to add:

- Frog genetics basics.
- Parent DNA and frogspawn.
- Tadpole overcrowding signs:
  - sickly tint
  - bubbles
  - witch/smoke particles above water
- Frog Slime production.
- Genetic froglight crafting and magma cube route.
- Frog population management.

## Mid-Term Features

### Frog Slime Tiers

Expand Frog Slime into a small green material economy.

Possible items:

- Thick Frog Slime
- Clear Frog Slime
- Genetic Slime
- Mutagenic Slime
- Mineral Slime
- Carbon Slime
- Crystalline Frog Slime

Design principle:

Frogs should produce frog-native intermediates, not direct vanilla treasure. Valuable outputs should come from processing, recipes, or rare lines.

### Non-Create Processing

Make Frog Slime useful without Create.

Possible routes:

- Cauldron refinement.
- Furnace/smoker processing.
- Crafting recipes for adhesives, slimeballs, glow materials, and incubation catalysts.
- Slow manual routes to resource fragments, not direct ore printing.

### Create Processing

Make Create automation feel like the industrial form of frog husbandry.

Ideas:

- Press Frog Slime into Frog Slime Sheets.
- Mix Frog Slime into Frog Glue.
- Wash Mineral Slime for nuggets/dusts.
- Heat Carbon Slime into graphite/coal-like outputs.
- Compact Crystalline Slime into gem fragments.
- Automate froglight routes through frogports and magma cube handling.

### Frogport Integration

The original Create-facing goal.

Next step:

- Replace the first Worker Frogport pass with deeper routing and package-handling hooks once the worker spec is written.
- Keep Supervisor Perch separate from worker behavior.

## Longer-Term Features

### Release / Wetland Gene Pool

Give players a non-lethal way to manage unwanted frogs.

Design:

- Release frogs into valid wetland/water habitats.
- Store released genomes in a local biome/zone gene pool.
- Future wild frogs or frogspawn nearby can inherit released traits.
- Releasing new traits can contribute to discovery/progression.

### Gene Sampling / Archiving

Non-lethal DNA preservation.

Ideas:

- Slime sample.
- Shed skin.
- Genetic imprint.
- Frogspawn sample.
- Archive genes into a bestiary or storage item.

### Habitat Bonuses

Make frog pens more ecological.

Ideas:

- Larger ponds support more tadpoles.
- More plant diversity improves growth.
- Cleaner/filtered water improves survival.
- Habitat blocks reduce overcrowding penalties.
- Diverse frog populations improve local cricket spawning or breeding success.

### Stable Lines and Discovery Progression

Make breeding feel like old genetic mods without becoming spreadsheet-only.

Ideas:

- Track discovered alleles.
- Track stable/purebred lines.
- Reward rare trait combinations.
- Let players name or register bloodlines.
- Add mutation hints through guidebook entries.

## Balancing Notes

- Avoid direct passive diamond/ore printing.
- Prefer frog-native materials that process into useful outputs.
- Killing frogs should not be the optimal way to manage genetics.
- The kind/ecological option should also be mechanically smart.
- Keep Create integration optional; base Amphibia should still be fun.
- Fewer offspring should be more meaningful than huge clutches.
- Visual clues should appear before explicit explanations, then guidebook pages can teach the mechanic.
