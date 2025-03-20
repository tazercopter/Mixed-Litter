package dev.tazer.mixed_litter.mixin.renderers;

import dev.tazer.mixed_litter.variants.MobVariant;
import dev.tazer.mixed_litter.variants.animals.TurtleVariant;
import net.minecraft.client.renderer.entity.TurtleRenderer;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Turtle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static dev.tazer.mixed_litter.VariantUtil.getVariants;

@Mixin(TurtleRenderer.class)
public class TurtleRendererMixin {
    @Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/animal/Turtle;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true)
    private void getVariantTextureLocation(Turtle entity, CallbackInfoReturnable<ResourceLocation> cir) {
        TurtleVariant variant = null;

        for (Holder<MobVariant> animalVariantHolder : getVariants(entity)) {
            if (animalVariantHolder.value() instanceof TurtleVariant turtleVariant) {
                variant = turtleVariant;
                break;
            }
        }

        if (variant != null) {
            ResourceLocation texture = variant.texture;
            cir.setReturnValue(texture.withPath(path -> "textures/" + path + ".png"));
        }
    }
}
