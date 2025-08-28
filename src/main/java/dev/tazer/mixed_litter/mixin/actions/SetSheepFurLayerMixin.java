package dev.tazer.mixed_litter.mixin.actions;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.tazer.mixed_litter.Config;
import dev.tazer.mixed_litter.VariantUtil;
import dev.tazer.mixed_litter.actions.Action;
import dev.tazer.mixed_litter.actions.ActionType;
import dev.tazer.mixed_litter.actions.SetSheepFurLayer;
import dev.tazer.mixed_litter.client.ModelLayers;
import dev.tazer.mixed_litter.client.models.SheepRemodel;
import dev.tazer.mixed_litter.variants.Variant;
import dev.tazer.mixed_litter.variants.VariantType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.SheepFurModel;
import net.minecraft.client.model.SheepModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.SheepFurLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SheepFurLayer.class)
public abstract class SetSheepFurLayerMixin {
    @Shadow
    @Final
    private SheepFurModel<Sheep> model;
    
    @Unique
    private SheepRemodel<Sheep> sheepRemodel;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(RenderLayerParent<Sheep, SheepModel<Sheep>> renderer, EntityModelSet modelSet, CallbackInfo ci) {
        sheepRemodel = new SheepRemodel<>(modelSet.bakeLayer(ModelLayers.SHEEP_LAYER));
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/animal/Sheep;FFFFFF)V", at = @At("HEAD"), cancellable = true)
    private void mixedLitter$render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, Sheep sheep, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        EntityModel<Sheep> model = Config.SHEEP.get() && sheep.getType() == EntityType.SHEEP ? sheepRemodel : this.model;
        ResourceLocation furTexture = null; // todo: should there be an interaction when two variants applied have the setsheepfurlayer action?

        for (Variant variant : VariantUtil.getVariants(sheep)) {
            VariantType variantType = VariantUtil.getType(sheep, variant);
            for (Action action : variantType.actions()) {
                ActionType actionType = action.type();

                actionType.initialize(action.arguments(), variant.arguments(), variantType.defaults());

                if (actionType instanceof SetSheepFurLayer setSheepFurLayer) {
                    furTexture = sheep.isBaby() ? setSheepFurLayer.babyTexture : sheep.isSheared() ? setSheepFurLayer.shearedTexture : setSheepFurLayer.texture;
                }
            }
        }

        if (furTexture != null) {
            if (sheep.isInvisible()) {
                Minecraft minecraft = Minecraft.getInstance();
                if (minecraft.shouldEntityAppearGlowing(sheep)) {
                    ((SheepFurLayer) (Object) this).getParentModel().copyPropertiesTo(model);
                    model.prepareMobModel(sheep, limbSwing, limbSwingAmount, partialTicks);
                    model.setupAnim(sheep, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
                    VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.outline(furTexture));
                    model.renderToBuffer(poseStack, vertexconsumer, packedLight, LivingEntityRenderer.getOverlayCoords(sheep, 0.0F), -16777216);
                }
            } else {
                int i;
                if (sheep.hasCustomName() && sheep.getName().getString().equals("jeb_")) {
                    int k = sheep.tickCount / 25 + sheep.getId();
                    int l = DyeColor.values().length;
                    int i1 = k % l;
                    int j1 = (k + 1) % l;
                    float f = ((float) (sheep.tickCount % 25) + partialTicks) / 25.0F;
                    int k1 = Sheep.getColor(DyeColor.byId(i1));
                    int l1 = Sheep.getColor(DyeColor.byId(j1));
                    i = FastColor.ARGB32.lerp(f, k1, l1);
                } else i = Sheep.getColor(sheep.getColor());

                mixedLitter$coloredCutoutModelCopyLayerRender(((SheepFurLayer) (Object) this).getParentModel(), model, furTexture, poseStack, buffer, packedLight, sheep, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, i);
                ci.cancel();
            }
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
