package dev.tazer.mixed_litter.models;

import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.animal.Sheep;

public class SheepRemodel<T extends Sheep> extends QuadrupedModel<T> {
    private float headXRot;
    private final ModelPart bodyAdult;
    private final ModelPart bodyWool;
    private final ModelPart rightFrontLegWool;
    private final ModelPart leftFrontLegWool;
    private final ModelPart rightHindLegWool;
    private final ModelPart leftHindLegWool;
    private final ModelPart headBaby;
    private final ModelPart bodyBaby;
    private final ModelPart rightFrontLegBaby;
    private final ModelPart leftFrontLegBaby;
    private final ModelPart rightHindLegBaby;
    private final ModelPart leftHindLegBaby;
    private final ModelPart shearedTail;

    public SheepRemodel(ModelPart root) {
        super(root, false, 8.0F, 4.0F, 2.0F, 2.0F, 24);

        bodyAdult = body.getChild("body_adult");
        bodyWool = bodyAdult.getChild("body_wool");
        rightHindLegWool = rightHindLeg.getChild("right_hind_leg_wool");
        leftHindLegWool = leftHindLeg.getChild("left_hind_leg_wool");
        rightFrontLegWool = rightFrontLeg.getChild("right_front_leg_wool");
        leftFrontLegWool = leftFrontLeg.getChild("left_front_leg_wool");
        headBaby = body.getChild("head_baby");
        bodyBaby = body.getChild("body_baby");
        rightFrontLegBaby = bodyBaby.getChild("right_front_leg_baby");
        leftFrontLegBaby = bodyBaby.getChild("left_front_leg_baby");
        rightHindLegBaby = bodyBaby.getChild("right_hind_leg_baby");
        leftHindLegBaby = bodyBaby.getChild("left_hind_leg_baby");
        shearedTail = bodyAdult.getChild("sheared_tail");
    }

