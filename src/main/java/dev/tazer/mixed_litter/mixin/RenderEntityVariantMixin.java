package dev.tazer.mixed_litter.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.tazer.mixed_litter.RemodelRegistry;
import dev.tazer.mixed_litter.VariantUtil;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = LivingEntityRenderer.class, priority = 999)
public class RenderEntityVariantMixin<T extends LivingEntity> {

    @ModifyVariable(method = "getRenderType", at = @At(value = "STORE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;getTextureLocation(Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/resources/ResourceLocation;"))
    public ResourceLocation getVariantTexture(ResourceLocation value, @Local(argsOnly = true) T livingEntity) {
        if (livingEntity == null) return value;
        boolean remodelActive = RemodelRegistry.remodelActive(livingEntity.getType());
        return VariantUtil.resolveTexture(livingEntity, value, remodelActive);
    }
}
