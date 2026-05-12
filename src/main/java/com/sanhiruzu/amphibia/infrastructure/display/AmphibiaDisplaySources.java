package com.sanhiruzu.amphibia.infrastructure.display;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.api.behaviour.display.DisplaySource;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class AmphibiaDisplaySources {

    public static final FrogGeneticsDisplaySource FROG_GENETICS = new FrogGeneticsDisplaySource();

    @SubscribeEvent
    public static void onRegister(RegisterEvent event) {
        if (event.getRegistryKey().equals(com.simibubi.create.api.registry.CreateRegistries.DISPLAY_SOURCE)) {
            event.register(com.simibubi.create.api.registry.CreateRegistries.DISPLAY_SOURCE, 
                ResourceLocation.fromNamespaceAndPath("amphibia", "frog_genetics"), 
                () -> FROG_GENETICS);
        }
    }

    public static void registerAll() {
        // Associate with blocks so it shows up in the Display Link UI
        DisplaySource.BY_BLOCK.add(AllBlocks.BELT.get(), FROG_GENETICS);
        DisplaySource.BY_BLOCK.add(AllBlocks.DEPOT.get(), FROG_GENETICS);
    }
}
