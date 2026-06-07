# Markdown Workflow

This file defines the repo-local planning workflow.

The goal is to keep project memory in markdown so future work sessions can recover context quickly without relying on external tooling.

## Principles

- Keep planning close to code
- Prefer short durable records over long chat reconstruction
- Record decisions separately from open questions
- Link specs, decisions, and implementation follow-up clearly
- Update docs as part of the work, not weeks later

## Folder Roles

### `docs/planning/`

This is the active coordination layer.

Files here:

- [README.md](/C:/WorkDir/Minecraft%20Mods/Amphibia/docs/planning/README.md)
  Index and session entry point
- [DECISIONS.md](/C:/WorkDir/Minecraft%20Mods/Amphibia/docs/planning/DECISIONS.md)
  Short durable design choices
- [TODO.md](/C:/WorkDir/Minecraft%20Mods/Amphibia/docs/planning/TODO.md)
  Active follow-up work
- [WORKFLOW.md](/C:/WorkDir/Minecraft%20Mods/Amphibia/docs/planning/WORKFLOW.md)
  Process conventions

### `docs/adr/`

This is for decision records when a decision needs more context than a single bullet in `DECISIONS.md`.

Use this for:

- architecture changes
- data model changes
- migration decisions
- integration boundary decisions

### Spec files

Larger feature and system specs should live under `docs/specs/`.

When a spec becomes important, make sure it is linked from:

- [README.md](/C:/WorkDir/Minecraft%20Mods/Amphibia/docs/planning/README.md)
- [TODO.md](/C:/WorkDir/Minecraft%20Mods/Amphibia/docs/planning/TODO.md) if work remains
- [docs/specs/README.md](/C:/WorkDir/Minecraft%20Mods/Amphibia/docs/specs/README.md)

## When To Write What

### Use `DECISIONS.md` when

- the decision is stable
- the explanation can fit in a few bullets
- future sessions need the answer more than the debate

### Use an ADR when

- the decision changes architecture or data shape
- tradeoffs matter
- alternatives were seriously considered
- future migrations will need the rationale

### Use a spec when

- a whole feature or subsystem is being designed
- multiple open questions still exist
- the design is too large for a single decision record

### Use `TODO.md` when

- the next actions are known
- implementation is pending
- the spec is approved enough to act on

## Recommended Work Sequence

For medium or large work:

1. Draft or update a spec
2. Extract stable conclusions into `DECISIONS.md`
3. If needed, write an ADR for high-impact choices
4. Add concrete follow-up to `TODO.md`
5. Implement code
6. Update docs after implementation if reality changed

## ADR Conventions

ADR files should live in `docs/adr/` with numeric prefixes.

Example:

- `0001-frog-professions-use-hats-for-role-access.md`
- `0002-frog-retirement-prefers-release-convert-archive.md`

Suggested sections:

- Status
- Context
- Decision
- Consequences
- Alternatives considered

Keep ADRs short. They are for durable reasoning, not full design docs.

## Spec Conventions

Specs should usually include:

- scope
- problem statement
- design principles
- gameplay goals
- implementation direction
- open questions
- recommendation

Specs can stay draft for a while. The important part is that they become the canonical written context for that topic.

## Session Start Convention

For future work sessions, use this order:

1. Read [README.md](/C:/WorkDir/Minecraft%20Mods/Amphibia/docs/planning/README.md)
2. Read [DECISIONS.md](/C:/WorkDir/Minecraft%20Mods/Amphibia/docs/planning/DECISIONS.md)
3. Read [TODO.md](/C:/WorkDir/Minecraft%20Mods/Amphibia/docs/planning/TODO.md)
4. Read only the specific spec or ADR files relevant to the task
5. Then inspect code

## Maintenance Rule

If a coding change materially changes system behavior, update at least one of:

- the relevant spec
- `DECISIONS.md`
- `TODO.md`
- a new ADR

The repo should not rely on chat history to explain why major systems work the way they do.
