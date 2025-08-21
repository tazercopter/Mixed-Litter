package dev.tazer.mixed_litter.mixin.actions;

import com.google.errorprone.annotations.Var;
import dev.tazer.mixed_litter.Registries;
import dev.tazer.mixed_litter.VariantUtil;
import dev.tazer.mixed_litter.actions.Action;
import dev.tazer.mixed_litter.actions.ActionType;
import dev.tazer.mixed_litter.actions.SetMooshroomMushroom;
import dev.tazer.mixed_litter.variants.Variant;
import dev.tazer.mixed_litter.variants.VariantGroup;
import dev.tazer.mixed_litter.variants.VariantType;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.event.EventHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.*;

import static dev.tazer.mixed_litter.VariantUtil.selectVariant;
import static dev.tazer.mixed_litter.VariantUtil.setVariants;

@Mixin(MushroomCow.class)
public abstract class SetMooshroomMushroomMixin {
    @Shadow
    @Nullable
    private UUID lastLightningBoltUUID;

    @Unique
    MushroomCow self = (MushroomCow) (Object) this;

    @Inject(method = "thunderHit", at = @At("HEAD"), cancellable = true)
    private void thunderRandomisesVariant(ServerLevel level, LightningBolt lightning, CallbackInfo ci) {
        UUID uuid = lightning.getUUID();

        if (!uuid.equals(lastLightningBoltUUID)) {
            Registry<VariantGroup> variantGroupRegistry = self.registryAccess().registryOrThrow(Registries.VARIANT_GROUP_KEY);
            Registry<Variant> variantRegistry = self.registryAccess().registryOrThrow(Registries.VARIANT_KEY);
            ArrayList<Holder<Variant>> availableVariants = new ArrayList<>(variantRegistry.holders().toList());
            ArrayList<Variant> selectedVariants = new ArrayList<>();

            availableVariants.removeIf(variant -> {
                VariantType variantType = VariantUtil.getType(self, variant.value());
                return variantType.actions().stream().filter(
                        action -> action.type() instanceof SetMooshroomMushroom
                ).toList().isEmpty();
            });

            for (Holder<VariantGroup> variantGroupHolder : variantGroupRegistry.holders().toList()) {
                VariantGroup group = variantGroupHolder.value();

                if (group.conditions().isPresent() && !group.conditions().get().matches(level, self.position(), self))
                    continue;

                ArrayList<Variant> matchingGroupVariants = new ArrayList<>();
                for (Holder<Variant> variantHolder : new ArrayList<>(availableVariants)) {
                    Variant variant = variantHolder.value();

                    variant.group().ifPresent(location -> {
                        if (variantGroupRegistry.get(location) == group) {
                            if (variant.conditions().isEmpty() || variant.conditions().get().matches(level, self.position(), self))
                                matchingGroupVariants.add(variant);
                            availableVariants.remove(variantHolder);
                        }
                    });
                }

                if (!matchingGroupVariants.isEmpty() && group.exclusive()) {
                    selectedVariants.add(selectVariant(group, matchingGroupVariants, self.getRandom()));
                }
            }

            for (Holder<Variant> variantHolder : availableVariants) {
                Variant variant = variantHolder.value();
                if (variant.group().isEmpty()) {
                    if (variant.conditions().isEmpty() || variant.conditions().get().matches(level, self.position(), self))
                        selectedVariants.add(variant);
                }
            }

            if (!selectedVariants.isEmpty()) {
                setVariants(self, selectedVariants);
            }
        }

        ci.cancel();
    }

    @Inject(method = "shear", at = @At("HEAD"), cancellable = true)
    private void biodiversity$shear(SoundSource category, CallbackInfo ci) {
        if (self.level() instanceof ServerLevel serverLevel) {
            // todo: make this randomise or mix or something between ALL variants the mooshroom has that contain mushrooms
            Block mushroom = null;

            for (Variant variant : VariantUtil.getVariants(self)) {
                VariantType variantType = VariantUtil.getType(self, variant);
                for (Action action : variantType.actions()) {
                    ActionType actionType = action.type();

                    actionType.initialize(action.arguments(), variant.arguments(), variantType.defaults());

                    if (actionType instanceof SetMooshroomMushroom setMooshroomMushroom) {
                        mushroom = setMooshroomMushroom.mushroom;
                    }
                }
            }

            if (mushroom != null) {
                self.level().playSound(null, self, SoundEvents.MOOSHROOM_SHEAR, category, 1.0F, 1.0F);
                if (!EventHooks.canLivingConvert(self, EntityType.COW, timer -> {})) {
                    return;
                }

                Cow cow = EntityType.COW.create(self.level());
                if (cow != null) {
                    EventHooks.onLivingConvert(self, cow);
                    serverLevel.sendParticles(ParticleTypes.EXPLOSION, self.getX(), self.getY(0.5), self.getZ(), 2, 0.0, 0.0, 0.0, 0.1);
                    self.discard();
                    cow.moveTo(self.getX(), self.getY(), self.getZ(), self.getYRot(), self.getXRot());
                    cow.setHealth(self.getHealth());
                    cow.yBodyRot = self.yBodyRot;
                    cow.setInvulnerable(self.isInvulnerable());
                    if (self.isPersistenceRequired()) cow.setPersistenceRequired();
                    cow.setCustomName(self.getCustomName());
                    cow.setCustomNameVisible(self.isCustomNameVisible());

                    self.level().addFreshEntity(cow);

                    if (mushroom != Blocks.AIR) {
                        for (int i = 0; i < self.getRandom().nextInt(3, 7); ++i) {
                            ItemEntity item = self.spawnAtLocation(new ItemStack(mushroom), self.getBbHeight());
                            if (item != null) {
                                item.setNoPickUpDelay();
                            }
                        }
                    }
                }

                ci.cancel();
            }
        }
    }
}
