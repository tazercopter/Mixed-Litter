package dev.tazer.mixed_litter;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.tazer.mixed_litter.registry.MLDataAttachmentTypes;
import dev.tazer.mixed_litter.variants.Variant;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.*;

import static dev.tazer.mixed_litter.VariantUtil.*;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = MixedLitter.MODID)
public class VariantCommand {

    private static final SuggestionProvider<CommandSourceStack> SUGGEST_ALL_VARIANTS = (ctx, builder) -> {
        Registry<Variant> reg = ctx.getSource().registryAccess().registryOrThrow(MLRegistries.VARIANT_KEY);
        return SharedSuggestionProvider.suggestResource(reg.keySet().stream(), builder);
    };

    private static final SuggestionProvider<CommandSourceStack> SUGGEST_TARGET_VARIANTS = (ctx, builder) -> {
        Set<ResourceLocation> present = new HashSet<>();
        try {
            for (Entity entity : EntityArgument.getEntities(ctx, "targets")) {
                if (entity.hasData(MLDataAttachmentTypes.VARIANTS)) {
                    present.addAll(entity.getData(MLDataAttachmentTypes.VARIANTS));
                }
            }
        } catch (com.mojang.brigadier.exceptions.CommandSyntaxException ignored) {
        }
        return SharedSuggestionProvider.suggestResource(present.stream(), builder);
    };

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("variant")
                .requires(src -> src.hasPermission(2));

        root.then(Commands.literal("get")
                .then(Commands.argument("targets", EntityArgument.entities())
                        .executes(VariantCommand::get)));

        root.then(Commands.literal("add")
                .then(Commands.argument("targets", EntityArgument.entities())
                        .then(Commands.argument("variant", ResourceLocationArgument.id())
                                .suggests(SUGGEST_ALL_VARIANTS)
                                .executes(VariantCommand::add))));

        root.then(Commands.literal("remove")
                .then(Commands.argument("targets", EntityArgument.entities())
                        .then(Commands.argument("variant", ResourceLocationArgument.id())
                                .suggests(SUGGEST_TARGET_VARIANTS)
                                .executes(VariantCommand::remove))));

        root.then(Commands.literal("clear")
                .then(Commands.argument("targets", EntityArgument.entities())
                        .executes(VariantCommand::clear)));

        root.then(Commands.literal("reroll")
                .then(Commands.argument("targets", EntityArgument.entities())
                        .executes(VariantCommand::reroll)));

        event.getDispatcher().register(root);
    }

    private static int get(CommandContext<CommandSourceStack> ctx) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(ctx, "targets");
        for (Entity entity : targets) {
            List<ResourceLocation> ids = entity.hasData(MLDataAttachmentTypes.VARIANTS)
                    ? entity.getData(MLDataAttachmentTypes.VARIANTS) : List.of();
            String label = ids.isEmpty() ? "(none)" : String.join(", ", ids.stream().map(ResourceLocation::toString).toList());
            ctx.getSource().sendSuccess(() -> Component.literal(entity.getName().getString() + ": " + label), false);
        }
        return targets.size();
    }

    private static int add(CommandContext<CommandSourceStack> ctx) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(ctx, "targets");
        ResourceLocation id = ResourceLocationArgument.getId(ctx, "variant");
        int count = 0;
        for (Entity entity : targets) {
            Registry<Variant> reg = entity.registryAccess().registryOrThrow(MLRegistries.VARIANT_KEY);
            Variant variant = reg.get(id);
            if (variant == null) continue;
            List<Variant> current = new ArrayList<>(getVariants(entity));
            if (!current.contains(variant)) current.add(variant);
            setVariants(entity, current);
            count++;
        }
        int applied = count;
        ctx.getSource().sendSuccess(() -> Component.literal("Added " + id + " to " + applied + " entit" + (targets.size() > 1 ? "ies" : "y")), true);
        return count;
    }

    private static int remove(CommandContext<CommandSourceStack> ctx) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(ctx, "targets");
        ResourceLocation id = ResourceLocationArgument.getId(ctx, "variant");
        int count = 0;
        for (Entity entity : targets) {
            Registry<Variant> reg = entity.registryAccess().registryOrThrow(MLRegistries.VARIANT_KEY);
            Variant variant = reg.get(id);
            if (variant == null) continue;
            List<Variant> current = new ArrayList<>(getVariants(entity));
            if (current.remove(variant)) {
                setVariants(entity, current);
                count++;
            }
        }
        int applied = count;
        ctx.getSource().sendSuccess(() -> Component.literal("Removed " + id + " from " + applied + " entit" + (targets.size() > 1 ? "ies" : "y")), true);
        return count;
    }

    private static int clear(CommandContext<CommandSourceStack> ctx) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(ctx, "targets");
        for (Entity entity : targets) {
            entity.removeData(MLDataAttachmentTypes.VARIANTS);
        }
        ctx.getSource().sendSuccess(() -> Component.literal("Cleared variants on " + targets.size() + " entit" + (targets.size() > 1 ? "ies" : "y")), true);
        return targets.size();
    }

    private static int reroll(CommandContext<CommandSourceStack> ctx) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(ctx, "targets");
        for (Entity entity : targets) {
            if (entity.level().isClientSide) continue;
            entity.removeData(MLDataAttachmentTypes.VARIANTS);
            applySuitableVariants(entity);
        }
        ctx.getSource().sendSuccess(() -> Component.literal("Rerolled variants on " + targets.size() + " entit" + (targets.size() > 1 ? "ies" : "y")), true);
        return targets.size();
    }
}
