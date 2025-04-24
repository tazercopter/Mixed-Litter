package dev.tazer.mixed_litter.mixin.models;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.tazer.mixed_litter.MLConfig;
import dev.tazer.mixed_litter.mixin.accessor.EntityModelAccessor;
import dev.tazer.mixed_litter.models.RabbitRemodel;
import net.minecraft.client.model.RabbitModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.world.entity.animal.Rabbit;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RabbitModel.class)
public abstract class RabbitModelMixin implements EntityModelAccessor {
    @Shadow @Final private ModelPart head;
    @Shadow @Final private ModelPart leftEar;
    @Shadow @Final private ModelPart rightEar;
    @Shadow @Final private ModelPart nose;
    @Shadow @Final private ModelPart leftRearFoot;
    @Shadow @Final private ModelPart rightRearFoot;
    @Shadow @Final private ModelPart leftHaunch;
    @Shadow @Final private ModelPart rightHaunch;
    @Shadow @Final private ModelPart body;
    @Shadow @Final private ModelPart leftFrontLeg;
    @Shadow @Final private ModelPart rightFrontLeg;
    @Shadow @Final private ModelPart tail;
    @Unique
    protected ModelPart mixedLitter$root;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(ModelPart root, CallbackInfo ci) {
        mixedLitter$root = root;
    }

    @Inject(method = "createBodyLayer", at = @At("RETURN"), cancellable = true)
    private static void mixedLitter$createBodyLayer(CallbackInfoReturnable<LayerDefinition> cir) {
        if (MLConfig.RABBIT.get()) cir.setReturnValue(RabbitRemodel.createBodyLayer());
    }

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/animal/Rabbit;FFFFF)V", at = @At("TAIL"))
    private void mixedLitter$setupAnim(Rabbit entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (MLConfig.RABBIT.get()) RabbitRemodel.setupAnim(entity, mixedLitter$root, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    @Inject(method = "renderToBuffer", at = @At("HEAD"), cancellable = true)
    private void mixedLitter$renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color, CallbackInfo ci) {
        if (MLConfig.RABBIT.get()) {
            if (getYoung()) {
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

            ci.cancel();
        }
    }
}
