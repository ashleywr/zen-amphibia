# Planning Hub

This folder is the canonical entry point for design and planning work in this repo.

Use this folder to reduce context rebuilding between sessions:

- read this file first
- check [DECISIONS.md](/C:/WorkDir/Minecraft%20Mods/Amphibia/docs/planning/DECISIONS.md)
- check [TODO.md](/C:/WorkDir/Minecraft%20Mods/Amphibia/docs/planning/TODO.md)
- check [WORKFLOW.md](/C:/WorkDir/Minecraft%20Mods/Amphibia/docs/planning/WORKFLOW.md) if process needs clarification
- open only the spec files relevant to the current task

## Current Core Specs

- [Amphibia Roadmap](/C:/WorkDir/Minecraft%20Mods/Amphibia/AMPHIBIA_ROADMAP.md)
- [Spec Index](/C:/WorkDir/Minecraft%20Mods/Amphibia/docs/specs/README.md)
- [Gene Migration Spec](/C:/WorkDir/Minecraft%20Mods/Amphibia/docs/specs/gameplay/GENE_MIGRATION_SPEC.md)
- [Frog Profession Spec](/C:/WorkDir/Minecraft%20Mods/Amphibia/docs/specs/gameplay/FROG_PROFESSION_SPEC.md)
- [Vanilla Professions Spec](/C:/WorkDir/Minecraft%20Mods/Amphibia/docs/specs/gameplay/VANILLA_PROFESSIONS_SPEC.md)
- [Frogport Supervisor Spec](/C:/WorkDir/Minecraft%20Mods/Amphibia/docs/specs/gameplay/FROGPORT_SUPERVISOR_SPEC.md)
- [Frog Retirement Spec](/C:/WorkDir/Minecraft%20Mods/Amphibia/docs/specs/gameplay/FROG_RETIREMENT_SPEC.md)
- [Frogport Design Notes](/C:/WorkDir/Minecraft%20Mods/Amphibia/docs/specs/gameplay/FROGPORT_DESIGN.md)
- [Terrarium Integration Plan](/C:/WorkDir/Minecraft%20Mods/Amphibia/docs/specs/integrations/TERRARIUM_INTEGRATION_PLAN.md)

## How To Use This Folder

### Specs

Keep larger design documents under [docs/specs/README.md](/C:/WorkDir/Minecraft%20Mods/Amphibia/docs/specs/README.md) and always link them here.

### Decisions

Use [DECISIONS.md](/C:/WorkDir/Minecraft%20Mods/Amphibia/docs/planning/DECISIONS.md) for short, durable design choices that should survive between sessions.

For larger durable decisions, use [docs/adr/README.md](/C:/WorkDir/Minecraft%20Mods/Amphibia/docs/adr/README.md).

### TODOs

Use [TODO.md](/C:/WorkDir/Minecraft%20Mods/Amphibia/docs/planning/TODO.md) for active follow-up work, especially when a spec is approved but not implemented.

## Current Design Direction

The current frog workforce model is:

- genes define aptitude
- hats define profession access
- stations define exact work
- mutations provide rare premium bonuses

The current population-management model is:

- convert qualified frogs into infrastructure
- release excess frogs into wetland gene pools
- archive exceptional frogs
- make direct culling legal but mechanically weak and locally distressing

## Suggested Session Start Order

1. Read [DECISIONS.md](/C:/WorkDir/Minecraft%20Mods/Amphibia/docs/planning/DECISIONS.md)
2. Read [TODO.md](/C:/WorkDir/Minecraft%20Mods/Amphibia/docs/planning/TODO.md)
3. Read [WORKFLOW.md](/C:/WorkDir/Minecraft%20Mods/Amphibia/docs/planning/WORKFLOW.md) if needed
4. Read only the spec files related to the task
5. Then inspect code
