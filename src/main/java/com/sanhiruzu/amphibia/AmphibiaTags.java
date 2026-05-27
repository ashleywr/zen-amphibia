package com.sanhiruzu.amphibia;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class AmphibiaTags {
    public static class Blocks {
        public static final TagKey<Block> FROG_PLANTS = BlockTags.create(
            ResourceLocation.fromNamespaceAndPath("zen_amphibia", "frog_plants"));
        public static final TagKey<Block> FROG_WARMING_BLOCKS = BlockTags.create(
            ResourceLocation.fromNamespaceAndPath("zen_amphibia", "frog_warming_blocks"));
        public static final TagKey<Block> FROG_COOLING_BLOCKS = BlockTags.create(
            ResourceLocation.fromNamespaceAndPath("zen_amphibia", "frog_cooling_blocks"));
        public static final TagKey<Block> FROG_HUMID_SOURCES = BlockTags.create(
            ResourceLocation.fromNamespaceAndPath("zen_amphibia", "frog_humid_sources"));
    }
}
