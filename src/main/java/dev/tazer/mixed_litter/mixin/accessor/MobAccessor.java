package dev.tazer.mixed_litter.mixin.accessor;

import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Mob.class)
public interface MobAccessor extends LivingEntityAccessor {
    @Invoker("isPersistenceRequired")
    boolean isPersistenceRequired();
}