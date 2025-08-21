package dev.tazer.mixed_litter.models;

import com.google.common.collect.ImmutableList;
import dev.tazer.mixed_litter.MixedLitter;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Chicken;

public class ChickenRemodel<T extends AgeableMob> extends AgeableListModel<T> {

    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart bodyAdult;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart bodyBaby;
    private final ModelPart headBaby;
    private final ModelPart rightLegBaby;
    private final ModelPart leftLegBaby;

    public ChickenRemodel(ModelPart root) {
        head = root.getChild("head");
        body = root.getChild("body");
        bodyAdult = body.getChild("body_adult");
        rightWing = root.getChild("right_wing");
        leftWing = root.getChild("left_wing");
        rightLeg = root.getChild("right_leg");
        leftLeg = root.getChild("left_leg");
        bodyBaby = body.getChild("body_baby");
        headBaby = bodyBaby.getChild("head_baby");
        rightLegBaby = bodyBaby.getChild("right_leg_baby");
        leftLegBaby = bodyBaby.getChild("left_leg_baby");
    }

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

    public void setupAnim(T chicken, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
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

    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of(head);
    }

    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(body, rightLeg, leftLeg, rightWing, leftWing);
    }
}
