package dev.tazer.mixed_litter.actions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class ReplaceTextures implements VariantActionType {
    public Map<ResourceLocation, ResourceLocation> replacements;


    @Override
    public void initialize(JsonObject actionsArgs, JsonObject variantArgs, JsonObject defaultArgs) {
        JsonObject arguments = VariantActionType.resolveArguments(actionsArgs, variantArgs, defaultArgs);

        JsonElement replacementsElement = arguments.get("replacements");
        JsonObject replacementsObject = replacementsElement.getAsJsonObject();
        Map<ResourceLocation, ResourceLocation> map = new HashMap<>();

        for (Map.Entry<String, JsonElement> entry : replacementsObject.entrySet()) {
            String fromKey = entry.getKey();
            JsonElement toValueElement = entry.getValue();

            ResourceLocation from = ResourceLocation.parse(fromKey).withPath(path -> "textures/" + path + ".png");
            ResourceLocation to = ResourceLocation.parse(toValueElement.getAsString()).withPath(path -> "textures/" + path + ".png");

            map.put(from, to);
        }

        replacements = map;
    }
}
