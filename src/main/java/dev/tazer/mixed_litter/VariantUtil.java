package dev.tazer.mixed_litter;

import com.google.gson.JsonElement;
import dev.tazer.mixed_litter.registry.MLDataAttachmentTypes;
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
        return entity.hasData(MLDataAttachmentTypes.VARIANTS) ? lookupVariantIds(entity.getData(MLDataAttachmentTypes.VARIANTS), entity.registryAccess()) : List.of();
    }

    public static List<Variant> lookupVariantIds(List<ResourceLocation> variants, RegistryAccess access) {
        Registry<Variant> variantRegistry = access.registryOrThrow(MLRegistries.VARIANT_KEY);
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
        Registry<VariantGroup> variantGroupRegistry = entity.registryAccess().registryOrThrow(MLRegistries.VARIANT_GROUP_KEY);
        Registry<Variant> variantRegistry = entity.registryAccess().registryOrThrow(MLRegistries.VARIANT_KEY);
        ArrayList<Holder<Variant>> availableVariants = new ArrayList<>(variantRegistry.holders().toList());
        ArrayList<Variant> selectedVariants = new ArrayList<>();
        boolean replaceDefault = false;

        for (Holder<VariantGroup> variantGroupHolder : variantGroupRegistry.holders().toList()) {
            VariantGroup group = variantGroupHolder.value();
            if (group.replaceDefault()) replaceDefault = true;
            if (group.conditions().isPresent() && !group.conditions().get().matches(serverLevel, entity.position(), entity)) continue;

            ArrayList<Variant> matchingGroupVariants = new ArrayList<>();
            for (Holder<Variant> variantHolder : new ArrayList<>(availableVariants)) {
                Variant variant = variantHolder.value();
                variant.group().ifPresent(location -> {
                    if (variantGroupRegistry.get(location) == group) {
                        if (variant.conditions().isEmpty() || variant.conditions().get().matches(serverLevel, entity.position(), entity)) {
                            matchingGroupVariants.add(variant);
                        }
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
                if (variant.conditions().isEmpty() || variant.conditions().get().matches(serverLevel, entity.position(), entity)) {
                    selectedVariants.add(variant);
                }
            }
        }

        if (replaceDefault) {
            for (Variant variant : new ArrayList<>(selectedVariants)) {
                variant.group().ifPresent(location -> {
                    if (location.equals(MixedLitter.location("default"))) selectedVariants.remove(variant);
                });
            }
        }

        if (selectedVariants.size() == 1 && selectedVariants.getFirst().group().isPresent()) {
            selectedVariants.getFirst().group().ifPresent(location -> {
                if (location.equals(MixedLitter.location("default"))) selectedVariants.remove(selectedVariants.getFirst());
            });
        }

        Map<ResourceLocation, List<Variant>> presentByGroup = new HashMap<>();
        for (Variant variant : selectedVariants) {
            if (variant.group().isPresent()) {
                ResourceLocation groupId = variant.group().get();
                presentByGroup.computeIfAbsent(groupId, k -> new ArrayList<>()).add(variant);
            }
        }

        Set<ResourceLocation> unresolved = new HashSet<>(presentByGroup.keySet());
        while (!unresolved.isEmpty()) {
            ResourceLocation seed = unresolved.iterator().next();
            Set<ResourceLocation> cluster = new HashSet<>();
            Deque<ResourceLocation> queue = new ArrayDeque<>();
            queue.add(seed);

            while (!queue.isEmpty()) {
                ResourceLocation current = queue.removeFirst();
                if (!cluster.add(current)) continue;
                VariantGroup currentGroup = variantGroupRegistry.get(current);
                if (currentGroup != null && currentGroup.conflicts() != null) {
                    for (ResourceLocation other : currentGroup.conflicts()) {
                        if (presentByGroup.containsKey(other) && !cluster.contains(other)) queue.addLast(other);
                    }
                }
                for (Map.Entry<ResourceLocation, List<Variant>> entry : presentByGroup.entrySet()) {
                    ResourceLocation candidate = entry.getKey();
                    if (candidate.equals(current)) continue;
                    VariantGroup candidateGroup = variantGroupRegistry.get(candidate);
                    if (candidateGroup != null && candidateGroup.conflicts() != null && candidateGroup.conflicts().contains(current) && !cluster.contains(candidate)) {
                        queue.addLast(candidate);
                    }
                }
            }

            if (cluster.size() > 1) {
                List<ResourceLocation> clusterList = new ArrayList<>(cluster);
                ResourceLocation winningGroupId = clusterList.get(entity.getRandom().nextInt(clusterList.size()));
                VariantGroup winningGroup = variantGroupRegistry.get(winningGroupId);
                List<Variant> winningCandidates = presentByGroup.get(winningGroupId);
                Variant winningVariant = selectVariant(winningGroup, winningCandidates, entity.getRandom());

                for (ResourceLocation gid : clusterList) {
                    if (!gid.equals(winningGroupId)) {
                        List<Variant> toRemove = presentByGroup.get(gid);
                        if (toRemove != null) selectedVariants.removeAll(toRemove);
                    }
                }
                presentByGroup.put(winningGroupId, Collections.singletonList(winningVariant));
                unresolved.removeAll(clusterList);
                continue;
            }

            unresolved.removeAll(cluster);
        }

        if (!selectedVariants.isEmpty()) {
            setVariants(entity, selectedVariants);
        }
    }

    public static void setVariants(Entity entity, List<Variant> variants) {
        List<ResourceLocation> variantLocations = new ArrayList<>();
        Registry<Variant> variantRegistry = entity.registryAccess().registryOrThrow(MLRegistries.VARIANT_KEY);
        variants.forEach(variant -> Optional.ofNullable(variantRegistry.getKey(variant)).map(variantLocations::add));

        if (!variantLocations.isEmpty()) entity.setData(MLDataAttachmentTypes.VARIANTS, variantLocations);
        else entity.removeData(MLDataAttachmentTypes.VARIANTS);
    }

    public static void setChildVariant(Entity parentA, Entity parentB, Entity child) {
        List<Variant> AVariants = new ArrayList<>(getVariants(parentA));
        List<Variant> BVariants = new ArrayList<>(getVariants(parentB));

        ServerLevel serverLevel = (ServerLevel) child.level();

        Registry<VariantGroup> variantGroupRegistry = child.registryAccess().registryOrThrow(MLRegistries.VARIANT_GROUP_KEY);
        Registry<Variant> variantRegistry = child.registryAccess().registryOrThrow(MLRegistries.VARIANT_KEY);
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
        Registry<VariantGroup> variantGroupRegistry = entity.registryAccess().registryOrThrow(MLRegistries.VARIANT_GROUP_KEY);
        Registry<Variant> variantRegistry = entity.registryAccess().registryOrThrow(MLRegistries.VARIANT_KEY);
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
        boolean replaceDefault = false;

        for (Holder<VariantGroup> variantGroupHolder : variantGroupRegistry.holders().toList()) {
            VariantGroup group = variantGroupHolder.value();
            if (group.replaceDefault()) replaceDefault = true;
            if (group.conditions().isPresent() && !group.conditions().get().matches(serverLevel, entity.position(), entity)) continue;

            ArrayList<Variant> matchingGroupVariants = new ArrayList<>();
            for (Holder<Variant> variantHolder : new ArrayList<>(availableVariants)) {
                Variant variant = variantHolder.value();
                variant.group().ifPresent(location -> {
                    if (variantGroupRegistry.get(location) == group) {
                        if (variant.conditions().isEmpty() || variant.conditions().get().matches(serverLevel, entity.position(), entity)) {
                            matchingGroupVariants.add(variant);
                        }
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
                if (variant.conditions().isEmpty() || variant.conditions().get().matches(serverLevel, entity.position(), entity)) {
                    selectedVariants.add(variant);
                }
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

        newVariants.addAll(selectedVariants);

        Map<ResourceLocation, List<Variant>> groupedVariants = new HashMap<>();
        for (Variant variant : new ArrayList<>(newVariants)) {
            if (replaceDefault) {
                variant.group().ifPresent(location -> {
                    if (location.equals(MixedLitter.location("default"))) newVariants.remove(variant);
                });
            }
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

        if (newVariants.size() == 1 && newVariants.getFirst().group().isPresent()) {
            newVariants.getFirst().group().ifPresent(location -> {
                if (location.equals(MixedLitter.location("default"))) newVariants.remove(newVariants.getFirst());
            });
        }

        Map<ResourceLocation, List<Variant>> presentByGroup = new HashMap<>();
        for (Variant variant : newVariants) {
            if (variant.group().isPresent()) {
                ResourceLocation groupId = variant.group().get();
                presentByGroup.computeIfAbsent(groupId, k -> new ArrayList<>()).add(variant);
            }
        }

        Set<ResourceLocation> oldGroupIds = new HashSet<>();
        for (Variant variant : oldVariants) {
            if (variant.group().isPresent()) oldGroupIds.add(variant.group().get());
        }

        Set<ResourceLocation> unresolved = new HashSet<>(presentByGroup.keySet());
        while (!unresolved.isEmpty()) {
            ResourceLocation seed = unresolved.iterator().next();
            Set<ResourceLocation> cluster = new HashSet<>();
            Deque<ResourceLocation> queue = new ArrayDeque<>();
            queue.add(seed);
            while (!queue.isEmpty()) {
                ResourceLocation gid = queue.removeFirst();
                if (!cluster.add(gid)) continue;
                VariantGroup group = variantGroupRegistry.get(gid);
                if (group == null) continue;
                List<ResourceLocation> conflicts = group.conflicts();
                if (conflicts == null) continue;
                for (ResourceLocation other : conflicts) {
                    if (presentByGroup.containsKey(other) && !cluster.contains(other)) queue.addLast(other);
                }
                for (Map.Entry<ResourceLocation, List<Variant>> e : presentByGroup.entrySet()) {
                    ResourceLocation candidate = e.getKey();
                    if (candidate.equals(gid)) continue;
                    VariantGroup cg = variantGroupRegistry.get(candidate);
                    if (cg == null) continue;
                    List<ResourceLocation> back = cg.conflicts();
                    if (back != null && back.contains(gid) && !cluster.contains(candidate)) queue.addLast(candidate);
                }
            }

            if (cluster.size() > 1) {
                List<ResourceLocation> clusterList = new ArrayList<>(cluster);
                ResourceLocation winningGroupId = null;
                for (ResourceLocation gid : clusterList) {
                    if (oldGroupIds.contains(gid)) {
                        winningGroupId = gid;
                        break;
                    }
                }
                if (winningGroupId == null) {
                    winningGroupId = clusterList.get(entity.getRandom().nextInt(clusterList.size()));
                }

                VariantGroup winningGroup = variantGroupRegistry.get(winningGroupId);
                List<Variant> winningCandidates = presentByGroup.get(winningGroupId);
                Variant winningVariant = selectVariant(winningGroup, winningCandidates, entity.getRandom());

                for (ResourceLocation gid : clusterList) {
                    List<Variant> toRemove = presentByGroup.get(gid);
                    if (toRemove != null) newVariants.removeAll(toRemove);
                }
                newVariants.add(winningVariant);

                Map<ResourceLocation, List<Variant>> updated = new HashMap<>(presentByGroup);
                updated.put(winningGroupId, Collections.singletonList(winningVariant));
                for (ResourceLocation gid : clusterList) {
                    if (!gid.equals(winningGroupId)) updated.remove(gid);
                }
                presentByGroup = updated;
                clusterList.forEach(unresolved::remove);
                continue;
            }

            unresolved.removeAll(cluster);
        }

        if (!newVariants.equals(oldVariants)) {
            setVariants(entity, newVariants);
        }
    }

    public static @Nullable VariantGroup getGroup(Entity entity, Variant variant) {
        Registry<VariantGroup> variantGroupRegistry = entity.registryAccess().registryOrThrow(MLRegistries.VARIANT_GROUP_KEY);

        return variant.group().map(variantGroupRegistry::get).orElse(null);
    }

    public static VariantType getType(Entity entity, Variant variant) {
        Registry<VariantType> variantTypeRegistry = entity.registryAccess().registryOrThrow(MLRegistries.VARIANT_TYPE_KEY);

        return variantTypeRegistry.get(variant.type());
    }
}
