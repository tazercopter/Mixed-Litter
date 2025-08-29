package dev.tazer.mixed_litter.mixin.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.tazer.mixed_litter.Config;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.SaddleLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Saddleable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SaddleLayer.class)
public abstract class SaddleLayerMixin<T extends Entity & Saddleable, M extends EntityModel<T>> {

    @Shadow
    @Final
    private M model;
    @Shadow
    @Final
    private ResourceLocation textureLocation;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void mixedLitter$render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (Config.PIG.get() && livingEntity.getType() == EntityType.PIG && livingEntity.isSaddled()) {
            ((SaddleLayer<T, M>) (Object) this).getParentModel().copyPropertiesTo(model);
            model.prepareMobModel(livingEntity, limbSwing, limbSwingAmount, partialTicks);
            model.setupAnim(livingEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            poseStack.pushPose();
            poseStack.scale(1.05F, 1, 1.05F);
            poseStack.translate(0, -0.025F, 0);
            VertexConsumer vertexconsumer;
            ResourceLocation saddleTexture = textureLocation;

            vertexconsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(saddleTexture));
            model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();

            ci.cancel();
        }
    }
}
