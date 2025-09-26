package com.thedrofdoctoring.synthetics.core;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class SyntheticsAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Synthetics.MODID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<SyntheticsPlayer>> SYNTHETICS_MANAGER = ATTACHMENT_TYPES.register(SyntheticsPlayer.MANAGER_KEY.getPath(), () -> AttachmentType.builder(new SyntheticsPlayer.Factory()).serialize(new SyntheticsPlayer.Serializer()).copyOnDeath().build());

    public static void register(IEventBus bus) {
        ATTACHMENT_TYPES.register(bus);
    }
}
