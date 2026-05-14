package com.sanhiruzu.amphibia.api;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import java.util.HashMap;
import java.util.Map;

/**
 * Registry for custom foods that frogs can eat.
 * Allows plugins to register items and mobs as frog food sources.
 */
public class FrogFoodRegistry {
    private static final Map<Item, Integer> FOOD_ITEMS = new HashMap<>();
    private static final Map<EntityType<?>, Integer> FOOD_MOBS = new HashMap<>();

    /**
     * Register an item as frog food.
     * @param item the item frogs can eat
     * @param nutritionValue how much this food helps tadpole growth (default 1)
     */
    public static void registerFoodItem(Item item, int nutritionValue) {
        FOOD_ITEMS.put(item, nutritionValue);
    }

    /**
     * Register a mob as frog food.
     * @param entityType the mob type frogs can eat
     * @param nutritionValue how much this food helps growth
     */
    public static void registerFoodMob(EntityType<?> entityType, int nutritionValue) {
        FOOD_MOBS.put(entityType, nutritionValue);
    }

    /**
     * Check if an item is registered as frog food.
     */
    public static boolean isFoodItem(Item item) {
        return FOOD_ITEMS.containsKey(item);
    }

    /**
     * Get nutrition value for an item.
     */
    public static int getFoodItemValue(Item item) {
        return FOOD_ITEMS.getOrDefault(item, 0);
    }

    /**
     * Check if a mob is registered as frog food.
     */
    public static boolean isFoodMob(EntityType<?> entityType) {
        return FOOD_MOBS.containsKey(entityType);
    }

    /**
     * Get nutrition value for a mob.
     */
    public static int getFoodMobValue(EntityType<?> entityType) {
        return FOOD_MOBS.getOrDefault(entityType, 0);
    }

    /**
     * Get all registered food items.
     */
    public static Map<Item, Integer> getFoodItems() {
        return new HashMap<>(FOOD_ITEMS);
    }

    /**
     * Get all registered food mobs.
     */
    public static Map<EntityType<?>, Integer> getFoodMobs() {
        return new HashMap<>(FOOD_MOBS);
    }
}
