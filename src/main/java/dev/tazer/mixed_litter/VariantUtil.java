package dev.tazer.mixed_litter;

import com.mojang.serialization.MapCodec;
import dev.tazer.mixed_litter.networking.VariantData;
import dev.tazer.mixed_litter.networking.VariantRequestData;
import dev.tazer.mixed_litter.variants.MobVariant;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;
import java.util.stream.Collectors;

public class VariantUtil {
    public static HolderSet<MobVariant> getVariants(Mob mob, LevelAccessor levelAccessor) {
        List<Holder<MobVariant>> animalVariantHolderSet = new ArrayList<>();
        Registry<MobVariant> variantRegistry = levelAccessor.registryAccess().registryOrThrow(MLRegistries.ANIMAL_VARIANT_KEY);
        if (levelAccessor.isClientSide()) {
            CompoundTag tag = new CompoundTag();
            mob.save(tag);
            if (!mob.hasData(MLDataAttachmentTypes.MOB_VARIANTS)) PacketDistributor.sendToServer(new VariantRequestData(mob.getId(), tag));
        }

        for (int i = 0; i < mob.getData(MLDataAttachmentTypes.MOB_VARIANTS).split(", ").length; i++)
            variantRegistry.getHolder(ResourceLocation.parse(mob.getData(MLDataAttachmentTypes.MOB_VARIANTS).split(", ")[i])).map(animalVariantHolderSet::add);

        return HolderSet.direct(animalVariantHolderSet);
    }

    public static void setVariants(Mob mob, ServerLevelAccessor levelAccessor, MobVariant... variants) {
        List<String> animalVariants = new ArrayList<>();
        Registry<MobVariant> variantRegistry = levelAccessor.registryAccess().registryOrThrow(MLRegistries.ANIMAL_VARIANT_KEY);
        Arrays.stream(variants).forEach(variant ->
                Optional.ofNullable(variantRegistry.getKey(variant)).map(ResourceLocation::toString).map(animalVariants::add));

        String variantString = String.join(", ", animalVariants);
        mob.setData(MLDataAttachmentTypes.MOB_VARIANTS, variantString);

        CompoundTag tag = new CompoundTag();
        mob.save(tag);
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(mob, new VariantData(mob.getId(), tag, variantString));
    }

    public static void setVariants(Mob mob, ServerLevelAccessor levelAccessor, List<MobVariant> variants) {
        List<String> animalVariants = new ArrayList<>();
        Registry<MobVariant> variantRegistry = levelAccessor.registryAccess().registryOrThrow(MLRegistries.ANIMAL_VARIANT_KEY);
        variants.forEach(variant ->
                Optional.ofNullable(variantRegistry.getKey(variant)).map(ResourceLocation::toString).map(animalVariants::add));

        String variantString = String.join(", ", animalVariants);
        mob.setData(MLDataAttachmentTypes.MOB_VARIANTS, variantString);

        CompoundTag tag = new CompoundTag();
        mob.save(tag);
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(mob, new VariantData(mob.getId(), tag, variantString));
    }

    public static void setVariants(Mob mob, HolderSet<MobVariant> variants) {
        List<String> animalVariants = new ArrayList<>();
        variants.forEach(variant ->
                variant.unwrapKey().map(ResourceKey::location).map(ResourceLocation::toString).map(animalVariants::add));

        String variantString = String.join(", ", animalVariants);
        mob.setData(MLDataAttachmentTypes.MOB_VARIANTS, variantString);

        CompoundTag tag = new CompoundTag();
        mob.save(tag);
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(mob, new VariantData(mob.getId(), tag, variantString));
    }

