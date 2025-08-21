package dev.tazer.mixed_litter.mixin;

import dev.tazer.mixed_litter.DataAttachmentTypes;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Mixin(Bucketable.class)
public interface BucketableMixin {
    @Inject(method = "saveDefaultDataToBucketTag", at = @At("TAIL"))
    private static void saveVariantDataToBucketTag(Mob mob, ItemStack bucket, CallbackInfo ci) {
        CustomData.update(DataComponents.BUCKET_ENTITY_DATA, bucket, (tag) -> {
            if (mob.hasData(DataAttachmentTypes.VARIANTS)) {
                StringBuilder variants = new StringBuilder();
                for (ResourceLocation resourceLocation : mob.getData(DataAttachmentTypes.VARIANTS)) {
                    variants.append(resourceLocation.toString()).append(", ");
                }

                tag.putString("Variants", variants.toString());
            }
        });
    }

    @Inject(method = "loadDefaultDataFromBucketTag", at = @At("TAIL"))
    private static void loadVariantDataFromBucketTag(Mob mob, CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("Variants")) {
            ArrayList<ResourceLocation> variants = new ArrayList<>();
            for (String variant : Arrays.stream(tag.getString("Variants").split(", ")).toList()) {
                Optional.ofNullable(ResourceLocation.tryParse(variant)).map(variants::add);
            }

            if (!variants.isEmpty()) mob.setData(DataAttachmentTypes.VARIANTS, variants);
        }
    }
}
