package com.sanhiruzu.amphibia.recipe;

import com.sanhiruzu.amphibia.genetics.FrogGenome;
import com.sanhiruzu.amphibia.register.AmphibiaDataComponents;
import com.sanhiruzu.amphibia.register.AmphibiaItems;
import com.sanhiruzu.amphibia.register.AmphibiaRecipeSerializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class FrogChestToBucketRecipe extends CustomRecipe {
    public FrogChestToBucketRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        ItemStack chest = ItemStack.EMPTY;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (!stack.is(AmphibiaItems.FROG_CHEST.get()) || !chest.isEmpty() || !isEmptyChest(stack)) {
                return false;
            }
            chest = stack;
        }
        return !chest.isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.is(AmphibiaItems.FROG_CHEST.get()) && isEmptyChest(stack)) {
                ItemStack bucket = new ItemStack(AmphibiaItems.FROG_BUCKET.get());
                bucket.set(AmphibiaDataComponents.FROG_DNA.get(),
                        stack.getOrDefault(AmphibiaDataComponents.FROG_DNA.get(), FrogGenome.createDefault()));
                return bucket;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return AmphibiaRecipeSerializers.FROG_CHEST_TO_BUCKET.get();
    }

    private static boolean isEmptyChest(ItemStack stack) {
        ItemContainerContents contents = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        return contents.nonEmptyStream().findAny().isEmpty();
    }
}
