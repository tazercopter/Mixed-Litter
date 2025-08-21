package dev.tazer.mixed_litter.variants;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum SelectionMethod implements StringRepresentable {
    WEIGHTED,
    UNIFORM;

    public static final Codec<SelectionMethod> CODEC = StringRepresentable.fromValues(SelectionMethod::values);

    @Override
    public String getSerializedName() {
        return this.toString().toLowerCase();
    }
}
