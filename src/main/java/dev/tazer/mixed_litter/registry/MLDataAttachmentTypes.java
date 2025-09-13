package dev.tazer.mixed_litter.registry;

import com.mojang.serialization.Codec;
import dev.tazer.mixed_litter.MixedLitter;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;
import java.util.function.Supplier;

public class MLDataAttachmentTypes {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MixedLitter.MODID);

    public static final Supplier<AttachmentType<List<ResourceLocation>>> VARIANTS = register("variants", List.of(), ResourceLocation.CODEC.listOf(), ByteBufCodecs.fromCodec(ResourceLocation.CODEC.listOf()));
    public static final Supplier<AttachmentType<GlobalPos>> SPAWN_LOCATION = register("spawn_location", null, GlobalPos.CODEC, GlobalPos.STREAM_CODEC);

    public static <T> Supplier<AttachmentType<T>> register(String name, T defaultValue, Codec<T> codec, StreamCodec<ByteBuf, T> streamCodec) {
        return register(name, AttachmentType.builder(() -> defaultValue).serialize(codec).sync(streamCodec));
    }

    public static <T> Supplier<AttachmentType<T>> register(String name, AttachmentType.Builder<T> builder) {
        return ATTACHMENT_TYPES.register(name, builder::build);
    }
}
