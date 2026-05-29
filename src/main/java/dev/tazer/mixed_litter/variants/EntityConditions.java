package dev.tazer.mixed_litter.variants;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Optional;

public record EntityConditions(Optional<Boolean> remodel, Optional<EntityPredicate> predicate, boolean unobtainable) {
    public static final Codec<EntityConditions> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.BOOL.optionalFieldOf("remodel").forGetter(EntityConditions::remodel),
                    EntityPredicate.CODEC.optionalFieldOf("entity_predicate").forGetter(EntityConditions::predicate),
                    Codec.BOOL.optionalFieldOf("unobtainable", false).forGetter(EntityConditions::unobtainable)
            ).apply(instance, EntityConditions::new)
    );

    public boolean matches(ServerLevel level, @Nullable Vec3 position, Entity entity) {
        if (unobtainable) return false;
        return predicate.map(p -> p.matches(level, position, entity)).orElse(true);
    }
}
