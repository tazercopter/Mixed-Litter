package dev.tazer.mixed_litter.client;

import dev.tazer.mixed_litter.Config;
import dev.tazer.mixed_litter.client.models.ChickenRemodel;
import dev.tazer.mixed_litter.client.models.CowRemodel;
import dev.tazer.mixed_litter.client.models.PigRemodel;
import dev.tazer.mixed_litter.client.models.RabbitRemodel;
import dev.tazer.mixed_litter.client.models.SheepRemodel;
import dev.tazer.mixed_litter.client.models.SquidRemodel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class RemodelRegistry {

    public record Factory(
            ModelLayerLocation layerLocation,
            Function<ModelPart, ?> modelFactory,
            Supplier<Boolean> enabled,
            Supplier<List<? extends String>> entityKeys
    ) {}

    private static final Map<String, Factory> FACTORIES = new HashMap<>();

    static {
        FACTORIES.put("pig", new Factory(ModelLayers.PIG_LAYER, PigRemodel::new, Config.PIG_REMODEL::get, Config.PIGS::get));
        FACTORIES.put("chicken", new Factory(ModelLayers.CHICKEN_LAYER, ChickenRemodel::new, Config.CHICKEN_REMODEL::get, Config.CHICKENS::get));
        FACTORIES.put("cow", new Factory(ModelLayers.COW_LAYER, CowRemodel::new, Config.COW_REMODEL::get, Config.COWS::get));
        FACTORIES.put("sheep", new Factory(ModelLayers.SHEEP_LAYER, SheepRemodel::new, Config.SHEEP_REMODEL::get, Config.SHEEP::get));
        FACTORIES.put("squid", new Factory(ModelLayers.SQUID_LAYER, SquidRemodel::new, Config.SQUID_REMODEL::get, Config.SQUIDS::get));
        FACTORIES.put("rabbit", new Factory(ModelLayers.RABBIT_LAYER, RabbitRemodel::new, Config.RABBIT_REMODEL::get, Config.RABBITS::get));
    }

    public static boolean isEnabled(String key, String entityKey) {
        Factory f = FACTORIES.get(key);
        if (f == null) return false;
        return f.enabled().get() && f.entityKeys().get().contains(entityKey);
    }

    public static Object createModel(String key, String entityKey, EntityModelSet modelSet) {
        Factory f = FACTORIES.get(key);
        if (f == null) return null;
        if (!f.enabled().get() || !f.entityKeys().get().contains(entityKey)) return null;
        return f.modelFactory().apply(modelSet.bakeLayer(f.layerLocation()));
    }

    public static Object createModelFromConfig(String entityKey, EntityModelSet modelSet) {
        for (Factory f : FACTORIES.values()) {
            if (f.enabled().get() && f.entityKeys().get().contains(entityKey)) {
                return f.modelFactory().apply(modelSet.bakeLayer(f.layerLocation()));
            }
        }
        return null;
    }
}
