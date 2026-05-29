package dev.tazer.mixed_litter.client.renderers;

import dev.tazer.mixed_litter.client.ModelLayers;
import dev.tazer.mixed_litter.client.models.SheepRemodel;
import dev.tazer.mixed_litter.client.models.SheepRemodelFurLayer;
import net.minecraft.client.model.SheepModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Sheep;

import java.util.function.Function;

public class SheepRemodelRenderer extends MobRenderer<Sheep, SheepModel<Sheep>> implements RemodelMarker {
    private final Function<Sheep, ResourceLocation> texture;

    public SheepRemodelRenderer(EntityRendererProvider.Context context, Function<Sheep, ResourceLocation> texture) {
        super(context, new SheepRemodel<>(context.bakeLayer(ModelLayers.SHEEP_LAYER)), 0.7F);
        this.texture = texture;
        addLayer(new SheepRemodelFurLayer(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(Sheep entity) {
        return texture.apply(entity);
    }
}
