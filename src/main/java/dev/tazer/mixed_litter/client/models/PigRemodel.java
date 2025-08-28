package dev.tazer.mixed_litter.client.models;

import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AgeableMob;

public class PigRemodel<T extends AgeableMob> extends QuadrupedModel<T> {

    private final ModelPart headBaby;
    private final ModelPart rightFrontLegBaby;
    private final ModelPart leftFrontLegBaby;
    private final ModelPart rightHindLegBaby;
    private final ModelPart leftHindLegBaby;
    private final ModelPart rightEar;
    private final ModelPart leftEar;

    public PigRemodel(ModelPart root) {
        super(root, false, 4.0F, 4.0F, 2.0F, 2.0F, 24);
        headBaby = body.getChild("head_baby");
        rightFrontLegBaby = headBaby.getChild("right_front_leg_baby");
        leftFrontLegBaby = headBaby.getChild("left_front_leg_baby");
        rightHindLegBaby = headBaby.getChild("right_hind_leg_baby");
        leftHindLegBaby = headBaby.getChild("left_hind_leg_baby");
        rightEar = head.getChild("right_ear");
        leftEar = head.getChild("left_ear");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(0, 0, 0, 0, 0, 0), PartPose.offset(0, 0, 0));

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-5, -5, -8, 10, 10, 16)
                .texOffs(0, 26).addBox(-5, -5, -9, 10, 5, 1)
                .texOffs(22, 26).mirror().addBox(-3, 2, -11, 2, 2, 3).mirror(false)
                .texOffs(32, 26).mirror().addBox(1, 2, -11, 2, 2, 3).mirror(false), PartPose.offset(0, 16, 0));


        head.addOrReplaceChild("snout", CubeListBuilder.create().texOffs(0, 6).addBox(-2, -1.5F, -2, 4, 3, 2), PartPose.offset(0, 1.5F, -8));

        head.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(36, 0).mirror().addBox(-1, 0, -2, 1, 4, 4).mirror(false), PartPose.offsetAndRotation(-5, -4, -6, 0, 0, 0.2443F));

        head.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(36, 0).addBox(0, 0, -2, 1, 4, 4), PartPose.offsetAndRotation(5, -4, -6, 0, 0, -0.2443F));

        head.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 7).addBox(0, -4.5F, 0, 0, 5, 4), PartPose.offset(0, -1.5F, 8));

        partdefinition.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(36, 8).addBox(-1.5F, -2, -1.5F, 3, 5, 3), PartPose.offset(-3.4F, 21, -4.5F));

        partdefinition.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(36, 8).mirror().addBox(-1.5F, -2, -1.5F, 3, 5, 3).mirror(false), PartPose.offset(3.4F, 21, -4.5F));

        partdefinition.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, 0, -1.5F, 3, 3, 3).mirror(false), PartPose.offset(-3.4F, 21, 6.5F));

        partdefinition.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-1.5F, 0, -1.5F, 3, 3, 3).mirror(false), PartPose.offset(3.4F, 21, 6.5F));

        // Baby
        PartDefinition headBaby = body.addOrReplaceChild("head_baby", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -2.5F, -4, 5, 5, 8), PartPose.offset(0, 17F, 0));

        headBaby.addOrReplaceChild("snout_baby", CubeListBuilder.create().texOffs(18, 0).addBox(-1.5F, -1, -1, 3, 2, 1), PartPose.offset(0, 1.5F, -4));

        headBaby.addOrReplaceChild("right_ear_baby", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-1, 0, -1.5F, 1, 3, 3).mirror(false), PartPose.offset(-2.5F, -2.5F, -2.5F));

        headBaby.addOrReplaceChild("left_ear_baby", CubeListBuilder.create().texOffs(0, 0).addBox(0, 0, -1.5F, 1, 3, 3), PartPose.offset(2.5F, -2.5F, -2.5F));

        headBaby.addOrReplaceChild("tail_baby", CubeListBuilder.create().texOffs(18, 0).addBox(0, -2.5F, 0, 0, 3, 3), PartPose.offset(0, 0, 4));

        headBaby.addOrReplaceChild("right_front_leg_baby", CubeListBuilder.create().texOffs(0, 13).mirror().addBox(-1, 0, -1, 2, 1, 2).mirror(false), PartPose.offset(-1.5F, 2.5F, -2));

        headBaby.addOrReplaceChild("left_front_leg_baby", CubeListBuilder.create().texOffs(0, 13).addBox(-1, 0, -1, 2, 1, 2), PartPose.offset(1.5F, 2.5F, -2));

        headBaby.addOrReplaceChild("right_hind_leg_baby", CubeListBuilder.create().texOffs(8, 13).mirror().addBox(-1, 0, -1, 2, 1, 2).mirror(false), PartPose.offset(-1.5F, 2.5F, 3));

        headBaby.addOrReplaceChild("left_hind_leg_baby", CubeListBuilder.create().texOffs(8, 13).addBox(-1, 0, -1, 2, 1, 2), PartPose.offset(1.5F, 2.5F, 3));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(T pig, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        boolean baby = pig.isBaby();

        headBaby.xScale = 2;
        headBaby.yScale = 2;
        headBaby.zScale = 2;

        head.xRot = headPitch * 0.017453292F / 2;
        head.yRot = netHeadYaw * 0.017453292F / 4;
        headBaby.xRot = head.xRot / 2;
        headBaby.yRot = head.yRot / 2;

        rightHindLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.7F * limbSwingAmount;
        leftHindLeg.xRot = Mth.cos(limbSwing * 0.6662F + 3.1415927F) * 1.7F * limbSwingAmount;
        rightFrontLeg.xRot = Mth.cos(limbSwing * 0.6662F + 3.1415927F) * 1.7F * limbSwingAmount;
        leftFrontLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.7F * limbSwingAmount;

        head.visible = !baby;
        leftHindLeg.visible = !baby;
        rightHindLeg.visible = !baby;
        leftFrontLeg.visible = !baby;
        rightFrontLeg.visible = !baby;
        headBaby.visible = baby;

        if (!pig.onGround())
            rightEar.zRot = (float) Mth.lerp(0.5, rightEar.zRot,pig.getDeltaMovement().length());
        else if (limbSwing > 0)
            rightEar.zRot = (float) Mth.lerp(0.5, rightEar.zRot,Mth.abs(Mth.cos(limbSwing * 0.6662F + 3.1415927F)) * limbSwingAmount / 2);
        leftEar.zRot = -rightEar.zRot;

        rightHindLegBaby.xRot = rightHindLeg.xRot;
        leftHindLegBaby.xRot = leftHindLeg.xRot;
        rightFrontLegBaby.xRot = rightFrontLeg.xRot;
        leftFrontLegBaby.xRot = leftFrontLeg.xRot;
    }
}
