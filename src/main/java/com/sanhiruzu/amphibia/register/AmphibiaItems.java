package com.sanhiruzu.amphibia.register;

import com.sanhiruzu.amphibia.entity.CricketEntity;
import com.sanhiruzu.amphibia.item.FrogBucketItem;
import com.sanhiruzu.amphibia.item.BottledFrogspawnItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;

public class AmphibiaItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("zen_amphibia");

    public static final DeferredItem<FrogBucketItem> FROG_BUCKET = ITEMS.register("frog_bucket",
            () -> new FrogBucketItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<BlockItem> DORMANT_FROGSPAWN = ITEMS.registerSimpleBlockItem("dormant_frogspawn", AmphibiaBlocks.DORMANT_FROGSPAWN);

    public static final DeferredItem<BlockItem> GENETIC_FROGSPAWN = ITEMS.registerSimpleBlockItem("genetic_frogspawn", AmphibiaBlocks.GENETIC_FROGSPAWN);

    public static final DeferredItem<BottledFrogspawnItem> BOTTLED_FROGSPAWN = ITEMS.register("bottled_frogspawn",
            () -> new BottledFrogspawnItem(new Item.Properties().stacksTo(64)));

    public static final DeferredItem<Item> CRICKET = ITEMS.register("cricket",
            () -> new Item(new Item.Properties().stacksTo(64)));

    public static final DeferredItem<SpawnEggItem> CRICKET_SPAWN_EGG = ITEMS.register("cricket_spawn_egg",
            () -> new SpawnEggItem(AmphibiaEntityTypes.CRICKET.get(), 0x8B4513, 0xA0522D, new Item.Properties().stacksTo(64)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
