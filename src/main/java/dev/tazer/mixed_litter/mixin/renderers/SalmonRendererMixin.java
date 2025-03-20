package dev.tazer.mixed_litter.mixin.renderers;

import dev.tazer.mixed_litter.variants.MobVariant;
import dev.tazer.mixed_litter.variants.animals.SalmonVariant;
import net.minecraft.client.renderer.entity.SalmonRenderer;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Salmon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static dev.tazer.mixed_litter.VariantUtil.getVariants;

@Mixin(SalmonRenderer.class)
public class SalmonRendererMixin {
    @Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/animal/Salmon;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true)
    private void getVariantTextureLocation(Salmon entity, CallbackInfoReturnable<ResourceLocation> cir) {
        SalmonVariant variant = null;

        for (Holder<MobVariant> animalVariantHolder : getVariants(entity)) {
            if (animalVariantHolder.value() instanceof SalmonVariant salmonVariant) {
                variant = salmonVariant;
                break;
            }
        }

        if (variant != null) {
            ResourceLocation texture = variant.texture;
            cir.setReturnValue(texture.withPath(path -> "textures/" + path + ".png"));
        }
    }
}
