package dev.tazer.mixed_litter.mixin;

import dev.tazer.mixed_litter.VariantDataHolder;
import dev.tazer.mixed_litter.networking.VariantRequestData;
import net.minecraft.world.entity.Mob;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntityMixin implements VariantDataHolder {
    @Shadow public abstract boolean isPersistenceRequired();
}