package dev.tazer.mixed_litter.variants;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

public record VariantGroup(Optional<EntityConditions> conditions, boolean exclusive, boolean replaceDefault, SelectionMethod selection, List<ResourceLocation> conflicts, JsonObject defaults) {

    public static final Codec<List<ResourceLocation>> CONFLICTS_CODEC = Codec.either(ResourceLocation.CODEC, ResourceLocation.CODEC.listOf())
            .xmap(
                    either -> either.map(List::of, list -> list),
                    list -> list.size() == 1
                            ? Either.left(list.getFirst())
                            : Either.right(list)
            );

    public static final Codec<VariantGroup> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    EntityConditions.CODEC.optionalFieldOf("conditions").forGetter(VariantGroup::conditions),
                    Codec.BOOL.optionalFieldOf("exclusive", true).forGetter(VariantGroup::exclusive),
                    Codec.BOOL.optionalFieldOf("replace_default", false).forGetter(VariantGroup::replaceDefault),
                    SelectionMethod.CODEC.optionalFieldOf("selection", SelectionMethod.UNIFORM).forGetter(VariantGroup::selection),
                    CONFLICTS_CODEC.optionalFieldOf("conflicts", List.of()).forGetter(VariantGroup::conflicts),
                    Codec.unboundedMap(Codec.STRING, ExtraCodecs.JSON).xmap(map -> {
                        var jsonObject = new JsonObject();
                        map.forEach(jsonObject::add);
                        return jsonObject;
                    }, jsonObject -> {
                        var map = new LinkedHashMap<String, JsonElement>();
                        for (var element : jsonObject.entrySet())
                            map.put(element.getKey(), element.getValue());
                        return map;
                    }).optionalFieldOf("defaults", new JsonObject()).forGetter(VariantGroup::defaults)
            ).apply(instance, VariantGroup::new)
    );
}
