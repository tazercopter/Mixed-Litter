package dev.tazer.mixed_litter;

import dev.tazer.mixed_litter.actions.*;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ActionTypes {
    public static final DeferredRegister<ActionType> ACTION_TYPES = DeferredRegister.create(Registries.ACTION_TYPES, MixedLitter.MODID);

    public static final Supplier<SetTexture> SET_TEXTURE = register("set_texture", new SetTexture());
    public static final Supplier<ReplaceTextures> REPLACE_TEXTURES = register("replace_textures", new ReplaceTextures());
    public static final Supplier<SetAgeableTexture> SET_AGEABLE_TEXTURE = register("set_ageable_texture", new SetAgeableTexture());
    public static final Supplier<SetSheepFurLayer> SET_SHEEP_FUR_LAYER = register("set_sheep_fur_layer", new SetSheepFurLayer());
    public static final Supplier<SetMooshroomMushroom> SET_MOOSHROOM_MUSHROOM = register("set_mooshroom_mushroom", new SetMooshroomMushroom());

    public static <T extends ActionType> Supplier<T> register(String name, T actionType) {
        return ACTION_TYPES.register(name, () -> actionType);
    }
}
