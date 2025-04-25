package dev.tazer.mixed_litter.variants;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.tazer.mixed_litter.MixedLitter;
import dev.tazer.mixed_litter.requirement.NbtRequirement;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.fml.loading.FMLLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DynamicVariant extends MobVariant {
    public static final MapCodec<DynamicVariant> INNER_CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            ResourceLocation.CODEC.fieldOf("texture").forGetter(DynamicVariant::texture),
            NbtRequirement.CODEC.optionalFieldOf("nbt", NbtRequirement.NONE).forGetter(DynamicVariant::nbt)
    ).apply(instance, (texture, nbt) -> new DynamicVariant(List.of(), 0, texture, nbt, List.of())));

    public static final MapCodec<DynamicVariant> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            BuiltInRegistries.ENTITY_TYPE.byNameCodec().listOf().fieldOf("entity_types").forGetter(DynamicVariant::entityTypes),
            Codec.INT.optionalFieldOf("weight", 1).forGetter(DynamicVariant::weight),
            INNER_CODEC.codec().listOf().optionalFieldOf("sub_variants", List.of()).forGetter((DynamicVariant::subVariants))
    ).apply(instance, (entityTypes, weight, subVariants) -> new DynamicVariant(entityTypes, weight, null, NbtRequirement.NONE, subVariants)));

    private final int weight;
    private final ResourceLocation texture;
    private final NbtRequirement nbt;
    private final List<DynamicVariant> subVariants;
    private final List<EntityType<?>> entityTypes;

    public DynamicVariant(List<EntityType<?>> entityTypes, int weight,
                          ResourceLocation texture, NbtRequirement nbt,
                          List<DynamicVariant> subVariants) {
        super(entityTypes);
        this.entityTypes = entityTypes;
        this.weight = weight;
        this.texture = texture;
        this.nbt = nbt;
        this.subVariants = subVariants;
    }

    public DynamicVariant(EntityType<?> entityType, int weight,
                          ResourceLocation texture, NbtRequirement nbt,
                          List<DynamicVariant> subVariants) {
        this(List.of(entityType), weight, texture, nbt, subVariants);
    }

    public List<EntityType<?>> entityTypes() {
        return this.entityTypes;
    }
    public int weight() {
        return this.weight;
    }
    public ResourceLocation texture() {
        return this.texture;
    }
    public NbtRequirement nbt() {
        return this.nbt;
    }
    public List<DynamicVariant> subVariants() {
        return this.subVariants;
    }

    public DynamicVariant select(LivingEntity entity, LevelAccessor level, List<? extends MobVariant> animalVariants) {
        EntityType<?> entityType = entity.getType();
        // Gather all variants available to the entity
        List<DynamicVariant> variants = new ArrayList<>();
        animalVariants.forEach((animalVariant) -> {
            if (animalVariant instanceof DynamicVariant variant) {
                variants.add(variant);
            }
        });

        // Filter variants based on the entity type
        SimpleWeightedRandomList.Builder<DynamicVariant> variantBuilder = SimpleWeightedRandomList.builder();
        for (DynamicVariant variant : variants) {
            if (variant.entityTypes.stream().anyMatch(e -> e.equals(entityType))) {
                variantBuilder.add(variant, variant.weight());
            }
        }

        // Choose random applicable variant
        return variantBuilder.build().getRandomValue(entity.getRandom()).orElse(null);
    }

    public Optional<ResourceLocation> fetchTexture(LivingEntity entity) {
        List<DynamicVariant> subVariants = this.subVariants();
        // Choose first valid subvariant
        for (DynamicVariant subVariant : subVariants) {
            if (subVariant.nbt().test(entity)) {
                return Optional.of(subVariant.texture());
            }
        }
        return Optional.empty();
    }

    public MapCodec<? extends MobVariant> codec() {
        return INNER_CODEC;
    }

    public static <T> Codec<Either<TagKey<T>, T>> tagOrBuiltinCodec(ResourceKey<Registry<T>> vanillaRegistry, DefaultedRegistry<T> forgeRegistry) {
        return Codec.either(
                // Tag
               Codec.STRING.comapFlatMap(
                    str -> {
                        if (!str.startsWith("#")) {
                            return DataResult.error(() -> String.format("Not a tag key for builtin registry %s: %s", vanillaRegistry.location(), str));
                        }
                        ResourceLocation itemLocation = ResourceLocation.parse(str.replace("#", ""));
                        return DataResult.success(TagKey.create(vanillaRegistry, itemLocation));
                    },
                    key -> "#" + key.location()),
               // Direct Reference
               Codec.STRING.comapFlatMap(
                    str -> {
                        ResourceLocation itemLocation = ResourceLocation.parse(str);
                        Optional<T> obj = forgeRegistry.getOptional(itemLocation);
                        if (obj.isEmpty()) {
                            // Mod loaded, object ID is incorrect
                            if (FMLLoader.getLoadingModList().getModFileById(itemLocation.getNamespace()) != null) {
                                MixedLitter.LOGGER.error("Error deserializing config: object \"{}\" does not exist", str);
                                return DataResult.error(() -> "Object does not exist");
                            }
                            // Mod isn't loaded
                            else {
                                MixedLitter.LOGGER.warn("Could not parse {} because mod \"{}\" is not loaded", itemLocation, itemLocation.getNamespace());
                                return DataResult.error(() -> "Required mod not loaded");
                            }
                        }
                        return DataResult.success(obj.get());
                    },
                    obj -> {
                        ResourceLocation itemLocation = forgeRegistry.getKey(obj);
                        return itemLocation.toString();
                    }
        ));
    }
}
