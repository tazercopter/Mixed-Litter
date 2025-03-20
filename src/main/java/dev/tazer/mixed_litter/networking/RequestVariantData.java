package dev.tazer.mixed_litter.networking;

import dev.tazer.mixed_litter.MixedLitter;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record RequestVariantData(int mobId) implements CustomPacketPayload {
    public static final Type<RequestVariantData> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MixedLitter.MODID, "variant_request_data"));

    public static final StreamCodec<ByteBuf, RequestVariantData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            RequestVariantData::mobId,
            RequestVariantData::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
