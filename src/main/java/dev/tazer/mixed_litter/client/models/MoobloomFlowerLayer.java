package dev.tazer.mixed_litter.client.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.teamabnormals.buzzier_bees.common.entity.animal.Moobloom;
import dev.tazer.mixed_litter.client.ModelLayers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.level.block.state.BlockState;

public class MoobloomFlowerLayer <T extends Moobloom> extends RenderLayer<T, CowModel<T>> {
    private CowRemodel<Moobloom> cowRemodel;

    public MoobloomFlowerLayer(RenderLayerParent<T, CowModel<T>> renderer, EntityModelSet modelSet) {
        super(renderer);
        cowRemodel = new CowRemodel<>(modelSet.bakeLayer(ModelLayers.COW_LAYER));
    }

    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLightIn, T moobloom, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!moobloom.isBaby() && !moobloom.isInvisible()) {
            BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
            BlockState blockstate = moobloom.getFlower().defaultBlockState();
            int i = LivingEntityRenderer.getOverlayCoords(moobloom, 0);
            poseStack.pushPose();
            poseStack.translate(0.2F, -0.4F, 0.5F);
            poseStack.mulPose(Axis.YP.rotationDegrees(-48));
            poseStack.scale(-1, -1, 1);
            poseStack.translate(-0.5F, -0.5F, -0.5F);
            dispatcher.renderSingleBlock(blockstate, poseStack, buffer, packedLightIn, i);
            poseStack.popPose();
            poseStack.pushPose();
            poseStack.translate(0.2F, -0.4F, 0.5F);
            poseStack.mulPose(Axis.YP.rotationDegrees(42));
            poseStack.translate(0.1F, 0, -0.6F);
            poseStack.mulPose(Axis.YP.rotationDegrees(-48));
            poseStack.scale(-1, -1, 1);
            poseStack.translate(-0.5F, -0.5F, -0.5F);
            dispatcher.renderSingleBlock(blockstate, poseStack, buffer, packedLightIn, i);
            poseStack.popPose();
            poseStack.pushPose();
            cowRemodel.getHead().translateAndRotate(poseStack);
            poseStack.translate(0, -0.7F, -0.2F);
            poseStack.mulPose(Axis.YP.rotationDegrees(-78));
            poseStack.scale(-1, -1, 1);
            poseStack.translate(-0.5F, -0.5F, -0.5F);
            dispatcher.renderSingleBlock(blockstate, poseStack, buffer, packedLightIn, i);
            poseStack.popPose();
        }

    }
}
