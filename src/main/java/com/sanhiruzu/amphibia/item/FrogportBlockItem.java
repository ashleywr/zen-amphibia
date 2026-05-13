package com.sanhiruzu.amphibia.item;

import com.sanhiruzu.amphibia.genetics.FrogDNA;
import com.sanhiruzu.amphibia.register.AmphibiaDataComponents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class FrogportBlockItem extends BlockItem {
    public FrogportBlockItem(Block block, Properties properties) {
        super(block, properties.stacksTo(64));
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        FrogDNA dna = stack.get(AmphibiaDataComponents.FROG_DNA.get());
        return dna != null ? 1 : 64;
    }
}
