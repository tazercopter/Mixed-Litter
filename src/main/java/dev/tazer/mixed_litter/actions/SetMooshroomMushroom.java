package dev.tazer.mixed_litter.actions;

import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class SetMooshroomMushroom implements VariantActionType {
    private boolean resolved;
    private Block block;

    public Block getBlock() { return resolved ? block : null; }

    @Override
    public SetMooshroomMushroom resolve(JsonObject actionsArgs, JsonObject variantArgs, JsonObject defaultArgs) {
        SetMooshroomMushroom result = new SetMooshroomMushroom();
        JsonObject arguments = VariantActionType.resolveArguments(actionsArgs, variantArgs, defaultArgs);
        result.block = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(arguments.get("mushroom").getAsString()));
        result.resolved = true;
        return result;
    }
}
