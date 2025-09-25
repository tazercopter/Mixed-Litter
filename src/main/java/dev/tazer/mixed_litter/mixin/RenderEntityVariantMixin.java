package dev.tazer.mixed_litter.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.tazer.mixed_litter.Config;
import dev.tazer.mixed_litter.VariantUtil;
import dev.tazer.mixed_litter.actions.*;
import dev.tazer.mixed_litter.client.ModelLayers;
import dev.tazer.mixed_litter.client.models.*;
import dev.tazer.mixed_litter.variants.Variant;
import dev.tazer.mixed_litter.variants.VariantType;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

@Mixin(value = LivingEntityRenderer.class, priority = 999)
public class RenderEntityVariantMixin<T extends LivingEntity, M extends EntityModel<T>> {
    @Shadow protected M model;
    @Unique
    private static EntityRendererProvider.Context CONTEXT = null;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void LivingEntityRenderer(EntityRendererProvider.Context context, EntityModel<?> model, float shadowRadius, CallbackInfo ci) {
        CONTEXT = context;
    }

    @Inject(method = "getRenderType", at = @At(value = "HEAD"))
    public void storeRenderTypeVariables(T livingEntity, boolean bodyVisible, boolean translucent, boolean glowing, CallbackInfoReturnable<RenderType> cir) {
        if (Config.STARTUP_CONFIG.isLoaded()) {
            if (Config.PIG.get() && livingEntity.getType() == EntityType.PIG && model instanceof PigModel) {
                model = (M) new PigRemodel<>(CONTEXT.bakeLayer(ModelLayers.PIG_LAYER));
            }

            if (Config.CHICKEN.get() && livingEntity.getType() == EntityType.CHICKEN && model instanceof ChickenModel) {
                model = (M) new ChickenRemodel<>(CONTEXT.bakeLayer(ModelLayers.CHICKEN_LAYER));
            }

            if (Config.COW.get() && (livingEntity.getType() == EntityType.COW || livingEntity.getType() == EntityType.MOOSHROOM) && model instanceof CowModel) {
                model = (M) new CowRemodel<>(CONTEXT.bakeLayer(ModelLayers.COW_LAYER));
            }

            if (Config.SHEEP.get() && livingEntity.getType() == EntityType.SHEEP && model instanceof SheepModel) {
                model = (M) new SheepRemodel<>(CONTEXT.bakeLayer(ModelLayers.SHEEP_LAYER));
            }

            if (Config.SQUID.get() && (livingEntity.getType() == EntityType.SQUID || livingEntity.getType() == EntityType.GLOW_SQUID) && model instanceof SquidModel) {
                model = (M) new SquidRemodel<>(CONTEXT.bakeLayer(ModelLayers.SQUID_LAYER));
            }

            if (Config.RABBIT.get() && livingEntity.getType() == EntityType.RABBIT && model instanceof RabbitModel) {
                model = (M) new RabbitRemodel<>(CONTEXT.bakeLayer(ModelLayers.RABBIT_LAYER));
            }
        }
    }

    @ModifyVariable(method = "getRenderType", at = @At(value = "STORE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;getTextureLocation(Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/resources/ResourceLocation;"))
    public ResourceLocation getVariantTexture(ResourceLocation value, @Local(argsOnly = true) T livingEntity) {
        if (livingEntity != null) {
            List<Variant> variants = VariantUtil.getVariants(livingEntity);

            for (Variant variant : variants) {
                VariantType variantType = VariantUtil.getType(livingEntity, variant);
                for (Action action : variantType.actions()) {
                    VariantActionType actionType = action.type();

                    actionType.initialize(action.arguments(), variant.arguments(), variantType.defaults());

                    switch (actionType) {
                        case SetTexture setTexture -> {
                            return setTexture.texture;
                        }
                        case SetAgeableTexture setAgeableTexture -> {
                            return livingEntity instanceof AgeableMob ageableMob && ageableMob.isBaby() ? setAgeableTexture.babyTexture : setAgeableTexture.texture;
                        }
                        case ReplaceTextures replaceTextures -> {
                            for (Map.Entry<ResourceLocation, ResourceLocation> resourceLocationEntry : replaceTextures.replacements.entrySet()) {
                                if (value.equals(resourceLocationEntry.getKey()))
                                    return resourceLocationEntry.getValue();
                            }
                        }
                        default -> {
                        }
                    }
                }
            }
        }

        return value;
    }
}
