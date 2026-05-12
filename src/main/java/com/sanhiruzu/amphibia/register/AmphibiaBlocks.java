package com.sanhiruzu.amphibia.register;

import com.sanhiruzu.amphibia.block.GeneticFrogspawnBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class AmphibiaBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, "amphibia");

    public static final DeferredHolder<Block, Block> MUCUS_COCOON = BLOCKS.register("mucus_cocoon",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_GREEN)
                    .strength(0.5f)
                    .sound(SoundType.SLIME_BLOCK)
                    .ignitedByLava()
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

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
