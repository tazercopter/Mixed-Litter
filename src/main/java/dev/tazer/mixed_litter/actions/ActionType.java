package dev.tazer.mixed_litter.actions;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Map;

public interface ActionType {

    void initialize(JsonObject actionsArgs, JsonObject variantArgs, JsonObject defaultArgs);

    static JsonObject resolveArguments(JsonElement actionArgs, JsonObject variantArgs, JsonObject defaultArgs) {
        if (actionArgs == null) return null;

        JsonElement mutableArgs = actionArgs.deepCopy();

        if (mutableArgs.isJsonObject()) {
            JsonObject mutableObject = mutableArgs.getAsJsonObject();
            ArrayList<Map.Entry<String, JsonElement>> entries = new ArrayList<>(mutableObject.entrySet());

            for (Map.Entry<String, JsonElement> entry : entries) {
                JsonElement value = entry.getValue();

                if (value != null && value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()) {
                    String parameter = value.getAsString();

                    if (variantArgs.has(parameter)) {
                        mutableObject.add(entry.getKey(), variantArgs.get(parameter).deepCopy());
                        continue;
                    }

                    if (defaultArgs.has(parameter)) {
                        mutableObject.add(entry.getKey(), defaultArgs.get(parameter).deepCopy());
                        continue;
                    }

                    throw new IllegalArgumentException("Missing parameter '" + parameter + "' in variant arguments and defaults.");
                }

                if (value != null && (value.isJsonObject() || value.isJsonArray())) {
                    resolveArguments(value, variantArgs, defaultArgs);
                }
            }

            return mutableArgs.getAsJsonObject();
        }

        if (mutableArgs.isJsonArray()) {
            JsonArray mutableArray = mutableArgs.getAsJsonArray();

            for (int i = 0; i < mutableArray.size(); i++) {
                JsonElement element = mutableArray.get(i);

                if (element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                    String parameter = element.getAsString();

                    if (variantArgs.has(parameter)) {
                        mutableArray.set(i, variantArgs.get(parameter).deepCopy());
                        continue;
                    }

                    if (defaultArgs.has(parameter)) {
                        mutableArray.set(i, defaultArgs.get(parameter).deepCopy());
                        continue;
                    }

                    throw new IllegalArgumentException("Missing parameter '" + parameter + "' in variant arguments or defaults.");
                }

                if (element != null && (element.isJsonObject() || element.isJsonArray())) {
                    resolveArguments(element, variantArgs, defaultArgs);
                }
            }

            return mutableArgs.getAsJsonObject();
        }

        return null;
    }
}