    public static void setChildVariant(Mob parentA, Mob parentB, Mob child, ServerLevelAccessor levelAccessor) {
        Registry<MobVariant> variantTypeRegistry = levelAccessor.registryAccess().registryOrThrow(MLRegistries.ANIMAL_VARIANT_KEY);

        Map<MapCodec<? extends MobVariant>, List<MobVariant>> groupedVariants = new HashMap<>();
        variantTypeRegistry.holders()
                .filter(animalVariantReference -> animalVariantReference.value().isFor(child.getType()))
                .forEach(animalVariantReference ->
                        groupedVariants.computeIfAbsent(animalVariantReference.value().codec(), mapCodec -> new ArrayList<>()).add(animalVariantReference.value())
                );

        List<MobVariant> AVariants = new ArrayList<>();
        List<MobVariant> BVariants = new ArrayList<>();

        for (Holder<MobVariant> animalVariantHolder: getVariants(parentA, levelAccessor).stream().toList()) {
            AVariants.add(animalVariantHolder.value());
        }

        for (Holder<MobVariant> animalVariantHolder: getVariants(parentB, levelAccessor).stream().toList()) {
            BVariants.add(animalVariantHolder.value());
        }

        List<MobVariant> childVariants = new ArrayList<>();

        for (List<MobVariant> possibleVariants : groupedVariants.values()) {
            List<MobVariant> availableVariants = new ArrayList<>();
            for (MobVariant variant : possibleVariants) {
                if (AVariants.contains(variant) || BVariants.contains(variant)) {
                    availableVariants.add(variant);
                }
            }

            if (!availableVariants.isEmpty()) {
                MobVariant chosenVariant = availableVariants.get(child.getRandom().nextInt(availableVariants.size()));
                childVariants.add(chosenVariant);
            }
        }

        setVariants(child, levelAccessor, childVariants);
    }

    public static void applySuitableVariants(Mob mob, ServerLevelAccessor levelAccessor) {
        Registry<MobVariant> variantTypeRegistry = levelAccessor.registryAccess().registryOrThrow(MLRegistries.ANIMAL_VARIANT_KEY);
        Map<MapCodec<? extends MobVariant>, List<MobVariant>> groupedAnimals = new HashMap<>();
        variantTypeRegistry.holders()
                .filter(animalVariantReference -> animalVariantReference.value().isFor(mob.getType()))
                .forEach(animalVariantReference ->
                        groupedAnimals.computeIfAbsent(animalVariantReference.value().codec(), mapCodec -> new ArrayList<>()).add(animalVariantReference.value()));

        if (!groupedAnimals.isEmpty()) {
            List<MobVariant> selectedVariants = new ArrayList<>();
            for (List<MobVariant> variants : groupedAnimals.values())
                selectedVariants.add(variants.getFirst().select(mob, levelAccessor, variants));

            setVariants(mob, levelAccessor, selectedVariants);
        }
    }

    public static void validateVariants(Mob mob, ServerLevelAccessor levelAccessor) {
        Registry<MobVariant> variantRegistry = levelAccessor.registryAccess().registryOrThrow(MLRegistries.ANIMAL_VARIANT_KEY);

        // Group variants by their codec (variant type)
        Map<MapCodec<? extends MobVariant>, List<MobVariant>> groupedVariants = new HashMap<>();
        variantRegistry.holders()
                .filter(ref -> ref.value().isFor(mob.getType()))
                .forEach(ref -> groupedVariants
                        .computeIfAbsent(ref.value().codec(), codec -> new ArrayList<>())
                        .add(ref.value()));

        // Get the mob's currently applied variants
        List<MobVariant> currentVariants = new ArrayList<>();

        for (Holder<MobVariant> animalVariantHolder: getVariants(mob, levelAccessor).stream().toList()) {
            currentVariants.add(animalVariantHolder.value());
        }

        // Keep only valid current variants (whose codec still exists and is valid for this mob)
        List<MobVariant> updatedVariants = currentVariants.stream()
                .filter(variant -> {
                    MapCodec<? extends MobVariant> codec = variant.codec();
                    List<MobVariant> validVariants = groupedVariants.get(codec);
                    return validVariants != null && validVariants.contains(variant);
                })
                .collect(Collectors.toList());

        // Apply missing variants (for codecs that the mob has none of)
        for (Map.Entry<MapCodec<? extends MobVariant>, List<MobVariant>> entry : groupedVariants.entrySet()) {
            MapCodec<? extends MobVariant> codec = entry.getKey();
            boolean alreadyPresent = updatedVariants.stream().anyMatch(v -> v.codec().equals(codec));
            if (!alreadyPresent) {
                MobVariant newVariant = entry.getValue().getFirst().select(mob, levelAccessor, entry.getValue());
                updatedVariants.add(newVariant);
            }
        }

        // Set the new variant list
        setVariants(mob, levelAccessor, updatedVariants);
    }
}
