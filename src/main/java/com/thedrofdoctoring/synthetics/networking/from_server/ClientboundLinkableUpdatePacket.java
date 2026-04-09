package com.thedrofdoctoring.synthetics.networking.from_server;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.capabilities.linkable.BlockLinkingPlayer;
import com.thedrofdoctoring.synthetics.capabilities.linkable.LinkableBlockLocation;
import com.thedrofdoctoring.synthetics.world.data.LinkableBlocksData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public record ClientboundLinkableUpdatePacket(List<LinkableBlockLocation> linkingLocations) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ClientboundLinkableUpdatePacket> TYPE = new CustomPacketPayload.Type<>(Synthetics.rl("update_linkable_player"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundLinkableUpdatePacket> CODEC = StreamCodec.composite(
            LinkableBlockLocation.STREAM_CODEC.apply(ByteBufCodecs.list()), ClientboundLinkableUpdatePacket::linkingLocations,
            ClientboundLinkableUpdatePacket::new
    );

    public static @NotNull ClientboundLinkableUpdatePacket create(ServerPlayer player) {
        MinecraftServer server = player.level().getServer();
        if(server == null) {
            Synthetics.LOGGER.warn("Failed to get server when creating linkable update packet");
            return new ClientboundLinkableUpdatePacket(Collections.emptyList());
        }
        return new ClientboundLinkableUpdatePacket(LinkableBlocksData.getData(server).getAllLocationsLinkedToPlayer(player));
    }



    @Override
    public @NotNull CustomPacketPayload.Type<ClientboundLinkableUpdatePacket> type() {
        return TYPE;
    }

    public static void handle(ClientboundLinkableUpdatePacket packet, final IPayloadContext context) {
        context.enqueueWork(() -> BlockLinkingPlayer.get(context.player()).setLinkableData(packet.linkingLocations()));
    }
}
