package dev.tazer.mixed_litter.client.renderers;

import dev.tazer.mixed_litter.client.ModelLayers;
import dev.tazer.mixed_litter.client.models.CowRemodel;
import dev.tazer.mixed_litter.client.models.MooshroomMushroomLayer;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.MushroomCow;

import java.util.function.Function;

public class MooshroomRemodelRenderer extends MobRenderer<MushroomCow, CowModel<MushroomCow>> implements RemodelMarker {
    private final Function<MushroomCow, ResourceLocation> texture;

    public MooshroomRemodelRenderer(EntityRendererProvider.Context context, Function<MushroomCow, ResourceLocation> texture) {
        super(context, new CowRemodel<>(context.bakeLayer(ModelLayers.COW_LAYER)), 0.7F);
        this.texture = texture;
        addLayer(new MooshroomMushroomLayer<>(this, context.getBlockRenderDispatcher()));
    }

    @Override
    public ResourceLocation getTextureLocation(MushroomCow entity) {
        return texture.apply(entity);
    }
}
