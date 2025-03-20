package dev.tazer.mixed_litter.mixin;

import dev.tazer.mixed_litter.VariantDataHolder;
import dev.tazer.mixed_litter.networking.RequestVariantData;
import net.minecraft.world.entity.Mob;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntityMixin implements VariantDataHolder {
    @Shadow public abstract boolean isPersistenceRequired();

    @Unique
    private String mixedLitter$variantData = "";

    @Override
    public void mixedLitter$setVariantData(String variantData) {
        mixedLitter$variantData = variantData;
    }

    @Override
    public String mixedLitter$getVariantData() {
        if (mixedLitter$variantData.isEmpty()) PacketDistributor.sendToServer(new RequestVariantData(getId()));
        return mixedLitter$variantData;
    }
}