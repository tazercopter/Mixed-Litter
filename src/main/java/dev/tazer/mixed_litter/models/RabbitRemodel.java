package dev.tazer.mixed_litter.models;

// Made with Blockbench 4.11.0
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.animal.Rabbit;

public class RabbitRemodel {
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(16, 0).addBox(-1.5F, -3, -4, 3, 3, 5), PartPose.offset(0, 17, -1.5F));

        PartDefinition right_front_leg = partdefinition.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-0.5F, 0, -0.5F, 1, 3, 1).mirror(false), PartPose.offset(-1, 21, -2));

        PartDefinition left_front_leg = partdefinition.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, 0, -0.5F, 1, 3, 1), PartPose.offset(1, 21, -2));

        PartDefinition right_hind_foot = partdefinition.addOrReplaceChild("right_hind_foot", CubeListBuilder.create().texOffs(10, 9).mirror().addBox(-1, 0, -3, 2, 1, 2).mirror(false), PartPose.offset(-2, 23.05F, 1));

        PartDefinition left_hind_foot = partdefinition.addOrReplaceChild("left_hind_foot", CubeListBuilder.create().texOffs(10, 9).addBox(-1, 0, -3, 2, 1, 2), PartPose.offset(2, 23.05F, 1));

        PartDefinition tail = partdefinition.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(19, 8).addBox(-1, -1, 0, 2, 2, 2), PartPose.offset(0, 20, 2.5F));

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -7, -2.5F, 3, 4, 5), PartPose.offset(0, 24, 0));

        PartDefinition left_haunch = partdefinition.addOrReplaceChild("left_haunch", CubeListBuilder.create().texOffs(0, 9).mirror().addBox(-0.5F, -1, 0, 2, 5, 3).mirror(false), PartPose.offset(1.5F, 20.05F, 0));

        PartDefinition right_haunch = partdefinition.addOrReplaceChild("right_haunch", CubeListBuilder.create().texOffs(0, 9).addBox(-1.5F, -1, 0, 2, 5, 3), PartPose.offset(-1.5F, 20.05F, 0));

        PartDefinition right_ear = partdefinition.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(24, 12).mirror().addBox(-1.5F, -6, 0, 2, 6, 1).mirror(false), PartPose.offset(-1, 14, -1.5F));

        PartDefinition left_ear = partdefinition.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(24, 12).addBox(-0.5F, -6, 0, 2, 6, 1), PartPose.offset(1, 14, -1.5F));

        PartDefinition nose = partdefinition.addOrReplaceChild("nose", CubeListBuilder.create(), PartPose.offset(0, 24, 0));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    public static void setupAnim(Rabbit rabbit, ModelPart root, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        ModelPart rightHaunch = root.getChild("right_haunch");
        ModelPart leftHaunch = root.getChild("left_haunch");

        ModelPart rightFoot = root.getChild("right_hind_foot");
        ModelPart leftFoot = root.getChild("left_hind_foot");

        rightHaunch.xRot += 0.36651916F;
        leftHaunch.xRot += 0.36651916F;

        rightFoot.xRot = rightHaunch.xRot;
        leftFoot.xRot = leftHaunch.xRot;
        rightFoot.yRot = rightHaunch.yRot;
        leftFoot.yRot = leftHaunch.yRot;
    }
}
