package com.thedrofdoctoring.synthetics.networking.from_server;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.capabilities.cache.SyntheticsPlayerCache;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ClientboundViewLinkPacket(boolean isNotViewingSelf) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientboundViewLinkPacket> TYPE = new Type<>(Synthetics.rl("view_linked_change"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundViewLinkPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ClientboundViewLinkPacket::isNotViewingSelf,
            ClientboundViewLinkPacket::new
    );


    @Override
    public @NotNull CustomPacketPayload.Type<ClientboundViewLinkPacket> type() {
        return TYPE;
    }

    public static void handle(ClientboundViewLinkPacket packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            SyntheticsPlayerCache cache = SyntheticsPlayerCache.get(context.player());
            if(Minecraft.getInstance().cameraEntity != context.player()) {
                cache.isNotViewingSelf = packet.isNotViewingSelf();
                Minecraft.getInstance().setScreen(null);
            }
        });
    }
}
