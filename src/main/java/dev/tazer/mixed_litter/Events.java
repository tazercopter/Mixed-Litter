package dev.tazer.mixed_litter;

import dev.tazer.mixed_litter.registry.MLDataAttachmentTypes;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;

import static dev.tazer.mixed_litter.VariantUtil.*;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = MixedLitter.MODID)
public class Events {

    @SubscribeEvent
    public static void onTagsUpdated(TagsUpdatedEvent event) {
        invalidateCaches();
    }

    @SubscribeEvent
    public static void onEntitySpawned(FinalizeSpawnEvent event) {
        Entity entity = event.getEntity();

        if (!entity.level().isClientSide) {
            applySuitableVariants(entity);
        }
    }

    @SubscribeEvent
    public static void onEntityLoaded(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide) return;
        Entity entity = event.getEntity();

        validateVariants(entity);

        if (!event.loadedFromDisk() && !entity.hasData(MLDataAttachmentTypes.VARIANTS)) {
            applySuitableVariants(entity);
        }
    }

    @SubscribeEvent
    public static void onBabyEntitySpawned(BabyEntitySpawnEvent event) {
        AgeableMob child = event.getChild();

        if (child != null && !child.level().isClientSide) {
            applyChildVariants(event.getParentA(), event.getParentB(), child);
        }
    }
}
