package dev.tazer.mixed_litter.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.tazer.mixed_litter.registry.MLDataAttachmentTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BeehiveBlockEntity.Occupant.class)
public class BeehiveOccupantMixin {
    @Inject(method = "createEntity", at = @At("RETURN"))
    private void addSpawningLocation(Level level, BlockPos pos, CallbackInfoReturnable<Entity> cir, @Local Entity entity) {
        if (entity != null && !entity.hasData(MLDataAttachmentTypes.SPAWN_LOCATION)) {
            entity.setData(MLDataAttachmentTypes.SPAWN_LOCATION, GlobalPos.of(level.dimension(), pos));
        }
    }
}
