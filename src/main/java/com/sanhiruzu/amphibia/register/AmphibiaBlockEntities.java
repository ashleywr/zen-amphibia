package com.sanhiruzu.amphibia.register;

import com.sanhiruzu.amphibia.block.DormantFrogspawnBlockEntity;
import com.sanhiruzu.amphibia.block.GeneticFrogspawnBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class AmphibiaBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "amphibia");

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DormantFrogspawnBlockEntity>> DORMANT_FROGSPAWN = BLOCK_ENTITIES.register("dormant_frogspawn",
            () -> BlockEntityType.Builder.of(DormantFrogspawnBlockEntity::new, AmphibiaBlocks.DORMANT_FROGSPAWN.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GeneticFrogspawnBlockEntity>> GENETIC_FROGSPAWN = BLOCK_ENTITIES.register("genetic_frogspawn",
            () -> BlockEntityType.Builder.of(GeneticFrogspawnBlockEntity::new, AmphibiaBlocks.GENETIC_FROGSPAWN.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
