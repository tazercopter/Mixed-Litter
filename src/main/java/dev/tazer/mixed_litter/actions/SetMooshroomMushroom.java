package dev.tazer.mixed_litter.actions;

import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class SetMooshroomMushroom implements VariantActionType {
    public Block mushroom;

    @Override
    public void initialize(JsonObject actionsArgs, JsonObject variantArgs, JsonObject defaultArgs) {
        JsonObject arguments = VariantActionType.resolveArguments(actionsArgs, variantArgs, defaultArgs);

        mushroom = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(arguments.get("mushroom").getAsString()));
    }
}
