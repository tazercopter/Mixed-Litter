package dev.tazer.mixed_litter.mixin.models;

import dev.tazer.mixed_litter.MLConfig;
import dev.tazer.mixed_litter.models.CowRemodel;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Cow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CowModel.class)
public class CowModelMixin<T extends Entity> extends QuadrupedModelMixin<T> {

    @Inject(method = "createBodyLayer", at = @At("RETURN"), cancellable = true)
    private static void mixedLitter$createBodyLayer(CallbackInfoReturnable<LayerDefinition> cir) {
        if (MLConfig.COW.get()) cir.setReturnValue(CowRemodel.createBodyLayer());
    }

    @Override
    public void mixedLitter$setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (MLConfig.COW.get() && entity instanceof AgeableMob cow) CowRemodel.setupAnim(cow, mixedLitter$root);
    }
}
