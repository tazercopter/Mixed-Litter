package dev.tazer.mixed_litter.client.renderers;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;

import java.util.function.Function;

public final class BuzzierBeesCompat {
    private BuzzierBeesCompat() {}

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static LivingEntityRenderer<?, ?> moobloomRenderer(EntityRendererProvider.Context context, Function texture) {
        return new MoobloomRemodelRenderer(context, texture);
    }
}
