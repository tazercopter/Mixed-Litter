package dev.tazer.mixed_litter.variants;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.tazer.mixed_litter.actions.Action;
import net.minecraft.util.ExtraCodecs;

import java.util.LinkedHashMap;
import java.util.List;

public record VariantType(JsonObject defaults, List<Action> actions) {

    public static final Codec<VariantType> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.unboundedMap(Codec.STRING, ExtraCodecs.JSON)
                            .xmap(map -> {
                                var jsonObject = new JsonObject();
                                map.forEach(jsonObject::add);
                                return jsonObject;
                            }, obj -> {
                                var map = new LinkedHashMap<String, JsonElement>();
                                for (var element : obj.entrySet()) map.put(element.getKey(), element.getValue());
                                return map;
                            }).optionalFieldOf("defaults", new JsonObject()).forGetter(VariantType::defaults),
                    Action.CODEC.listOf().fieldOf("actions").forGetter(VariantType::actions)
            ).apply(instance, VariantType::new));
}
