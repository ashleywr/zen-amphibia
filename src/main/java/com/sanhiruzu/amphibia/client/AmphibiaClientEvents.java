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
import net.minecraft.client.model.FrogModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.FrogRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.animal.frog.Frog;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.minecraft.client.Minecraft;

import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import com.sanhiruzu.amphibia.register.AmphibiaItems;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = "zen_amphibia", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class AmphibiaClientEvents {

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
        event.registerAbove(VanillaGuiLayers.HOTBAR, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("zen_amphibia", "frog_dna_overlay"),
            (guiGraphics, partialTick) -> {
                net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
                if (mc.options.hideGui || mc.gameMode == null || mc.player == null) return;

                boolean overlayEnabled = mc.player.getData(AmphibiaAttachments.FROG_DNA_OVERLAY_ENABLED);
                boolean wearingGoggles = false;
                if (ModList.get().isLoaded("create")) {
                    wearingGoggles = com.simibubi.create.content.equipment.goggles.GogglesItem.isWearingGoggles(mc.player);
                }
                if (!overlayEnabled && !wearingGoggles) return;

                net.minecraft.world.phys.HitResult hitResult = mc.hitResult;
                if (!(hitResult instanceof net.minecraft.world.phys.EntityHitResult entityHitResult)) return;

                net.minecraft.world.entity.Entity entity = entityHitResult.getEntity();
                if (!(entity instanceof Frog frog)) return;

                FrogGenome genome = frog.getData(AmphibiaAttachments.FROG_GENOME);
                java.util.List<net.minecraft.network.chat.Component> tooltip = FrogDNADisplayHelper.getFrogDebugInfo(frog, genome);

                var lines = tooltip.stream()
                    .map(net.minecraft.network.chat.Component::getVisualOrderText)
                    .toList();

                // Measure tooltip to clamp within screen bounds
                int maxWidth = 0;
                for (var line : lines) {
                    int w = mc.font.width(line);
                    if (w > maxWidth) maxWidth = w;
                }
                int lineHeight = mc.font.lineHeight + 1;
                int paddingX = 12;
                int paddingY = 6;
                int tooltipW = maxWidth + paddingX;
                int tooltipH = lines.size() * lineHeight + paddingY;

                int screenWidth = guiGraphics.guiWidth();
                int screenHeight = guiGraphics.guiHeight();
                int cx = screenWidth / 2;
                int cy = screenHeight / 2;

                // Default: right of crosshair, slightly below
                int x = cx + 15;
                int y = cy + 15;

                // If it clips the right edge, flip to left of crosshair
                if (x + tooltipW > screenWidth - 4) {
                    x = cx - tooltipW - 17;
                }
                // If it clips the bottom, pull up
                if (y + tooltipH > screenHeight - 4) {
                    y = screenHeight - tooltipH - 4;
                }
                if (x < 4) x = 4;
                if (y < 4) y = 4;

                guiGraphics.renderTooltip(mc.font, lines, x, y);
            });
    }

    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        FrogRenderer renderer = event.getRenderer(net.minecraft.world.entity.EntityType.FROG);
        if (renderer != null) {
            renderer.addLayer(new FrogColorLayer(renderer));
            renderer.addLayer(new MutationRenderLayer(renderer));
        }
    }

    @SubscribeEvent
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

}
