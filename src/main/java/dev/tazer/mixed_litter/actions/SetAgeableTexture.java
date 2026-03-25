package dev.tazer.mixed_litter.actions;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

public class SetAgeableTexture implements VariantActionType {
    private boolean resolved;
    private ResourceLocation texture;
    private ResourceLocation babyTexture;

    public ResourceLocation getTexture() { return resolved ? texture : null; }
    public ResourceLocation getBabyTexture() { return resolved ? babyTexture : null; }

    @Override
    public SetAgeableTexture resolve(JsonObject actionsArgs, JsonObject variantArgs, JsonObject defaultArgs) {
        SetAgeableTexture result = new SetAgeableTexture();
        JsonObject arguments = VariantActionType.resolveArguments(actionsArgs, variantArgs, defaultArgs);
        result.texture = VariantActionType.resolveTexturePath(arguments.get("texture").getAsString());
        result.babyTexture = VariantActionType.resolveTexturePath(arguments.get("baby_texture").getAsString());
        result.resolved = true;
        return result;
    }
}
