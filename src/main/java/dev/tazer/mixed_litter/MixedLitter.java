package dev.tazer.mixed_litter;

import dev.tazer.mixed_litter.variants.Variant;
import dev.tazer.mixed_litter.variants.VariantGroup;
import dev.tazer.mixed_litter.variants.VariantType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
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

        DataAttachmentTypes.ATTACHMENT_TYPES.register(modEventBus);
        ActionTypes.ACTION_TYPES.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.CLIENT, MLConfig.CLIENT_CONFIG);
        // todo: variant priorities somehow? like an overlay variant that does its actions last? or maybe variants with more predicates get applied last
    }

    private void registerRegistries(final NewRegistryEvent event) {
        event.register(Registries.ACTION_TYPES);
    }

    private void registerDatapackRegistries(final DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(Registries.VARIANT_TYPE_KEY, VariantType.CODEC, VariantType.CODEC);
        event.dataPackRegistry(Registries.VARIANT_GROUP_KEY, VariantGroup.CODEC, VariantGroup.CODEC);
        event.dataPackRegistry(Registries.VARIANT_KEY, Variant.CODEC, Variant.CODEC);
    }

    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}