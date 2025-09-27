package dev.tazer.mixed_litter;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public class Config {

    public static ModConfigSpec STARTUP_CONFIG;
    public static final String CATEGORY_REMODELS = "remodels";
    public static ModConfigSpec.BooleanValue CHICKEN_REMODEL;
    public static ModConfigSpec.ConfigValue<List<? extends String>> CHICKENS;
    public static ModConfigSpec.BooleanValue COW_REMODEL;
    public static ModConfigSpec.ConfigValue<List<? extends String>> COWS;
    public static ModConfigSpec.BooleanValue PIG_REMODEL;
    public static ModConfigSpec.ConfigValue<List<? extends String>> PIGS;
    public static ModConfigSpec.BooleanValue RABBIT_REMODEL;
    public static ModConfigSpec.ConfigValue<List<? extends String>> RABBITS;
    public static ModConfigSpec.BooleanValue SHEEP_REMODEL;
    public static ModConfigSpec.ConfigValue<List<? extends String>> SHEEP;
    public static ModConfigSpec.BooleanValue SQUID_REMODEL;
    public static ModConfigSpec.ConfigValue<List<? extends String>> SQUIDS;

    static {
        ModConfigSpec.Builder STARTUP_BUILDER = new ModConfigSpec.Builder();

        STARTUP_BUILDER.push(CATEGORY_REMODELS);
        STARTUP_BUILDER.comment("Config changes require a restart to take effect");
        CHICKEN_REMODEL = STARTUP_BUILDER
                .comment("If the chicken remodel and texture variants are enabled")
                .gameRestart()
                .define("chickenRemodel", true);
        CHICKENS = STARTUP_BUILDER
                .comment("Entities that should use the chicken remodel")
                .gameRestart()
                .defineListAllowEmpty("chickens", () -> List.of("minecraft:chicken"), () -> "", s -> s instanceof String);
        COW_REMODEL = STARTUP_BUILDER
                .comment("If the cow remodel and texture variants are enabled")
                .gameRestart()
                .define("cowRemodel", true);
        COWS = STARTUP_BUILDER
                .comment("Entities that should use the cow remodel")
                .gameRestart()
                .defineListAllowEmpty("cows", () -> List.of("minecraft:cow", "minecraft:mooshroom", "buzzier_bees:moobloom"), () -> "", s -> s instanceof String);
        PIG_REMODEL = STARTUP_BUILDER
                .comment("If the pig remodel and texture variants are enabled")
                .gameRestart()
                .define("pigRemodel", true);
        PIGS = STARTUP_BUILDER
                .comment("Entities that should use the pig remodel")
                .gameRestart()
                .defineListAllowEmpty("pigs", () -> List.of("minecraft:pig"), () -> "", s -> s instanceof String);
        RABBIT_REMODEL = STARTUP_BUILDER
                .comment("If the rabbit remodel and texture variants are enabled")
                .gameRestart()
                .define("rabbitRemodel", true);
        RABBITS = STARTUP_BUILDER
                .comment("Entities that should use the rabbit remodel")
                .gameRestart()
                .defineListAllowEmpty("rabbits", () -> List.of("minecraft:rabbit"), () -> "", s -> s instanceof String);
        SHEEP_REMODEL = STARTUP_BUILDER
                .comment("If the sheep remodel and texture variants are enabled")
                .gameRestart()
                .define("sheepRemodel", true);
        SHEEP = STARTUP_BUILDER
                .comment("Entities that should use the sheep remodel")
                .gameRestart()
                .defineListAllowEmpty("sheep", () -> List.of("minecraft:sheep"), () -> "", s -> s instanceof String);
        SQUID_REMODEL = STARTUP_BUILDER
                .comment("If the squid remodel and texture variants are enabled")
                .gameRestart()
                .define("squidRemodel", true);
        SQUIDS = STARTUP_BUILDER
                .comment("Entities that should use the squid remodel")
                .gameRestart()
                .defineListAllowEmpty("squids", () -> List.of("minecraft:squid", "minecraft:glow_squid"), () -> "", s -> s instanceof String);
        STARTUP_BUILDER.pop();

        STARTUP_CONFIG = STARTUP_BUILDER.build();
    }

}
