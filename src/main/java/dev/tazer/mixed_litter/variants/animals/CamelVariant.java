package dev.tazer.mixed_litter.variants.animals;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.tazer.mixed_litter.variants.MobVariant;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.List;

public class CamelVariant extends MobVariant {
    public static final MapCodec<CamelVariant> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.INT.fieldOf("weight").orElse(1).forGetter(v -> v.weight),
                    RegistryCodecs.homogeneousList(Registries.BIOME).fieldOf("biomes").orElse(HolderSet.empty()).forGetter(v -> v.biomes),
                    ResourceLocation.CODEC.fieldOf("texture").forGetter(v -> v.texture),
                    ResourceLocation.CODEC.fieldOf("baby_texture").forGetter(v -> v.babyTexture)
            ).apply(instance, CamelVariant::new)
    );

    public final int weight;
    public final HolderSet<Biome> biomes;
    public final ResourceLocation texture;
    public final ResourceLocation babyTexture;

    public CamelVariant(int weight, HolderSet<Biome> biomes, ResourceLocation texture, ResourceLocation babyTexture) {
        super(List.of(EntityType.CAMEL));
        this.weight = weight;
        this.biomes = biomes;
        this.texture = texture;
        this.babyTexture = babyTexture;
    }

    @Override
    public int weight() {
        return weight;
    }

    @Override
    public MobVariant select(LivingEntity entity, LevelAccessor level, List<? extends MobVariant> animalVariants) {
        MobVariant selectedVariant = animalVariants.getFirst();
        List<CamelVariant> variants = new ArrayList<>();
        animalVariants.forEach(animalVariant -> {
            if (animalVariant instanceof CamelVariant variant) variants.add(variant);
        });

        List<CamelVariant> defaultVariants = variants.stream().filter(v -> v.biomes.size() == 0).toList();
        List<CamelVariant> biomeVariants = variants.stream().filter(v -> v.biomes.size() > 0 && v.biomes.contains(level.getBiome(entity.blockPosition()))).toList();

        int cumulativeWeight = 0;
        if (!biomeVariants.isEmpty()) {
            int totalWeight = biomeVariants.stream().mapToInt(v -> v.weight).sum();
            int randomWeight = level.getRandom().nextInt(totalWeight);
            for (CamelVariant variant : biomeVariants) {
                cumulativeWeight += variant.weight;
                if (randomWeight < cumulativeWeight) {
                    selectedVariant = variant;
                    break;
                }
            }
        } else {
            int totalWeight = defaultVariants.stream().mapToInt(v -> v.weight).sum();
            int randomWeight = level.getRandom().nextInt(totalWeight);
            for (CamelVariant variant : defaultVariants) {
                cumulativeWeight += variant.weight;
                if (randomWeight < cumulativeWeight) {
                    selectedVariant = variant;
                    break;
                }
            }
        }

        return selectedVariant;
    }

    @Override
    public MapCodec<? extends MobVariant> codec() {
        return CODEC;
    }
}
