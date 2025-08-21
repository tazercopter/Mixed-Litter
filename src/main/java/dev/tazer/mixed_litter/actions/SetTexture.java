package dev.tazer.mixed_litter.actions;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

public class SetTexture implements ActionType {
    public ResourceLocation texture;


    @Override
    public void initialize(JsonObject actionsArgs, JsonObject variantArgs, JsonObject defaultArgs) {
        JsonObject arguments = ActionType.resolveArguments(actionsArgs, variantArgs, defaultArgs);

        texture = ResourceLocation.parse(arguments.get("texture").getAsString()).withPath(path -> "textures/" + path + ".png");
    }
}
