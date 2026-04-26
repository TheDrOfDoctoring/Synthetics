package com.thedrofdoctoring.synthetics.networking.from_client;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.entities.linkable.ILinkableEntity;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public class ServerboundResetCameraPacket implements CustomPacketPayload {
    private static ServerboundResetCameraPacket INSTANCE;

    private ServerboundResetCameraPacket() {
    }

    public static ServerboundResetCameraPacket getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new ServerboundResetCameraPacket();
        }
        return INSTANCE;
    }

    public static final CustomPacketPayload.Type<ServerboundResetCameraPacket> TYPE = new CustomPacketPayload.Type<>(Synthetics.rl("reset_linkable_camera"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundResetCameraPacket> CODEC = StreamCodec.unit(getInstance());

    @NotNull
    public Type<ServerboundResetCameraPacket> type() {
        return TYPE;
    }

    public static void handle(ServerboundResetCameraPacket __, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if(context.player() instanceof ServerPlayer player) {
                Entity camera = player.getCamera();
                if(camera instanceof ILinkableEntity) {
                    player.setCamera(player);
                }
            }

        });
    }
}
