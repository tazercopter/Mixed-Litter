package dev.tazer.mixed_litter.mixin;

import dev.tazer.mixed_litter.MLDataAttachmentTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LivingEntityRenderer.class, priority = 999)
public class RenderEntityVariantMixin<T extends LivingEntity> {
    @Unique
    private static Mob STORED_ENTITY = null;

    @Inject(method = "getRenderType", at = @At(value = "HEAD"))
    public void storeRenderTypeVariables(T entity, boolean p_115323_, boolean p_115324_, boolean p_115325_, CallbackInfoReturnable<RenderType> cir)
    {
        // Store instance of entity for getting variant
        if (entity instanceof Mob mob) {
            STORED_ENTITY = mob;
        }
    }

    @ModifyVariable(method = "getRenderType", at = @At(value = "STORE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;getTextureLocation(Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/resources/ResourceLocation;"))
    public ResourceLocation getVariantTexture(ResourceLocation value)
    {
        if (STORED_ENTITY != null) {
            Mob entity = STORED_ENTITY;
            // Check subvariant
            String subVariant = entity.getData(MLDataAttachmentTypes.SUB_VARIANT);
            if (subVariant.contains(":")) {
                // Override with subvariant texture
                ResourceLocation location = ResourceLocation.tryParse(subVariant);
                if (location == null) return value;
                location = ResourceLocation.fromNamespaceAndPath(location.getNamespace(), "textures/" + location.getPath() + ".png");
                return location;
            }
        }
        return value;
    }
}
