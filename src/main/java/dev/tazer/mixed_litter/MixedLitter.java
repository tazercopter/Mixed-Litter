package dev.tazer.mixed_litter;

import dev.tazer.mixed_litter.registry.MLActionTypes;
import dev.tazer.mixed_litter.registry.MLDataAttachmentTypes;
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

@Mod(MixedLitter.MODID)
public class MixedLitter {
    public static final String MODID = "mixed_litter";

    public MixedLitter(IEventBus modEventBus, ModContainer modContainer) {

        modEventBus.addListener(this::registerRegistries);
        modEventBus.addListener(this::registerDatapackRegistries);

        MLDataAttachmentTypes.ATTACHMENT_TYPES.register(modEventBus);
        MLActionTypes.ACTION_TYPES.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
    }

    private void registerRegistries(final NewRegistryEvent event) {
        event.register(MLRegistries.VARIANT_ACTION_TYPES);
    }

    private void registerDatapackRegistries(final DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(MLRegistries.VARIANT_TYPE_KEY, VariantType.CODEC, VariantType.CODEC);
        event.dataPackRegistry(MLRegistries.VARIANT_GROUP_KEY, VariantGroup.CODEC, VariantGroup.CODEC);
        event.dataPackRegistry(MLRegistries.VARIANT_KEY, Variant.CODEC, Variant.CODEC);
    }

    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}