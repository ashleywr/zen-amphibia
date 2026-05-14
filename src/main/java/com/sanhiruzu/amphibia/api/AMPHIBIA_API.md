# Amphibia Plugin API

## Overview

Amphibia provides a plugin system for other mods to integrate with the frog breeding system.

## Plugin Registration

### Basic Plugin

Create a class implementing `AmphibiaPlugin`:

```java
public class MyAmphibiaPlugin implements AmphibiaPlugin {
    @Override
    public void onLoad() {
        // Register your integrations here
    }

    @Override
    public String getPluginName() {
        return "My Mod Amphibia Integration";
    }
}
```

Register it in your mod's setup event:

```java
@Mod.EventBusSubscriber(modid = "mymod")
public class MyModEvents {
    @SubscribeEvent
    public static void setup(FMLCommonSetupEvent event) {
        AmphibiaPluginRegistry.register(new MyAmphibiaPlugin());
    }
}
```

## Registering Frog Foods

Use `FrogFoodRegistry` to register items or mobs that frogs can eat:

```java
@Override
public void onLoad() {
    // Register item as frog food (nutrition value 5)
    FrogFoodRegistry.registerFoodItem(ModItems.FLY.get(), 5);
    
    // Register mob as frog food
    FrogFoodRegistry.registerFoodMob(ModEntities.FLY.get(), 5);
}
```

Frogs will preferentially seek out and eat registered food items and mobs, boosting their growth rate.

## Patchouli Book Integration

If Patchouli is installed, you can provide documentation books:

1. Create book JSON at:
   ```
   src/main/resources/data/mymod/patchouli_books/mybook.json
   ```

2. Add book definition with Amphibia category/entries

3. Amphibia will auto-detect and load it when Patchouli is present

Example book structure:
```
src/main/resources/data/mymod/
├── patchouli_books/
│   └── mybook.json
├── patchouli_categories/
│   └── amphibia_frog_care.json
└── patchouli_entries/
    └── feeding_frogs.json
```

## Mutation System

Access frog genetics and apply custom mutations:

```java
FrogGenome genome = tadpole.getData(AmphibiaAttachments.FROG_GENOME);
if (genome != null) {
    // Check traits
    if (genome.genes().containsKey("temperature_tolerance")) {
        // Custom behavior
    }
}
```

## Best Practices

- Keep plugins lightweight
- Use try-catch in `onLoad()` to prevent breaking on version mismatches
- Use reflection for optional dependencies
- Register foods with appropriate nutrition values (1-10 typical range)
