package dev.tazer.mixed_litter.client.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.tazer.mixed_litter.VariantUtil;
import dev.tazer.mixed_litter.actions.SetSheepFurLayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.SheepModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.SheepFurLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;

public class SheepRemodelFurLayer extends SheepFurLayer {

    public SheepRemodelFurLayer(RenderLayerParent<Sheep, SheepModel<Sheep>> renderer, EntityModelSet models) {
        super(renderer, models);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, Sheep sheep, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        SetSheepFurLayer furData = VariantUtil.findAction(sheep, SetSheepFurLayer.class);
        ResourceLocation furTexture = null;
        if (furData != null) {
            furTexture = sheep.isBaby() ? furData.getBabyTexture() : sheep.isSheared() ? furData.getShearedTexture() : furData.getTexture();
        }

        if (furTexture == null) {
            super.render(poseStack, buffer, packedLight, sheep, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
            return;
        }

        EntityModel<Sheep> model = this.getParentModel();
        if (sheep.isInvisible()) {
            if (Minecraft.getInstance().shouldEntityAppearGlowing(sheep)) {
                VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.outline(furTexture));
                model.renderToBuffer(poseStack, vertexconsumer, packedLight, LivingEntityRenderer.getOverlayCoords(sheep, 0.0F), -16777216);
            } else {
                super.render(poseStack, buffer, packedLight, sheep, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
            }
            return;
        }

        int color;
        if (sheep.hasCustomName() && sheep.getName().getString().equals("jeb_")) {
            int k = sheep.tickCount / 25 + sheep.getId();
            int l = DyeColor.values().length;
            int i1 = k % l;
            int j1 = (k + 1) % l;
            float f = ((float) (sheep.tickCount % 25) + partialTicks) / 25.0F;
            int k1 = Sheep.getColor(DyeColor.byId(i1));
            int l1 = Sheep.getColor(DyeColor.byId(j1));
            color = FastColor.ARGB32.lerp(f, k1, l1);
        } else {
            color = Sheep.getColor(sheep.getColor());
        }

        VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(furTexture));
        model.renderToBuffer(poseStack, vertexconsumer, packedLight, LivingEntityRenderer.getOverlayCoords(sheep, 0.0F), color);
    }
}
