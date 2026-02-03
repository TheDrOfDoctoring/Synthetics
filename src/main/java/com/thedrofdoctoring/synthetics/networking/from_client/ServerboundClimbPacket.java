package com.thedrofdoctoring.synthetics.networking.from_client;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.capabilities.cache.SyntheticsPlayerCache;
import com.thedrofdoctoring.synthetics.networking.from_server.ClientboundWallClimbPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ServerboundClimbPacket(boolean onWall, boolean onRoof) implements CustomPacketPayload {

    public static final Type<ServerboundClimbPacket> TYPE = new Type<>(Synthetics.rl("serverbound_toggle_climb"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundClimbPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ServerboundClimbPacket::onWall,
            ByteBufCodecs.BOOL, ServerboundClimbPacket::onRoof,
            ServerboundClimbPacket::new
    );


    @Override
    public @NotNull Type<ServerboundClimbPacket> type() {
        return TYPE;
    }

    public static void handle(ServerboundClimbPacket packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            SyntheticsPlayerCache cache = SyntheticsPlayerCache.get(context.player());
            if(cache.hasWallClimb) {
                cache.onRoof = packet.onRoof();
                cache.onWall = packet.onWall();
                context.player().refreshDimensions();
                if(context.player().level() instanceof ServerLevel level) {
                    ClientboundWallClimbPacket toOthers = new ClientboundWallClimbPacket(context.player().getId(), true, cache.onWall, cache.onRoof);
                    ServerChunkCache serverchunkcache = level.getChunkSource();
                    serverchunkcache.broadcastAndSend(context.player(), toOthers);
                }
            }
        });
    }
}
