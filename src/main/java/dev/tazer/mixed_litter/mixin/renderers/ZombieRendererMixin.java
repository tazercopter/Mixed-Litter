package dev.tazer.mixed_litter.mixin.renderers;

import dev.tazer.mixed_litter.variants.MobVariant;
import dev.tazer.mixed_litter.variants.monsters.ZombieVariant;
import net.minecraft.client.renderer.entity.AbstractZombieRenderer;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zombie;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static dev.tazer.mixed_litter.VariantUtil.getVariants;

@Mixin(AbstractZombieRenderer.class)
public class ZombieRendererMixin {
    @Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/monster/Zombie;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true)
    private void getVariantTextureLocation(Zombie entity, CallbackInfoReturnable<ResourceLocation> cir) {
        ZombieVariant variant = null;

        for (Holder<MobVariant> animalVariantHolder : getVariants(entity)) {
            if (animalVariantHolder.value() instanceof ZombieVariant zombieVariant) {
                variant = zombieVariant;
                break;
            }
        }

        if (variant != null) {
            ResourceLocation texture = variant.texture;
            cir.setReturnValue(texture.withPath(path -> "textures/" + path + ".png"));
        }
    }
}
