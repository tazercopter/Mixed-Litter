package dev.tazer.mixed_litter.mixin.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.tazer.mixed_litter.variants.MobVariant;
import dev.tazer.mixed_litter.variants.monsters.DrownedVariant;
import net.minecraft.client.model.DrownedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.DrownedOuterLayer;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Drowned;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.tazer.mixed_litter.VariantUtil.getVariants;

@Mixin(DrownedOuterLayer.class)
public abstract class DrownedOuterLayerMixin<T extends Drowned> extends RenderLayerMixin<T, DrownedModel<T>> {

    @Shadow
    @Final
    private DrownedModel<T> model;

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/monster/Drowned;FFFFFF)V", at = @At("HEAD"), cancellable = true)
    private void mixedLitter$render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        DrownedVariant variant = null;

        for (Holder<MobVariant> mobVariantHolder : getVariants(livingEntity, livingEntity.level())) {
            if (mobVariantHolder.value() instanceof DrownedVariant drownedVariant) {
                variant = drownedVariant;
                break;
            }
        }

        if (variant != null) {
            ResourceLocation texture = variant.outerLayerTexture;
            mixedLitter$coloredCutoutModelCopyLayerRender(this.getParentModel(), model, texture.withPath(path -> "textures/" + path + ".png"), poseStack, buffer, packedLight, livingEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, -1);
            ci.cancel();
        }
    }

    @Unique
    private static <T extends LivingEntity> void mixedLitter$coloredCutoutModelCopyLayerRender(EntityModel<T> modelParent, EntityModel<T> model, ResourceLocation textureLocation, PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float partialTick, int color) {
        if (!entity.isInvisible()) {
            modelParent.copyPropertiesTo(model);
            model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
            model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            mixedLitter$renderColoredCutoutModel(model, textureLocation, poseStack, buffer, packedLight, entity, color);
        }

    }

    @Unique
    private static <T extends LivingEntity> void mixedLitter$renderColoredCutoutModel(EntityModel<T> model, ResourceLocation textureLocation, PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity, int color) {
        VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(textureLocation));
        model.renderToBuffer(poseStack, vertexconsumer, packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), color);
    }
}
