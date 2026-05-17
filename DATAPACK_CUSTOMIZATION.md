# Amphibia Datapack Customization

This document explains how modpack creators can customize Amphibia's frog estivation (hibernation) behavior.

## Estivation Configuration

Frogs will enter a protective mucus cocoon when environmental conditions become too harsh. Modpack creators can customize when this happens.

### Custom Config

Create a datapack with the following file:
```
your_datapack/
└── data/
    └── amphibia/
        └── estivation/
            └── config.json
```

### Config Format

```json
{
  "enabled": true,
  "max_temperature": 35.0,
  "min_humidity": 20.0,
  "cooldown_ticks_after_revival": 6000
}
```

### Settings

| Setting | Type | Default | Description |
|---------|------|---------|-------------|
| `enabled` | Boolean | `true` | Enable/disable estivation entirely |
| `max_temperature` | Float | `35.0` | Frogs estivate when temp exceeds this (°C) |
| `min_humidity` | Float | `20.0` | Frogs estivate when humidity drops below this (%) |
| `cooldown_ticks_after_revival` | Long | `6000` | Ticks before frog can estivate again after revival (6000 = 5 minutes) |

### Examples

**Disable estivation entirely:**
```json
{
  "enabled": false,
  "max_temperature": 35.0,
  "min_humidity": 20.0,
  "cooldown_ticks_after_revival": 6000
}
```

**Harsher conditions (frogs estivate more easily):**
```json
{
  "enabled": true,
  "max_temperature": 28.0,
  "min_humidity": 35.0,
  "cooldown_ticks_after_revival": 6000
}
```

**Longer recovery time (10 minutes before re-estivating):**
```json
{
  "enabled": true,
  "max_temperature": 35.0,
  "min_humidity": 20.0,
  "cooldown_ticks_after_revival": 12000
}
```

## Creating a Datapack

1. Create a folder for your datapack
2. Add `pack.mcmeta` in the root:
```json
{
  "pack": {
    "pack_format": 48,
    "description": "My Amphibia Tweaks"
  }
}
```

3. Create the `data/amphibia/estivation/config.json` file with your settings
4. Place the entire folder in `world/datapacks/` (single player) or the server's datapacks folder
5. Use `/reload` to apply changes

## Zone Integration

Estivation respects zone atmospheres (via Zen Zones integration). Frogs in detected zones will check temperature and humidity from their zone definition. Outside zones, they check ambient conditions.
