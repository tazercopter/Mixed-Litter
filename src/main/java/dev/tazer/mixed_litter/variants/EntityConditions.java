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

public record EntityConditions(Optional<ConfigCondition> configCondition, Optional<EntityPredicate> predicate, boolean unobtainable) {
    public static final Codec<EntityConditions> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    ConfigCondition.CODEC.optionalFieldOf("remodel").forGetter(EntityConditions::configCondition),
                    EntityPredicate.CODEC.optionalFieldOf("entity_predicate").forGetter(EntityConditions::predicate),
                    Codec.BOOL.optionalFieldOf("unobtainable", false).forGetter(EntityConditions::unobtainable)
            ).apply(instance, EntityConditions::new)
    );

    public boolean matches(ServerLevel level, @Nullable Vec3 position, Entity entity) {
        if (unobtainable) return false;
        if (configCondition.isPresent()) {
            UnmodifiableConfig config = Config.STARTUP_CONFIG.getValues().get("remodels");
            ModConfigSpec.BooleanValue booleanValue = config.get(configCondition.get().path() + "Remodel");
            if (booleanValue.get() != configCondition.get().status()) return false;
        }
        return predicate.map(p -> p.matches(level, position, entity)).orElse(true);
    }
}
