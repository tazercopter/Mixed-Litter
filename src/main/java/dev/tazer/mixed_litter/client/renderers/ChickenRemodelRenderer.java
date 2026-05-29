package dev.tazer.mixed_litter.client.renderers;

import dev.tazer.mixed_litter.client.ModelLayers;
import dev.tazer.mixed_litter.client.models.ChickenRemodel;
import net.minecraft.client.renderer.entity.ChickenRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Chicken;

import java.util.function.Function;

public class ChickenRemodelRenderer extends ChickenRenderer implements RemodelMarker {
    private final Function<Chicken, ResourceLocation> texture;

    public ChickenRemodelRenderer(EntityRendererProvider.Context context, Function<Chicken, ResourceLocation> texture) {
        super(context);
        this.model = new ChickenRemodel<>(context.bakeLayer(ModelLayers.CHICKEN_LAYER));
        this.texture = texture;
    }

    @Override
    public ResourceLocation getTextureLocation(Chicken entity) {
        return texture.apply(entity);
    }
}
