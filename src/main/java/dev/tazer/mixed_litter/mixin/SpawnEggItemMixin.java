package dev.tazer.mixed_litter.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

import static dev.tazer.mixed_litter.VariantUtil.setChildVariant;

@Mixin(SpawnEggItem.class)
public class SpawnEggItemMixin {
    @Inject(method = "spawnOffspringFromSpawnEgg", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;moveTo(DDDFF)V", shift = At.Shift.AFTER))
    private void setOffspringVariant(Player player, Mob p_mob, EntityType<? extends Mob> entityType, ServerLevel serverLevel, Vec3 pos, ItemStack stack, CallbackInfoReturnable<Optional<Mob>> cir, @Local(ordinal = 1) Mob mob) {
        if (mob instanceof AgeableMob) setChildVariant(p_mob, p_mob, mob);
    }

}
