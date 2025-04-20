package dev.tazer.mixed_litter.mixin.accessor;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityAccessor {

    @Invoker("spawnAtLocation")
    ItemEntity invokeSpawnAtLocation(ItemStack stack, float offsetY);

    @Invoker("getBbHeight")
    float invokeGetBbHeight();

    @Invoker("isInvulnerable")
    boolean invokeIsInvulnerable();

    @Invoker("isCustomNameVisible")
    boolean invokeIsCustomNameVisible();

    @Invoker("hasCustomName")
    boolean invokeHasCustomName();

    @Invoker("getY")
    double invokeGetY(double scale);

    @Invoker("discard")
    void invokeDiscard();

    @Invoker("getXRot")
    float invokeGetXRot();

    @Invoker("getYRot")
    float invokeGetYRot();

    @Invoker("getZ")
    double invokeGetZ();

    @Invoker("getY")
    double invokeGetY();

    @Invoker("getX")
    double invokeGetX();

    @Invoker("level")
    Level invokeLevel();

    @Invoker("registryAccess")
    RegistryAccess invokeRegistryAccess();

    @Invoker("getType")
    EntityType<?> invokeGetType();

    @Invoker("getRandom")
    RandomSource invokeGetRandom();

    @Invoker("getCustomName")
    Component invokeGetCustomName();

    @Accessor
    AABB getBb();

    @Accessor
    float getEyeHeight();
}
