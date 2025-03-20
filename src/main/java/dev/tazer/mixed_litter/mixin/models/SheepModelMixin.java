package dev.tazer.mixed_litter.mixin.models;

import dev.tazer.mixed_litter.MLConfig;
import dev.tazer.mixed_litter.models.SheepRemodel;
import net.minecraft.client.model.SheepModel;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.world.entity.animal.Sheep;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SheepModel.class)
public class SheepModelMixin<T extends Sheep> extends QuadrupedModelMixin<T> {
    @Unique
    private float biodiversity$headXRot;

    @Inject(method = "createBodyLayer", at = @At("RETURN"), cancellable = true)
    private static void biodiversity$createBodyLayer(CallbackInfoReturnable<LayerDefinition> cir) {
        if (MLConfig.SHEEP.get()) cir.setReturnValue(SheepRemodel.createBodyLayer());
    }

    @Override
    public void biodiversity$setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (MLConfig.SHEEP.get()) SheepRemodel.setupAnim(entity, biodiversity$root, ageInTicks, biodiversity$headXRot);
    }

    @Inject(method = "prepareMobModel(Lnet/minecraft/world/entity/animal/Sheep;FFF)V", at = @At("HEAD"), cancellable = true)
    private void biodiversity$prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick, CallbackInfo ci) {
        if (MLConfig.SHEEP.get()) {
            head.y = 11 + entity.getHeadEatPositionScale(partialTick) * 7.0F;
            biodiversity$headXRot = entity.getHeadEatAngleScale(partialTick);
            ci.cancel();
        }
    }
}
