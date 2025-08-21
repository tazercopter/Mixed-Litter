package dev.tazer.mixed_litter;

import com.google.gson.JsonElement;
import dev.tazer.mixed_litter.variants.Variant;
import dev.tazer.mixed_litter.variants.VariantGroup;
import dev.tazer.mixed_litter.variants.VariantType;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;
import java.util.*;

public class VariantUtil {

    public static List<Variant> getVariants(Entity entity) {
        return lookupVariantIds(entity.getData(DataAttachmentTypes.VARIANTS), entity.registryAccess());
    }

    public static List<Variant> lookupVariantIds(List<ResourceLocation> variants, RegistryAccess access) {
        Registry<Variant> variantRegistry = access.registryOrThrow(Registries.VARIANT_KEY);
        ArrayList<Variant> variantList = new ArrayList<>(variants.size());
        for (ResourceLocation id : variants)
            variantRegistry.getOptional(id).ifPresent(variantList::add);

        return variantList;
    }

    public static Variant selectVariant(VariantGroup group, List<Variant> groupVariants, RandomSource random) {
        boolean spawningLocation = false;
        for (Variant variant : groupVariants) {
            if (variant.conditions().isPresent() && variant.conditions().get().spawningLocation().isPresent()) {
                spawningLocation = true;
                break;
            }
        }

        if (spawningLocation)
            groupVariants.removeIf(
                variant -> variant.conditions().isEmpty() || variant.conditions().get().spawningLocation().isEmpty()
            );

        return switch (group.selection()) {
            case UNIFORM -> groupVariants.get(random.nextInt(groupVariants.size()));
            case WEIGHTED -> {
                Variant chosen = null;
                int cumulative = 0;

                for (Variant variant : groupVariants) {
                    int weight = Optional.ofNullable(variant.arguments().get("weight"))
                            .map(JsonElement::getAsInt).orElse(1);

                    if (random.nextInt(cumulative + weight) < weight)
                        chosen = variant;
                    cumulative += weight;
                }

                yield chosen;
            }
        };
    }

    public static void applySuitableVariants(Entity entity) {
        ServerLevel serverLevel = (ServerLevel) entity.level();

        Registry<VariantGroup> variantGroupRegistry = entity.registryAccess().registryOrThrow(Registries.VARIANT_GROUP_KEY);
        Registry<Variant> variantRegistry = entity.registryAccess().registryOrThrow(Registries.VARIANT_KEY);
        ArrayList<Holder<Variant>> availableVariants = new ArrayList<>(variantRegistry.holders().toList());
        ArrayList<Variant> selectedVariants = new ArrayList<>();

        for (Holder<VariantGroup> variantGroupHolder : variantGroupRegistry.holders().toList()) {
            VariantGroup group = variantGroupHolder.value();

            if (group.conditions().isPresent() && !group.conditions().get().matches(serverLevel, entity.position(), entity))
                continue;

            ArrayList<Variant> matchingGroupVariants = new ArrayList<>();
            for (Holder<Variant> variantHolder : new ArrayList<>(availableVariants)) {
                Variant variant = variantHolder.value();

                variant.group().ifPresent(location -> {
                    if (variantGroupRegistry.get(location) == group) {
                        if (variant.conditions().isEmpty() || variant.conditions().get().matches(serverLevel, entity.position(), entity))
                            matchingGroupVariants.add(variant);
                        availableVariants.remove(variantHolder);
                    }
                });
            }

            if (!matchingGroupVariants.isEmpty() && group.exclusive()) {
                selectedVariants.add(selectVariant(group, matchingGroupVariants, entity.getRandom()));
            }
        }

        for (Holder<Variant> variantHolder : availableVariants) {
            Variant variant = variantHolder.value();
            if (variant.group().isEmpty()) {
                if (variant.conditions().isEmpty() || variant.conditions().get().matches(serverLevel, entity.position(), entity))
                    selectedVariants.add(variant);
            }
        }

        if (!selectedVariants.isEmpty()) {
            setVariants(entity, selectedVariants);
        }
    }

