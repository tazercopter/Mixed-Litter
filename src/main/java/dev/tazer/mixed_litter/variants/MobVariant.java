package dev.tazer.mixed_litter.variants;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.tazer.mixed_litter.MLRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;

import java.util.List;
import java.util.function.Function;

public abstract class MobVariant {
    public final List<EntityType<?>> entityTypes;
    public MobVariant(List<EntityType<?>> entityTypes) {
        this.entityTypes = entityTypes;
    }

    public static Codec<MobVariant> DIRECT_CODEC = MLRegistries.ANIMAL_VARIANT_TYPE.byNameCodec().dispatch(MobVariant::codec, Function.identity());

    public abstract MobVariant select(LivingEntity entity, LevelAccessor level, List<? extends MobVariant> animalVariants);
    public boolean isFor(EntityType<?> entityType) {
        return entityTypes.contains(entityType);
    }

    public abstract MapCodec<? extends MobVariant> codec();

    public int weight() {
        return 1;
    }
}
