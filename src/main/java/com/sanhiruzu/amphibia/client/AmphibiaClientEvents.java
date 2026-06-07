package com.sanhiruzu.amphibia.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sanhiruzu.amphibia.client.render.MutationRenderLayer;
import com.sanhiruzu.amphibia.client.render.MutationVisualRegistry;
import com.sanhiruzu.amphibia.client.render.MutationVisuals;
import com.sanhiruzu.amphibia.genetics.FrogGenome;
import com.sanhiruzu.amphibia.infrastructure.FrogDNADisplayHelper;
import com.sanhiruzu.amphibia.register.AmphibiaAttachments;
import com.sanhiruzu.amphibia.register.AmphibiaDataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.FrogModel;
import net.minecraft.client.model.TadpoleModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.FrogRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.TadpoleRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import com.sanhiruzu.amphibia.register.AmphibiaItems;

public class AmphibiaClientEvents {
    public static void register(IEventBus modEventBus) {
        modEventBus.register(AmphibiaClientEvents.class);
        NeoForge.EVENT_BUS.addListener(AmphibiaClientEvents::onClientTick);
    }

    @SubscribeEvent
    public static void onRegisterItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> {
            if (tintIndex == 1) { // Assume layer 1 is the tintable frog part
                FrogGenome genome = stack.get(AmphibiaDataComponents.FROG_DNA.get());
                if (genome != null) {
                    return FrogDNADisplayHelper.getGenomeColor(genome);
                }
            }
            return -1;
        }, AmphibiaItems.FROG_BUCKET.get());
    }

    @SubscribeEvent
    public static void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR,
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("zen_amphibia", "frog_dna_overlay"),
            (guiGraphics, partialTick) -> CrosshairTooltipHandler.renderIfNeeded(guiGraphics, Minecraft.getInstance()));
    }

    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        FrogRenderer renderer = event.getRenderer(net.minecraft.world.entity.EntityType.FROG);
        if (renderer != null) {
            renderer.addLayer(new FrogColorLayer(renderer));
            renderer.addLayer(new MutationRenderLayer(renderer));
        }

        TadpoleRenderer tadpoleRenderer = event.getRenderer(net.minecraft.world.entity.EntityType.TADPOLE);
        if (tadpoleRenderer != null) {
            tadpoleRenderer.addLayer(new TadpoleStressColorLayer(tadpoleRenderer));
        }
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        long gameTime = mc.level.getGameTime();

        for (Frog frog : mc.level.getEntitiesOfClass(Frog.class, mc.player.getBoundingBox().inflate(32))) {
            FrogGenome genome = frog.getData(AmphibiaAttachments.FROG_GENOME);
            if (genome == null || genome.mutations().isEmpty()) continue;

            for (String mutationId : genome.mutations()) {
                MutationVisuals visuals = MutationVisualRegistry.get(mutationId);
                if (visuals != null && gameTime % visuals.particleInterval() == 0) {
                    double x = frog.getX() + (mc.level.random.nextDouble() - 0.5) * 1.5;
                    double y = frog.getY() + mc.level.random.nextDouble() * 1.5;
                    double z = frog.getZ() + (mc.level.random.nextDouble() - 0.5) * 1.5;

                    mc.level.addParticle(visuals.particleType(), x, y, z,
                        (mc.level.random.nextDouble() - 0.5) * 0.2,
                        (mc.level.random.nextDouble() - 0.5) * 0.2,
                        (mc.level.random.nextDouble() - 0.5) * 0.2);
                }
            }

            if (frog.getData(AmphibiaAttachments.SLIME_HARVEST_READY) && gameTime % 15 == 0) {
                double x = frog.getX() + (mc.level.random.nextDouble() - 0.5) * 0.8;
                double y = frog.getY() + 0.3 + mc.level.random.nextDouble() * 0.5;
                double z = frog.getZ() + (mc.level.random.nextDouble() - 0.5) * 0.8;
                mc.level.addParticle(ParticleTypes.DRIPPING_WATER, x, y, z, 0, 0, 0);
            }
        }
    }

    @SuppressWarnings("NullableProblems")
    public static class FrogColorLayer extends RenderLayer<Frog, FrogModel<Frog>> {
        public FrogColorLayer(RenderLayerParent<Frog, FrogModel<Frog>> pRenderer) {
            super(pRenderer);
        }

        @Override
        public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, Frog frog, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
            FrogGenome genome = frog.getData(AmphibiaAttachments.FROG_GENOME);
            if (genome != null) {
                int color = FrogDNADisplayHelper.getGenomeColor(genome);
                VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(frog)));
                this.getParentModel().renderToBuffer(pPoseStack, vertexconsumer, pPackedLight, net.minecraft.client.renderer.entity.LivingEntityRenderer.getOverlayCoords(frog, 0.0F), color);
            }
        }
    }

    @SuppressWarnings("NullableProblems")
    public static class TadpoleStressColorLayer extends RenderLayer<Tadpole, TadpoleModel<Tadpole>> {
        private static final int OVERCROWDED_TINT = 0xFF7D9B67;

        public TadpoleStressColorLayer(RenderLayerParent<Tadpole, TadpoleModel<Tadpole>> renderer) {
            super(renderer);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, Tadpole tadpole, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (!tadpole.getData(AmphibiaAttachments.STUNTED_GROWTH)) {
                return;
            }

            VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(tadpole)));
            this.getParentModel().renderToBuffer(
                poseStack,
                vertexConsumer,
                packedLight,
                net.minecraft.client.renderer.entity.LivingEntityRenderer.getOverlayCoords(tadpole, 0.0F),
                OVERCROWDED_TINT
            );
        }
    }

}
