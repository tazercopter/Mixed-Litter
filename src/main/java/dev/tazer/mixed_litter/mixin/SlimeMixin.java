package dev.tazer.mixed_litter.mixin;

import dev.tazer.mixed_litter.registry.MLDataAttachmentTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Mixin(Slime.class)
public abstract class SlimeMixin {
    @Redirect(method = "remove", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Slime;setCustomName(Lnet/minecraft/network/chat/Component;)V"))
    private void mixedLitter$copyVariantsToChild(Slime slime, Component name) {
        Slime parent = (Slime) (Object) this;
        if (parent.hasData(MLDataAttachmentTypes.VARIANTS)) {
            List<ResourceLocation> parentVariants = parent.getData(MLDataAttachmentTypes.VARIANTS);
            slime.setData(MLDataAttachmentTypes.VARIANTS, new ArrayList<>(parentVariants));
        }

        slime.setCustomName(name);
    }
}
