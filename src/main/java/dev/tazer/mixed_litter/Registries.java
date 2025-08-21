package dev.tazer.mixed_litter;

import dev.tazer.mixed_litter.actions.ActionType;
import dev.tazer.mixed_litter.variants.Variant;
import dev.tazer.mixed_litter.variants.VariantGroup;
import dev.tazer.mixed_litter.variants.VariantType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class Registries {

    public static final ResourceKey<Registry<ActionType>> ACTION_TYPES_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MixedLitter.MODID, "action_types"));
    public static final Registry<ActionType> ACTION_TYPES = new RegistryBuilder<>(ACTION_TYPES_KEY).create();

    public static final ResourceKey<Registry<VariantType>> VARIANT_TYPE_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MixedLitter.MODID, "variant_types"));
    public static final ResourceKey<Registry<VariantGroup>> VARIANT_GROUP_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MixedLitter.MODID, "variant_groups"));
    public static final ResourceKey<Registry<Variant>> VARIANT_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MixedLitter.MODID, "variants"));

}
