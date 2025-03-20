package dev.tazer.mixed_litter;

import com.mojang.serialization.MapCodec;
import dev.tazer.mixed_litter.networking.VariantData;
import dev.tazer.mixed_litter.variants.MobVariant;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;

public class VariantUtil {
    public static HolderSet<MobVariant> getVariants(Mob mob) {
        List<Holder<MobVariant>> animalVariantHolderSet = new ArrayList<>();
        Registry<MobVariant> variantRegistry = mob.registryAccess().registryOrThrow(MLRegistries.ANIMAL_VARIANT_KEY);
        if (mob.level().isClientSide) {
            for (int i = 0; i < ((VariantDataHolder) mob).mixedLitter$getVariantData().split(", ").length; i++)
                variantRegistry.getHolder(ResourceLocation.parse(((VariantDataHolder) mob).mixedLitter$getVariantData().split(", ")[i])).map(animalVariantHolderSet::add);
        } else {
            for (int i = 0; i < mob.getData(MLDataAttachementTypes.MOB_VARIANTS).split(", ").length; i++)
                variantRegistry.getHolder(ResourceLocation.parse(mob.getData(MLDataAttachementTypes.MOB_VARIANTS).split(", ")[i])).map(animalVariantHolderSet::add);
        }
        return HolderSet.direct(animalVariantHolderSet);
    }

    public static void setVariants(Mob mob, MobVariant... variants) {
        List<String> animalVariants = new ArrayList<>();
        Registry<MobVariant> variantRegistry = mob.registryAccess().registryOrThrow(MLRegistries.ANIMAL_VARIANT_KEY);
        Arrays.stream(variants).forEach(variant ->
                Optional.ofNullable(variantRegistry.getKey(variant)).map(ResourceLocation::toString).map(animalVariants::add));

        String variantString = String.join(", ", animalVariants);
        mob.setData(MLDataAttachementTypes.MOB_VARIANTS, variantString);
        PacketDistributor.sendToPlayersTrackingEntity(mob, new VariantData(mob.getId(), variantString));
    }

    public static void setVariants(Mob mob, List<MobVariant> variants) {
        List<String> animalVariants = new ArrayList<>();
        Registry<MobVariant> variantRegistry = mob.registryAccess().registryOrThrow(MLRegistries.ANIMAL_VARIANT_KEY);
        variants.forEach(variant ->
                Optional.ofNullable(variantRegistry.getKey(variant)).map(ResourceLocation::toString).map(animalVariants::add));

        String variantString = String.join(", ", animalVariants);
        mob.setData(MLDataAttachementTypes.MOB_VARIANTS, variantString);
        PacketDistributor.sendToPlayersTrackingEntity(mob, new VariantData(mob.getId(), variantString));
    }

    public static void setVariants(Mob mob, HolderSet<MobVariant> variants) {
        List<String> animalVariants = new ArrayList<>();
        variants.forEach(variant ->
                variant.unwrapKey().map(ResourceKey::location).map(ResourceLocation::toString).map(animalVariants::add));

        String variantString = String.join(", ", animalVariants);
        mob.setData(MLDataAttachementTypes.MOB_VARIANTS, variantString);
        PacketDistributor.sendToPlayersTrackingEntity(mob, new VariantData(mob.getId(), variantString));
    }

    public static void setChildVariant(Mob parentA, Mob parentB, Mob child) {
        Registry<MobVariant> variantTypeRegistry = child.registryAccess().registryOrThrow(MLRegistries.ANIMAL_VARIANT_KEY);

        Map<MapCodec<? extends MobVariant>, List<MobVariant>> groupedVariants = new HashMap<>();
        variantTypeRegistry.holders()
                .filter(animalVariantReference -> animalVariantReference.value().isFor(child.getType()))
                .forEach(animalVariantReference ->
                        groupedVariants.computeIfAbsent(animalVariantReference.value().codec(), mapCodec -> new ArrayList<>()).add(animalVariantReference.value())
                );

        List<MobVariant> AVariants = new ArrayList<>();
        List<MobVariant> BVariants = new ArrayList<>();

        for (Holder<MobVariant> animalVariantHolder: getVariants(parentA).stream().toList()) {
            AVariants.add(animalVariantHolder.value());
        }

        for (Holder<MobVariant> animalVariantHolder: getVariants(parentB).stream().toList()) {
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

        setVariants(child, childVariants);
    }

    public static void applySuitableVariants(Mob mob) {
        Registry<MobVariant> variantTypeRegistry = mob.registryAccess().registryOrThrow(MLRegistries.ANIMAL_VARIANT_KEY);
        Map<MapCodec<? extends MobVariant>, List<MobVariant>> groupedAnimals = new HashMap<>();
        variantTypeRegistry.holders()
                .filter(animalVariantReference -> animalVariantReference.value().isFor(mob.getType()))
                .forEach(animalVariantReference ->
                        groupedAnimals.computeIfAbsent(animalVariantReference.value().codec(), mapCodec -> new ArrayList<>()).add(animalVariantReference.value()));

        if (!groupedAnimals.isEmpty()) {
            List<MobVariant> selectedVariants = new ArrayList<>();
            for (List<MobVariant> variants : groupedAnimals.values())
                selectedVariants.add(variants.getFirst().select(mob, mob.level(), variants));

            setVariants(mob, selectedVariants);
        }
    }
}
