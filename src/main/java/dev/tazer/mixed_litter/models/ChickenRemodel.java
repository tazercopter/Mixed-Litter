package dev.tazer.mixed_litter.models;

// Made with Blockbench 4.11.0
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.animal.Chicken;

public class ChickenRemodel {
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(0, 0, 0, 0, 0, 0), PartPose.offset(0, 0, 0));

        PartDefinition bodyAdult = body.addOrReplaceChild("body_adult", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -2.5F, -3.5F, 5.0F, 5.0F, 7.0F), PartPose.offset(0.0F, 18.5F, 0.0F));

        partdefinition.addOrReplaceChild("beak", CubeListBuilder.create().texOffs(0, 0).addBox(0, 0, 0, 0, 0, 0), PartPose.offset(0, 0, 0));
        partdefinition.addOrReplaceChild("red_thing", CubeListBuilder.create().texOffs(0, 0).addBox(0, 0, 0, 0, 0, 0), PartPose.offset(0, 0, 0));

        partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(17, 0).addBox(-1.5F, -5.0F, -2.0F, 3.0F, 4.0F, 3.0F)
                .texOffs(0, 0).addBox(-0.5F, -4.0F, -4.0F, 1.0F, 2.0F, 2.0F)
                .texOffs(35, 7).addBox(0.0F, -7.0F, -3.0F, 0.0F, 7.0F, 3.0F), PartPose.offset(0.0F, 18, -2.5F));

        partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(38, 0).addBox(-0.5F, 0.0F, -2.0F, 2.0F, 3.0F, 2.0F), PartPose.offset(1.0F, 21.0F, 0.5F));

        partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(38, 0).mirror().addBox(-1.5F, 0.0F, -2.0F, 2.0F, 3.0F, 2.0F).mirror(false), PartPose.offset(-1.0F, 21.0F, 0.5F));

        partdefinition.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(30, 0).addBox(0.0F, 0.0F, -3.0F, 1.0F, 4.0F, 6.0F), PartPose.offset(2.5F, 16.0F, -0.5F));

        partdefinition.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(30, 0).mirror().addBox(-1.0F, 0.0F, -3.0F, 1.0F, 4.0F, 6.0F).mirror(false), PartPose.offset(-2.5F, 16.0F, -0.5F));

        bodyAdult.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(19, 8).addBox(-1.5F, -3.0F, -1.0F, 3.0F, 4.0F, 5.0F), PartPose.offset(0.0F, -1.5F, 3.5F));

        PartDefinition bodyBaby = body.addOrReplaceChild("body_baby", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -1.5F, -1.0F, 4.0F, 3.0F, 4.0F), PartPose.offset(0.0F, 16.9F, -0.5F));

        bodyBaby.addOrReplaceChild("head_baby", CubeListBuilder.create().texOffs(0, 7).addBox(-1.5F, -2.0F, -3.0F, 3.0F, 3.0F, 3.0F)
                .texOffs(12, 0).addBox(-0.5F, -1.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0)), PartPose.offset(0.0F, -0.5F, 0.0F));

        bodyBaby.addOrReplaceChild("right_leg_baby", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 2.0F, 1.0F), PartPose.offset(-1.0F, 1.5F, 1.0F));

        bodyBaby.addOrReplaceChild("left_leg_baby", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 2.0F, 1.0F), PartPose.offset(1.0F, 1.5F, 1.0F));


        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    public static void setupAnim(AgeableMob chicken, ModelPart root, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        ModelPart head = root.getChild("head");
        ModelPart body = root.getChild("body");
        ModelPart bodyAdult = body.getChild("body_adult");
        ModelPart rightWing = root.getChild("right_wing");
        ModelPart leftWing = root.getChild("left_wing");
        ModelPart rightLeg = root.getChild("right_leg");
        ModelPart leftLeg = root.getChild("left_leg");
        ModelPart bodyBaby = body.getChild("body_baby");
        ModelPart headBaby = bodyBaby.getChild("head_baby");
        ModelPart rightLegBaby = bodyBaby.getChild("right_leg_baby");
        ModelPart leftLegBaby = bodyBaby.getChild("left_leg_baby");
        boolean baby = chicken.isBaby();

        bodyBaby.xScale = 2;
        bodyBaby.yScale = 2;
        bodyBaby.zScale = 2;

        head.xRot = headPitch * 0.017453292F;
        head.yRot = netHeadYaw * 0.017453292F;
        headBaby.xRot = head.xRot / 2;
        headBaby.yRot = head.yRot / 2;

//        rightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
//        leftLeg.xRot = Mth.cos(limbSwing * 0.6662F + 3.1415927F) * 1.4F * limbSwingAmount;

        rightWing.zRot = ageInTicks;
        leftWing.zRot = -ageInTicks;

        bodyAdult.visible = !baby;
        head.visible = !baby;
        leftLeg.visible = !baby;
        rightLeg.visible = !baby;
        leftWing.visible = !baby;
        rightWing.visible = !baby;

        bodyBaby.visible = baby;

        rightLegBaby.xRot = rightLeg.xRot;
        leftLegBaby.xRot = leftLeg.xRot;
    }
}
