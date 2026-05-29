package dev.tazer.mixed_litter.client;

import dev.tazer.mixed_litter.Config;
import dev.tazer.mixed_litter.MixedLitter;
import dev.tazer.mixed_litter.client.models.*;
import dev.tazer.mixed_litter.client.renderers.RemodelMarker;
import dev.tazer.mixed_litter.client.renderers.RemodelRenderers;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;

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

    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        RemodelRenderers.clear();
    }

    @SubscribeEvent
    public static void onRenderLivingPre(RenderLivingEvent.Pre<?, ?> event) {
        if (!Config.STARTUP_CONFIG.isLoaded()) return;

        LivingEntityRenderer<?, ?> vanilla = event.getRenderer();
        if (vanilla instanceof RemodelMarker) return;

        LivingEntity entity = event.getEntity();
        LivingEntityRenderer custom = RemodelRenderers.get(entity, vanilla);
        if (custom == null) return;

        float entityYaw = Mth.lerp(event.getPartialTick(), entity.yRotO, entity.getYRot());
        custom.render(entity, entityYaw, event.getPartialTick(), event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight());
        event.setCanceled(true);
    }
}
