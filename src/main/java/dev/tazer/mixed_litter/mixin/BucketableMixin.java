package dev.tazer.mixed_litter.mixin;

import dev.tazer.mixed_litter.MLDataAttachementTypes;
import dev.tazer.mixed_litter.VariantUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Bucketable.class)
public interface BucketableMixin {
    @Inject(method = "saveDefaultDataToBucketTag", at = @At("TAIL"))
    private static void saveVariantDataToBucketTag(Mob mob, ItemStack bucket, CallbackInfo ci) {
        CustomData.update(DataComponents.BUCKET_ENTITY_DATA, bucket, (tag) -> {
            if (mob.hasData(MLDataAttachementTypes.MOB_VARIANTS)) {
                tag.putString("Variants", mob.getData(MLDataAttachementTypes.MOB_VARIANTS));
            }
        });
    }

    @Inject(method = "loadDefaultDataFromBucketTag", at = @At("TAIL"))
    private static void loadVariantDataFromBucketTag(Mob mob, CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("Variants")) {
            mob.setData(MLDataAttachementTypes.MOB_VARIANTS, tag.getString("Variants"));
        }
    }
}
