package dev.tazer.mixed_litter.client.models;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Rabbit;

public class RabbitRemodel<T extends Rabbit> extends EntityModel<T> {
    private final ModelPart leftRearFoot;
    private final ModelPart rightRearFoot;
    private final ModelPart leftHaunch;
    private final ModelPart rightHaunch;
    private final ModelPart body;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart head;
    private final ModelPart rightEar;
    private final ModelPart leftEar;
    private final ModelPart tail;
    private final ModelPart nose;
    private float jumpRotation;
    public RabbitRemodel(ModelPart root) {
        leftRearFoot = root.getChild("left_hind_foot");
        rightRearFoot = root.getChild("right_hind_foot");
        leftHaunch = root.getChild("left_haunch");
        rightHaunch = root.getChild("right_haunch");
        body = root.getChild("body");
        leftFrontLeg = root.getChild("left_front_leg");
        rightFrontLeg = root.getChild("right_front_leg");
        head = root.getChild("head");
        rightEar = root.getChild("right_ear");
        leftEar = root.getChild("left_ear");
        tail = root.getChild("tail");
        nose = root.getChild("nose");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(16, 0).addBox(-1.5F, -3, -4, 3, 3, 5), PartPose.offset(0, 17, -1.5F));

        PartDefinition right_front_leg = partdefinition.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-0.5F, 0, -0.5F, 1, 3, 1).mirror(false), PartPose.offset(-1, 21, -2));

        PartDefinition left_front_leg = partdefinition.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, 0, -0.5F, 1, 3, 1), PartPose.offset(1, 21, -2));

        PartDefinition tail = partdefinition.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(19, 8).addBox(-1, -1, 0, 2, 2, 2, new CubeDeformation(0.01F)), PartPose.offset(0, 20, 2.5F));

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -7, -2.5F, 3, 4, 5), PartPose.offset(0, 24, 0));

        PartDefinition right_haunch = partdefinition.addOrReplaceChild("right_haunch", CubeListBuilder.create().texOffs(0, 9).addBox(-1.5F, -1, 0, 2, 5, 3), PartPose.offsetAndRotation(-1.5F, 20F, 1, 0, 0.3491F, 0));

        PartDefinition right_foot = right_haunch.addOrReplaceChild("right_foot", CubeListBuilder.create().texOffs(10, 9).mirror().addBox(-1, 0, -2, 2, 1, 2).mirror(false), PartPose.offset(-0.5F, 3, 0));

        PartDefinition left_haunch = partdefinition.addOrReplaceChild("left_haunch", CubeListBuilder.create().texOffs(0, 9).mirror().addBox(-0.5F, -1, 0, 2, 5, 3).mirror(false), PartPose.offsetAndRotation(1.5F, 20F, 1, 0, -0.3491F, 0));

        PartDefinition left_foot = left_haunch.addOrReplaceChild("left_foot", CubeListBuilder.create().texOffs(10, 9).addBox(-1, 0, -2, 2, 1, 2), PartPose.offset(0.5F, 3, 0));

        PartDefinition right_ear = partdefinition.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(24, 12).mirror().addBox(-1.5F, -6, 0, 2, 6, 1).mirror(false), PartPose.offset(-1, 14, -1.5F));

        PartDefinition left_ear = partdefinition.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(24, 12).addBox(-0.5F, -6, 0, 2, 6, 1), PartPose.offset(1, 14, -1.5F));

        PartDefinition nose = partdefinition.addOrReplaceChild("nose", CubeListBuilder.create(), PartPose.offset(0, 24, 0));

        PartDefinition left_hind_foot = partdefinition.addOrReplaceChild("left_hind_foot", CubeListBuilder.create().texOffs(0, 0).addBox(0, 0, 0, 0, 0, 0), PartPose.offset(0, 0, 0));
        PartDefinition right_hind_foot = partdefinition.addOrReplaceChild("right_hind_foot", CubeListBuilder.create().texOffs(0, 0).addBox(0, 0, 0, 0, 0, 0), PartPose.offset(0, 0, 0));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(T rabbit, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float f = ageInTicks - rabbit.tickCount;
        this.nose.xRot = headPitch * 0.017453292F;
        this.head.xRot = headPitch * 0.017453292F;
        this.rightEar.xRot = headPitch * 0.017453292F;
        this.leftEar.xRot = headPitch * 0.017453292F;
        this.nose.yRot = netHeadYaw * 0.017453292F;
        this.head.yRot = netHeadYaw * 0.017453292F;
        this.rightEar.yRot = this.nose.yRot - 0.2617994F;
        this.leftEar.yRot = this.nose.yRot + 0.2617994F;
        this.jumpRotation = Mth.sin(rabbit.getJumpCompletion(f) * 3.1415927F);
        this.leftHaunch.xRot = (this.jumpRotation * 50.0F - 21.0F) * 0.017453292F;
        this.rightHaunch.xRot = (this.jumpRotation * 50.0F - 21.0F) * 0.017453292F;
        this.leftRearFoot.xRot = this.jumpRotation * 50.0F * 0.017453292F;
        this.rightRearFoot.xRot = this.jumpRotation * 50.0F * 0.017453292F;
        this.leftFrontLeg.xRot = (this.jumpRotation * -40.0F - 11.0F) * 0.017453292F;
        this.rightFrontLeg.xRot = (this.jumpRotation * -40.0F - 11.0F) * 0.017453292F;
        rightHaunch.xRot += 0.36651916F;
        leftHaunch.xRot += 0.36651916F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        if (young) {
            poseStack.pushPose();
            poseStack.scale(0.5F, 0.5F, 0.5F);
            poseStack.translate(0, 1.490625, 0);
            ImmutableList.of(leftHaunch, rightHaunch, body, leftFrontLeg, rightFrontLeg, tail, head, leftEar, rightEar, nose).forEach((p_349849_) -> {
                p_349849_.render(poseStack, buffer, packedLight, packedOverlay, color);
            });
            poseStack.popPose();
        } else {
            poseStack.pushPose();
            poseStack.scale(0.75F, 0.75F, 0.75F);
            poseStack.translate(0, 0.5, 0);
            ImmutableList.of(leftRearFoot, rightRearFoot, leftHaunch, rightHaunch, body, leftFrontLeg, rightFrontLeg, head, rightEar, leftEar, tail, nose, new ModelPart[0]).forEach((p_349861_) -> {
                p_349861_.render(poseStack, buffer, packedLight, packedOverlay, color);
            });
            poseStack.popPose();
        }
    }

    @Override
    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
        this.jumpRotation = Mth.sin(entity.getJumpCompletion(partialTick) * 3.1415927F);
    }
}
