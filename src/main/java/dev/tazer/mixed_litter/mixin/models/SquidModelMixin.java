package dev.tazer.mixed_litter.mixin.models;

import dev.tazer.mixed_litter.MLConfig;
import dev.tazer.mixed_litter.models.SquidRemodel;
import net.minecraft.client.model.SquidModel;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SquidModel.class)
public class SquidModelMixin {
    @Inject(method = "createBodyLayer", at = @At("RETURN"), cancellable = true)
    private static void createBodyLayer(CallbackInfoReturnable<LayerDefinition> cir) {
        if (MLConfig.SQUID.get()) cir.setReturnValue(SquidRemodel.createBodyLayer());
    }
}