    public static void setVariants(Entity entity, List<Variant> variants) {
        List<ResourceLocation> variantLocations = new ArrayList<>();
        Registry<Variant> variantRegistry = entity.registryAccess().registryOrThrow(Registries.VARIANT_KEY);
        variants.forEach(variant -> Optional.ofNullable(variantRegistry.getKey(variant)).map(variantLocations::add));

        entity.setData(DataAttachmentTypes.VARIANTS, variantLocations);
    }

    public static void setChildVariant(Entity parentA, Entity parentB, Entity child) {
        List<Variant> AVariants = new ArrayList<>(getVariants(parentA));
        List<Variant> BVariants = new ArrayList<>(getVariants(parentB));

        ServerLevel serverLevel = (ServerLevel) child.level();

        Registry<VariantGroup> variantGroupRegistry = child.registryAccess().registryOrThrow(Registries.VARIANT_GROUP_KEY);
        Registry<Variant> variantRegistry = child.registryAccess().registryOrThrow(Registries.VARIANT_KEY);
        ArrayList<Holder<Variant>> availableVariants = new ArrayList<>(variantRegistry.holders().toList());
        ArrayList<Variant> childVariants = new ArrayList<>();

        for (Holder<VariantGroup> variantGroupHolder : variantGroupRegistry.holders().toList()) {
            VariantGroup group = variantGroupHolder.value();

            if (group.conditions().isPresent() && !group.conditions().get().matches(serverLevel, child.position(), child))
                continue;

            ArrayList<Variant> matchingGroupVariants = new ArrayList<>();
            for (Holder<Variant> variantHolder : new ArrayList<>(availableVariants)) {
                Variant variant = variantHolder.value();

                if (AVariants.contains(variant) || BVariants.contains(variant)) {
                    variant.group().ifPresent(location -> {
                        if (variantGroupRegistry.get(location) == group) {
                            if (variant.conditions().isEmpty() || variant.conditions().get().matches(serverLevel, child.position(), child))
                                matchingGroupVariants.add(variant);
                            availableVariants.remove(variantHolder);
                        }
                    });
                }
            }

            if (!matchingGroupVariants.isEmpty() && group.exclusive()) {
                childVariants.add(matchingGroupVariants.get(child.getRandom().nextInt(matchingGroupVariants.size())));
            }
        }

        for (Holder<Variant> variantHolder : availableVariants) {
            Variant variant = variantHolder.value();
            if ((AVariants.contains(variant) || BVariants.contains(variant)) && variant.group().isEmpty()) {
                if (variant.conditions().isEmpty() || variant.conditions().get().matches(serverLevel, child.position(), child))
                    childVariants.add(variantHolder.value());
            }
        }

        if (!childVariants.isEmpty()) {
            setVariants(child, childVariants);
        }
    }

