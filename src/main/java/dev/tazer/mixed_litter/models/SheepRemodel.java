package dev.tazer.mixed_litter.models;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.animal.Sheep;

public class SheepRemodel {
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

    public static void setupAnim(Sheep sheep, ModelPart root, float ageInTicks, float headXRot) {
        ModelPart head = root.getChild("head");
        ModelPart body = root.getChild("body");
        ModelPart bodyAdult = body.getChild("body_adult");
        ModelPart bodyWool = bodyAdult.getChild("body_wool");
        ModelPart rightHindLeg = root.getChild("right_hind_leg");
        ModelPart leftHindLeg = root.getChild("left_hind_leg");
        ModelPart rightFrontLeg = root.getChild("right_front_leg");
        ModelPart leftFrontLeg = root.getChild("left_front_leg");
        ModelPart rightHindLegWool = rightHindLeg.getChild("right_hind_leg_wool");
        ModelPart leftHindLegWool = leftHindLeg.getChild("left_hind_leg_wool");
        ModelPart rightFrontLegWool = rightFrontLeg.getChild("right_front_leg_wool");
        ModelPart leftFrontLegWool = leftFrontLeg.getChild("left_front_leg_wool");
        ModelPart headBaby = body.getChild("head_baby");
        ModelPart bodyBaby = body.getChild("body_baby");
        ModelPart rightFrontLegBaby = bodyBaby.getChild("right_front_leg_baby");
        ModelPart leftFrontLegBaby = bodyBaby.getChild("left_front_leg_baby");
        ModelPart rightHindLegBaby = bodyBaby.getChild("right_hind_leg_baby");
        ModelPart leftHindLegBaby = bodyBaby.getChild("left_hind_leg_baby");
        ModelPart shearedTail = bodyAdult.getChild("sheared_tail");
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

