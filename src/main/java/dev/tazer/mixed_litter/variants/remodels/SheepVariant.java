package dev.tazer.mixed_litter.variants.remodels;

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

public class SheepVariant extends MobVariant {
    public static final MapCodec<SheepVariant> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.INT.fieldOf("weight").orElse(1).forGetter(v -> v.weight),
                    RegistryCodecs.homogeneousList(Registries.BIOME).fieldOf("biomes").orElse(HolderSet.empty()).forGetter(v -> v.biomes),
                    ResourceLocation.CODEC.fieldOf("texture").forGetter(v -> v.texture),
                    ResourceLocation.CODEC.fieldOf("baby_texture").forGetter(v -> v.babyTexture),
                    ResourceLocation.CODEC.fieldOf("fur_texture").forGetter(v -> v.furTexture),
                    ResourceLocation.CODEC.fieldOf("baby_fur_texture").forGetter(v -> v.babyFurTexture),
                    ResourceLocation.CODEC.fieldOf("sheared_fur_texture").forGetter(v -> v.shearedFurTexture)
                    ).apply(instance, SheepVariant::new)
    );

    public final int weight;
    public final HolderSet<Biome> biomes;
    public final ResourceLocation texture;
    public final ResourceLocation babyTexture;
    public final ResourceLocation furTexture;
    public final ResourceLocation babyFurTexture;
    public final ResourceLocation shearedFurTexture;

    public SheepVariant(int weight, HolderSet<Biome> biomes, ResourceLocation texture, ResourceLocation babyTexture, ResourceLocation furTexture, ResourceLocation babyFurTexture, ResourceLocation shearedFurTexture) {
        super(List.of(EntityType.SHEEP));
        this.weight = weight;
        this.biomes = biomes;
        this.texture = texture;
        this.babyTexture = babyTexture;
        this.furTexture = furTexture;
        this.babyFurTexture = babyFurTexture;
        this.shearedFurTexture = shearedFurTexture;
    }

    @Override
    public int weight() {
        return weight;
    }

    @Override
    public MobVariant select(LivingEntity entity, LevelAccessor level, List<? extends MobVariant> animalVariants) {
        MobVariant selectedVariant = animalVariants.getFirst();
        List<SheepVariant> variants = new ArrayList<>();
        animalVariants.forEach(animalVariant -> {
            if (animalVariant instanceof SheepVariant variant) variants.add(variant);
        });

        List<SheepVariant> defaultVariants = variants.stream().filter(v -> v.biomes.size() == 0).toList();
        List<SheepVariant> biomeVariants = variants.stream().filter(v -> v.biomes.size() > 0 && v.biomes.contains(level.getBiome(entity.blockPosition()))).toList();

        int cumulativeWeight = 0;
        if (!biomeVariants.isEmpty()) {
            int totalWeight = biomeVariants.stream().mapToInt(v -> v.weight).sum();
            int randomWeight = level.getRandom().nextInt(totalWeight);
            for (SheepVariant variant : biomeVariants) {
                cumulativeWeight += variant.weight;
                if (randomWeight < cumulativeWeight) {
                    selectedVariant = variant;
                    break;
                }
            }
        } else {
            int totalWeight = defaultVariants.stream().mapToInt(v -> v.weight).sum();
            int randomWeight = level.getRandom().nextInt(totalWeight);
            for (SheepVariant variant : defaultVariants) {
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
