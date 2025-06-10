package dev.tazer.mixed_litter;

import net.neoforged.neoforge.common.ModConfigSpec;

public class MLConfig {

    public static ModConfigSpec CLIENT_CONFIG;
    public static final String CATEGORY_REMODELS = "animal_remodels";
    public static ModConfigSpec.BooleanValue CHICKEN;
    public static ModConfigSpec.BooleanValue COW;
    public static ModConfigSpec.BooleanValue PIG;
    public static ModConfigSpec.BooleanValue RABBIT;
    public static ModConfigSpec.BooleanValue SHEEP;
    public static ModConfigSpec.BooleanValue SQUID;

    static {
        ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();

        CLIENT_BUILDER.push(CATEGORY_REMODELS);
        CLIENT_BUILDER.comment("Config changes require a restart to take effect");
        CHICKEN = CLIENT_BUILDER
                .comment("If the chicken remodel and texture variants are enabled")
                .gameRestart()
                .define("chickenRemodel", true);
        COW = CLIENT_BUILDER
                .comment("If the cow remodel and texture variants are enabled")
                .gameRestart()
                .define("cowRemodel", true);
        PIG = CLIENT_BUILDER
                .comment("If the pig remodel and texture variants are enabled")
                .gameRestart()
                .define("pigRemodel", true);
        RABBIT = CLIENT_BUILDER
                .comment("If the rabbit remodel and texture variants are enabled")
                .gameRestart()
                .define("rabbitRemodel", true);
        SHEEP = CLIENT_BUILDER
                .comment("If the sheep remodel and texture variants are enabled")
                .gameRestart()
                .define("sheepRemodel", true);
        SQUID = CLIENT_BUILDER
                .comment("If the squid remodel and texture variants are enabled")
                .gameRestart()
                .define("squidRemodel", true);
        CLIENT_BUILDER.pop();

        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }

}
