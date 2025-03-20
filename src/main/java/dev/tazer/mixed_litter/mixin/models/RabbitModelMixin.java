package dev.tazer.mixed_litter.mixin.models;

import dev.tazer.mixed_litter.MLConfig;
import dev.tazer.mixed_litter.models.RabbitRemodel;
import net.minecraft.client.model.RabbitModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Rabbit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RabbitModel.class)
public class RabbitModelMixin<T extends Entity> {
    @Unique
    protected ModelPart biodiversity$root;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(ModelPart root, CallbackInfo ci) {
        this.biodiversity$root = root;
    }

    @Inject(method = "createBodyLayer", at = @At("RETURN"), cancellable = true)
    private static void biodiversity$createBodyLayer(CallbackInfoReturnable<LayerDefinition> cir) {
        if (MLConfig.RABBIT.get()) cir.setReturnValue(RabbitRemodel.createBodyLayer());
    }

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/animal/Rabbit;FFFFF)V", at = @At("TAIL"))
    private void biodiversity$setupAnim(Rabbit entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (MLConfig.RABBIT.get()) RabbitRemodel.setupAnim(entity, biodiversity$root, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }
}
