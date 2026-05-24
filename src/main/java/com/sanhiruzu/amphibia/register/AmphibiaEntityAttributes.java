package com.sanhiruzu.amphibia.register;

import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = "zen_amphibia")
public class AmphibiaEntityAttributes {

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        // Frog already has MAX_HEALTH, just need to add ATTACK_DAMAGE for combat
    }
}
