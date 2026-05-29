package dev.tazer.mixed_litter.client.renderers;

import dev.tazer.mixed_litter.client.ModelLayers;
import dev.tazer.mixed_litter.client.models.CowRemodel;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Cow;

import java.util.function.Function;

public class CowRemodelRenderer extends MobRenderer<Cow, CowModel<Cow>> implements RemodelMarker {
    private final Function<Cow, ResourceLocation> texture;

    public CowRemodelRenderer(EntityRendererProvider.Context context, Function<Cow, ResourceLocation> texture) {
        super(context, new CowRemodel<>(context.bakeLayer(ModelLayers.COW_LAYER)), 0.7F);
        this.texture = texture;
    }

    @Override
    public ResourceLocation getTextureLocation(Cow entity) {
        return texture.apply(entity);
    }
}
