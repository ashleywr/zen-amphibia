package com.sanhiruzu.amphibia.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sanhiruzu.amphibia.genetics.FrogDNA;
import com.sanhiruzu.amphibia.register.AmphibiaAttachments;
import net.minecraft.client.model.FrogModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.animal.frog.Frog;

public class MutationRenderLayer extends RenderLayer<Frog, FrogModel<Frog>> {
    public MutationRenderLayer(RenderLayerParent<Frog, FrogModel<Frog>> pRenderer) {
        super(pRenderer);
    }

    @Override
    public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, Frog frog,
                       float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks,
                       float pNetHeadYaw, float pHeadPitch) {
        FrogDNA dna = frog.getData(AmphibiaAttachments.FROG_DNA);
        if (dna == null || dna.mutations().isEmpty()) {
            return;
        }

        for (String mutationId : dna.mutations()) {
            MutationVisuals visuals = MutationVisualRegistry.get(mutationId);
            if (visuals != null) {
                renderMutationTint(pPoseStack, pBuffer, pPackedLight, frog, visuals);
            }
        }
    }

    private void renderMutationTint(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight,
                                    Frog frog, MutationVisuals visuals) {
        VertexConsumer consumer = pBuffer.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(frog)));
        this.getParentModel().renderToBuffer(pPoseStack, consumer, pPackedLight,
            LivingEntityRenderer.getOverlayCoords(frog, 0.0F), visuals.colorTint());
    }
}
