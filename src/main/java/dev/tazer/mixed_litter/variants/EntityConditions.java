package dev.tazer.mixed_litter.variants;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.tazer.mixed_litter.DataAttachmentTypes;
import dev.tazer.mixed_litter.Config;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ModConfigSpec;

import javax.annotation.Nullable;
import java.util.Optional;

public record EntityConditions(Optional<ConfigCondition> configCondition, Optional<LocationPredicate> spawningLocation, Optional<EntityPredicate> predicate, boolean unobtainable) {
    public static final Codec<EntityConditions> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    ConfigCondition.CODEC.optionalFieldOf("remodel").forGetter(EntityConditions::configCondition),
                    LocationPredicate.CODEC.optionalFieldOf("spawning_location").forGetter(EntityConditions::spawningLocation),
                    EntityPredicate.CODEC.optionalFieldOf("entity_predicate").forGetter(EntityConditions::predicate),
                    Codec.BOOL.optionalFieldOf("unobtainable", false).forGetter(EntityConditions::unobtainable)
            ).apply(instance, EntityConditions::new)
    );

    public boolean matches(ServerLevel level, @Nullable Vec3 position, Entity entity) {
        if (unobtainable) return false;

        if (configCondition.isPresent()) {
            UnmodifiableConfig config = Config.CLIENT_CONFIG.getValues().get("animal_remodels");
            ModConfigSpec.BooleanValue booleanValue = config.get(configCondition.get().path() + "Remodel");
            if (booleanValue.get() != configCondition.get().status()) return false;
        }

        if (entity.hasData(DataAttachmentTypes.SPAWN_LOCATION) && spawningLocation.isPresent()) {
            GlobalPos globalPos = entity.getData(DataAttachmentTypes.SPAWN_LOCATION);
            BlockPos pos = globalPos.pos();
            ServerLevel dimensionLevel = level.getServer().getLevel(globalPos.dimension());
            if (dimensionLevel == null || !spawningLocation.get().matches(dimensionLevel, pos.getX(), pos.getY(), pos.getZ())) return false;
        }

        if (predicate.isPresent()) {
            if (!predicate.get().matches(level, position, entity)) return false;
        }

        return true;
    }
}
