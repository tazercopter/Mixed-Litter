package dev.tazer.mixed_litter.client.renderers;

import com.teamabnormals.buzzier_bees.common.entity.animal.Moobloom;
import dev.tazer.mixed_litter.client.ModelLayers;
import dev.tazer.mixed_litter.client.models.CowRemodel;
import dev.tazer.mixed_litter.client.models.MoobloomFlowerLayer;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class MoobloomRemodelRenderer extends MobRenderer<Moobloom, CowModel<Moobloom>> implements RemodelMarker {
    private final Function<Moobloom, ResourceLocation> texture;

    public MoobloomRemodelRenderer(EntityRendererProvider.Context context, Function<Moobloom, ResourceLocation> texture) {
        super(context, new CowRemodel<>(context.bakeLayer(ModelLayers.COW_LAYER)), 0.7F);
        this.texture = texture;
        addLayer(new MoobloomFlowerLayer<>(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(Moobloom entity) {
        return texture.apply(entity);
    }
}
