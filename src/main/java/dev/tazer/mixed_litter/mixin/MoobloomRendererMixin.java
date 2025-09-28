package dev.tazer.mixed_litter.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.teamabnormals.buzzier_bees.client.render.entity.MoobloomRenderer;
import com.teamabnormals.buzzier_bees.common.entity.animal.Moobloom;
import dev.tazer.mixed_litter.Config;
import dev.tazer.mixed_litter.client.models.MoobloomFlowerLayer;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@IfModLoaded("buzzier_bees")
@Mixin(MoobloomRenderer.class)
public class MoobloomRendererMixin {
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/teamabnormals/buzzier_bees/client/render/entity/MoobloomRenderer;addLayer(Lnet/minecraft/client/renderer/entity/layers/RenderLayer;)Z"))
    private boolean addLayer(MoobloomRenderer instance, RenderLayer<Moobloom, CowModel<Moobloom>> renderLayer, @Local(argsOnly = true) EntityRendererProvider.Context context) {
        return (Config.STARTUP_CONFIG.isLoaded() && Config.COW_REMODEL.get()) ? instance.addLayer(new MoobloomFlowerLayer<>(instance, context.getModelSet())) : instance.addLayer(renderLayer);
    }
}
