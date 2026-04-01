package dev.tazer.mixed_litter.actions;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

public class SetSlimeOuterLayer implements VariantActionType {
    private boolean resolved;
    private ResourceLocation texture;

    public ResourceLocation getTexture() { return resolved ? texture : null; }

    @Override
    public SetSlimeOuterLayer resolve(JsonObject actionsArgs, JsonObject variantArgs, JsonObject defaultArgs) {
        SetSlimeOuterLayer result = new SetSlimeOuterLayer();
        JsonObject arguments = VariantActionType.resolveArguments(actionsArgs, variantArgs, defaultArgs);
        result.texture = VariantActionType.resolveTexturePath(arguments.get("texture").getAsString());
        result.resolved = true;
        return result;
    }
}
