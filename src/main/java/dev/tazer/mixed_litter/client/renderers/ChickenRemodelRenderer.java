package dev.tazer.mixed_litter.client.renderers;

import dev.tazer.mixed_litter.client.ModelLayers;
import dev.tazer.mixed_litter.client.models.ChickenRemodel;
import net.minecraft.client.model.ChickenModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Chicken;

import java.util.function.Function;

public class ChickenRemodelRenderer extends MobRenderer<Chicken, ChickenModel<Chicken>> implements RemodelMarker {
    private final Function<Chicken, ResourceLocation> texture;

    public ChickenRemodelRenderer(EntityRendererProvider.Context context, Function<Chicken, ResourceLocation> texture) {
        super(context, new ChickenRemodel<>(context.bakeLayer(ModelLayers.CHICKEN_LAYER)), 0.3F);
        this.texture = texture;
    }

    @Override
    public ResourceLocation getTextureLocation(Chicken entity) {
        return texture.apply(entity);
    }
}
