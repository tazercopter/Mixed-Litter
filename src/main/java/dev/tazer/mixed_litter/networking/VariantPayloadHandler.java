package dev.tazer.mixed_litter.networking;

import dev.tazer.mixed_litter.MLDataAttachmentTypes;
import dev.tazer.mixed_litter.VariantUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class VariantPayloadHandler {
    public static void handleData(final VariantData data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Entity entity = context.player().level().getEntity(data.id());

            if (entity == null)
                entity = EntityType.loadEntityRecursive(data.tag(), context.player().level(), e -> e);

            if (entity instanceof Mob mob) {
                mob.setData(MLDataAttachmentTypes.MOB_VARIANTS, data.variants());
                mob.setData(MLDataAttachmentTypes.SUB_VARIANT, data.subvariant());
            }
        });
    }

    public static void handleRequestForData(final VariantRequestData data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Entity entity = context.player().level().getEntity(data.id());

            if (entity == null)
                entity = EntityType.loadEntityRecursive(data.tag(), context.player().level(), e -> e);


            if (entity instanceof Mob mob) {
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(mob, VariantUtil.getVariantData(mob));
            }
        });
    }
}
