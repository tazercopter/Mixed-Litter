package dev.tazer.mixed_litter.networking;

import dev.tazer.mixed_litter.MLDataAttachementTypes;
import dev.tazer.mixed_litter.VariantDataHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class VariantPayloadHandler {
    public static void handleData(final VariantData data, final IPayloadContext context) {
        Entity entity = context.player().level().getEntity(data.mobId());
        if (entity instanceof Mob mob) ((VariantDataHolder) mob).mixedLitter$setVariantData(data.variants());
    }

    public static void handleRequestForData(final RequestVariantData data, final IPayloadContext context) {
        Entity entity = context.player().level().getEntity(data.mobId());
        if (entity instanceof Mob mob) {
            PacketDistributor.sendToPlayersTrackingEntity(mob, new VariantData(data.mobId(), mob.getData(MLDataAttachementTypes.MOB_VARIANTS)));
        }
    }
}
