package dev.tazer.mixed_litter.mixin;

import dev.tazer.mixed_litter.mixin.accessor.EntityAccessor;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Cow.class)
public abstract class CowMixin extends EntityMixin implements EntityAccessor {

    @Override
    protected void mixedLitter$getBoundingBox(CallbackInfoReturnable<AABB> cir) {
        cir.setReturnValue(getBb().inflate(0.25, 0, 0.25).expandTowards(0, 0.15, 0));
    }
}
