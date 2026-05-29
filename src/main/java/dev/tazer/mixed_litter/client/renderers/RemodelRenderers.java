package dev.tazer.mixed_litter.client.renderers;

import dev.tazer.mixed_litter.client.RemodelRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.fml.ModList;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class RemodelRenderers {
    private RemodelRenderers() {}

    private static final Map<EntityType<?>, LivingEntityRenderer<?, ?>> CACHE = new HashMap<>();

    public static void clear() {
        CACHE.clear();
    }

    public static LivingEntityRenderer<?, ?> get(LivingEntity entity, LivingEntityRenderer<?, ?> vanilla) {
        EntityType<?> type = entity.getType();
        if (CACHE.containsKey(type)) {
            return CACHE.get(type);
        }
        LivingEntityRenderer<?, ?> built = build(entity, vanilla);
        CACHE.put(type, built);
        return built;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static LivingEntityRenderer<?, ?> build(LivingEntity entity, LivingEntityRenderer<?, ?> vanilla) {
        ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        if (id == null) return null;
        String entityKey = id.toString();

        String remodel = RemodelRegistry.remodelFor(entityKey);
        if (remodel == null) return null;

        EntityRendererProvider.Context context = context();
        LivingEntityRenderer raw = vanilla;
        Function texture = e -> raw.getTextureLocation((Entity) e);

        return switch (remodel) {
            case "pig" -> new PigRemodelRenderer(context, texture);
            case "chicken" -> new ChickenRemodelRenderer(context, texture);
            case "cow" -> {
                if (entityKey.equals("minecraft:mooshroom")) {
                    yield new MooshroomRemodelRenderer(context, texture);
                }
                if (entityKey.equals("buzzier_bees:moobloom")) {
                    yield ModList.get().isLoaded("buzzier_bees") ? BuzzierBeesCompat.moobloomRenderer(context, texture) : null;
                }
                yield new CowRemodelRenderer(context, texture);
            }
            case "sheep" -> new SheepRemodelRenderer(context, texture);
            case "squid" -> new SquidRemodelRenderer(context, texture);
            case "rabbit" -> new RabbitRemodelRenderer(context, texture);
            default -> null;
        };
    }

    private static EntityRendererProvider.Context context() {
        Minecraft mc = Minecraft.getInstance();
        return new EntityRendererProvider.Context(
                mc.getEntityRenderDispatcher(),
                mc.getItemRenderer(),
                mc.getBlockRenderer(),
                mc.getEntityRenderDispatcher().getItemInHandRenderer(),
                mc.getResourceManager(),
                mc.getEntityModels(),
                mc.font
        );
    }
}
