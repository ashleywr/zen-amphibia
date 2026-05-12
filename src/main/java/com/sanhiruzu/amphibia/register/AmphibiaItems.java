package com.sanhiruzu.amphibia.register;

import net.minecraft.world.item.BlockItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;

public class AmphibiaItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("amphibia");

    public static final DeferredItem<BlockItem> MUCUS_COCOON = ITEMS.registerSimpleBlockItem("mucus_cocoon", AmphibiaBlocks.MUCUS_COCOON);

    public static final DeferredItem<com.sanhiruzu.amphibia.item.FrogBucketItem> FROG_BUCKET = ITEMS.register("frog_bucket",
            () -> new com.sanhiruzu.amphibia.item.FrogBucketItem(new net.minecraft.world.item.Item.Properties().stacksTo(1)));

    public static final DeferredItem<BlockItem> GENETIC_FROGSPAWN = ITEMS.registerSimpleBlockItem("genetic_frogspawn", AmphibiaBlocks.GENETIC_FROGSPAWN);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
