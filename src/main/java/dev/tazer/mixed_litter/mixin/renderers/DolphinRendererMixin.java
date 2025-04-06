package dev.tazer.mixed_litter.mixin.renderers;

import dev.tazer.mixed_litter.variants.MobVariant;
import dev.tazer.mixed_litter.variants.animals.DolphinVariant;
import net.minecraft.client.renderer.entity.DolphinRenderer;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Dolphin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static dev.tazer.mixed_litter.VariantUtil.getVariants;

@Mixin(DolphinRenderer.class)
public class DolphinRendererMixin {
    @Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/animal/Dolphin;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true)
    private void getVariantTextureLocation(Dolphin entity, CallbackInfoReturnable<ResourceLocation> cir) {
        DolphinVariant variant = null;

        for (Holder<MobVariant> animalVariantHolder : getVariants(entity, entity.level())) {
            if (animalVariantHolder.value() instanceof DolphinVariant dolphinVariant) {
                variant = dolphinVariant;
                break;
            }
        }

        if (variant != null) {
            ResourceLocation texture = variant.texture;
            cir.setReturnValue(texture.withPath(path -> "textures/" + path + ".png"));
        }
    }
}
