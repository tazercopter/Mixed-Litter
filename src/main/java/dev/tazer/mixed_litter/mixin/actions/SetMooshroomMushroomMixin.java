package dev.tazer.mixed_litter.mixin.actions;

import dev.tazer.mixed_litter.MLRegistries;
import dev.tazer.mixed_litter.VariantUtil;
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
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.event.EventHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
            Registry<VariantGroup> variantGroupRegistry = self.registryAccess().registryOrThrow(MLRegistries.VARIANT_GROUP_KEY);
            Registry<Variant> variantRegistry = self.registryAccess().registryOrThrow(MLRegistries.VARIANT_KEY);
            ArrayList<Holder<Variant>> availableVariants = new ArrayList<>(variantRegistry.holders().toList());

            availableVariants.removeIf(variant -> {
                VariantType variantType = VariantUtil.getType(self, variant.value());
                if (variantType == null) return true;
                return variantType.actions().stream().noneMatch(action -> action.type() instanceof SetMooshroomMushroom);
            });

            List<Variant> selectedVariants = VariantUtil.collectVariants(
                    self, level, availableVariants, variantGroupRegistry,
                    v -> true,
                    (group, variants) -> selectVariant(group, variants, self.getRandom()),
                    true
            );

            if (!selectedVariants.isEmpty()) {
                setVariants(self, selectedVariants);
            }
        }

        ci.cancel();
    }

    @Redirect(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/MushroomCow;getVariant()Lnet/minecraft/world/entity/animal/MushroomCow$MushroomType;"))
    private MushroomCow.MushroomType hasBrownMushroom(MushroomCow instance) {
        SetMooshroomMushroom mushroom = VariantUtil.findAction(self, SetMooshroomMushroom.class);
        return mushroom != null && mushroom.getBlock() == Blocks.BROWN_MUSHROOM ? MushroomCow.MushroomType.BROWN : MushroomCow.MushroomType.RED;
    }

    @Inject(method = "shear", at = @At("HEAD"), cancellable = true)
    private void shear(SoundSource category, CallbackInfo ci) {
        if (self.level() instanceof ServerLevel serverLevel) {
            SetMooshroomMushroom mushroom = VariantUtil.findAction(self, SetMooshroomMushroom.class);

            if (mushroom != null && mushroom.getBlock() != null) {
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

                    for (int i = 0; i < self.getRandom().nextInt(3, 7); ++i) {
                        ItemEntity item = self.spawnAtLocation(new ItemStack(mushroom.getBlock()), self.getBbHeight());
                        if (item != null) {
                            item.setNoPickUpDelay();
                        }
                    }
                }

                ci.cancel();
            }
        }
    }
}
