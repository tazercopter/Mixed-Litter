package dev.tazer.mixed_litter.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.tazer.mixed_litter.Config;
import dev.tazer.mixed_litter.VariantUtil;
import dev.tazer.mixed_litter.actions.SetRemodel;
import dev.tazer.mixed_litter.client.RemodelRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(value = LivingEntityRenderer.class, priority = 999)
public class RenderEntityVariantMixin<T extends LivingEntity, M extends EntityModel<T>> {
    @Shadow protected M model;

    @Unique
    private final Map<String, EntityModel<?>> mixedLitter$cachedRemodels = new HashMap<>();

    @Inject(method = "getRenderType", at = @At(value = "HEAD"))
    public void storeRenderTypeVariables(T livingEntity, boolean bodyVisible, boolean translucent, boolean glowing, CallbackInfoReturnable<RenderType> cir) {
        if (Config.STARTUP_CONFIG.isLoaded()) {
            String entityKey = BuiltInRegistries.ENTITY_TYPE.getKey(livingEntity.getType()).toString();

            if (!mixedLitter$cachedRemodels.containsKey(entityKey)) {
                EntityModelSet modelSet = Minecraft.getInstance().getEntityModels();
                EntityModel<?> newModel = null;

                SetRemodel remodel = VariantUtil.findAction(livingEntity, SetRemodel.class);
                if (remodel != null && remodel.getRemodel() != null) {
                    Object m = RemodelRegistry.createModel(remodel.getRemodel(), entityKey, modelSet);
                    if (m != null) newModel = (EntityModel<?>) m;
                }

                if (newModel == null) {
                    Object m = RemodelRegistry.createModelFromConfig(entityKey, modelSet);
                    if (m != null) newModel = (EntityModel<?>) m;
                }

                mixedLitter$cachedRemodels.put(entityKey, newModel);
            }

            EntityModel<?> cached = mixedLitter$cachedRemodels.get(entityKey);
            if (cached != null) model = (M) cached;
        }
    }

    @ModifyVariable(method = "getRenderType", at = @At(value = "STORE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;getTextureLocation(Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/resources/ResourceLocation;"))
    public ResourceLocation getVariantTexture(ResourceLocation value, @Local(argsOnly = true) T livingEntity) {
        if (livingEntity != null) {
            return VariantUtil.resolveTexture(livingEntity, value);
        }
        return value;
    }
}
