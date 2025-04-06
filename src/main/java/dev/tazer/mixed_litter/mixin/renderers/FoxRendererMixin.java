package dev.tazer.mixed_litter.mixin.renderers;

import dev.tazer.mixed_litter.variants.MobVariant;
import dev.tazer.mixed_litter.variants.animals.FoxVariant;
import net.minecraft.client.renderer.entity.FoxRenderer;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Fox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static dev.tazer.mixed_litter.VariantUtil.getVariants;

@Mixin(FoxRenderer.class)
public class FoxRendererMixin {
    @Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/animal/Fox;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true)
    private void getVariantTextureLocation(Fox entity, CallbackInfoReturnable<ResourceLocation> cir) {
        FoxVariant variant = null;

        for (Holder<MobVariant> animalVariantHolder : getVariants(entity, entity.level())) {
            if (animalVariantHolder.value() instanceof FoxVariant foxVariant) {
                variant = foxVariant;
                break;
            }
        }

        if (variant != null) {
            ResourceLocation texture = entity.isSleeping() ? variant.sleepingTexture : variant.texture;
            cir.setReturnValue(texture.withPath(path -> "textures/" + path + ".png"));
        }
    }
}
