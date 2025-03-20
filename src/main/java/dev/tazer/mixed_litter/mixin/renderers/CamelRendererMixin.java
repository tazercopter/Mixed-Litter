package dev.tazer.mixed_litter.mixin.renderers;

import dev.tazer.mixed_litter.variants.MobVariant;
import dev.tazer.mixed_litter.variants.animals.CamelVariant;
import net.minecraft.client.renderer.entity.CamelRenderer;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.camel.Camel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static dev.tazer.mixed_litter.VariantUtil.getVariants;

@Mixin(CamelRenderer.class)
public class CamelRendererMixin {
    @Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/animal/camel/Camel;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true)
    private void getVariantTextureLocation(Camel entity, CallbackInfoReturnable<ResourceLocation> cir) {
        CamelVariant variant = null;

        for (Holder<MobVariant> animalVariantHolder : getVariants(entity)) {
            if (animalVariantHolder.value() instanceof CamelVariant camelVariant) {
                variant = camelVariant;
                break;
            }
        }

        if (variant != null) {
            ResourceLocation texture = entity.isBaby() ? variant.babyTexture : variant.texture;
            cir.setReturnValue(texture.withPath(path -> "textures/" + path + ".png"));
        }
    }
}
