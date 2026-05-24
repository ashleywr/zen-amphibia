package com.sanhiruzu.amphibia.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.client.model.geom.ModelLayerLocation;
import com.sanhiruzu.amphibia.entity.CricketEntity;

public class CricketModel extends EntityModel<CricketEntity> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("zen_amphibia", "cricket"), "main");

	private final ModelPart root;
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart thorax;
	private final ModelPart abdomen1;
	private final ModelPart abdomen2;
	private final ModelPart leftEye1;
	private final ModelPart leftEye2;
	private final ModelPart leftAntenna1;
	private final ModelPart leftAntenna2;
	private final ModelPart frontLeftLeg1;
	private final ModelPart frontLeftLeg2;
	private final ModelPart frontLeftLeg3;
	private final ModelPart frontLeftLeg4;
	private final ModelPart midRightLeg1;
	private final ModelPart midRightLeg2;
	private final ModelPart backRightLeg1;
	private final ModelPart backRightLeg2;

	public CricketModel(ModelPart root) {
		this.root = root;
		this.body = root.getChild("body");
		this.head = this.body.getChild("head");
		this.thorax = this.body.getChild("thorax");
		this.abdomen1 = this.body.getChild("abdomen1");
		this.abdomen2 = this.body.getChild("abdomen2");
		this.leftEye1 = this.body.getChild("leftEye1");
		this.leftEye2 = this.body.getChild("leftEye2");
		this.leftAntenna1 = this.body.getChild("leftAntenna1");
		this.leftAntenna2 = this.body.getChild("leftAntenna2");
		this.frontLeftLeg1 = this.body.getChild("frontLeftLeg1");
		this.frontLeftLeg2 = this.body.getChild("frontLeftLeg2");
		this.frontLeftLeg3 = this.body.getChild("frontLeftLeg3");
		this.frontLeftLeg4 = this.body.getChild("frontLeftLeg4");
		this.midRightLeg1 = this.body.getChild("midRightLeg1");
		this.midRightLeg2 = this.body.getChild("midRightLeg2");
		this.backRightLeg1 = this.body.getChild("backRightLeg1");
		this.backRightLeg2 = this.body.getChild("backRightLeg2");
	}

	@Override
	public void setupAnim(CricketEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		head.yRot = netHeadYaw * Mth.PI / 180F;
		head.xRot = headPitch * Mth.PI / 180F;

		float legSwing = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		frontLeftLeg1.xRot = legSwing;
		frontLeftLeg2.xRot = -legSwing;
		frontLeftLeg3.xRot = -legSwing;
		frontLeftLeg4.xRot = legSwing;
		midRightLeg1.xRot = -legSwing;
		midRightLeg2.xRot = legSwing;
		backRightLeg1.xRot = legSwing;
		backRightLeg2.xRot = -legSwing;

		leftAntenna1.zRot = Mth.cos(ageInTicks * 0.09F) * 0.2F;
		leftAntenna2.zRot = Mth.cos(ageInTicks * 0.09F) * 0.2F;
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
		this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshDefinition = new MeshDefinition();
		PartDefinition partDefinition = meshDefinition.getRoot();

		PartDefinition body = partDefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0F, 0F, 0F));

		body.addOrReplaceChild("head", CubeListBuilder.create().addBox(-2, -2, -2, 4, 4, 4), PartPose.offset(0, 2, 0));

		body.addOrReplaceChild("thorax", CubeListBuilder.create().addBox(-2, -2, 2, 4, 4, 4), PartPose.offset(0, 1, 2));

		body.addOrReplaceChild("abdomen1", CubeListBuilder.create().addBox(-3, -2, 4, 6, 4, 5), PartPose.offset(0, 1, 4));

		body.addOrReplaceChild("abdomen2", CubeListBuilder.create().addBox(-2, -2, 9, 4, 4, 5), PartPose.offset(0, 1, 9));

		body.addOrReplaceChild("leftEye1", CubeListBuilder.create().addBox(-1, 0, -2, 1, 1, 1), PartPose.offset(-1, 3, 0));

		body.addOrReplaceChild("leftEye2", CubeListBuilder.create().addBox(0, 0, -2, 1, 1, 1), PartPose.offset(1, 3, 0));

		body.addOrReplaceChild("leftAntenna1", CubeListBuilder.create().addBox(-1, 0, -1, 1, 7, 1), PartPose.offsetAndRotation(-1, 3, 0, 0.6109F, 0, 0));

		body.addOrReplaceChild("leftAntenna2", CubeListBuilder.create().addBox(0, 0, -1, 1, 7, 1), PartPose.offsetAndRotation(1, 3, 0, 0.6109F, 0, 0));

		body.addOrReplaceChild("frontLeftLeg1", CubeListBuilder.create().addBox(2, -4, 1, 1, 5, 5), PartPose.offsetAndRotation(0, 1, 1, 1.5708F, 0, 0.6981F));

		body.addOrReplaceChild("frontLeftLeg2", CubeListBuilder.create().addBox(2, -4, -1, 1, 5, 5), PartPose.offsetAndRotation(0, 1, -1, 1.5708F, 0, 0.6981F));

		body.addOrReplaceChild("frontLeftLeg3", CubeListBuilder.create().addBox(-3, 1, 1, 1, 5, 5), PartPose.offsetAndRotation(0, 1, 1, 1.5708F, 0, -0.6981F));

		body.addOrReplaceChild("frontLeftLeg4", CubeListBuilder.create().addBox(-3, 1, -1, 1, 5, 5), PartPose.offsetAndRotation(0, 1, -1, 1.5708F, 0, -0.6981F));

		body.addOrReplaceChild("midRightLeg1", CubeListBuilder.create().addBox(2, 1, 2, 2, 2, 6), PartPose.offsetAndRotation(0, 1, 4, -0.7854F, 0, 0));

		body.addOrReplaceChild("midRightLeg2", CubeListBuilder.create().addBox(-4, 1, 2, 2, 2, 6), PartPose.offsetAndRotation(0, 1, 4, -0.7854F, 0, 0));

		body.addOrReplaceChild("backRightLeg1", CubeListBuilder.create().addBox(2, 1, 0, 1, 1, 5), PartPose.offsetAndRotation(2, 1, 8, 0.7854F, 0, 0));

		body.addOrReplaceChild("backRightLeg2", CubeListBuilder.create().addBox(-3, 1, 0, 1, 1, 5), PartPose.offsetAndRotation(-2, 1, 8, 0.7854F, 0, 0));

		return LayerDefinition.create(meshDefinition, 64, 64);
	}
}
