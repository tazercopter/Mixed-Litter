package dev.tazer.mixed_litter.networking;

import dev.tazer.mixed_litter.MLDataAttachementTypes;
import dev.tazer.mixed_litter.VariantDataHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class VariantPayloadHandler {
    public static void handleData(final VariantData data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Entity entity = null;

            entity = context.player().level().getEntity(data.id());

            if (entity == null) {
                entity = EntityType.loadEntityRecursive(data.tag(), context.player().level(), e -> e);
            }

            if (entity instanceof Mob mob) {
                mob.setData(MLDataAttachementTypes.MOB_VARIANTS, data.variants());
            }
        });
    }

    public static void handleRequestForData(final VariantRequestData data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Entity entity = null;

            entity = context.player().level().getEntity(data.id());

            if (entity == null) {
                 entity = EntityType.loadEntityRecursive(data.tag(), context.player().level(), e -> e);
            }

            if (entity instanceof Mob mob) {
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(mob, new VariantData(data.id(), data.tag(), mob.getData(MLDataAttachementTypes.MOB_VARIANTS)));
            }

        });
    }
}
