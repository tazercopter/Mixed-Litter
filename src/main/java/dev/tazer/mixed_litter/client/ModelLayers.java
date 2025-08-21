package dev.tazer.mixed_litter.client;

import dev.tazer.mixed_litter.MixedLitter;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelLayers {

    public static final ModelLayerLocation PIG_LAYER = new ModelLayerLocation(MixedLitter.location("pig"), "main");
    public static final ModelLayerLocation CHICKEN_LAYER = new ModelLayerLocation(MixedLitter.location("chicken"), "main");
    public static final ModelLayerLocation COW_LAYER = new ModelLayerLocation(MixedLitter.location("cow"), "main");
    public static final ModelLayerLocation SHEEP_LAYER = new ModelLayerLocation(MixedLitter.location("sheep"), "main");
    public static final ModelLayerLocation SQUID_LAYER = new ModelLayerLocation(MixedLitter.location("squid"), "main");
    public static final ModelLayerLocation RABBIT_LAYER = new ModelLayerLocation(MixedLitter.location("rabbit"), "main");
}