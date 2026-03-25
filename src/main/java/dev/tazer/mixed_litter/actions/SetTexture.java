package dev.tazer.mixed_litter.actions;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

public class SetTexture implements VariantActionType {
    private boolean resolved;
    private ResourceLocation texture;

    public ResourceLocation getTexture() { return resolved ? texture : null; }

    @Override
    public SetTexture resolve(JsonObject actionsArgs, JsonObject variantArgs, JsonObject defaultArgs) {
        SetTexture result = new SetTexture();
        JsonObject arguments = VariantActionType.resolveArguments(actionsArgs, variantArgs, defaultArgs);
        result.texture = VariantActionType.resolveTexturePath(arguments.get("texture").getAsString());
        result.resolved = true;
        return result;
    }
}
