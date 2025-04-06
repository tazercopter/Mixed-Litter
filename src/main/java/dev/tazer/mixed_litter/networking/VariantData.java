package dev.tazer.mixed_litter.networking;

import dev.tazer.mixed_litter.MixedLitter;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;

public record VariantData(int id, CompoundTag tag, String variants) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<VariantData> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(MixedLitter.MODID, "variant_data"));

    public static final StreamCodec<ByteBuf, VariantData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            VariantData::id,
            ByteBufCodecs.COMPOUND_TAG,
            VariantData::tag,
            ByteBufCodecs.STRING_UTF8,
            VariantData::variants,
            VariantData::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
