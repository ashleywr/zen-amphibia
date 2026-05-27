package com.sanhiruzu.amphibia.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sanhiruzu.amphibia.entity.CricketEntity;
import com.sanhiruzu.amphibia.client.model.CricketModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class CricketRenderer extends MobRenderer<CricketEntity, CricketModel> {
    private static final ResourceLocation CRICKET_TEXTURE = ResourceLocation.fromNamespaceAndPath("zen_amphibia", "textures/entity/cricket.png");

    public CricketRenderer(EntityRendererProvider.Context context) {
        super(context, new CricketModel(context.bakeLayer(CricketModel.LAYER_LOCATION)), 0.15f);
    }

    @Override
    protected void scale(CricketEntity entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(0.5f, 0.5f, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(CricketEntity entity) {
        return CRICKET_TEXTURE;
    }
}
