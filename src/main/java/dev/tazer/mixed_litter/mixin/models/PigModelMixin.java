package dev.tazer.mixed_litter.mixin.models;

import dev.tazer.mixed_litter.MLConfig;
import dev.tazer.mixed_litter.models.PigRemodel;
import net.minecraft.client.model.PigModel;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Pig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PigModel.class)
public class PigModelMixin<T extends Entity> extends QuadrupedModelMixin<T> {
    @Inject(method = "createBodyLayer", at = @At("RETURN"), cancellable = true)
    private static void biodiversity$createBodyLayer(CubeDeformation cubeDeformation, CallbackInfoReturnable<LayerDefinition> cir) {
        if (MLConfig.PIG.get()) cir.setReturnValue(PigRemodel.createBodyLayer());
    }

    @Override
    public void mixedLitter$setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (MLConfig.PIG.get() && entity instanceof Pig pig) PigRemodel.setupAnim(pig, biodiversity$root, limbSwing, limbSwingAmount, netHeadYaw, headPitch);
    }
}
