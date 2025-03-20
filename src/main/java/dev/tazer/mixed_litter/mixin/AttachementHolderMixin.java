package dev.tazer.mixed_litter.mixin;

import net.neoforged.neoforge.attachment.AttachmentHolder;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AttachmentHolder.class)
public abstract class AttachementHolderMixin {
    @Shadow public abstract <T> T getData(AttachmentType<T> type);
}
