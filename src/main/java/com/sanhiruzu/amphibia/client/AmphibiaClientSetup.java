package com.sanhiruzu.amphibia.client;

import com.sanhiruzu.amphibia.client.render.CricketRenderer;
import com.sanhiruzu.amphibia.client.model.CricketModel;
import com.sanhiruzu.amphibia.register.AmphibiaEntityTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = "zen_amphibia", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class AmphibiaClientSetup {

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(CricketModel.LAYER_LOCATION, CricketModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(AmphibiaEntityTypes.CRICKET.get(), CricketRenderer::new);
    }
}
