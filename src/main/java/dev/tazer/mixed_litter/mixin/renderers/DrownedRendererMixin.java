package dev.tazer.mixed_litter.mixin.renderers;

import dev.tazer.mixed_litter.variants.MobVariant;
import dev.tazer.mixed_litter.variants.monsters.DrownedVariant;
import net.minecraft.client.renderer.entity.DrownedRenderer;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zombie;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static dev.tazer.mixed_litter.VariantUtil.getVariants;

@Mixin(DrownedRenderer.class)
public class DrownedRendererMixin {
    @Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/monster/Zombie;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true)
    private void getVariantTextureLocation(Zombie entity, CallbackInfoReturnable<ResourceLocation> cir) {
        DrownedVariant variant = null;

        for (Holder<MobVariant> animalVariantHolder : getVariants(entity, entity.level())) {
            if (animalVariantHolder.value() instanceof DrownedVariant drownedVariant) {
                variant = drownedVariant;
                break;
            }
        }

        if (variant != null) {
            ResourceLocation texture = variant.texture;
            cir.setReturnValue(texture.withPath(path -> "textures/" + path + ".png"));
        }
    }
}
