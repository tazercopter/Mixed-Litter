package dev.tazer.mixed_litter;

import com.mojang.serialization.Codec;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class MLDataAttachmentTypes {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MixedLitter.MODID);

    public static final Supplier<AttachmentType<String>> MOB_VARIANTS = ATTACHMENT_TYPES.register(
            "mob_variant", () -> AttachmentType.builder(() -> "").serialize(Codec.STRING).build()
    );
    public static final Supplier<AttachmentType<String>> SUB_VARIANT = ATTACHMENT_TYPES.register(
            "sub_variant", () -> AttachmentType.builder(() -> "").serialize(Codec.STRING).build()
    );
}
