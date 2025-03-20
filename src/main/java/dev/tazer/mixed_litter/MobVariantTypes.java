package dev.tazer.mixed_litter;

import com.mojang.serialization.MapCodec;
import dev.tazer.mixed_litter.variants.MobVariant;
import dev.tazer.mixed_litter.variants.animals.*;
import dev.tazer.mixed_litter.variants.monsters.DrownedVariant;
import dev.tazer.mixed_litter.variants.monsters.HuskVariant;
import dev.tazer.mixed_litter.variants.monsters.ZombieVariant;
import dev.tazer.mixed_litter.variants.remodels.*;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class MobVariantTypes {
    public static final DeferredRegister<MapCodec<? extends MobVariant>> VARIANT_TYPES = DeferredRegister.create(MLRegistries.ANIMAL_VARIANT_TYPE, MixedLitter.MODID);

    public static final Supplier<MapCodec<ChickenVariant>> CHICKEN_VARIANT = register("chicken", ChickenVariant.CODEC);
    public static final Supplier<MapCodec<CowVariant>> COW_VARIANT = register("cow", CowVariant.CODEC);
    public static final Supplier<MapCodec<MooshroomVariant>> MOOSHROOM_VARIANT = register("mooshroom", MooshroomVariant.CODEC);
    public static final Supplier<MapCodec<PigVariant>> PIG_VARIANT = register("pig", PigVariant.CODEC);
    public static final Supplier<MapCodec<RabbitVariant>> RABBIT_VARIANT = register("rabbit", RabbitVariant.CODEC);
    public static final Supplier<MapCodec<SheepVariant>> SHEEP_VARIANT = register("sheep", SheepVariant.CODEC);
    public static final Supplier<MapCodec<SquidVariant>> SQUID_VARIANT = register("squid", SquidVariant.CODEC);
    public static final Supplier<MapCodec<GlowSquidVariant>> GLOW_SQUID_VARIANT = register("glow_squid", GlowSquidVariant.CODEC);

    public static final Supplier<MapCodec<BeeVariant>> BEE_VARIANT = register("bee", BeeVariant.CODEC);
    public static final Supplier<MapCodec<CamelVariant>> CAMEL_VARIANT = register("camel", CamelVariant.CODEC);
    public static final Supplier<MapCodec<CodVariant>> COD_VARIANT = register("cod", CodVariant.CODEC);
    public static final Supplier<MapCodec<DolphinVariant>> DOLPHIN_VARIANT = register("dolphin", DolphinVariant.CODEC);
    public static final Supplier<MapCodec<DrownedVariant>> DROWNED_VARIANT = register("drowned", DrownedVariant.CODEC);
    public static final Supplier<MapCodec<FoxVariant>> FOX_VARIANT = register("fox", FoxVariant.CODEC);
    public static final Supplier<MapCodec<GoatVariant>> GOAT_VARIANT = register("goat", GoatVariant.CODEC);
    public static final Supplier<MapCodec<HuskVariant>> HUSK_VARIANT = register("husk", HuskVariant.CODEC);
    public static final Supplier<MapCodec<LLamaVariant>> LLAMA_VARIANT = register("llama", LLamaVariant.CODEC);
    public static final Supplier<MapCodec<SalmonVariant>> SALMON_VARIANT = register("salmon", SalmonVariant.CODEC);
    public static final Supplier<MapCodec<TurtleVariant>> TURTLE_VARIANT = register("turtle", TurtleVariant.CODEC);
    public static final Supplier<MapCodec<ZombieVariant>> ZOMBIE_VARIANT = register("zombie", ZombieVariant.CODEC);

    private static <P extends MobVariant> DeferredHolder<MapCodec<? extends MobVariant>, MapCodec<P>> register(String name, MapCodec<P> codec) {
        return VARIANT_TYPES.register(name, () -> codec);
    }
}
