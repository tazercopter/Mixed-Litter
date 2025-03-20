package dev.tazer.mixed_litter.variants.remodels;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.tazer.mixed_litter.MixedLitter;
import dev.tazer.mixed_litter.variants.MobVariant;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;

public class MooshroomVariant extends MobVariant {
    public static final MapCodec<MooshroomVariant> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.INT.fieldOf("weight").orElse(1).forGetter(v -> v.weight),
                    RegistryCodecs.homogeneousList(Registries.BIOME).fieldOf("biomes").orElse(HolderSet.empty()).forGetter(v -> v.biomes),
                    ResourceLocation.CODEC.fieldOf("texture").forGetter(v -> v.texture),
                    ResourceLocation.CODEC.fieldOf("baby_texture").forGetter(v -> v.babyTexture),
                    ResourceLocation.CODEC.fieldOf("cow_variant").orElse(ResourceLocation.fromNamespaceAndPath(MixedLitter.MODID, "cow/vintage")).forGetter(v -> v.cowVariant),
                    BuiltInRegistries.BLOCK.byNameCodec().fieldOf("mushroom").orElse(Blocks.AIR).forGetter(v -> v.mushroom)
            ).apply(instance, MooshroomVariant::new)
    );

    public final int weight;
    public final HolderSet<Biome> biomes;
    public final ResourceLocation texture;
    public final ResourceLocation babyTexture;
    public final ResourceLocation cowVariant;
    public final Block mushroom;

    public MooshroomVariant(int weight, HolderSet<Biome> biomes, ResourceLocation texture, ResourceLocation babyTexture, ResourceLocation cowVariant, Block mushroom) {
        super(List.of(EntityType.MOOSHROOM));
        this.weight = weight;
        this.biomes = biomes;
        this.texture = texture;
        this.babyTexture = babyTexture;
        this.cowVariant = cowVariant;
        this.mushroom = mushroom;
    }

    @Override
    public int weight() {
        return weight;
    }

    @Override
    public MobVariant select(LivingEntity entity, LevelAccessor level, List<? extends MobVariant> animalVariants) {
        MobVariant selectedVariant = animalVariants.getFirst();
        List<MooshroomVariant> variants = new ArrayList<>();
        animalVariants.forEach(animalVariant -> {
            if (animalVariant instanceof MooshroomVariant variant) variants.add(variant);
        });

        List<MooshroomVariant> defaultVariants = variants.stream().filter(v -> v.biomes.size() == 0).toList();
        List<MooshroomVariant> biomeVariants = variants.stream().filter(v -> v.biomes.size() > 0 && v.biomes.contains(level.getBiome(entity.blockPosition()))).toList();

        int cumulativeWeight = 0;
        if (!biomeVariants.isEmpty()) {
            int totalWeight = biomeVariants.stream().mapToInt(v -> v.weight).sum();
            int randomWeight = level.getRandom().nextInt(totalWeight);
            for (MooshroomVariant variant : biomeVariants) {
                cumulativeWeight += variant.weight;
                if (randomWeight < cumulativeWeight) {
                    selectedVariant = variant;
                    break;
                }
            }
        } else {
            int totalWeight = defaultVariants.stream().mapToInt(v -> v.weight).sum();
            int randomWeight = level.getRandom().nextInt(totalWeight);
            for (MooshroomVariant variant : defaultVariants) {
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
