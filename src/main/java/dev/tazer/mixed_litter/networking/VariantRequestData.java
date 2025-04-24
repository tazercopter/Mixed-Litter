package dev.tazer.mixed_litter.networking;

import dev.tazer.mixed_litter.MixedLitter;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record VariantRequestData(int id, CompoundTag tag) implements CustomPacketPayload {
    public static final Type<VariantRequestData> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MixedLitter.MODID, "variant_request_data"));

    public static final StreamCodec<ByteBuf, VariantRequestData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            VariantRequestData::id,
            ByteBufCodecs.COMPOUND_TAG,
            VariantRequestData::tag,
            VariantRequestData::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
