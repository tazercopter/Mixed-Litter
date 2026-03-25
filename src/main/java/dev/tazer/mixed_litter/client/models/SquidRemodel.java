package dev.tazer.mixed_litter.client.models;

import net.minecraft.client.model.SquidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.Entity;

public class SquidRemodel<T extends Entity> extends SquidModel<T> {
    private final ModelPart[] tentacles = new ModelPart[8];

    public SquidRemodel(ModelPart root) {
        super(root);
        for (int i = 0; i < tentacles.length; i++) {
            tentacles[i] = root.getChild("tentacle" + i);
        }
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-6, -8, -6, 12, 16, 12), PartPose.offset(0, 4, 0));

        body.addOrReplaceChild("crown", CubeListBuilder.create().texOffs(0, 28).addBox(-8, -8, -1, 16, 16, 2), PartPose.offsetAndRotation(0, -7, 0, 0, 0, 0.7854F));

        partdefinition.addOrReplaceChild("tentacle0", CubeListBuilder.create().texOffs(48, 0).addBox(-1, 0, -1, 2, 18, 2)
                .texOffs(0, 0).addBox(-2, 18, -1, 4, 5, 2), PartPose.offsetAndRotation(5, 11, 0, 0, 1.5708F, 0));

        partdefinition.addOrReplaceChild("tentacle1", CubeListBuilder.create().texOffs(48, 0).addBox(-1, 0, -1, 2, 18, 2), PartPose.offsetAndRotation(3.5F, 11, 3.5F, 0, 0.7854F, 0));

        partdefinition.addOrReplaceChild("tentacle2", CubeListBuilder.create().texOffs(48, 0).addBox(-1, 0, -1, 2, 18, 2), PartPose.offset(0, 11, 5));

        partdefinition.addOrReplaceChild("tentacle3", CubeListBuilder.create().texOffs(48, 0).addBox(-1, 0, -1, 2, 18, 2), PartPose.offsetAndRotation(-3.5F, 11, 3.5F, 0, -0.7854F, 0));

        partdefinition.addOrReplaceChild("tentacle4", CubeListBuilder.create().texOffs(48, 0).addBox(-1, 0, -1, 2, 18, 2)
                .texOffs(0, 0).addBox(-2, 18, -1, 4, 5, 2), PartPose.offsetAndRotation(-5, 11, 0, 0, -1.5708F, 0));

        partdefinition.addOrReplaceChild("tentacle5", CubeListBuilder.create().texOffs(48, 0).addBox(-1, 0, -1, 2, 18, 2), PartPose.offsetAndRotation(-3.5F, 11, -3.5F, 0, -2.3562F, 0));

        partdefinition.addOrReplaceChild("tentacle6", CubeListBuilder.create().texOffs(48, 0).addBox(-1, 0, -1, 2, 18, 2), PartPose.offsetAndRotation(0, 11, -5, 0, -3.1416F, 0));

        partdefinition.addOrReplaceChild("tentacle7", CubeListBuilder.create().texOffs(48, 0).addBox(-1, 0, -1, 2, 18, 2), PartPose.offsetAndRotation(3.5F, 11, -3.5F, 0, -3.927F, 0));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(T squid, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        for (ModelPart tentacle : tentacles) {
            tentacle.xRot = ageInTicks;
        }
    }

}
