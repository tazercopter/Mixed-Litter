package dev.tazer.mixed_litter.mixin;

import dev.tazer.mixed_litter.registry.MLDataAttachmentTypes;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
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

@Mixin(Bucketable.class)
public interface BucketableMixin {
    @Inject(method = "saveDefaultDataToBucketTag", at = @At("TAIL"))
    private static void saveVariantDataToBucketTag(Mob mob, ItemStack bucket, CallbackInfo ci) {
        CustomData.update(DataComponents.BUCKET_ENTITY_DATA, bucket, (tag) -> {
            if (mob.hasData(MLDataAttachmentTypes.VARIANTS)) {
                ListTag list = new ListTag();
                for (ResourceLocation id : mob.getData(MLDataAttachmentTypes.VARIANTS)) {
                    list.add(StringTag.valueOf(id.toString()));
                }
                tag.put("Variants", list);
            }
        });
    }

    @Inject(method = "loadDefaultDataFromBucketTag", at = @At("TAIL"))
    private static void loadVariantDataFromBucketTag(Mob mob, CompoundTag tag, CallbackInfo ci) {
        if (!tag.contains("Variants", Tag.TAG_LIST)) return;

        ListTag list = tag.getList("Variants", Tag.TAG_STRING);
        ArrayList<ResourceLocation> variants = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            ResourceLocation rl = ResourceLocation.tryParse(list.getString(i));
            if (rl != null) variants.add(rl);
        }

        if (!variants.isEmpty()) mob.setData(MLDataAttachmentTypes.VARIANTS, variants);
    }
}
