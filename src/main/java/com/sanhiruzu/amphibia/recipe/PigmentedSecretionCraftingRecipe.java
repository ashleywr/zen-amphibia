package com.sanhiruzu.amphibia.recipe;

import com.sanhiruzu.amphibia.register.AmphibiaDataComponents;
import com.sanhiruzu.amphibia.register.AmphibiaItems;
import com.sanhiruzu.amphibia.register.AmphibiaRecipeSerializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class PigmentedSecretionCraftingRecipe extends CustomRecipe {

    public PigmentedSecretionCraftingRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        int pigmented = 0;
        int white = 0;
        int glowstone = 0;
        int magmaCream = 0;
        int other = 0;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;
            if (stack.is(AmphibiaItems.PIGMENTED_FROG_SECRETION.get())
                    && stack.has(AmphibiaDataComponents.FROGLIGHT_TYPE.get())) {
                pigmented++;
            } else if (stack.is(AmphibiaItems.LUMINOUS_FROG_SECRETION.get())) {
                white++;
            } else if (stack.is(Items.GLOWSTONE_DUST)) {
                glowstone++;
            } else if (stack.is(Items.MAGMA_CREAM)) {
                magmaCream++;
            } else {
                other++;
            }
        }

        return pigmented == 1 && white >= 2 && glowstone == 1 && magmaCream == 1 && other == 0;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.is(AmphibiaItems.PIGMENTED_FROG_SECRETION.get())) continue;
            ResourceLocation froglightId = stack.get(AmphibiaDataComponents.FROGLIGHT_TYPE.get());
            if (froglightId == null) continue;
            Item froglightItem = BuiltInRegistries.ITEM.get(froglightId);
            if (froglightItem == null || froglightItem == Items.AIR) continue;
            return new ItemStack(froglightItem, 1);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 5;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return AmphibiaRecipeSerializers.PIGMENTED_SECRETION_CRAFTING.get();
    }
}
