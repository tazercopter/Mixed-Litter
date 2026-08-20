package dev.tazer.mixed_litter.mixin;

import dev.tazer.mixed_litter.RemodelRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class PassengerAttachmentMixin {
    @Unique private static final double mixed_litter$VANILLA_CHICKEN_BACK_HEIGHT = 0.7;
    @Unique private static final double mixed_litter$REMODEL_CHICKEN_BACK_HEIGHT = 0.5;

    @Inject(method = "getPassengerAttachmentPoint", at = @At("RETURN"), cancellable = true)
    private void mixed_litter$matchRemodelledChickenBack(Entity entity, EntityDimensions dimensions, float partialTick, CallbackInfoReturnable<Vec3> cir) {
        if (!((Object) this instanceof Chicken chicken)) return;
        if (!RemodelRegistry.remodelActive(chicken.getType())) return;
        cir.setReturnValue(cir.getReturnValue().multiply(1.0, mixed_litter$REMODEL_CHICKEN_BACK_HEIGHT / mixed_litter$VANILLA_CHICKEN_BACK_HEIGHT, 1.0));
    }
}
