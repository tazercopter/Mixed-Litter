package dev.tazer.mixed_litter.actions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

public class SetSheepFurLayer implements ActionType {
    public ResourceLocation texture;
    public ResourceLocation babyTexture;
    public ResourceLocation shearedTexture;

    @Override
    public void initialize(JsonObject actionsArgs, JsonObject variantArgs, JsonObject defaultArgs) {
        JsonObject arguments = ActionType.resolveArguments(actionsArgs, variantArgs, defaultArgs);

        texture = ResourceLocation.parse(arguments.get("texture").getAsString()).withPath(path -> "textures/" + path + ".png");
        babyTexture = ResourceLocation.parse(arguments.get("baby_texture").getAsString()).withPath(path -> "textures/" + path + ".png");
        shearedTexture = ResourceLocation.parse(arguments.get("sheared_texture").getAsString()).withPath(path -> "textures/" + path + ".png");
    }
}
