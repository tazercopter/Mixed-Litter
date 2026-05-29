package dev.tazer.mixed_litter.client.renderers;

import dev.tazer.mixed_litter.client.ModelLayers;
import dev.tazer.mixed_litter.client.models.PigRemodel;
import net.minecraft.client.model.PigModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Pig;

import java.util.function.Function;

public class PigRemodelRenderer extends MobRenderer<Pig, PigModel<Pig>> implements RemodelMarker {
    private final Function<Pig, ResourceLocation> texture;

    public PigRemodelRenderer(EntityRendererProvider.Context context, Function<Pig, ResourceLocation> texture) {
        super(context, new PigRemodel<>(context.bakeLayer(ModelLayers.PIG_LAYER)), 0.7F);
        this.texture = texture;
    }

    @Override
    public ResourceLocation getTextureLocation(Pig entity) {
        return texture.apply(entity);
    }
}
