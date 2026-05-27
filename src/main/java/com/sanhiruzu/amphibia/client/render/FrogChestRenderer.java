package com.sanhiruzu.amphibia.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.sanhiruzu.amphibia.Amphibia;
import com.sanhiruzu.amphibia.block.FrogChestBlockEntity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;

public class FrogChestRenderer implements BlockEntityRenderer<FrogChestBlockEntity> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Amphibia.MOD_ID, "textures/entity/chest/frog_chest.png");

    private final ModelPart lid;
    private final ModelPart bottom;
    private final ModelPart lock;

    public FrogChestRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart model = context.bakeLayer(ModelLayers.CHEST);
        this.bottom = model.getChild("bottom");
        this.lid = model.getChild("lid");
        this.lock = model.getChild("lock");
    }

    @Override
    public void render(
            FrogChestBlockEntity blockEntity,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay
    ) {
        BlockState state = blockEntity.getBlockState();
        Direction facing = state.hasProperty(ChestBlock.FACING) ? state.getValue(ChestBlock.FACING) : Direction.SOUTH;
        float scale = Mth.clamp(blockEntity.getGenome().getScale(), 0.5F, 2.48F);

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.0F, 0.5F);
        poseStack.scale(scale, scale, scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
        poseStack.translate(-0.5F, 0.0F, -0.5F);

        float openness = 1.0F - blockEntity.getOpenNess(partialTick);
        openness = 1.0F - openness * openness * openness;
        lid.xRot = -(openness * (float) (Math.PI / 2));
        lock.xRot = lid.xRot;

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutout(TEXTURE));
        int color = blockEntity.getGenome().getColor();
        lid.render(poseStack, consumer, packedLight, packedOverlay, color);
        lock.render(poseStack, consumer, packedLight, packedOverlay, color);
        bottom.render(poseStack, consumer, packedLight, packedOverlay, color);
        poseStack.popPose();
    }
}
