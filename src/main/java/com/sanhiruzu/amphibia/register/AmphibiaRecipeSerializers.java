package com.sanhiruzu.amphibia.register;

import com.sanhiruzu.amphibia.recipe.FrogChestToBucketRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AmphibiaRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, "zen_amphibia");

    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<FrogChestToBucketRecipe>> FROG_CHEST_TO_BUCKET =
            RECIPE_SERIALIZERS.register("frog_chest_to_bucket", () -> new SimpleCraftingRecipeSerializer<>(FrogChestToBucketRecipe::new));

    public static void register(IEventBus eventBus) {
        RECIPE_SERIALIZERS.register(eventBus);
    }
}
