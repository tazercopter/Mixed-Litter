package dev.tazer.mixed_litter.client.renderers;

import dev.tazer.mixed_litter.client.ModelLayers;
import dev.tazer.mixed_litter.client.models.SquidRemodel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SquidRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.GlowSquid;
import net.minecraft.world.entity.animal.Squid;

import java.util.function.Function;

public class SquidRemodelRenderer extends SquidRenderer<Squid> implements RemodelMarker {
    private final Function<Squid, ResourceLocation> texture;

    public SquidRemodelRenderer(EntityRendererProvider.Context context, Function<Squid, ResourceLocation> texture) {
        super(context, new SquidRemodel<>(context.bakeLayer(ModelLayers.SQUID_LAYER)));
        this.texture = texture;
    }

    @Override
    public ResourceLocation getTextureLocation(Squid entity) {
        return texture.apply(entity);
    }

    @Override
    protected int getBlockLightLevel(Squid entity, BlockPos pos) {
        return entity instanceof GlowSquid ? 15 : super.getBlockLightLevel(entity, pos);
    }
}
