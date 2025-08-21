package dev.tazer.mixed_litter.client;

import dev.tazer.mixed_litter.MixedLitter;
import dev.tazer.mixed_litter.models.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = MixedLitter.MODID, value = Dist.CLIENT)
public class Events {
    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModelLayers.PIG_LAYER, PigRemodel::createBodyLayer);
        event.registerLayerDefinition(ModelLayers.CHICKEN_LAYER, ChickenRemodel::createBodyLayer);
        event.registerLayerDefinition(ModelLayers.COW_LAYER, CowRemodel::createBodyLayer);
        event.registerLayerDefinition(ModelLayers.SHEEP_LAYER, SheepRemodel::createBodyLayer);
        event.registerLayerDefinition(ModelLayers.SQUID_LAYER, SquidRemodel::createBodyLayer);
        event.registerLayerDefinition(ModelLayers.RABBIT_LAYER, RabbitRemodel::createBodyLayer);
    }
}
