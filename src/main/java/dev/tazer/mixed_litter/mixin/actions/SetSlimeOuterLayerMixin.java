package dev.tazer.mixed_litter.mixin.actions;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.tazer.mixed_litter.VariantUtil;
import dev.tazer.mixed_litter.actions.SetSlimeOuterLayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.SlimeOuterLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Slime;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SlimeOuterLayer.class)
public abstract class SetSlimeOuterLayerMixin<T extends LivingEntity> {
    @Shadow
    @Final
    private EntityModel<T> model;

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V", at = @At("HEAD"), cancellable = true)
    private void mixedLitter$render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T slime, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        SetSlimeOuterLayer outerData = VariantUtil.findAction(slime, SetSlimeOuterLayer.class);
        if (outerData == null) return;

        ResourceLocation outerTexture = outerData.getTexture();

        Minecraft minecraft = Minecraft.getInstance();
        boolean glowing = minecraft.shouldEntityAppearGlowing(slime) && slime.isInvisible();
        if (!slime.isInvisible() || glowing) {
            VertexConsumer vertexconsumer;
            if (glowing) {
                vertexconsumer = buffer.getBuffer(RenderType.outline(outerTexture));
            } else {
                vertexconsumer = buffer.getBuffer(RenderType.entityTranslucent(outerTexture));
            }

            ((SlimeOuterLayer) (Object) this).getParentModel().copyPropertiesTo(this.model);
            this.model.prepareMobModel(slime, limbSwing, limbSwingAmount, partialTicks);
            this.model.setupAnim(slime, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            this.model.renderToBuffer(poseStack, vertexconsumer, packedLight, LivingEntityRenderer.getOverlayCoords(slime, 0.0F), -1);
        }

        ci.cancel();
    }
}
