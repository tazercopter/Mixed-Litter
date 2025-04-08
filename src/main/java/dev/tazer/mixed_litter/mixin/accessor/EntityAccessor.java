package dev.tazer.mixed_litter.mixin.accessor;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityAccessor {

    @Invoker("spawnAtLocation")
    ItemEntity spawnAtLocation(ItemStack stack, float offsetY);

    @Invoker("getBbHeight")
    float getBbHeight();

    @Invoker("isInvulnerable")
    boolean isInvulnerable();

    @Invoker("isCustomNameVisible")
    boolean isCustomNameVisible();

    @Invoker("hasCustomName")
    boolean hasCustomName();

    @Invoker("getY")
    double getY(double scale);

    @Invoker("discard")
    void discard();

    @Invoker("getXRot")
    float getXRot();

    @Invoker("getYRot")
    float getYRot();

    @Invoker("getZ")
    double getZ();

    @Invoker("getY")
    double getY();

    @Invoker("getX")
    double getX();

    @Invoker("level")
    Level level();

    @Invoker("registryAccess")
    RegistryAccess registryAccess();

    @Invoker("getType")
    EntityType<?> getType();

    @Invoker("getRandom")
    RandomSource getRandom();

    @Invoker("getCustomName")
    Component getCustomName();
}
