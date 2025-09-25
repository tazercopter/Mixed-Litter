package dev.tazer.mixed_litter.variants;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

import java.util.LinkedHashMap;
import java.util.Optional;

public record Variant(ResourceLocation type, Optional<ResourceLocation> group, Optional<EntityConditions> conditions, JsonObject arguments) {

    public static final Codec<Variant> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("type").forGetter(Variant::type),
                    ResourceLocation.CODEC.optionalFieldOf("group").forGetter(Variant::group),
                    EntityConditions.CODEC.optionalFieldOf("conditions").forGetter(Variant::conditions),
                    Codec.unboundedMap(Codec.STRING, ExtraCodecs.JSON).xmap(map -> {
                        var jsonObject = new JsonObject();
                        map.forEach(jsonObject::add);

                        return jsonObject;
                        }, jsonObject -> {
                        var map = new LinkedHashMap<String, JsonElement>();
                        for (var element : jsonObject.entrySet())
                            map.put(element.getKey(), element.getValue());

                        return map;
                    }).optionalFieldOf("arguments", new JsonObject()).forGetter(Variant::arguments)
            ).apply(instance, Variant::new)
    );
}
