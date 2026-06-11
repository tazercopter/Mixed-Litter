package dev.tazer.mixed_litter.variants;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.tazer.mixed_litter.RemodelRegistry;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
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
        if (remodelMismatches(entity.getType())) return false;
        return predicate.map(p -> p.matches(level, position, entity)).orElse(true);
    }

    public boolean typeCouldMatch(EntityType<?> type) {
        if (unobtainable) return false;
        if (remodelMismatches(type)) return false;
        return predicate.flatMap(EntityPredicate::entityType).map(typePredicate -> typePredicate.matches(type)).orElse(true);
    }

    public boolean clientEvaluable() {
        if (predicate.isEmpty()) return true;
        EntityPredicate entityPredicate = predicate.get();
        return entityPredicate.entityType().isPresent()
                && entityPredicate.equals(EntityPredicate.Builder.entity().entityType(entityPredicate.entityType().get()).build());
    }

    private boolean remodelMismatches(EntityType<?> type) {
        return remodel.isPresent() && remodel.get() != RemodelRegistry.remodelActive(type);
    }
}
