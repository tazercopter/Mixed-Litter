package dev.tazer.mixed_litter.mixin.renderers;

import dev.tazer.mixed_litter.MLConfig;
import dev.tazer.mixed_litter.variants.MobVariant;
import dev.tazer.mixed_litter.variants.remodels.SheepVariant;
import net.minecraft.client.renderer.entity.SheepRenderer;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Sheep;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static dev.tazer.mixed_litter.VariantUtil.getVariants;

@Mixin(SheepRenderer.class)
public class SheepRendererMixin {
    @Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/animal/Sheep;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true)
    private void getVariantTextureLocation(Sheep entity, CallbackInfoReturnable<ResourceLocation> cir) {
        if (MLConfig.SHEEP.get()) {
            SheepVariant variant = null;

            for (Holder<MobVariant> animalVariantHolder : getVariants(entity)) {
                if (animalVariantHolder.value() instanceof SheepVariant sheepVariant) {
                    variant = sheepVariant;
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
