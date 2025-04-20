package dev.tazer.mixed_litter.mixin;

import dev.tazer.mixed_litter.mixin.accessor.EntityAccessor;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Sheep.class)
public abstract class SheepMixin extends EntityMixin implements EntityAccessor {

    @Override
    protected void mixedLitter$getBoundingBox(CallbackInfoReturnable<AABB> cir) {
        cir.setReturnValue(getBb().inflate(-0.1, 0, -0.1).expandTowards(0, -0.15, 0));
    }
}
