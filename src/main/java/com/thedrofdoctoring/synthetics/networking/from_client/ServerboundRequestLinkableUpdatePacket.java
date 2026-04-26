package com.thedrofdoctoring.synthetics.networking.from_client;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.capabilities.linkable.BlockLinkingPlayer;
import com.thedrofdoctoring.synthetics.networking.from_server.ClientboundLinkableUpdatePacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;


public class ServerboundRequestLinkableUpdatePacket implements CustomPacketPayload {
    private static ServerboundRequestLinkableUpdatePacket INSTANCE;

    private ServerboundRequestLinkableUpdatePacket() {}

    public static ServerboundRequestLinkableUpdatePacket getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new ServerboundRequestLinkableUpdatePacket();
        }
        return INSTANCE;
    }

    public static final CustomPacketPayload.Type<ServerboundRequestLinkableUpdatePacket> TYPE = new CustomPacketPayload.Type<>(Synthetics.rl("request_linkable_update"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundRequestLinkableUpdatePacket> CODEC = StreamCodec.unit(getInstance());

    @NotNull
    public Type<ServerboundRequestLinkableUpdatePacket> type() {
        return TYPE;
    }

    public static void handle(ServerboundRequestLinkableUpdatePacket __, final IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientboundLinkableUpdatePacket update = ClientboundLinkableUpdatePacket.create((ServerPlayer) context.player());
            BlockLinkingPlayer.get(context.player()).setLinkableData(update.linkingLocations());
            context.reply(update);
        });
    }
}
