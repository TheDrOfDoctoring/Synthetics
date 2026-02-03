package com.thedrofdoctoring.synthetics.networking.from_server;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.SyntheticsClient;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record ClientboundLevelUUIDPacket(UUID uuid) implements CustomPacketPayload {

    public static final Type<ClientboundLevelUUIDPacket> TYPE = new Type<>(Synthetics.rl("level_uuid"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundLevelUUIDPacket> CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, ClientboundLevelUUIDPacket::uuid,
            ClientboundLevelUUIDPacket::new
    );

    @Override
    public @NotNull CustomPacketPayload.Type<ClientboundLevelUUIDPacket> type() {
        return TYPE;
    }

    public static void handle(ClientboundLevelUUIDPacket packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            SyntheticsClient.getInstance().getAdvancedClientConfig().setLevelUUID(packet.uuid);
            SyntheticsClient.getInstance().getAdvancedClientConfig().loadLevelConfig(context.player().level());
        });
    }
}
