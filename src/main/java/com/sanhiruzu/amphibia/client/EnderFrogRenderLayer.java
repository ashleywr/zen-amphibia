package com.sanhiruzu.amphibia.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sanhiruzu.amphibia.genetics.FrogMutation;
import com.sanhiruzu.amphibia.register.AmphibiaAttachments;
import net.minecraft.client.model.FrogModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.animal.frog.Frog;

public class EnderFrogRenderLayer extends RenderLayer<Frog, FrogModel<Frog>> {
    public EnderFrogRenderLayer(RenderLayerParent<Frog, FrogModel<Frog>> pRenderer) {
        super(pRenderer);
    }

    @Override
    public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, Frog frog,
                       float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks,
                       float pNetHeadYaw, float pHeadPitch) {
        if (!FrogMutation.hasEnderMutation(frog)) {
            return;
        }

        // Render dark purple overlay for Ender Frog body
        // This creates a distinctive dark tint that makes the frog look otherworldly
        int enderColor = 0xFF4B0082;  // Deep indigo/purple tint
        VertexConsumer bodyConsumer = pBuffer.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(frog)));
        this.getParentModel().renderToBuffer(pPoseStack, bodyConsumer, pPackedLight,
            net.minecraft.client.renderer.entity.LivingEntityRenderer.getOverlayCoords(frog, 0.0F), enderColor);
    }
}
