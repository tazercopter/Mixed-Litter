package dev.tazer.mixed_litter.actions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class ReplaceTextures implements VariantActionType {
    private boolean resolved;
    private Map<ResourceLocation, ResourceLocation> replacements;

    public Map<ResourceLocation, ResourceLocation> getReplacements() { return resolved ? replacements : null; }

    @Override
    public ReplaceTextures resolve(JsonObject actionsArgs, JsonObject variantArgs, JsonObject defaultArgs) {
        ReplaceTextures result = new ReplaceTextures();
        JsonObject arguments = VariantActionType.resolveArguments(actionsArgs, variantArgs, defaultArgs);
        JsonObject replacementsObject = arguments.get("replacements").getAsJsonObject();
        result.replacements = new HashMap<>();

        for (Map.Entry<String, JsonElement> entry : replacementsObject.entrySet()) {
            ResourceLocation from = VariantActionType.resolveTexturePath(entry.getKey());
            ResourceLocation to = VariantActionType.resolveTexturePath(entry.getValue().getAsString());
            result.replacements.put(from, to);
        }

        result.resolved = true;
        return result;
    }
}
