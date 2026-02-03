package com.thedrofdoctoring.synthetics.networking.from_server;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.capabilities.cache.SyntheticsPlayerCache;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

// Used to update players on the given player's wall climbing cache status
public record ClientboundWallClimbPacket(int entityID, boolean isActive, boolean onWall, boolean onRoof) implements CustomPacketPayload {

    public static final Type<ClientboundWallClimbPacket> TYPE = new Type<>(Synthetics.rl("toggle_wall_climb"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundWallClimbPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ClientboundWallClimbPacket::entityID,
            ByteBufCodecs.BOOL, ClientboundWallClimbPacket::isActive,
            ByteBufCodecs.BOOL, ClientboundWallClimbPacket::onWall,
            ByteBufCodecs.BOOL, ClientboundWallClimbPacket::onRoof,
            ClientboundWallClimbPacket::new
    );


    @Override
    public @NotNull Type<ClientboundWallClimbPacket> type() {
        return TYPE;
    }

    public static void handle(ClientboundWallClimbPacket packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Entity entity = context.player().level().getEntity(packet.entityID);
            if(entity instanceof Player player && context.player().getId() != packet.entityID) {
                SyntheticsPlayerCache.get(player).hasWallClimb = packet.isActive;
                SyntheticsPlayerCache.get(player).onWall = packet.onWall;
                SyntheticsPlayerCache.get(player).onRoof = packet.onRoof;
            }
        });
    }
}
