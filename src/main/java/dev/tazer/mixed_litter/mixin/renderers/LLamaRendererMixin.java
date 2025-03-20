package dev.tazer.mixed_litter.mixin.renderers;

import dev.tazer.mixed_litter.variants.MobVariant;
import dev.tazer.mixed_litter.variants.animals.LLamaVariant;
import net.minecraft.client.renderer.entity.LlamaRenderer;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.Llama;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static dev.tazer.mixed_litter.VariantUtil.getVariants;

@Mixin(LlamaRenderer.class)
public class LLamaRendererMixin {
    @Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/animal/horse/Llama;)Lnet/minecraft/resources/ResourceLocation;", at = @At("RETURN"), cancellable = true)
    private void getVariantTextureLocation(Llama entity, CallbackInfoReturnable<ResourceLocation> cir) {
        LLamaVariant variant = null;

        for (Holder<MobVariant> animalVariantHolder : getVariants(entity)) {
            if (animalVariantHolder.value() instanceof LLamaVariant lLamaVariant) {
                variant = lLamaVariant;
                break;
            }
        }

        if (variant != null) {
            ResourceLocation texture = entity.isBaby() ? variant.babyTexture : variant.texture;
            cir.setReturnValue(texture.withPath(path -> "textures/" + path + ".png"));
        }
    }
}
