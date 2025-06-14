package dev.tazer.mixed_litter;

import dev.tazer.mixed_litter.networking.VariantData;
import dev.tazer.mixed_litter.networking.VariantPayloadHandler;
import dev.tazer.mixed_litter.networking.VariantRequestData;
import dev.tazer.mixed_litter.variants.MobVariant;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(MixedLitter.MODID)
public class MixedLitter {
    public static final String MODID = "mixed_litter";
    public static final Logger LOGGER = LogManager.getLogger("Mixed Litter");

    public MixedLitter(IEventBus modEventBus, ModContainer modContainer) {

        modEventBus.addListener(this::registerRegistries);
        modEventBus.addListener(this::registerDatapackRegistries);
        modEventBus.addListener(this::registerPayloadHandlers);

        MobVariantTypes.VARIANT_TYPES.register(modEventBus);
        MLDataAttachmentTypes.ATTACHMENT_TYPES.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.CLIENT, MLConfig.CLIENT_CONFIG);
    }

    public void registerPayloadHandlers(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1").executesOn(HandlerThread.MAIN);
        registrar.playToClient(
                VariantData.TYPE,
                VariantData.STREAM_CODEC,
                VariantPayloadHandler::handleData
        );

        registrar.playToServer(
                VariantRequestData.TYPE,
                VariantRequestData.STREAM_CODEC,
                VariantPayloadHandler::handleRequestForData
        );
    }

    private void registerRegistries(final NewRegistryEvent event) {
        event.register(MLRegistries.ANIMAL_VARIANT_TYPE);
    }

    private void registerDatapackRegistries(final DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(MLRegistries.ANIMAL_VARIANT_KEY, MobVariant.DIRECT_CODEC, MobVariant.DIRECT_CODEC);
    }
}