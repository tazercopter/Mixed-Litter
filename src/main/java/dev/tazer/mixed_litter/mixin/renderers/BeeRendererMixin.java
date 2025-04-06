package dev.tazer.mixed_litter.mixin.renderers;

import dev.tazer.mixed_litter.variants.MobVariant;
import dev.tazer.mixed_litter.variants.animals.BeeVariant;
import net.minecraft.client.renderer.entity.BeeRenderer;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Bee;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static dev.tazer.mixed_litter.VariantUtil.getVariants;

@Mixin(BeeRenderer.class)
public class BeeRendererMixin {
    @Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/animal/Bee;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true)
    private void getVariantTextureLocation(Bee entity, CallbackInfoReturnable<ResourceLocation> cir) {
        BeeVariant variant = null;

        for (Holder<MobVariant> animalVariantHolder : getVariants(entity, entity.level())) {
            if (animalVariantHolder.value() instanceof BeeVariant beeVariant) {
                variant = beeVariant;
                break;
            }
        }

        if (variant != null) {
            ResourceLocation texture;
            if (entity.hasNectar()) texture = entity.isAngry() ? variant.angryNectarTexture : variant.nectarTexture;
            else texture = entity.isAngry() ? variant.angryTexture : variant.texture;
            cir.setReturnValue(texture.withPath(path -> "textures/" + path + ".png"));
        }
    }
}
