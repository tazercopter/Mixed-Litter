package dev.tazer.mixed_litter.actions;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

public class SetSheepFurLayer implements VariantActionType {
    private boolean resolved;
    private ResourceLocation texture;
    private ResourceLocation babyTexture;
    private ResourceLocation shearedTexture;

    public ResourceLocation getTexture() { return resolved ? texture : null; }
    public ResourceLocation getBabyTexture() { return resolved ? babyTexture : null; }
    public ResourceLocation getShearedTexture() { return resolved ? shearedTexture : null; }

    @Override
    public SetSheepFurLayer resolve(JsonObject actionsArgs, JsonObject variantArgs, JsonObject defaultArgs) {
        SetSheepFurLayer result = new SetSheepFurLayer();
        JsonObject arguments = VariantActionType.resolveArguments(actionsArgs, variantArgs, defaultArgs);
        result.texture = VariantActionType.resolveTexturePath(arguments.get("texture").getAsString());
        result.babyTexture = VariantActionType.resolveTexturePath(arguments.get("baby_texture").getAsString());
        result.shearedTexture = VariantActionType.resolveTexturePath(arguments.get("sheared_texture").getAsString());
        result.resolved = true;
        return result;
    }
}
