package dev.tazer.mixed_litter.mixin.renderers;

import dev.tazer.mixed_litter.MLConfig;
import dev.tazer.mixed_litter.variants.MobVariant;
import dev.tazer.mixed_litter.variants.remodels.MooshroomVariant;
import net.minecraft.client.renderer.entity.MushroomCowRenderer;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.MushroomCow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static dev.tazer.mixed_litter.VariantUtil.getVariants;

@Mixin(MushroomCowRenderer.class)
public class MushroomCowRendererMixin {
    @Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/animal/MushroomCow;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true)
    private void getVariantTextureLocation(MushroomCow entity, CallbackInfoReturnable<ResourceLocation> cir) {
        if (MLConfig.COW.get()) {
            MooshroomVariant variant = null;

            for (Holder<MobVariant> animalVariantHolder : getVariants(entity, entity.level())) {
                if (animalVariantHolder.value() instanceof MooshroomVariant mooshroomVariant) {
                    variant = mooshroomVariant;
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
