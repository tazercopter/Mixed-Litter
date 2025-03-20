package dev.tazer.mixed_litter;

import com.mojang.serialization.MapCodec;
import dev.tazer.mixed_litter.variants.MobVariant;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class MLRegistries {

    public static final ResourceKey<Registry<MapCodec<? extends MobVariant>>> ANIMAL_VARIANT_TYPE_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MixedLitter.MODID, "mob_variant_type"));
    public static final Registry<MapCodec<? extends MobVariant>> ANIMAL_VARIANT_TYPE = new RegistryBuilder<>(ANIMAL_VARIANT_TYPE_KEY).create();

    public static final ResourceKey<Registry<MobVariant>> ANIMAL_VARIANT_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MixedLitter.MODID, "mob_variants"));
}
