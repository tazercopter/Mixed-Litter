package dev.tazer.mixed_litter;

import dev.tazer.mixed_litter.networking.VariantData;
import dev.tazer.mixed_litter.variants.DynamicVariant;
import dev.tazer.mixed_litter.variants.MobVariant;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.ServerLevelAccessor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

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
    public static void onEntityLoaded(EntityJoinLevelEvent event) {
        if (event.getLevel() instanceof ServerLevel serverLevel && event.getEntity() instanceof Mob mob)
            validateVariants(mob, serverLevel);
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

    @SubscribeEvent
    public static void updateSubVariant(EntityTickEvent.Pre event) {
        Entity entity = event.getEntity();

        if (entity instanceof Mob mob && entity.tickCount % 5 == 0 && !entity.level().isClientSide) {
            List<MobVariant> variants = VariantUtil.getVariants(mob, entity.level()).stream().map(Holder::value).toList();
            String subVariant = mob.getData(MLDataAttachmentTypes.SUB_VARIANT);
            String newSubVariant = "";
            for (MobVariant variant : variants) {
                if (variant instanceof DynamicVariant dynamic) {
                    newSubVariant = dynamic.fetchTexture(mob).map(ResourceLocation::toString).orElse("");
                    if (!newSubVariant.isEmpty()) break;
                }
            }

            if (!newSubVariant.equals(subVariant)) {
                // Sync variant data
                CompoundTag tag = new CompoundTag();
                entity.save(tag);
                String variantsString = entity.getData(MLDataAttachmentTypes.MOB_VARIANTS);
                PacketDistributor.sendToPlayersTrackingEntity(entity, new VariantData(entity.getId(), tag, variantsString, newSubVariant));
            }
        }
    }

    @SubscribeEvent
    public static void sendVariantsToTrackers(PlayerEvent.StartTracking event) {
        if (event.getEntity() instanceof ServerPlayer player
        && event.getTarget() instanceof Mob mob) {
            PacketDistributor.sendToPlayer(player, VariantUtil.getVariantData(mob));
        }
    }
}
