package dev.tazer.mixed_litter.actions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.tazer.mixed_litter.Registries;
import net.minecraft.util.ExtraCodecs;

import java.util.LinkedHashMap;

public record Action(ActionType type, JsonObject arguments) {

    public static final Codec<Action> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Registries.ACTION_TYPES.byNameCodec().fieldOf("type").forGetter(Action::type),
                    Codec.unboundedMap(Codec.STRING, ExtraCodecs.JSON)
                            .xmap(map -> {
                                var jsonObject = new JsonObject();
                                map.forEach(jsonObject::add);
                                return jsonObject;
                            }, obj -> {
                                var map = new LinkedHashMap<String, JsonElement>();
                                for (var element : obj.entrySet()) map.put(element.getKey(), element.getValue());
                                return map;
                            }).fieldOf("arguments").forGetter(Action::arguments)
            ).apply(instance, Action::new)
    );
}
