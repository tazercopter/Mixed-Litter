package dev.tazer.mixed_litter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.tazer.mixed_litter.actions.*;
import dev.tazer.mixed_litter.registry.MLDataAttachmentTypes;
import dev.tazer.mixed_litter.variants.EntityConditions;
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
import net.minecraft.world.entity.EntityType;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class VariantUtil {

    private static final Map<EntityType<?>, Boolean> TARGETED_TYPES = new ConcurrentHashMap<>();
    private static final Map<EntityType<?>, List<Variant>> RENDER_FALLBACKS = new ConcurrentHashMap<>();

    public static void invalidateCaches() {
        TARGETED_TYPES.clear();
        RENDER_FALLBACKS.clear();
    }

    public static List<Variant> getVariants(Entity entity) {
        return entity.hasData(MLDataAttachmentTypes.VARIANTS) ? lookupVariantIds(entity.getData(MLDataAttachmentTypes.VARIANTS), entity.registryAccess()) : List.of();
    }

    public static boolean anyVariantTargets(Entity entity) {
        return TARGETED_TYPES.computeIfAbsent(entity.getType(), type -> computeAnyVariantTargets(type, entity.registryAccess()));
    }

    private static boolean computeAnyVariantTargets(EntityType<?> type, RegistryAccess access) {
        Registry<VariantGroup> groupRegistry = access.registryOrThrow(MLRegistries.VARIANT_GROUP_KEY);
        Registry<Variant> variantRegistry = access.registryOrThrow(MLRegistries.VARIANT_KEY);

        for (Holder<Variant> holder : variantRegistry.holders().toList()) {
            Variant variant = holder.value();
            if (variant.group().isPresent()) {
                if (variant.group().get().equals(MixedLitter.DEFAULT_GROUP)) continue;
                VariantGroup group = groupRegistry.get(variant.group().get());
                if (group == null) continue;
                if (group.conditions().isPresent() && !group.conditions().get().typeCouldMatch(type)) continue;
            }
            if (variant.conditions().isPresent() && !variant.conditions().get().typeCouldMatch(type)) continue;
            return true;
        }

        return false;
    }

    public static List<Variant> variantsForRendering(Entity entity) {
        if (entity.hasData(MLDataAttachmentTypes.VARIANTS)) {
            return lookupVariantIds(entity.getData(MLDataAttachmentTypes.VARIANTS), entity.registryAccess());
        }
        if (entity.level() == null || !entity.level().isClientSide) return List.of();
        return RENDER_FALLBACKS.computeIfAbsent(entity.getType(), type -> computeRenderFallbacks(type, entity.registryAccess()));
    }

    private static List<Variant> computeRenderFallbacks(EntityType<?> type, RegistryAccess access) {
        Registry<VariantGroup> groupRegistry = access.registryOrThrow(MLRegistries.VARIANT_GROUP_KEY);
        Registry<Variant> variantRegistry = access.registryOrThrow(MLRegistries.VARIANT_KEY);

        Map<ResourceLocation, Variant> bestByGroup = new LinkedHashMap<>();
        Map<ResourceLocation, Integer> bestWeight = new HashMap<>();

        for (Holder<Variant> holder : variantRegistry.holders().toList()) {
            Variant variant = holder.value();
            if (variant.group().isEmpty() || variant.group().get().equals(MixedLitter.DEFAULT_GROUP)) continue;
            ResourceLocation groupId = variant.group().get();
            VariantGroup group = groupRegistry.get(groupId);
            if (group == null || !group.replaceDefault()) continue;
            if (!fallbackConditionsMatch(group.conditions(), type) || !fallbackConditionsMatch(variant.conditions(), type)) continue;

            int weight = Optional.ofNullable(variant.arguments().get("weight"))
                    .map(JsonElement::getAsInt).orElse(1);
            Integer currentBest = bestWeight.get(groupId);
            if (currentBest == null || weight > currentBest) {
                bestByGroup.put(groupId, variant);
                bestWeight.put(groupId, weight);
            }
        }

        return List.copyOf(bestByGroup.values());
    }

    private static boolean fallbackConditionsMatch(Optional<EntityConditions> conditions, EntityType<?> type) {
        if (conditions.isEmpty()) return true;
        return conditions.get().clientEvaluable() && conditions.get().typeCouldMatch(type);
    }

    public static List<Variant> lookupVariantIds(List<ResourceLocation> variants, RegistryAccess access) {
        Registry<Variant> variantRegistry = access.registryOrThrow(MLRegistries.VARIANT_KEY);
        ArrayList<Variant> variantList = new ArrayList<>(variants.size());
        for (ResourceLocation id : variants)
            variantRegistry.getOptional(id).ifPresent(variantList::add);

        return variantList;
    }

    public static <T extends VariantActionType> T findAction(Entity entity, Class<T> type) {
        for (Variant variant : variantsForRendering(entity)) {
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

    public static ResourceLocation resolveTexture(Entity entity, ResourceLocation defaultTexture, boolean remodelActive) {
        List<Variant> variants = variantsForRendering(entity);
        for (Variant variant : variants) {
            if (!remodelConditionMatches(entity, variant, remodelActive)) continue;
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
        if (remodelActive && variants.isEmpty()) {
            ResourceLocation fallback = RemodelRegistry.fallbackTexture(entity);
            if (fallback != null) return fallback;
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

    public static Variant weightedSelect(List<Variant> variants, RandomSource random) {
        Variant chosen = null;
        int cumulative = 0;
        for (Variant variant : variants) {
            int weight = Optional.ofNullable(variant.arguments().get("weight"))
                    .map(JsonElement::getAsInt).orElse(1);
            if (random.nextInt(cumulative + weight) < weight)
                chosen = variant;
            cumulative += weight;
        }
        return chosen;
    }

    public static void applyExclusivity(List<Variant> variants, Registry<VariantGroup> groupRegistry, RandomSource random) {
        Map<ResourceLocation, List<Variant>> byGroup = new LinkedHashMap<>();
        for (Variant variant : variants) {
            variant.group().ifPresent(groupId ->
                    byGroup.computeIfAbsent(groupId, k -> new ArrayList<>()).add(variant));
        }

        for (Map.Entry<ResourceLocation, List<Variant>> entry : byGroup.entrySet()) {
            VariantGroup group = groupRegistry.get(entry.getKey());
            if (group != null && group.exclusive() && entry.getValue().size() > 1) {
                Variant selected = selectVariant(group, entry.getValue(), random);
                variants.removeAll(entry.getValue());
                variants.add(selected);
            }
        }
    }

    public static List<Variant> collectVariants(
            Entity entity,
            ServerLevel level,
            ArrayList<Holder<Variant>> availableVariants,
            Registry<VariantGroup> groupRegistry,
            Predicate<Variant> filter
    ) {
        ArrayList<Variant> selected = new ArrayList<>();

        for (Holder<VariantGroup> groupHolder : groupRegistry.holders().toList()) {
            VariantGroup group = groupHolder.value();
            if (group.conditions().isPresent() && !group.conditions().get().matches(level, entity.position(), entity)) continue;

            ArrayList<Variant> matching = new ArrayList<>();
            for (Holder<Variant> variantHolder : new ArrayList<>(availableVariants)) {
                Variant variant = variantHolder.value();
                if (!filter.test(variant)) continue;
                variant.group().ifPresent(location -> {
                    if (groupRegistry.get(location) == group) {
                        boolean variantMatches = variant.conditions().isEmpty()
                                || variant.conditions().get().matches(level, entity.position(), entity);
                        if (variantMatches) matching.add(variant);
                        availableVariants.remove(variantHolder);
                    }
                });
            }

            preferConditioned(matching);
            if (!matching.isEmpty()) selected.addAll(matching);
        }

        ArrayList<Variant> groupless = new ArrayList<>();
        for (Holder<Variant> variantHolder : availableVariants) {
            Variant variant = variantHolder.value();
            if (filter.test(variant) && variant.group().isEmpty()) {
                boolean variantMatches = variant.conditions().isEmpty()
                        || variant.conditions().get().matches(level, entity.position(), entity);
                if (variantMatches) groupless.add(variant);
            }
        }
        preferConditioned(groupless);
        selected.addAll(groupless);

        return selected;
    }

    private static void preferConditioned(List<Variant> variants) {
        if (variants.stream().anyMatch(v -> v.conditions().isPresent())) {
            variants.removeIf(v -> v.conditions().isEmpty());
        }
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
                List<Variant> pool = new ArrayList<>();
                for (ResourceLocation gid : cluster) {
                    List<Variant> groupVariants = presentByGroup.get(gid);
                    if (groupVariants != null) pool.addAll(groupVariants);
                }

                preferConditioned(pool);

                List<Variant> selectionPool = pool;
                if (preferredGroups != null) {
                    List<Variant> preferred = pool.stream()
                            .filter(v -> v.group().isPresent() && preferredGroups.contains(v.group().get()))
                            .toList();
                    if (!preferred.isEmpty()) selectionPool = preferred;
                }

                Variant winningVariant = weightedSelect(selectionPool, random);
                if (winningVariant == null) {
                    unresolved.removeAll(cluster);
                    continue;
                }

                ResourceLocation winningGroupId = winningVariant.group().orElse(null);
                VariantGroup winningGroup = winningGroupId != null ? groupRegistry.get(winningGroupId) : null;

                for (ResourceLocation gid : cluster) {
                    List<Variant> toRemove = presentByGroup.get(gid);
                    if (toRemove != null) variants.removeAll(toRemove);
                }

                if (winningGroup != null && !winningGroup.exclusive()) {
                    List<Variant> winningGroupVariants = presentByGroup.get(winningGroupId);
                    if (winningGroupVariants != null) variants.addAll(winningGroupVariants);
                    presentByGroup.put(winningGroupId, winningGroupVariants);
                } else {
                    variants.add(winningVariant);
                    if (winningGroupId != null) presentByGroup.put(winningGroupId, Collections.singletonList(winningVariant));
                }

                for (ResourceLocation gid : cluster) {
                    if (!gid.equals(winningGroupId)) presentByGroup.remove(gid);
                }
            }

            unresolved.removeAll(cluster);
        }
    }

    public static void applySuitableVariants(Entity entity) {
        if (!anyVariantTargets(entity)) return;
        ServerLevel serverLevel = (ServerLevel) entity.level();
        Registry<VariantGroup> groupRegistry = entity.registryAccess().registryOrThrow(MLRegistries.VARIANT_GROUP_KEY);
        Registry<Variant> variantRegistry = entity.registryAccess().registryOrThrow(MLRegistries.VARIANT_KEY);
        ArrayList<Holder<Variant>> availableVariants = new ArrayList<>(variantRegistry.holders().toList());

        List<Variant> selectedVariants = collectVariants(
                entity, serverLevel, availableVariants, groupRegistry,
                v -> true
        );

        boolean replaceDefault = selectedVariants.stream().anyMatch(v ->
                v.group().isPresent() && groupRegistry.get(v.group().get()) != null && groupRegistry.get(v.group().get()).replaceDefault());

        if (replaceDefault) {
            selectedVariants.removeIf(v -> v.group().isPresent() && v.group().get().equals(MixedLitter.DEFAULT_GROUP));
        }

        resolveConflicts(selectedVariants, groupRegistry, entity.getRandom(), null);
        applyExclusivity(selectedVariants, groupRegistry, entity.getRandom());

        if (selectedVariants.size() == 1 && selectedVariants.getFirst().group().isPresent()
                && selectedVariants.getFirst().group().get().equals(MixedLitter.DEFAULT_GROUP)) {
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

    public static void applyChildVariants(Entity parentA, Entity parentB, Entity child) {
        LinkedHashSet<Variant> union = new LinkedHashSet<>();
        union.addAll(getVariants(parentA));
        union.addAll(getVariants(parentB));

        List<Variant> childVariants = new ArrayList<>(union);
        Registry<VariantGroup> groupRegistry = child.registryAccess().registryOrThrow(MLRegistries.VARIANT_GROUP_KEY);

        Map<ResourceLocation, List<Variant>> byGroup = new LinkedHashMap<>();
        for (Variant v : childVariants) {
            v.group().ifPresent(groupId ->
                    byGroup.computeIfAbsent(groupId, k -> new ArrayList<>()).add(v));
        }
        for (Map.Entry<ResourceLocation, List<Variant>> entry : byGroup.entrySet()) {
            VariantGroup group = groupRegistry.get(entry.getKey());
            if (group != null && group.exclusive() && entry.getValue().size() > 1) {
                Variant picked = entry.getValue().get(child.getRandom().nextInt(entry.getValue().size()));
                childVariants.removeAll(entry.getValue());
                childVariants.add(picked);
            }
        }

        if (!childVariants.isEmpty()) {
            setVariants(child, childVariants);
        }
    }

    public static void validateVariants(Entity entity) {
        if (!(entity.level() instanceof ServerLevel serverLevel)) return;
        if (!entity.hasData(MLDataAttachmentTypes.VARIANTS) && !anyVariantTargets(entity)) return;

        Registry<Variant> variantRegistry = entity.registryAccess().registryOrThrow(MLRegistries.VARIANT_KEY);
        Registry<VariantGroup> groupRegistry = entity.registryAccess().registryOrThrow(MLRegistries.VARIANT_GROUP_KEY);

        List<ResourceLocation> storedIds = entity.hasData(MLDataAttachmentTypes.VARIANTS)
                ? entity.getData(MLDataAttachmentTypes.VARIANTS) : List.of();

        boolean remodelActive = RemodelRegistry.remodelActive(entity.getType());
        List<Variant> kept = new ArrayList<>(storedIds.size());
        for (ResourceLocation id : storedIds) {
            Variant variant = variantRegistry.get(id);
            if (variant == null) continue;
            if (variant.group().isPresent() && groupRegistry.get(variant.group().get()) == null) continue;
            if (!remodelConditionMatches(entity, variant, remodelActive)) continue;
            kept.add(variant);
        }

        if (kept.size() != storedIds.size()) {
            setVariants(entity, kept);
        }

        boolean needsVariant = kept.isEmpty() || kept.stream().allMatch(v ->
                v.group().isPresent() && v.group().get().equals(MixedLitter.DEFAULT_GROUP));

        if (needsVariant) {
            ArrayList<Holder<Variant>> availableVariants = new ArrayList<>(variantRegistry.holders().toList());
            List<Variant> potential = collectVariants(entity, serverLevel, availableVariants, groupRegistry, v -> true);
            boolean replacingApplies = potential.stream().anyMatch(v ->
                    v.group().isPresent() && Optional.ofNullable(groupRegistry.get(v.group().get()))
                            .map(VariantGroup::replaceDefault).orElse(false));
            if (replacingApplies) {
                applySuitableVariants(entity);
            }
        }
    }

    public static boolean isVariantSuitable(Entity entity, ServerLevel level, Variant variant, Registry<VariantGroup> groupRegistry) {
        if (variant.conditions().isPresent() && !variant.conditions().get().matches(level, entity.position(), entity)) return false;
        VariantGroup group = variant.group().map(groupRegistry::get).orElse(null);
        return group == null || group.conditions().isEmpty()
                || group.conditions().get().matches(level, entity.position(), entity);
    }

    public static @Nullable VariantGroup getGroup(Entity entity, Variant variant) {
        Registry<VariantGroup> variantGroupRegistry = entity.registryAccess().registryOrThrow(MLRegistries.VARIANT_GROUP_KEY);
        return variant.group().map(variantGroupRegistry::get).orElse(null);
    }

    public static @Nullable VariantType getType(Entity entity, Variant variant) {
        Registry<VariantType> variantTypeRegistry = entity.registryAccess().registryOrThrow(MLRegistries.VARIANT_TYPE_KEY);
        return variantTypeRegistry.get(variant.type());
    }

    private static boolean remodelConditionMatches(Entity entity, Variant variant, boolean remodelActive) {
        if (!remodelConditionOk(variant.conditions(), remodelActive)) return false;
        VariantGroup group = getGroup(entity, variant);
        return group == null || remodelConditionOk(group.conditions(), remodelActive);
    }

    private static boolean remodelConditionOk(Optional<EntityConditions> conditions, boolean remodelActive) {
        if (conditions.isEmpty()) return true;
        Optional<Boolean> remodel = conditions.get().remodel();
        return remodel.isEmpty() || remodel.get() == remodelActive;
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
