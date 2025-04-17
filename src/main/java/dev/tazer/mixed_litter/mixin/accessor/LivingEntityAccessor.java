package dev.tazer.mixed_litter.mixin.accessor;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor extends EntityAccessor {
    @Invoker("getHealth")
    float invokeGetHealth();

    @Accessor
    float getYBodyRot();
}
