package com.sanhiruzu.amphibia.client;

import com.sanhiruzu.amphibia.client.render.AmphibiaFrogRenderer;
import com.sanhiruzu.amphibia.client.render.CricketRenderer;
import com.sanhiruzu.amphibia.client.render.FrogChestRenderer;
import com.sanhiruzu.amphibia.client.screen.FrogChestScreen;
import com.sanhiruzu.amphibia.client.model.CricketModel;
import com.sanhiruzu.amphibia.register.AmphibiaBlockEntities;
import com.sanhiruzu.amphibia.register.AmphibiaEntityTypes;
import com.sanhiruzu.amphibia.register.AmphibiaMenuTypes;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class AmphibiaClientSetup {
    public static void register(IEventBus modEventBus) {
        modEventBus.register(AmphibiaClientSetup.class);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(CricketModel.LAYER_LOCATION, CricketModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityType.FROG, AmphibiaFrogRenderer::new);
        event.registerEntityRenderer(AmphibiaEntityTypes.CRICKET.get(), CricketRenderer::new);
        event.registerBlockEntityRenderer(AmphibiaBlockEntities.FROG_CHEST.get(), FrogChestRenderer::new);
    }

    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(AmphibiaMenuTypes.FROG_CHEST_3_ROWS.get(), FrogChestScreen::new);
        event.register(AmphibiaMenuTypes.FROG_CHEST_4_ROWS.get(), FrogChestScreen::new);
        event.register(AmphibiaMenuTypes.FROG_CHEST_5_ROWS.get(), FrogChestScreen::new);
        event.register(AmphibiaMenuTypes.FROG_CHEST_6_ROWS.get(), FrogChestScreen::new);
        event.register(AmphibiaMenuTypes.FROG_CHEST_7_ROWS.get(), FrogChestScreen::new);
        event.register(AmphibiaMenuTypes.FROG_CHEST_8_ROWS.get(), FrogChestScreen::new);
        event.register(AmphibiaMenuTypes.FROG_CHEST_9_ROWS.get(), FrogChestScreen::new);
    }
}
