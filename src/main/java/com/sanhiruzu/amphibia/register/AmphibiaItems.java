package com.sanhiruzu.amphibia.register;

import com.sanhiruzu.amphibia.entity.CricketEntity;
import com.sanhiruzu.amphibia.item.AmphibiaTiers;
import com.sanhiruzu.amphibia.item.FrogBucketItem;
import com.sanhiruzu.amphibia.item.BottledFrogspawnItem;
import com.sanhiruzu.amphibia.item.FrogspawnBlockItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;

public class AmphibiaItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("zen_amphibia");

    public static final DeferredItem<FrogBucketItem> FROG_BUCKET = ITEMS.register("frog_bucket",
            () -> new FrogBucketItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<BlockItem> DORMANT_FROGSPAWN = ITEMS.register("dormant_frogspawn",
            () -> new FrogspawnBlockItem(AmphibiaBlocks.DORMANT_FROGSPAWN.get(), new Item.Properties(), true));

    public static final DeferredItem<BlockItem> GENETIC_FROGSPAWN = ITEMS.register("genetic_frogspawn",
            () -> new FrogspawnBlockItem(AmphibiaBlocks.GENETIC_FROGSPAWN.get(), new Item.Properties(), false));

    public static final DeferredItem<BottledFrogspawnItem> BOTTLED_FROGSPAWN = ITEMS.register("bottled_frogspawn",
            () -> new BottledFrogspawnItem(new Item.Properties().stacksTo(64)));

    public static final DeferredItem<BlockItem> FROG_CHEST = ITEMS.register("frog_chest",
            () -> new BlockItem(AmphibiaBlocks.FROG_CHEST.get(), new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> CRICKET = ITEMS.register("cricket",
            () -> new Item(new Item.Properties().stacksTo(64)));

    public static final DeferredItem<Item> FROG_SLIME = ITEMS.register("frog_slime",
            () -> new Item(new Item.Properties().stacksTo(64)));

    public static final DeferredItem<Item> FROG_SLIME_INGOT = ITEMS.register("frog_slime_ingot",
            () -> new Item(new Item.Properties().stacksTo(64)));

    public static final DeferredItem<SwordItem> FROG_SLIME_SWORD = ITEMS.register("frog_slime_sword",
            () -> new SwordItem(AmphibiaTiers.FROG_SLIME,
                    new Item.Properties().attributes(SwordItem.createAttributes(AmphibiaTiers.FROG_SLIME, 3, -2.4F))));
    public static final DeferredItem<ShovelItem> FROG_SLIME_SHOVEL = ITEMS.register("frog_slime_shovel",
            () -> new ShovelItem(AmphibiaTiers.FROG_SLIME,
                    new Item.Properties().attributes(ShovelItem.createAttributes(AmphibiaTiers.FROG_SLIME, 1.5F, -3.0F))));
    public static final DeferredItem<PickaxeItem> FROG_SLIME_PICKAXE = ITEMS.register("frog_slime_pickaxe",
            () -> new PickaxeItem(AmphibiaTiers.FROG_SLIME,
                    new Item.Properties().attributes(PickaxeItem.createAttributes(AmphibiaTiers.FROG_SLIME, 1.0F, -2.8F))));
    public static final DeferredItem<AxeItem> FROG_SLIME_AXE = ITEMS.register("frog_slime_axe",
            () -> new AxeItem(AmphibiaTiers.FROG_SLIME,
                    new Item.Properties().attributes(AxeItem.createAttributes(AmphibiaTiers.FROG_SLIME, 6.0F, -3.1F))));
    public static final DeferredItem<HoeItem> FROG_SLIME_HOE = ITEMS.register("frog_slime_hoe",
            () -> new HoeItem(AmphibiaTiers.FROG_SLIME,
                    new Item.Properties().attributes(HoeItem.createAttributes(AmphibiaTiers.FROG_SLIME, -2.0F, -1.0F))));

    public static final DeferredItem<ArmorItem> FROG_SLIME_HELMET = ITEMS.register("frog_slime_helmet",
            () -> new ArmorItem(AmphibiaArmorMaterials.frogSlime(), ArmorItem.Type.HELMET,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(18))));
    public static final DeferredItem<ArmorItem> FROG_SLIME_CHESTPLATE = ITEMS.register("frog_slime_chestplate",
            () -> new ArmorItem(AmphibiaArmorMaterials.frogSlime(), ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(18))));
    public static final DeferredItem<ArmorItem> FROG_SLIME_LEGGINGS = ITEMS.register("frog_slime_leggings",
            () -> new ArmorItem(AmphibiaArmorMaterials.frogSlime(), ArmorItem.Type.LEGGINGS,
                    new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(18))));
    public static final DeferredItem<ArmorItem> FROG_SLIME_BOOTS = ITEMS.register("frog_slime_boots",
            () -> new ArmorItem(AmphibiaArmorMaterials.frogSlime(), ArmorItem.Type.BOOTS,
                    new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(18))));

    public static final DeferredItem<DeferredSpawnEggItem> CRICKET_SPAWN_EGG = ITEMS.register("cricket_spawn_egg",
            () -> new DeferredSpawnEggItem(AmphibiaEntityTypes.CRICKET, 0x8B4513, 0xA0522D, new Item.Properties().stacksTo(64)));

    public static final DeferredItem<BlockItem> VERDANT_GENETIC_FROGLIGHT = registerBlockItem("verdant_genetic_froglight", AmphibiaBlocks.VERDANT_GENETIC_FROGLIGHT);
    public static final DeferredItem<BlockItem> AZURE_GENETIC_FROGLIGHT = registerBlockItem("azure_genetic_froglight", AmphibiaBlocks.AZURE_GENETIC_FROGLIGHT);
    public static final DeferredItem<BlockItem> ROSE_GENETIC_FROGLIGHT = registerBlockItem("rose_genetic_froglight", AmphibiaBlocks.ROSE_GENETIC_FROGLIGHT);
    public static final DeferredItem<BlockItem> AMBER_GENETIC_FROGLIGHT = registerBlockItem("amber_genetic_froglight", AmphibiaBlocks.AMBER_GENETIC_FROGLIGHT);
    public static final DeferredItem<BlockItem> VIOLET_GENETIC_FROGLIGHT = registerBlockItem("violet_genetic_froglight", AmphibiaBlocks.VIOLET_GENETIC_FROGLIGHT);
    public static final DeferredItem<BlockItem> PEARL_GENETIC_FROGLIGHT = registerBlockItem("pearl_genetic_froglight", AmphibiaBlocks.PEARL_GENETIC_FROGLIGHT);
    public static final DeferredItem<BlockItem> UMBRAL_GENETIC_FROGLIGHT = registerBlockItem("umbral_genetic_froglight", AmphibiaBlocks.UMBRAL_GENETIC_FROGLIGHT);

    private static DeferredItem<BlockItem> registerBlockItem(String name, java.util.function.Supplier<? extends net.minecraft.world.level.block.Block> block) {
        return ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
