package dev.tazer.mixed_litter.mixin;

import com.mojang.serialization.MapCodec;
import dev.tazer.mixed_litter.MLRegistries;
import dev.tazer.mixed_litter.mixin.accessor.MobAccessor;
import dev.tazer.mixed_litter.variants.MobVariant;
import dev.tazer.mixed_litter.variants.remodels.CowVariant;
import dev.tazer.mixed_litter.variants.remodels.MooshroomVariant;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.event.EventHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.*;

import static dev.tazer.mixed_litter.VariantUtil.getVariants;
import static dev.tazer.mixed_litter.VariantUtil.setVariants;

@Mixin(MushroomCow.class)
public abstract class MushroomCowMixin implements MobAccessor {
    @Shadow
    @Nullable
    private UUID lastLightningBoltUUID;

    @Inject(method = "thunderHit", at = @At("HEAD"), cancellable = true)
    private void thunderRandomisesVariant(ServerLevel level, LightningBolt lightning, CallbackInfo ci) {
        UUID uuid = lightning.getUUID();

        if (!uuid.equals(lastLightningBoltUUID)) {
            Registry<MobVariant> variantTypeRegistry = invokeRegistryAccess().registryOrThrow(MLRegistries.ANIMAL_VARIANT_KEY);
            Map<MapCodec<? extends MobVariant>, List<MobVariant>> groupedAnimals = new HashMap<>();
            variantTypeRegistry.holders()
                    .filter(animalVariantReference -> animalVariantReference.value().isFor(invokeGetType()))
                    .forEach(animalVariantReference ->
                            groupedAnimals.computeIfAbsent(animalVariantReference.value().codec(), mapCodec -> new ArrayList<>()).add(animalVariantReference.value()));

            List<MobVariant> selectedVariants = new ArrayList<>();
            for (List<MobVariant> variants : groupedAnimals.values())
                selectedVariants.add(variants.get(invokeGetRandom().nextInt(variants.size())));

            setVariants((Mob) (Object) this, level, selectedVariants);
        }

        ci.cancel();
    }

    @Inject(method = "shear", at = @At("HEAD"), cancellable = true)
    private void biodiversity$shear(SoundSource category, CallbackInfo ci) {
        if (invokeLevel() instanceof ServerLevel serverLevel) {
            MooshroomVariant variant = null;

            for (Holder<MobVariant> animalVariantHolder : getVariants((Mob) (Object) this, serverLevel)) {
                if (animalVariantHolder.value() instanceof MooshroomVariant mooshroomVariant) {
                    variant = mooshroomVariant;
                    break;
                }
            }

            if (variant != null) {
                invokeLevel().playSound(null, (LivingEntity) (Object) this, SoundEvents.MOOSHROOM_SHEAR, category, 1.0F, 1.0F);
                if (!EventHooks.canLivingConvert((LivingEntity) (Object) this, EntityType.COW, (timer) -> {
                })) {
                    return;
                }

                Cow cow = EntityType.COW.create(invokeLevel());
                if (cow != null) {
                    EventHooks.onLivingConvert((LivingEntity) (Object) this, cow);
                    serverLevel.sendParticles(ParticleTypes.EXPLOSION, invokeGetX(), invokeGetY(0.5), invokeGetZ(), 2, 0.0, 0.0, 0.0, 0.1);
                    invokeDiscard();
                    cow.moveTo(invokeGetX(), invokeGetY(), invokeGetZ(), invokeGetYRot(), invokeGetXRot());
                    cow.setHealth(invokeGetHealth());
                    cow.yBodyRot = getYBodyRot();
                    cow.setInvulnerable(invokeIsInvulnerable());
                    if (invokeIsPersistenceRequired()) cow.setPersistenceRequired();
                    if (invokeHasCustomName()) {
                        cow.setCustomName(invokeGetCustomName());
                        cow.setCustomNameVisible(invokeIsCustomNameVisible());
                    }

                    Map<MapCodec<? extends MobVariant>, List<MobVariant>> groupedAnimals = new HashMap<>();
                    getVariants(cow, serverLevel).forEach(animalVariantReference ->
                            groupedAnimals.computeIfAbsent(animalVariantReference.value().codec(), mapCodec -> new ArrayList<>()).add(animalVariantReference.value()));

                    Registry<MobVariant> variantRegistry = invokeRegistryAccess().registryOrThrow(MLRegistries.ANIMAL_VARIANT_KEY);

                    List<MobVariant> selectedVariants = new ArrayList<>();
                    for (List<MobVariant> variants : groupedAnimals.values()) {
                        if (variants.stream().findAny().orElseThrow() instanceof CowVariant) {
                            selectedVariants.add(variantRegistry.get(variant.cowVariant));
                        } else selectedVariants.add(variants.stream().findAny().orElseThrow());
                    }

                    setVariants((Mob) (Object) this, serverLevel, selectedVariants);
                    invokeLevel().addFreshEntity(cow);

                    if (variant.mushroom != Blocks.AIR) {
                        for (int i = 0; i < invokeGetRandom().nextInt(3, 7); ++i) {
                            ItemEntity item = invokeSpawnAtLocation(new ItemStack(variant.mushroom), invokeGetBbHeight());
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
