package dev.tazer.mixed_litter.actions;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

public class SetAgeableTexture implements VariantActionType {
    public ResourceLocation texture;
    public ResourceLocation babyTexture;

    @Override
    public void initialize(JsonObject actionsArgs, JsonObject variantArgs, JsonObject defaultArgs) {
        JsonObject arguments = VariantActionType.resolveArguments(actionsArgs, variantArgs, defaultArgs);

        texture = ResourceLocation.parse(arguments.get("texture").getAsString()).withPath(path -> "textures/" + path + ".png");
        babyTexture = ResourceLocation.parse(arguments.get("baby_texture").getAsString()).withPath(path -> "textures/" + path + ".png");
    }
}
