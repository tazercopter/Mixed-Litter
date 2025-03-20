package dev.tazer.mixed_litter;

import dev.tazer.mixed_litter.networking.RequestVariantData;
import dev.tazer.mixed_litter.networking.VariantData;
import dev.tazer.mixed_litter.networking.VariantPayloadHandler;
import dev.tazer.mixed_litter.variants.MobVariant;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;

@Mod(MixedLitter.MODID)
public class MixedLitter {
    public static final String MODID = "mixed_litter";

    public MixedLitter(IEventBus modEventBus, ModContainer modContainer) {

        modEventBus.addListener(this::registerRegistries);
        modEventBus.addListener(this::registerDatapackRegistries);
        modEventBus.addListener(this::registerPayloadHandlers);

        MobVariantTypes.VARIANT_TYPES.register(modEventBus);
        MLDataAttachementTypes.ATTACHMENT_TYPES.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.STARTUP, MLConfig.STARTUP_CONFIG);
    }

    public void registerPayloadHandlers(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(
                VariantData.TYPE,
                VariantData.STREAM_CODEC,
                VariantPayloadHandler::handleData
        );

        registrar.playToServer(
                RequestVariantData.TYPE,
                RequestVariantData.STREAM_CODEC,
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