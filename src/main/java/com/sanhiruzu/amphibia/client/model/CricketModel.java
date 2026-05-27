package com.sanhiruzu.amphibia.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.geom.ModelLayerLocation;
import com.sanhiruzu.amphibia.entity.CricketEntity;

public class CricketModel extends EntityModel<CricketEntity> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("zen_amphibia", "cricket"), "main");

	private final ModelPart root;

	public CricketModel(ModelPart root) {
		this.root = root;
	}

	@Override
	public void setupAnim(CricketEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root.getAllParts().forEach(ModelPart::resetPose);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
		this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshDefinition = new MeshDefinition();
		PartDefinition partDefinition = meshDefinition.getRoot();

		partDefinition.addOrReplaceChild("Body", CubeListBuilder.create()
				.texOffs(15, 22).addBox(-2.0F, -6.0F, -8.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(16, 22).addBox(-2.0F, -5.0F, -4.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(16, 22).addBox(-3.0F, -5.0F, 0.0F, 6.0F, 4.0F, 5.0F, new CubeDeformation(0.0F))
				.texOffs(16, 22).addBox(-2.0F, -5.0F, 5.0F, 4.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition headDetails = partDefinition.addOrReplaceChild("HeadDetails", CubeListBuilder.create()
				.texOffs(20, 13).addBox(2.0F, -2.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(20, 13).addBox(-3.0F, -2.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 20.0F, -4.0F));

		PartDefinition leftAntenna = headDetails.addOrReplaceChild("LeftAntenna", CubeListBuilder.create(), PartPose.offset(-5.0F, 0.0F, -3.0F));
		leftAntenna.addOrReplaceChild("LeftAntenna_r1", CubeListBuilder.create()
				.texOffs(11, 28).addBox(2.0F, -7.0F, 2.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(11, 28).addBox(7.0F, -7.0F, 2.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.3491F, 0.0F, 0.0F));

		PartDefinition frontLegs2 = partDefinition.addOrReplaceChild("Front_Legs2", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 3.0F));
		frontLegs2.addOrReplaceChild("FrontLeftLeg_r1", CubeListBuilder.create()
				.texOffs(25, 23).addBox(-8.0F, -1.0F, -2.0F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(2.0F, 2.0F, -6.0F, -1.5708F, 0.0F, 0.6981F));
		frontLegs2.addOrReplaceChild("FrontLeftLeg_r2", CubeListBuilder.create()
				.texOffs(25, 23).addBox(-8.0F, -1.0F, -2.0F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(2.0F, 2.0F, -4.0F, -1.5708F, 0.0F, 0.6981F));

		PartDefinition frontLegs = partDefinition.addOrReplaceChild("FrontLegs", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 3.0F));
		frontLegs.addOrReplaceChild("FrontLeftLeg_r3", CubeListBuilder.create()
				.texOffs(27, 18).addBox(-1.0F, -1.0F, -2.0F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(4.0F, -3.0F, -6.0F, -1.5708F, 0.0F, -0.6981F));
		frontLegs.addOrReplaceChild("FrontLeftLeg_r4", CubeListBuilder.create()
				.texOffs(27, 18).addBox(-1.0F, -1.0F, -2.0F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(4.0F, -3.0F, -4.0F, -1.5708F, 0.0F, -0.6981F));

		partDefinition.addOrReplaceChild("Front_Legs3", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 3.0F));

		PartDefinition middleLegs = partDefinition.addOrReplaceChild("MiddleLegs", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 6.0F));
		middleLegs.addOrReplaceChild("MidRightLeg_r1", CubeListBuilder.create()
				.texOffs(14, 22).addBox(-5.0F, -1.0F, -3.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(14, 22).addBox(-13.0F, -1.0F, -3.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(8.0F, -3.0F, -1.0F, 0.7854F, 0.0F, 0.0F));

		PartDefinition backLegs = middleLegs.addOrReplaceChild("BackLegs", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 4.0F));
		backLegs.addOrReplaceChild("BackRightLeg_r1", CubeListBuilder.create()
				.texOffs(16, 23).addBox(0.0F, 1.0F, -1.0F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
				.texOffs(16, 23).addBox(-7.0F, 1.0F, -1.0F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(3.0F, -4.0F, -1.0F, -0.7854F, 0.0F, 0.0F));

		return LayerDefinition.create(meshDefinition, 64, 64);
	}
}
