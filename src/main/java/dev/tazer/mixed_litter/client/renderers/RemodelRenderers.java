package dev.tazer.mixed_litter.client.renderers;

import dev.tazer.mixed_litter.RemodelRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.Squid;
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
            case "pig" -> entity instanceof Pig ? new PigRemodelRenderer(context, texture) : null;
            case "chicken" -> entity instanceof Chicken ? new ChickenRemodelRenderer(context, texture) : null;
            case "cow" -> {
                if (entityKey.equals("buzzier_bees:moobloom")) {
                    yield ModList.get().isLoaded("buzzier_bees") ? BuzzierBeesCompat.moobloomRenderer(context, texture) : null;
                }
                if (entity instanceof MushroomCow) {
                    yield new MooshroomRemodelRenderer(context, texture);
                }
                yield entity instanceof Cow ? new CowRemodelRenderer(context, texture) : null;
            }
            case "sheep" -> entity instanceof Sheep ? new SheepRemodelRenderer(context, texture) : null;
            case "squid" -> entity instanceof Squid ? new SquidRemodelRenderer(context, texture) : null;
            case "rabbit" -> entity instanceof Rabbit ? new RabbitRemodelRenderer(context, texture) : null;
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
