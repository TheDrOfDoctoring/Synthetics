package com.thedrofdoctoring.synthetics.networking.from_server;

import com.thedrofdoctoring.synthetics.Synthetics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ClientboundLeapPacket(double factor) implements CustomPacketPayload {

    public static final Type<ClientboundLeapPacket> TYPE = new Type<>(Synthetics.rl("player_leap"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundLeapPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, ClientboundLeapPacket::factor,
            ClientboundLeapPacket::new
    );


    @Override
    public @NotNull Type<ClientboundLeapPacket> type() {
        return TYPE;
    }

    public static void handle(ClientboundLeapPacket packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            Vec3 vec = player.getViewVector(1);
            player.setDeltaMovement(new Vec3(vec.x * packet.factor(), vec.y * packet.factor(), player.getLookAngle().z * packet.factor()));
        });
    }
}
