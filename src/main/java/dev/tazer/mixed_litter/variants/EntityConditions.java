package dev.tazer.mixed_litter.variants;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.tazer.mixed_litter.Config;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ModConfigSpec;

import javax.annotation.Nullable;
import java.util.Optional;

public record EntityConditions(Optional<ConfigCondition> configCondition, Optional<EntityPredicate> spawnPredicate, Optional<EntityPredicate> predicate, boolean unobtainable) {
    public static final Codec<EntityConditions> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    ConfigCondition.CODEC.optionalFieldOf("remodel").forGetter(EntityConditions::configCondition),
                    EntityPredicate.CODEC.optionalFieldOf("spawn_predicate").forGetter(EntityConditions::spawnPredicate),
                    EntityPredicate.CODEC.optionalFieldOf("entity_predicate").forGetter(EntityConditions::predicate),
                    Codec.BOOL.optionalFieldOf("unobtainable", false).forGetter(EntityConditions::unobtainable)
            ).apply(instance, EntityConditions::new)
    );

    private boolean matchesConfig() {
        if (configCondition.isEmpty()) return true;
        UnmodifiableConfig config = Config.STARTUP_CONFIG.getValues().get("remodels");
        ModConfigSpec.BooleanValue booleanValue = config.get(configCondition.get().path() + "Remodel");
        return booleanValue.get() == configCondition.get().status();
    }

    public boolean matchesSpawn(ServerLevel level, @Nullable Vec3 position, Entity entity) {
        if (unobtainable) return false;
        if (!matchesConfig()) return false;
        if (spawnPredicate.isPresent() && !spawnPredicate.get().matches(level, position, entity)) return false;
        return predicate.map(p -> p.matches(level, position, entity)).orElse(true);
    }

    public boolean matchesPersistent(ServerLevel level, @Nullable Vec3 position, Entity entity) {
        if (unobtainable) return false;
        if (!matchesConfig()) return false;
        return predicate.map(p -> p.matches(level, position, entity)).orElse(true);
    }
}
