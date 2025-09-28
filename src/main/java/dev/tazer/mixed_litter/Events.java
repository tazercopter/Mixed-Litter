package dev.tazer.mixed_litter;

import dev.tazer.mixed_litter.registry.MLDataAttachmentTypes;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import static dev.tazer.mixed_litter.VariantUtil.*;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = MixedLitter.MODID)
public class Events {

    @SubscribeEvent
    public static void onEntitySpawned(FinalizeSpawnEvent event) {
        Entity entity = event.getEntity();

        if (!entity.level().isClientSide) {
            entity.setData(MLDataAttachmentTypes.SPAWN_LOCATION, GlobalPos.of(entity.level().dimension(), entity.blockPosition()));
            applySuitableVariants(entity);
        }
    }

    @SubscribeEvent
    public static void onEntityLoaded(EntityJoinLevelEvent event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            validateVariants(event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onBabyEntitySpawned(BabyEntitySpawnEvent event) {
        AgeableMob child = event.getChild();

        if (child != null && !child.level().isClientSide) {
            Mob parentA = event.getParentA();
            Mob parentB = event.getParentB();

            GlobalPos spawnLocation = GlobalPos.of(child.level().dimension(), child.blockPosition());

            if (parentA.hasData(MLDataAttachmentTypes.SPAWN_LOCATION)) spawnLocation = parentA.getData(MLDataAttachmentTypes.SPAWN_LOCATION);
            else if (parentB.hasData(MLDataAttachmentTypes.SPAWN_LOCATION)) spawnLocation = parentB.getData(MLDataAttachmentTypes.SPAWN_LOCATION);

            child.setData(MLDataAttachmentTypes.SPAWN_LOCATION, spawnLocation);
            setChildVariant(parentA, parentB, child);
        }
    }

    @SubscribeEvent
    public static void updateVariant(EntityTickEvent.Pre event) {
        if (!event.getEntity().level().isClientSide && event.getEntity().tickCount % 20 == 0) validateVariants(event.getEntity());
    }
}
