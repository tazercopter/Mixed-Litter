package dev.tazer.mixed_litter.variants;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

public record VariantGroup(Optional<EntityConditions> conditions, boolean exclusive, SelectionMethod selection) {

    public static final Codec<VariantGroup> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    EntityConditions.CODEC.optionalFieldOf("conditions").forGetter(VariantGroup::conditions),
                    Codec.BOOL.optionalFieldOf("exclusive", true).forGetter(VariantGroup::exclusive),
                    SelectionMethod.CODEC.optionalFieldOf("selection", SelectionMethod.UNIFORM).forGetter(VariantGroup::selection)
            ).apply(instance, VariantGroup::new)
    );
}