    public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition body_adult = body.addOrReplaceChild("body_adult", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -7.0F, 8.0F, 8.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, -1.0F));

		PartDefinition sheared_tail = body_adult.addOrReplaceChild("sheared_tail", CubeListBuilder.create().texOffs(0, 3).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.0F, 7.0F));

		PartDefinition body_wool = body_adult.addOrReplaceChild("body_wool", CubeListBuilder.create().texOffs(45, 1).addBox(-4.5F, -4.5F, -7.0F, 9.0F, 9.0F, 15.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition tail_wool = body_wool.addOrReplaceChild("tail_wool", CubeListBuilder.create().texOffs(52, 9).addBox(-1.0F, -1.5F, 0.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.2F)), PartPose.offset(0.0F, -2.3F, 8.3F));

		PartDefinition body_baby = body.addOrReplaceChild("body_baby", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -2.5F, -4.0F, 5.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -13, 0.0F));

		PartDefinition tail_baby = body_baby.addOrReplaceChild("tail_baby", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.5F, 4.0F));

		PartDefinition right_front_leg_baby = body_baby.addOrReplaceChild("right_front_leg_baby", CubeListBuilder.create().texOffs(14, 13).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-1.5F, 2.5F, -3.0F));

		PartDefinition left_front_leg_baby = body_baby.addOrReplaceChild("left_front_leg_baby", CubeListBuilder.create().texOffs(14, 13).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, 2.5F, -3.0F));

		PartDefinition right_hind_leg_baby = body_baby.addOrReplaceChild("right_hind_leg_baby", CubeListBuilder.create().texOffs(22, 13).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-1.5F, 2.5F, 3.0F));

		PartDefinition left_hind_leg_baby = body_baby.addOrReplaceChild("left_hind_leg_baby", CubeListBuilder.create().texOffs(22, 13).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, 2.5F, 3.0F));

		PartDefinition head_baby = body.addOrReplaceChild("head_baby", CubeListBuilder.create().texOffs(0, 13).addBox(-1.5F, -1.0778F, -3.0015F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -16F, -5.5F, -0.6981F, 0.0F, 0.0F));

		PartDefinition right_ear_baby = head_baby.addOrReplaceChild("right_ear_baby", CubeListBuilder.create().texOffs(0, 4).mirror().addBox(-1.5F, -1.0F, -0.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-2.0F, 0.0829F, -1.693F, 0.6981F, 0.0F, 0.0F));

		PartDefinition left_ear_baby = head_baby.addOrReplaceChild("left_ear_baby", CubeListBuilder.create().texOffs(0, 4).addBox(-0.75F, -1.0F, -0.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 0.0829F, -1.693F, 0.6981F, 0.0F, 0.0F));

		PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(94, 0).addBox(-2.0F, -3.0F, -1.0F, 4.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 22).addBox(-3.0F, -3.0F, -5.0F, 6.0F, 7.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 33).addBox(-3.0F, -4.0F, -5.0F, 6.0F, 8.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(0.0F, 11.0F, -8.0F, -0.2738F, 0.0F, 0.0F));

		PartDefinition right_ear = head.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -1.0F, 0.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, -0.9F, -3.0F));

		PartDefinition left_ear = head.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(0.0F, -1.0F, -0.5F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(3.0F, -0.9F, -2.5F));

		PartDefinition right_front_leg = partdefinition.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(30, 0).mirror().addBox(-1.5F, 0.0F, -1.5F, 3.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-2.4F, 16.0F, -5.5F));

		PartDefinition right_front_leg_wool = right_front_leg.addOrReplaceChild("right_front_leg_wool", CubeListBuilder.create().texOffs(78, 0).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition left_front_leg = partdefinition.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(42, 0).mirror().addBox(-1.5F, 0.0F, -1.5F, 3.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(2.4F, 16.0F, -5.5F));

		PartDefinition left_front_leg_wool = left_front_leg.addOrReplaceChild("left_front_leg_wool", CubeListBuilder.create().texOffs(78, 8).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition right_hind_leg = partdefinition.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(24, 22).mirror().addBox(-1.5F, -2.0F, -1.5F, 3.0F, 10.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-2.4F, 16.0F, 5.5F));

		PartDefinition right_hind_leg_wool = right_hind_leg.addOrReplaceChild("right_hind_leg_wool", CubeListBuilder.create().texOffs(48, 25).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition left_hind_leg = partdefinition.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(36, 22).mirror().addBox(-1.5F, -2.0F, -1.5F, 3.0F, 10.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(2.4F, 16.0F, 5.5F));

		PartDefinition left_hind_leg_wool = left_hind_leg.addOrReplaceChild("left_hind_leg_wool", CubeListBuilder.create().texOffs(64, 25).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 64);
    }

    public void prepareMobModel(T sheep, float limbSwing, float limbSwingAmount, float partialTick) {
        super.prepareMobModel(sheep, limbSwing, limbSwingAmount, partialTick);
        head.y = 11 + sheep.getHeadEatPositionScale(partialTick) * 7.0F;
        headXRot = sheep.getHeadEatAngleScale(partialTick);
    }

    public void setupAnim(T sheep, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(sheep, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        this.head.xRot = this.headXRot;

        boolean baby = sheep.isBaby();
        boolean sheared = sheep.isSheared();

        head.xRot = headXRot - 0.1569F;

        headBaby.xScale = 2;
        headBaby.yScale = 2;
        headBaby.zScale = 2;
        bodyBaby.xScale = 2;
        bodyBaby.yScale = 2;
        bodyBaby.zScale = 2;

        headBaby.xRot = headXRot - 0.5412F;
        headBaby.y = head.y - 27;
        headBaby.yRot = head.yRot / 2;

        bodyBaby.visible = baby;
        headBaby.visible = baby;

        bodyAdult.visible = !baby;
        head.visible = !baby;
        leftHindLeg.visible = !baby;
        rightHindLeg.visible = !baby;
        leftFrontLeg.visible = !baby;
        rightFrontLeg.visible = !baby;

        shearedTail.visible = sheared;

        leftHindLegWool.visible = !sheared;
        rightHindLegWool.visible = !sheared;
        leftFrontLegWool.visible = !sheared;
        rightFrontLegWool.visible = !sheared;
        bodyWool.visible = !sheared;

        rightHindLegBaby.xRot = rightHindLeg.xRot;
        leftHindLegBaby.xRot = leftHindLeg.xRot;
        rightFrontLegBaby.xRot = rightFrontLeg.xRot;
        leftFrontLegBaby.xRot = leftFrontLeg.xRot;
    }
}

