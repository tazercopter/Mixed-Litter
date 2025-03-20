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

public class BeeVariant extends MobVariant {
    public static final MapCodec<BeeVariant> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.INT.fieldOf("weight").orElse(1).forGetter(v -> v.weight),
                    RegistryCodecs.homogeneousList(Registries.BIOME).fieldOf("biomes").orElse(HolderSet.empty()).forGetter(v -> v.biomes),
                    ResourceLocation.CODEC.fieldOf("texture").forGetter(v -> v.texture),
                    ResourceLocation.CODEC.fieldOf("angry_texture").forGetter(v -> v.angryTexture),
                    ResourceLocation.CODEC.fieldOf("nectar_texture").forGetter(v -> v.nectarTexture),
                    ResourceLocation.CODEC.fieldOf("angry_nectar_texture").forGetter(v -> v.angryNectarTexture),
                    ResourceLocation.CODEC.fieldOf("stinger_texture").forGetter(v -> v.stingerTexture)
            ).apply(instance, BeeVariant::new)
    );

    public final int weight;
    public final HolderSet<Biome> biomes;
    public final ResourceLocation texture;
    public final ResourceLocation angryTexture;
    public final ResourceLocation nectarTexture;
    public final ResourceLocation angryNectarTexture;
    public final ResourceLocation stingerTexture;


    public BeeVariant(int weight, HolderSet<Biome> biomes, ResourceLocation texture, ResourceLocation angryTexture, ResourceLocation nectarTexture, ResourceLocation angryNectarTexture, ResourceLocation stingerTexture) {
        super(List.of(EntityType.BEE));
        this.weight = weight;
        this.biomes = biomes;
        this.texture = texture;
        this.angryTexture = angryTexture;
        this.nectarTexture = nectarTexture;
        this.angryNectarTexture = angryNectarTexture;
        this.stingerTexture = stingerTexture;
    }

    @Override
    public int weight() {
        return weight;
    }

    @Override
    public MobVariant select(LivingEntity entity, LevelAccessor level, List<? extends MobVariant> animalVariants) {
        MobVariant selectedVariant = animalVariants.getFirst();
        List<BeeVariant> variants = new ArrayList<>();
        animalVariants.forEach(animalVariant -> {
            if (animalVariant instanceof BeeVariant variant) variants.add(variant);
        });

        List<BeeVariant> defaultVariants = variants.stream().filter(v -> v.biomes.size() == 0).toList();
        List<BeeVariant> biomeVariants = variants.stream().filter(v -> v.biomes.size() > 0 && v.biomes.contains(level.getBiome(entity.blockPosition()))).toList();

        int cumulativeWeight = 0;
        if (!biomeVariants.isEmpty()) {
            int totalWeight = biomeVariants.stream().mapToInt(v -> v.weight).sum();
            int randomWeight = level.getRandom().nextInt(totalWeight);
            for (BeeVariant variant : biomeVariants) {
                cumulativeWeight += variant.weight;
                if (randomWeight < cumulativeWeight) {
                    selectedVariant = variant;
                    break;
                }
            }
        } else {
            int totalWeight = defaultVariants.stream().mapToInt(v -> v.weight).sum();
            int randomWeight = level.getRandom().nextInt(totalWeight);
            for (BeeVariant variant : defaultVariants) {
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
