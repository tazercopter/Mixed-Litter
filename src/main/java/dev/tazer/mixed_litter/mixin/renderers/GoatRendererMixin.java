package dev.tazer.mixed_litter.mixin.renderers;

import dev.tazer.mixed_litter.variants.MobVariant;
import dev.tazer.mixed_litter.variants.animals.GoatVariant;
import net.minecraft.client.renderer.entity.GoatRenderer;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.goat.Goat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static dev.tazer.mixed_litter.VariantUtil.getVariants;

@Mixin(GoatRenderer.class)
public class GoatRendererMixin {
    @Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/animal/goat/Goat;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true)
    private void getVariantTextureLocation(Goat entity, CallbackInfoReturnable<ResourceLocation> cir) {
        GoatVariant variant = null;

        for (Holder<MobVariant> animalVariantHolder : getVariants(entity)) {
            if (animalVariantHolder.value() instanceof GoatVariant goatVariant) {
                variant = goatVariant;
                break;
            }
        }

        if (variant != null) {
            ResourceLocation texture = variant.texture;
            cir.setReturnValue(texture.withPath(path -> "textures/" + path + ".png"));
        }
    }
}
