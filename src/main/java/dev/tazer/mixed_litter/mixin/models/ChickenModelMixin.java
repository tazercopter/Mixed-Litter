package dev.tazer.mixed_litter.mixin.models;

import dev.tazer.mixed_litter.MLConfig;
import dev.tazer.mixed_litter.models.ChickenRemodel;
import net.minecraft.client.model.ChickenModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Chicken;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChickenModel.class)
public class ChickenModelMixin<T extends Entity> {
    @Unique
    protected ModelPart biodiversity$root;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(ModelPart root, CallbackInfo ci) {
        this.biodiversity$root = root;
    }

    @Inject(method = "createBodyLayer", at = @At("RETURN"), cancellable = true)
    private static void biodiversity$createBodyLayer(CallbackInfoReturnable<LayerDefinition> cir) {
        if (MLConfig.CHICKEN.get()) cir.setReturnValue(ChickenRemodel.createBodyLayer());
    }

    @Inject(method = "setupAnim", at = @At("TAIL"))
    private void biodiversity$setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (MLConfig.CHICKEN.get()) ChickenRemodel.setupAnim((Chicken) entity, biodiversity$root, limbSwing, limbSwing, ageInTicks, netHeadYaw, headPitch);
    }
}
