package dev.tazer.mixed_litter.mixin.models;

import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(QuadrupedModel.class)
public abstract class QuadrupedModelMixin<T extends Entity> {
    @Shadow @Final protected ModelPart head;

    @Unique
    protected ModelPart mixedLitter$root;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void mixedLitter$init(ModelPart root, boolean scaleHead, float babyYHeadOffset, float babyZHeadOffset, float babyHeadScale, float babyBodyScale, int bodyYOffset, CallbackInfo ci) {
        this.mixedLitter$root = root;
    }

    @Inject(method = "setupAnim", at = @At("TAIL"))
    public void mixedLitter$setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
    }
}
