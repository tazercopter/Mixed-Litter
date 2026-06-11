package dev.tazer.mixed_litter;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class RemodelRegistry {

    private record Entry(Supplier<Boolean> enabled, Supplier<List<? extends String>> entityKeys) {}

    private static final Map<String, Entry> ENTRIES = new LinkedHashMap<>();

    static {
        ENTRIES.put("pig", new Entry(Config.PIG_REMODEL::get, Config.PIGS::get));
        ENTRIES.put("chicken", new Entry(Config.CHICKEN_REMODEL::get, Config.CHICKENS::get));
        ENTRIES.put("cow", new Entry(Config.COW_REMODEL::get, Config.COWS::get));
        ENTRIES.put("sheep", new Entry(Config.SHEEP_REMODEL::get, Config.SHEEP::get));
        ENTRIES.put("squid", new Entry(Config.SQUID_REMODEL::get, Config.SQUIDS::get));
        ENTRIES.put("rabbit", new Entry(Config.RABBIT_REMODEL::get, Config.RABBITS::get));
    }

    public static String remodelFor(String entityKey) {
        if (!Config.STARTUP_CONFIG.isLoaded()) return null;
        for (Map.Entry<String, Entry> entry : ENTRIES.entrySet()) {
            Entry e = entry.getValue();
            if (e.enabled().get() && e.entityKeys().get().contains(entityKey)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static String remodelFor(EntityType<?> type) {
        return remodelFor(BuiltInRegistries.ENTITY_TYPE.getKey(type).toString());
    }

    public static boolean remodelActive(EntityType<?> type) {
        return remodelFor(type) != null;
    }

    public static ResourceLocation fallbackTexture(Entity entity) {
        String remodel = remodelFor(entity.getType());
        if (remodel == null) return null;
        boolean baby = entity instanceof AgeableMob ageable && ageable.isBaby();
        String path = switch (remodel) {
            case "pig" -> baby ? "pig/pig_baby" : "pig/pig";
            case "chicken" -> baby ? "chicken/chicken_baby" : "chicken/chicken";
            case "cow" -> baby ? "cow/cow_baby" : "cow/cow";
            case "sheep" -> baby ? "sheep/sheep_baby" : "sheep/sheep";
            case "squid" -> "squid";
            case "rabbit" -> "rabbit/rabbit_brown";
            default -> null;
        };
        return path == null ? null : MixedLitter.location("textures/entity/" + path + ".png");
    }
}
