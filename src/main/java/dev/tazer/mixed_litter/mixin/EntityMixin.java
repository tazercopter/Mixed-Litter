package dev.tazer.mixed_litter.mixin;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;

@Mixin(Entity.class)
public abstract class EntityMixin extends AttachementHolderMixin {

    @Shadow public abstract int getId();

    @Shadow @Nullable public abstract <T> T setData(AttachmentType<T> type, T data);

    @Shadow @Nullable public abstract ItemEntity spawnAtLocation(ItemStack stack, float offsetY);

    @Shadow public abstract float getBbHeight();

    @Shadow @Nullable public abstract ItemEntity spawnAtLocation(ItemLike item);

    @Shadow public abstract boolean isInvulnerable();

    @Shadow public abstract boolean isCustomNameVisible();

    @Shadow @Nullable public abstract Component getCustomName();

    @Shadow public abstract boolean hasCustomName();

    @Shadow public abstract double getY(double scale);

    @Shadow public abstract void discard();

    @Shadow public abstract float getXRot();

    @Shadow public abstract float getYRot();

    @Shadow public abstract double getZ();

    @Shadow public abstract double getY();

    @Shadow public abstract double getX();

    @Shadow public abstract Level level();

    @Shadow @Final protected RandomSource random;

    @Shadow public abstract SynchedEntityData getEntityData();

    @Shadow @Final protected SynchedEntityData entityData;

    @Shadow public abstract EntityType<?> getType();

    @Shadow public abstract RegistryAccess registryAccess();
}
