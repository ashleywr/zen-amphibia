# Agent Notes

This is a NeoForge Minecraft mod targeting Minecraft 1.21.1.

Before changing Minecraft/NeoForge API usage, run:

```powershell
.\gradlew.bat syncReferenceSources
```

Then inspect the exact local reference sources under:

```text
internal/reference-sources/
```

Use these sources instead of guessing from older tutorials or memory. In particular, check superclass contracts for entities, registrations, events, attributes, renderers, data components, and spawn behavior.

Compilation is not enough for runtime Minecraft contracts. For custom `Mob`/`Animal` entities, verify the attribute builder starts from the appropriate superclass baseline, such as `Animal.createMobAttributes()`, before adding custom attributes.
