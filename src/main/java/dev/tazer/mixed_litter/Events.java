package dev.tazer.mixed_litter;

import dev.tazer.mixed_litter.variants.MobVariant;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.ServerLevelAccessor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;

import static dev.tazer.mixed_litter.VariantUtil.*;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = MixedLitter.MODID)
public class Events {

    @SubscribeEvent
    public static void onEntitySpawned(FinalizeSpawnEvent event) {
        Mob mob = event.getEntity();
        ServerLevelAccessor levelAccessor = event.getLevel();

        applySuitableVariants(mob, levelAccessor);
    }

    @SubscribeEvent
    public static void onEntitySpawned(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide && event.getEntity() instanceof Mob mob && event.getLevel() instanceof ServerLevel serverLevel) {
            validateVariants(mob, serverLevel);
        }
    }

    @SubscribeEvent
    public static void onBabyEntitySpawned(BabyEntitySpawnEvent event) {
        AgeableMob child = event.getChild();

        if (child != null && child.level() instanceof ServerLevel serverLevel) {
            Mob parentA = event.getParentA();
            Mob parentB = event.getParentB();

            setChildVariant(parentA, parentB, child, serverLevel);
        }
    }
}
