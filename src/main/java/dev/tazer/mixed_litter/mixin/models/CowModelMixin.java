package dev.tazer.mixed_litter.mixin.models;

import dev.tazer.mixed_litter.MLConfig;
import dev.tazer.mixed_litter.models.CowRemodel;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.world.entity.animal.Cow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CowModel.class)
public class CowModelMixin<T extends Cow> extends QuadrupedModelMixin<T> {

    @Inject(method = "createBodyLayer", at = @At("RETURN"), cancellable = true)
    private static void biodiversity$createBodyLayer(CallbackInfoReturnable<LayerDefinition> cir) {
        if (MLConfig.COW.get()) cir.setReturnValue(CowRemodel.createBodyLayer());
    }

    @Override
    public void biodiversity$setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (MLConfig.COW.get()) CowRemodel.setupAnim(entity, biodiversity$root);
    }
}