    public static void validateVariants(Entity entity) {
        List<Variant> oldVariants = getVariants(entity);
        ArrayList<Variant> newVariants = new ArrayList<>(oldVariants);
        Registry<VariantGroup> variantGroupRegistry = entity.registryAccess().registryOrThrow(Registries.VARIANT_GROUP_KEY);
        Registry<Variant> variantRegistry = entity.registryAccess().registryOrThrow(Registries.VARIANT_KEY);

        ServerLevel serverLevel = (ServerLevel) entity.level();

        for (Variant variant : oldVariants) {
            if (variant.group().isPresent()) {
                VariantGroup group = variantGroupRegistry.get(variant.group().get());
                if (group != null) {
                    if (group.conditions().isPresent()) {
                        if (!group.conditions().get().matches(serverLevel, entity.position(), entity)) {
                            newVariants.remove(variant);
                            continue;
                        }
                    }
                } else {
                    newVariants.remove(variant);
                    continue;
                }
            }

            if (variant.conditions().isPresent()) {
                if (!variant.conditions().get().matches(serverLevel, entity.position(), entity)) {
                    newVariants.remove(variant);
                }
            }
        }

        ArrayList<Holder<Variant>> availableVariants = new ArrayList<>(variantRegistry.holders().toList());
        ArrayList<Variant> selectedVariants = new ArrayList<>();


        for (Holder<VariantGroup> variantGroupHolder : variantGroupRegistry.holders().toList()) {
            VariantGroup group = variantGroupHolder.value();

            if (group.conditions().isPresent() && !group.conditions().get().matches(serverLevel, entity.position(), entity))
                continue;

            ArrayList<Variant> matchingGroupVariants = new ArrayList<>();
            for (Holder<Variant> variantHolder : new ArrayList<>(availableVariants)) {
                Variant variant = variantHolder.value();

                variant.group().ifPresent(location -> {
                    if (variantGroupRegistry.get(location) == group) {
                        if (variant.conditions().isEmpty() || variant.conditions().get().matches(serverLevel, entity.position(), entity))
                            matchingGroupVariants.add(variant);
                        availableVariants.remove(variantHolder);
                    }
                });
            }

            if (!matchingGroupVariants.isEmpty() && group.exclusive()) {
                selectedVariants.add(selectVariant(group, matchingGroupVariants, entity.getRandom()));
            }
        }

        for (Holder<Variant> variantHolder : availableVariants) {
            Variant variant = variantHolder.value();
            if (variant.group().isEmpty()) {
                if (variant.conditions().isEmpty() || variant.conditions().get().matches(serverLevel, entity.position(), entity))
                    selectedVariants.add(variant);
            }
        }

        for (Variant selectedVariant : new ArrayList<>(selectedVariants)) {
            for (Variant newVariant : newVariants) {

                if (selectedVariant == newVariant) selectedVariants.remove(selectedVariant);

                if (newVariant.group().isPresent() && getGroup(entity, newVariant).exclusive() && selectedVariant.group().equals(newVariant.group())) {
                    selectedVariants.remove(selectedVariant);
                }
            }
        }

        Map<ResourceLocation, List<Variant>> groupedVariants = new HashMap<>();

        for (Variant variant : new ArrayList<>(newVariants)) {
            if (variant.group().isPresent()) {
                ResourceLocation groupId = variant.group().get();
                VariantGroup group = variantGroupRegistry.get(groupId);
                if (group != null && group.exclusive()) {
                    groupedVariants.computeIfAbsent(groupId, k -> new ArrayList<>()).add(variant);
                }
            }
        }

        for (Map.Entry<ResourceLocation, List<Variant>> entry : groupedVariants.entrySet()) {
            List<Variant> groupVariants = entry.getValue();
            if (groupVariants.size() > 1) {
                VariantGroup group = variantGroupRegistry.get(entry.getKey());
                if (group != null) {
                    Variant chosen = selectVariant(group, groupVariants, entity.getRandom());
                    newVariants.removeAll(groupVariants);
                    newVariants.add(chosen);
                }
            }
        }

        newVariants.addAll(selectedVariants);

        if (!newVariants.equals(oldVariants)) {
            setVariants(entity, newVariants);
        }
    }

    public static @Nullable VariantGroup getGroup(Entity entity, Variant variant) {
        Registry<VariantGroup> variantGroupRegistry = entity.registryAccess().registryOrThrow(Registries.VARIANT_GROUP_KEY);

        return variant.group().map(variantGroupRegistry::get).orElse(null);
    }

    public static VariantType getType(Entity entity, Variant variant) {
        Registry<VariantType> variantTypeRegistry = entity.registryAccess().registryOrThrow(Registries.VARIANT_TYPE_KEY);

        return variantTypeRegistry.get(variant.type());
    }
}
