package dev.tazer.mixed_litter.actions;

import com.google.gson.JsonObject;

public class SetRemodel implements VariantActionType {
    private boolean resolved;
    private String remodel;

    public String getRemodel() { return resolved && remodel != null && !remodel.isEmpty() ? remodel : null; }

    @Override
    public SetRemodel resolve(JsonObject actionsArgs, JsonObject variantArgs, JsonObject defaultArgs) {
        SetRemodel result = new SetRemodel();
        JsonObject arguments = VariantActionType.resolveArguments(actionsArgs, variantArgs, defaultArgs);
        result.remodel = arguments.get("remodel").getAsString();
        result.resolved = true;
        return result;
    }
}
