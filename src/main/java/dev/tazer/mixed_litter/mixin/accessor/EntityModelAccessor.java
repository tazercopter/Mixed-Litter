package dev.tazer.mixed_litter.mixin.accessor;

import net.minecraft.client.model.EntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityModel.class)
public interface EntityModelAccessor {
    @Accessor("young")
    boolean getYoung();
}
