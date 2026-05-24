package com.sanhiruzu.amphibia.register;

import com.sanhiruzu.amphibia.entity.CricketEntity;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = "zen_amphibia")
public class AmphibiaEntityAttributes {

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(AmphibiaEntityTypes.CRICKET.get(), CricketEntity.createAttributes().build());
    }
}
