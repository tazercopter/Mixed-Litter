package dev.tazer.mixed_litter.mixin.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.tazer.mixed_litter.MLConfig;
import dev.tazer.mixed_litter.variants.MobVariant;
import dev.tazer.mixed_litter.variants.remodels.MooshroomVariant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.MushroomCowMushroomLayer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.tazer.mixed_litter.VariantUtil.getVariants;

@Mixin(MushroomCowMushroomLayer.class)
public abstract class MushroomCowMushroomLayerMixin<T extends MushroomCow> extends RenderLayerMixin<T, CowModel<T>> {
    @Shadow @Final private BlockRenderDispatcher blockRenderer;

    @Shadow protected abstract void renderMushroomBlock(PoseStack poseStack, MultiBufferSource buffer, int packedLight, boolean outlineOnly, BlockState state, int packedOverlay, BakedModel model);

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/animal/MushroomCow;FFFFFF)V", at = @At("HEAD"), cancellable = true)
    private void biodiversity$render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (MLConfig.COW.get()) {
            if (!livingEntity.isBaby()) {
                Minecraft minecraft = Minecraft.getInstance();
                boolean flag = minecraft.shouldEntityAppearGlowing(livingEntity) && livingEntity.isInvisible();
                if (!livingEntity.isInvisible() || flag) {
                    MooshroomVariant variant = null;

                    for (Holder<MobVariant> animalVariantHolder : getVariants(livingEntity)) {
                        if (animalVariantHolder.value() instanceof MooshroomVariant mooshroomVariant) {
                            variant = mooshroomVariant;
                            break;
                        }
                    }

                    if (variant != null) {
                        ResourceLocation texture = livingEntity.isBaby() ? variant.texture : variant.babyTexture;
                    }

                    if (variant != null) {
                        BlockState blockstate = variant.mushroom.defaultBlockState();
                        int i = LivingEntityRenderer.getOverlayCoords(livingEntity, 0);
                        BakedModel bakedmodel = blockRenderer.getBlockModel(blockstate);
                        poseStack.pushPose();
                        poseStack.translate(0.2F, -0.35F, 0.5F);
                        poseStack.mulPose(Axis.YP.rotationDegrees(-48));
                        poseStack.scale(-1, -1, 1);
                        poseStack.translate(-0.5F, -0.3F, -0.5F);
                        renderMushroomBlock(poseStack, buffer, packedLight, flag, blockstate, i, bakedmodel);
                        poseStack.popPose();
                        poseStack.pushPose();
                        poseStack.translate(0.2F, -0.35F, 0.5F);
                        poseStack.mulPose(Axis.YP.rotationDegrees(42));
                        poseStack.translate(0.1F, 0, -0.6F);
                        poseStack.mulPose(Axis.YP.rotationDegrees(-48));
                        poseStack.scale(-1, -1, 1);
                        poseStack.translate(-0.5F, -0.3F, -0.5F);
                        renderMushroomBlock(poseStack, buffer, packedLight, flag, blockstate, i, bakedmodel);
                        poseStack.popPose();
                        poseStack.pushPose();
                        getParentModel().getHead().translateAndRotate(poseStack);
                        poseStack.translate(0, -0.6F, -0.2F);
                        poseStack.mulPose(Axis.YP.rotationDegrees(-78));
                        poseStack.scale(-1, -1, 1);
                        poseStack.translate(-0.5F, -0.3F, -0.5F);
                        renderMushroomBlock(poseStack, buffer, packedLight, flag, blockstate, i, bakedmodel);
                        poseStack.popPose();
                    }
                }
            }
            ci.cancel();
        }
    }
}
