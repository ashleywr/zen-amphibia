package com.sanhiruzu.amphibia.register;

import com.sanhiruzu.amphibia.genetics.FrogGenome;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.UnaryOperator;

public class AmphibiaDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "zen_amphibia");

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FrogGenome>> FROG_DNA =
        register("frog_dna", builder -> builder.persistent(FrogGenome.CODEC).networkSynchronized(FrogGenome.STREAM_CODEC));

    // Stores which genetic froglight type a pigmented secretion will produce.
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceLocation>> FROGLIGHT_TYPE =
        register("froglight_type", builder -> builder
            .persistent(ResourceLocation.CODEC)
            .networkSynchronized(ResourceLocation.STREAM_CODEC));

    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
        return DATA_COMPONENT_TYPES.register(name, () -> builder.apply(DataComponentType.builder()).build());
    }

    public static void register(IEventBus eventBus) {
        DATA_COMPONENT_TYPES.register(eventBus);
    }
}
