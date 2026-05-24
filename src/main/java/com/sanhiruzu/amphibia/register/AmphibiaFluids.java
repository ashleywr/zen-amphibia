package com.sanhiruzu.amphibia.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class AmphibiaFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, "zen_amphibia");
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, "zen_amphibia");

    // Raw Genetic Fluid (Raw Breeding Output)
    public static final Supplier<FluidType> RAW_GENETIC_FLUID_TYPE = FLUID_TYPES.register("raw_genetic_fluid", () -> new FluidType(FluidType.Properties.create()
            .descriptionId("fluid.amphibia.raw_genetic_fluid")
            .density(1050)
            .viscosity(1500)
            .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
            .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)));

    public static final Supplier<FlowingFluid> RAW_GENETIC_FLUID = FLUIDS.register("raw_genetic_fluid", () -> new BaseFlowingFluid.Source(AmphibiaFluids.RAW_GENETIC_FLUID_PROPERTIES));
    public static final Supplier<FlowingFluid> FLOWING_RAW_GENETIC_FLUID = FLUIDS.register("flowing_raw_genetic_fluid", () -> new BaseFlowingFluid.Flowing(AmphibiaFluids.RAW_GENETIC_FLUID_PROPERTIES));

    public static final Supplier<LiquidBlock> RAW_GENETIC_FLUID_BLOCK = AmphibiaBlocks.BLOCKS.register("raw_genetic_fluid", () -> new LiquidBlock(RAW_GENETIC_FLUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable()));
    public static final Supplier<Item> RAW_GENETIC_FLUID_BUCKET = AmphibiaItems.ITEMS.register("raw_genetic_fluid_bucket", () -> new BucketItem(RAW_GENETIC_FLUID.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    private static final BaseFlowingFluid.Properties RAW_GENETIC_FLUID_PROPERTIES = new BaseFlowingFluid.Properties(
            RAW_GENETIC_FLUID_TYPE, RAW_GENETIC_FLUID, FLOWING_RAW_GENETIC_FLUID)
            .block(RAW_GENETIC_FLUID_BLOCK)
            .bucket(RAW_GENETIC_FLUID_BUCKET);

    // Refined Genetic Fluid (Centrifuge Elite Output - The "Ink")
    public static final Supplier<FluidType> REFINED_GENETIC_FLUID_TYPE = FLUID_TYPES.register("refined_genetic_fluid", () -> new FluidType(FluidType.Properties.create()
            .descriptionId("fluid.amphibia.refined_genetic_fluid")
            .density(2000)
            .viscosity(4000)
            .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
            .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)));

    public static final Supplier<FlowingFluid> REFINED_GENETIC_FLUID = FLUIDS.register("refined_genetic_fluid", () -> new BaseFlowingFluid.Source(AmphibiaFluids.REFINED_GENETIC_FLUID_PROPERTIES));
    public static final Supplier<FlowingFluid> FLOWING_REFINED_GENETIC_FLUID = FLUIDS.register("flowing_refined_genetic_fluid", () -> new BaseFlowingFluid.Flowing(AmphibiaFluids.REFINED_GENETIC_FLUID_PROPERTIES));

    public static final Supplier<LiquidBlock> REFINED_GENETIC_FLUID_BLOCK = AmphibiaBlocks.BLOCKS.register("refined_genetic_fluid", () -> new LiquidBlock(REFINED_GENETIC_FLUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable()));
    public static final Supplier<Item> REFINED_GENETIC_FLUID_BUCKET = AmphibiaItems.ITEMS.register("refined_genetic_fluid_bucket", () -> new BucketItem(REFINED_GENETIC_FLUID.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    private static final BaseFlowingFluid.Properties REFINED_GENETIC_FLUID_PROPERTIES = new BaseFlowingFluid.Properties(
            REFINED_GENETIC_FLUID_TYPE, REFINED_GENETIC_FLUID, FLOWING_REFINED_GENETIC_FLUID)
            .block(REFINED_GENETIC_FLUID_BLOCK)
            .bucket(REFINED_GENETIC_FLUID_BUCKET);

    // Sequenced Genetic Fluid (Hatchery Output - Ready for Spout)
    public static final Supplier<FluidType> SEQUENCED_GENETIC_FLUID_TYPE = FLUID_TYPES.register("sequenced_genetic_fluid", () -> new FluidType(FluidType.Properties.create()
            .descriptionId("fluid.amphibia.sequenced_genetic_fluid")
            .density(1200)
            .viscosity(2000)
            .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
            .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)));

    public static final Supplier<FlowingFluid> SEQUENCED_GENETIC_FLUID = FLUIDS.register("sequenced_genetic_fluid", () -> new BaseFlowingFluid.Source(AmphibiaFluids.SEQUENCED_GENETIC_FLUID_PROPERTIES));
    public static final Supplier<FlowingFluid> FLOWING_SEQUENCED_GENETIC_FLUID = FLUIDS.register("flowing_sequenced_genetic_fluid", () -> new BaseFlowingFluid.Flowing(AmphibiaFluids.SEQUENCED_GENETIC_FLUID_PROPERTIES));

    public static final Supplier<LiquidBlock> SEQUENCED_GENETIC_FLUID_BLOCK = AmphibiaBlocks.BLOCKS.register("sequenced_genetic_fluid", () -> new LiquidBlock(SEQUENCED_GENETIC_FLUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable()));
    public static final Supplier<Item> SEQUENCED_GENETIC_FLUID_BUCKET = AmphibiaItems.ITEMS.register("sequenced_genetic_fluid_bucket", () -> new BucketItem(SEQUENCED_GENETIC_FLUID.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    private static final BaseFlowingFluid.Properties SEQUENCED_GENETIC_FLUID_PROPERTIES = new BaseFlowingFluid.Properties(
            SEQUENCED_GENETIC_FLUID_TYPE, SEQUENCED_GENETIC_FLUID, FLOWING_SEQUENCED_GENETIC_FLUID)
            .block(SEQUENCED_GENETIC_FLUID_BLOCK)
            .bucket(SEQUENCED_GENETIC_FLUID_BUCKET);


    public static void register(IEventBus eventBus) {
        FLUID_TYPES.register(eventBus);
        FLUIDS.register(eventBus);
    }
}
