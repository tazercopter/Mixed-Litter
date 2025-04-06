package dev.tazer.mixed_litter.mixin.renderers;

import dev.tazer.mixed_litter.MLConfig;
import dev.tazer.mixed_litter.variants.MobVariant;
import dev.tazer.mixed_litter.variants.remodels.GlowSquidVariant;
import net.minecraft.client.renderer.entity.GlowSquidRenderer;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.GlowSquid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static dev.tazer.mixed_litter.VariantUtil.getVariants;

@Mixin(GlowSquidRenderer.class)
public class GlowSquidRendererMixin {
    @Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/GlowSquid;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true)
    private void getVariantTextureLocation(GlowSquid entity, CallbackInfoReturnable<ResourceLocation> cir) {
        if (MLConfig.SQUID.get()) {
            GlowSquidVariant variant = null;

            for (Holder<MobVariant> animalVariantHolder : getVariants(entity, entity.level())) {
                if (animalVariantHolder.value() instanceof GlowSquidVariant glowSquidVariant) {
                    variant = glowSquidVariant;
                    break;
                }
            }

            if (variant != null) {
                ResourceLocation texture = variant.texture;
                cir.setReturnValue(texture.withPath(path -> "textures/" + path + ".png"));
            }
        }
    }
}
