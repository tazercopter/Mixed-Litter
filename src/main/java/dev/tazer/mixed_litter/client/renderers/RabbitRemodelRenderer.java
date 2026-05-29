package dev.tazer.mixed_litter.client.renderers;

import dev.tazer.mixed_litter.client.ModelLayers;
import dev.tazer.mixed_litter.client.models.RabbitRemodel;
import net.minecraft.client.model.RabbitModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Rabbit;

import java.util.function.Function;

public class RabbitRemodelRenderer extends MobRenderer<Rabbit, RabbitModel<Rabbit>> implements RemodelMarker {
    private final Function<Rabbit, ResourceLocation> texture;

    public RabbitRemodelRenderer(EntityRendererProvider.Context context, Function<Rabbit, ResourceLocation> texture) {
        super(context, new RabbitRemodel<>(context.bakeLayer(ModelLayers.RABBIT_LAYER)), 0.3F);
        this.texture = texture;
    }

    @Override
    public ResourceLocation getTextureLocation(Rabbit entity) {
        return texture.apply(entity);
    }
}
