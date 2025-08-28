package dev.tazer.mixed_litter.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.tazer.mixed_litter.Config;
import dev.tazer.mixed_litter.client.models.MooshroomMushroomLayer;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MushroomCowRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.animal.MushroomCow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MushroomCowRenderer.class)
public class MushroomCowRendererMixin {
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/MushroomCowRenderer;addLayer(Lnet/minecraft/client/renderer/entity/layers/RenderLayer;)Z"))
    private boolean addLayer(MushroomCowRenderer instance, RenderLayer<MushroomCow, CowModel<MushroomCow>> renderLayer, @Local(argsOnly = true) EntityRendererProvider.Context context) {
        return Config.COW.get() ? instance.addLayer(new MooshroomMushroomLayer<>(instance, context.getBlockRenderDispatcher(), context.getModelSet())) : instance.addLayer(renderLayer);
    }
}
