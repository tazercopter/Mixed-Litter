package dev.tazer.mixed_litter.variants;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ConfigCondition(String path, boolean status) {
    public static final Codec<ConfigCondition> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.STRING.fieldOf("type").forGetter(ConfigCondition::path),
                    Codec.BOOL.fieldOf("status").forGetter(ConfigCondition::status)
            ).apply(instance, ConfigCondition::new)
    );
}
