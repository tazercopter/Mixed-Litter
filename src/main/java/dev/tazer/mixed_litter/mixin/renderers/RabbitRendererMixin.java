package dev.tazer.mixed_litter.mixin.renderers;

import dev.tazer.mixed_litter.MLConfig;
import dev.tazer.mixed_litter.variants.MobVariant;
import dev.tazer.mixed_litter.variants.remodels.RabbitVariant;
import net.minecraft.client.renderer.entity.RabbitRenderer;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Rabbit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static dev.tazer.mixed_litter.VariantUtil.getVariants;

@Mixin(RabbitRenderer.class)
public class RabbitRendererMixin {
    @Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/animal/Rabbit;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true)
    private void getVariantTextureLocation(Rabbit entity, CallbackInfoReturnable<ResourceLocation> cir) {
        if (MLConfig.RABBIT.get()) {
            RabbitVariant variant = null;

            for (Holder<MobVariant> animalVariantHolder : getVariants(entity)) {
                if (animalVariantHolder.value() instanceof RabbitVariant rabbitVariant) {
                    variant = rabbitVariant;
                    break;
                }
            }

            if (variant != null) {
                ResourceLocation texture = entity.isBaby() ? variant.babyTexture : variant.texture;
                cir.setReturnValue(texture.withPath(path -> "textures/" + path + ".png"));
            }
        }
    }
}
