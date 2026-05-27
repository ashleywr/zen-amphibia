package com.sanhiruzu.amphibia.register;

import com.sanhiruzu.amphibia.block.DormantFrogspawnBlock;
import com.sanhiruzu.amphibia.block.FrogChestBlock;
import com.sanhiruzu.amphibia.block.GeneticFrogspawnBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class AmphibiaBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, "zen_amphibia");

    public static final DeferredHolder<Block, DormantFrogspawnBlock> DORMANT_FROGSPAWN = BLOCKS.register("dormant_frogspawn",
            () -> new DormantFrogspawnBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WATER)
                    .noCollission()
                    .noOcclusion()
                    .instabreak()
                    .sound(SoundType.FROGSPAWN)
                    .pushReaction(PushReaction.DESTROY)
            ));

    public static final DeferredHolder<Block, GeneticFrogspawnBlock> GENETIC_FROGSPAWN = BLOCKS.register("genetic_frogspawn",
            () -> new GeneticFrogspawnBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WATER)
                    .noCollission()
                    .noOcclusion()
                    .randomTicks()
                    .instabreak()
                    .sound(SoundType.FROGSPAWN)
                    .pushReaction(PushReaction.DESTROY)
            ));

    public static final DeferredHolder<Block, FrogChestBlock> FROG_CHEST = BLOCKS.register("frog_chest",
            () -> new FrogChestBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .strength(2.5F)
                    .sound(SoundType.WOOD)
            ));

    public static final DeferredHolder<Block, RotatedPillarBlock> VERDANT_GENETIC_FROGLIGHT = registerGeneticFroglight("verdant_genetic_froglight", MapColor.GLOW_LICHEN);
    public static final DeferredHolder<Block, RotatedPillarBlock> AZURE_GENETIC_FROGLIGHT = registerGeneticFroglight("azure_genetic_froglight", MapColor.COLOR_LIGHT_BLUE);
    public static final DeferredHolder<Block, RotatedPillarBlock> ROSE_GENETIC_FROGLIGHT = registerGeneticFroglight("rose_genetic_froglight", MapColor.COLOR_PINK);
    public static final DeferredHolder<Block, RotatedPillarBlock> AMBER_GENETIC_FROGLIGHT = registerGeneticFroglight("amber_genetic_froglight", MapColor.COLOR_YELLOW);
    public static final DeferredHolder<Block, RotatedPillarBlock> VIOLET_GENETIC_FROGLIGHT = registerGeneticFroglight("violet_genetic_froglight", MapColor.COLOR_PURPLE);
    public static final DeferredHolder<Block, RotatedPillarBlock> PEARL_GENETIC_FROGLIGHT = registerGeneticFroglight("pearl_genetic_froglight", MapColor.SNOW);
    public static final DeferredHolder<Block, RotatedPillarBlock> UMBRAL_GENETIC_FROGLIGHT = registerGeneticFroglight("umbral_genetic_froglight", MapColor.COLOR_BLACK);

    private static DeferredHolder<Block, RotatedPillarBlock> registerGeneticFroglight(String name, MapColor mapColor) {
        return BLOCKS.register(name, () -> new RotatedPillarBlock(BlockBehaviour.Properties.of()
                .mapColor(mapColor)
                .strength(0.3F)
                .lightLevel(state -> 15)
                .sound(SoundType.FROGLIGHT)
        ));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
