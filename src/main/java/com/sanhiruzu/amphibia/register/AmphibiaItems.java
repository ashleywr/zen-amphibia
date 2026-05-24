package com.sanhiruzu.amphibia.register;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;

public class AmphibiaItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("zen_amphibia");

    public static final DeferredItem<com.sanhiruzu.amphibia.item.FrogBucketItem> FROG_BUCKET = ITEMS.register("frog_bucket",
            () -> new com.sanhiruzu.amphibia.item.FrogBucketItem(new net.minecraft.world.item.Item.Properties().stacksTo(1)));

    public static final DeferredItem<BlockItem> DORMANT_FROGSPAWN = ITEMS.registerSimpleBlockItem("dormant_frogspawn", AmphibiaBlocks.DORMANT_FROGSPAWN);

    public static final DeferredItem<BlockItem> GENETIC_FROGSPAWN = ITEMS.registerSimpleBlockItem("genetic_frogspawn", AmphibiaBlocks.GENETIC_FROGSPAWN);

    public static final DeferredItem<com.sanhiruzu.amphibia.item.BottledFrogspawnItem> BOTTLED_FROGSPAWN = ITEMS.register("bottled_frogspawn",
            () -> new com.sanhiruzu.amphibia.item.BottledFrogspawnItem(new net.minecraft.world.item.Item.Properties().stacksTo(64)));

    public static final DeferredItem<net.minecraft.world.item.Item> CRICKET = ITEMS.register("cricket",
            () -> new net.minecraft.world.item.Item(new net.minecraft.world.item.Item.Properties().stacksTo(64)));

    public static final DeferredItem<SpawnEggItem> CRICKET_SPAWN_EGG = ITEMS.register("cricket_spawn_egg",
            () -> new SpawnEggItem(AmphibiaEntityTypes.CRICKET.get(), 0x8B4513, 0xA0522D, new net.minecraft.world.item.Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
