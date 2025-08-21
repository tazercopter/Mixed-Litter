package dev.tazer.mixed_litter.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.tazer.mixed_litter.VariantUtil;
import dev.tazer.mixed_litter.actions.Action;
import dev.tazer.mixed_litter.actions.ActionType;
import dev.tazer.mixed_litter.actions.SetMooshroomMushroom;
import dev.tazer.mixed_litter.client.ModelLayers;
import dev.tazer.mixed_litter.variants.Variant;
import dev.tazer.mixed_litter.variants.VariantType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class MooshroomMushroomLayer<T extends MushroomCow> extends RenderLayer<T, CowModel<T>>  {
    private final BlockRenderDispatcher blockRenderer;
    private CowRemodel<MushroomCow> cowRemodel;

    public MooshroomMushroomLayer(RenderLayerParent<T, CowModel<T>> renderer, BlockRenderDispatcher blockRenderer, EntityModelSet modelSet) {
        super(renderer);
        this.blockRenderer = blockRenderer;
        cowRemodel = new CowRemodel<>(modelSet.bakeLayer(ModelLayers.COW_LAYER));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!livingEntity.isBaby()) {
            Minecraft minecraft = Minecraft.getInstance();
            if (!livingEntity.isInvisible() || minecraft.shouldEntityAppearGlowing(livingEntity)) {
                // todo: make this randomise or mix or something between ALL variants the mooshroom has that contain mushrooms
                Block mushroom = null;

                for (Variant variant : VariantUtil.getVariants(livingEntity)) {
                    VariantType variantType = VariantUtil.getType(livingEntity, variant);
                    for (Action action : variantType.actions()) {
                        ActionType actionType = action.type();

                        actionType.initialize(action.arguments(), variant.arguments(), variantType.defaults());

                        if (actionType instanceof SetMooshroomMushroom setMooshroomMushroom) {
                            mushroom = setMooshroomMushroom.mushroom;
                        }
                    }
                }

                if (mushroom != null) {
                    boolean outlineOnly = minecraft.shouldEntityAppearGlowing(livingEntity) && livingEntity.isInvisible();
                    BlockState blockstate = mushroom.defaultBlockState();
                    int i = LivingEntityRenderer.getOverlayCoords(livingEntity, 0);

                    BakedModel bakedmodel = blockRenderer.getBlockModel(blockstate);
                    poseStack.pushPose();
                    poseStack.translate(0.2F, -0.35F, 0.5F);
                    poseStack.mulPose(Axis.YP.rotationDegrees(-48));
                    poseStack.scale(-1, -1, 1);
                    poseStack.translate(-0.5F, -0.3F, -0.5F);
                    renderMushroomBlock(poseStack, buffer, packedLight, outlineOnly, blockstate, i, bakedmodel);
                    poseStack.popPose();
                    poseStack.pushPose();
                    poseStack.translate(0.2F, -0.35F, 0.5F);
                    poseStack.mulPose(Axis.YP.rotationDegrees(42));
                    poseStack.translate(0.1F, 0, -0.6F);
                    poseStack.mulPose(Axis.YP.rotationDegrees(-48));
                    poseStack.scale(-1, -1, 1);
                    poseStack.translate(-0.5F, -0.3F, -0.5F);
                    renderMushroomBlock(poseStack, buffer, packedLight, outlineOnly, blockstate, i, bakedmodel);
                    poseStack.popPose();
                    poseStack.pushPose();
                    cowRemodel.getHead().translateAndRotate(poseStack);
                    poseStack.translate(0, -0.6F, -0.2F);
                    poseStack.mulPose(Axis.YP.rotationDegrees(-78));
                    poseStack.scale(-1, -1, 1);
                    poseStack.translate(-0.5F, -0.3F, -0.5F);
                    renderMushroomBlock(poseStack, buffer, packedLight, outlineOnly, blockstate, i, bakedmodel);
                    poseStack.popPose();
                }
            }
        }
    }

    private void renderMushroomBlock(PoseStack poseStack, MultiBufferSource buffer, int packedLight, boolean outlineOnly, BlockState state, int packedOverlay, BakedModel model) {
        if (outlineOnly) {
            blockRenderer.getModelRenderer().renderModel(poseStack.last(), buffer.getBuffer(RenderType.outline(TextureAtlas.LOCATION_BLOCKS)), state, model, 0.0F, 0.0F, 0.0F, packedLight, packedOverlay);
        } else {
            blockRenderer.renderSingleBlock(state, poseStack, buffer, packedLight, packedOverlay);
        }
    }
}
