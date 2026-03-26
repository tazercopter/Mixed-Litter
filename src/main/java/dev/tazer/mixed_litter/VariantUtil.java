package dev.tazer.mixed_litter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.tazer.mixed_litter.actions.Action;
import dev.tazer.mixed_litter.actions.ReplaceTextures;
import dev.tazer.mixed_litter.actions.SetAgeableTexture;
import dev.tazer.mixed_litter.actions.SetTexture;
import dev.tazer.mixed_litter.actions.VariantActionType;
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
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;

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

    public static <T extends VariantActionType> T findAction(Entity entity, Class<T> type) {
        for (Variant variant : getVariants(entity)) {
            VariantType variantType = getType(entity, variant);
            if (variantType == null) continue;
            JsonObject defaults = getEffectiveDefaults(entity, variant, variantType);
            for (Action action : variantType.actions()) {
                if (type.isInstance(action.type())) {
                    return type.cast(action.type().resolve(action.arguments(), variant.arguments(), defaults));
                }
            }
        }
        return null;
    }

    public static ResourceLocation resolveTexture(Entity entity, ResourceLocation defaultTexture) {
        for (Variant variant : getVariants(entity)) {
            VariantType variantType = getType(entity, variant);
            if (variantType == null) continue;
            JsonObject defaults = getEffectiveDefaults(entity, variant, variantType);
            for (Action action : variantType.actions()) {
                VariantActionType resolved = action.type().resolve(action.arguments(), variant.arguments(), defaults);

                if (resolved instanceof SetTexture setTexture) {
                    return setTexture.getTexture();
                }
                if (resolved instanceof SetAgeableTexture sat) {
                    return entity instanceof AgeableMob am && am.isBaby() ? sat.getBabyTexture() : sat.getTexture();
                }
                if (resolved instanceof ReplaceTextures rt && rt.getReplacements() != null) {
                    for (Map.Entry<ResourceLocation, ResourceLocation> entry : rt.getReplacements().entrySet()) {
                        if (defaultTexture.equals(entry.getKey())) return entry.getValue();
                    }
                }
            }
        }
        return defaultTexture;
    }

    public static Variant selectVariant(VariantGroup group, List<Variant> groupVariants, RandomSource random) {
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

    public static List<Variant> collectVariants(
            Entity entity,
            ServerLevel level,
            ArrayList<Holder<Variant>> availableVariants,
            Registry<VariantGroup> groupRegistry,
            Predicate<Variant> filter,
            BiFunction<VariantGroup, List<Variant>, Variant> selector,
            boolean isSpawn
    ) {
        ArrayList<Variant> selected = new ArrayList<>();

        for (Holder<VariantGroup> groupHolder : groupRegistry.holders().toList()) {
            VariantGroup group = groupHolder.value();
            if (group.conditions().isPresent()) {
                boolean matches = isSpawn
                        ? group.conditions().get().matchesSpawn(level, entity.position(), entity)
                        : group.conditions().get().matchesPersistent(level, entity.position(), entity);
                if (!matches) continue;
            }

            ArrayList<Variant> matching = new ArrayList<>();
            for (Holder<Variant> variantHolder : new ArrayList<>(availableVariants)) {
                Variant variant = variantHolder.value();
                if (!filter.test(variant)) continue;
                variant.group().ifPresent(location -> {
                    if (groupRegistry.get(location) == group) {
                        boolean variantMatches = true;
                        if (variant.conditions().isPresent()) {
                            variantMatches = isSpawn
                                    ? variant.conditions().get().matchesSpawn(level, entity.position(), entity)
                                    : variant.conditions().get().matchesPersistent(level, entity.position(), entity);
                        }
                        if (variantMatches) matching.add(variant);
                        availableVariants.remove(variantHolder);
                    }
                });
            }

            if (!matching.isEmpty() && group.exclusive()) {
                selected.add(selector.apply(group, matching));
            }
        }

        for (Holder<Variant> variantHolder : availableVariants) {
            Variant variant = variantHolder.value();
            if (filter.test(variant) && variant.group().isEmpty()) {
                boolean variantMatches = true;
                if (variant.conditions().isPresent()) {
                    variantMatches = isSpawn
                            ? variant.conditions().get().matchesSpawn(level, entity.position(), entity)
                            : variant.conditions().get().matchesPersistent(level, entity.position(), entity);
                }
                if (variantMatches) selected.add(variant);
            }
        }

        return selected;
    }

    public static void resolveConflicts(
            List<Variant> variants,
            Registry<VariantGroup> groupRegistry,
            RandomSource random,
            @Nullable Set<ResourceLocation> preferredGroups
    ) {
        Map<ResourceLocation, List<Variant>> presentByGroup = new HashMap<>();
        for (Variant variant : variants) {
            variant.group().ifPresent(groupId ->
                    presentByGroup.computeIfAbsent(groupId, k -> new ArrayList<>()).add(variant));
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
                VariantGroup group = groupRegistry.get(current);
                if (group != null && group.conflicts() != null) {
                    for (ResourceLocation other : group.conflicts()) {
                        if (presentByGroup.containsKey(other) && !cluster.contains(other)) queue.addLast(other);
                    }
                }
                for (Map.Entry<ResourceLocation, List<Variant>> entry : presentByGroup.entrySet()) {
                    ResourceLocation candidate = entry.getKey();
                    if (candidate.equals(current)) continue;
                    VariantGroup candidateGroup = groupRegistry.get(candidate);
                    if (candidateGroup != null && candidateGroup.conflicts() != null && candidateGroup.conflicts().contains(current) && !cluster.contains(candidate)) {
                        queue.addLast(candidate);
                    }
                }
            }

            if (cluster.size() > 1) {
                List<ResourceLocation> clusterList = new ArrayList<>(cluster);
                ResourceLocation winningGroupId = null;

                if (preferredGroups != null) {
                    for (ResourceLocation gid : clusterList) {
                        if (preferredGroups.contains(gid)) {
                            winningGroupId = gid;
                            break;
                        }
                    }
                }
                if (winningGroupId == null) {
                    winningGroupId = clusterList.get(random.nextInt(clusterList.size()));
                }

                VariantGroup winningGroup = groupRegistry.get(winningGroupId);
                List<Variant> winningCandidates = presentByGroup.get(winningGroupId);
                if (winningGroup == null || winningCandidates == null) {
                    unresolved.removeAll(cluster);
                    continue;
                }
                Variant winningVariant = selectVariant(winningGroup, new ArrayList<>(winningCandidates), random);

                for (ResourceLocation gid : clusterList) {
                    List<Variant> toRemove = presentByGroup.get(gid);
                    if (toRemove != null) variants.removeAll(toRemove);
                }
                variants.add(winningVariant);

                presentByGroup.put(winningGroupId, Collections.singletonList(winningVariant));
                for (ResourceLocation gid : clusterList) {
                    if (!gid.equals(winningGroupId)) presentByGroup.remove(gid);
                }
            }

            unresolved.removeAll(cluster);
        }
    }

    public static void applySuitableVariants(Entity entity) {
        ServerLevel serverLevel = (ServerLevel) entity.level();
        Registry<VariantGroup> groupRegistry = entity.registryAccess().registryOrThrow(MLRegistries.VARIANT_GROUP_KEY);
        Registry<Variant> variantRegistry = entity.registryAccess().registryOrThrow(MLRegistries.VARIANT_KEY);
        ArrayList<Holder<Variant>> availableVariants = new ArrayList<>(variantRegistry.holders().toList());

        List<Variant> selectedVariants = collectVariants(
                entity, serverLevel, availableVariants, groupRegistry,
                v -> true,
                (group, variants) -> selectVariant(group, variants, entity.getRandom()),
                true
        );

        boolean replaceDefault = selectedVariants.stream().anyMatch(v ->
                v.group().isPresent() && groupRegistry.get(v.group().get()) != null && groupRegistry.get(v.group().get()).replaceDefault());

        if (replaceDefault) {
            selectedVariants.removeIf(v -> v.group().isPresent() && v.group().get().equals(MixedLitter.location("default")));
        }

        resolveConflicts(selectedVariants, groupRegistry, entity.getRandom(), null);

        if (selectedVariants.size() == 1 && selectedVariants.getFirst().group().isPresent()
                && selectedVariants.getFirst().group().get().equals(MixedLitter.location("default"))) {
            selectedVariants.clear();
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
        List<Variant> aVariants = new ArrayList<>(getVariants(parentA));
        List<Variant> bVariants = new ArrayList<>(getVariants(parentB));

        ServerLevel serverLevel = (ServerLevel) child.level();
        Registry<VariantGroup> groupRegistry = child.registryAccess().registryOrThrow(MLRegistries.VARIANT_GROUP_KEY);
        Registry<Variant> variantRegistry = child.registryAccess().registryOrThrow(MLRegistries.VARIANT_KEY);
        ArrayList<Holder<Variant>> availableVariants = new ArrayList<>(variantRegistry.holders().toList());

        List<Variant> childVariants = collectVariants(
                child, serverLevel, availableVariants, groupRegistry,
                v -> aVariants.contains(v) || bVariants.contains(v),
                (group, variants) -> variants.get(child.getRandom().nextInt(variants.size())),
                false
        );

        if (!childVariants.isEmpty()) {
            setVariants(child, childVariants);
        }
    }

    public static void validateVariants(Entity entity) {
        List<Variant> oldVariants = getVariants(entity);
        if (oldVariants.isEmpty()) return;

        ArrayList<Variant> newVariants = new ArrayList<>(oldVariants);
        Registry<VariantGroup> groupRegistry = entity.registryAccess().registryOrThrow(MLRegistries.VARIANT_GROUP_KEY);
        ServerLevel serverLevel = (ServerLevel) entity.level();

        for (Variant variant : oldVariants) {
            if (variant.group().isPresent()) {
                VariantGroup group = groupRegistry.get(variant.group().get());
                if (group == null) {
                    newVariants.remove(variant);
                    continue;
                }
                if (group.conditions().isPresent() && !group.conditions().get().matchesPersistent(serverLevel, entity.position(), entity)) {
                    newVariants.remove(variant);
                    continue;
                }
            }

            if (variant.conditions().isPresent() && !variant.conditions().get().matchesPersistent(serverLevel, entity.position(), entity)) {
                newVariants.remove(variant);
            }
        }

        if (!newVariants.equals(oldVariants)) {
            setVariants(entity, newVariants);
        }
    }

    public static @Nullable VariantGroup getGroup(Entity entity, Variant variant) {
        Registry<VariantGroup> variantGroupRegistry = entity.registryAccess().registryOrThrow(MLRegistries.VARIANT_GROUP_KEY);
        return variant.group().map(variantGroupRegistry::get).orElse(null);
    }

    public static @Nullable VariantType getType(Entity entity, Variant variant) {
        Registry<VariantType> variantTypeRegistry = entity.registryAccess().registryOrThrow(MLRegistries.VARIANT_TYPE_KEY);
        return variantTypeRegistry.get(variant.type());
    }

    private static JsonObject getEffectiveDefaults(Entity entity, Variant variant, VariantType variantType) {
        JsonObject defaults = variantType.defaults().deepCopy();
        VariantGroup group = getGroup(entity, variant);
        if (group != null) {
            for (Map.Entry<String, JsonElement> entry : group.defaults().entrySet()) {
                defaults.add(entry.getKey(), entry.getValue());
            }
        }
        return defaults;
    }
}
