package dev.tazer.mixed_litter.client;

import dev.tazer.mixed_litter.Config;

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
}
