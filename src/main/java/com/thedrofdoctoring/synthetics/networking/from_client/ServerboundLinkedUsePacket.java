package com.thedrofdoctoring.synthetics.networking.from_client;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.capabilities.cache.SyntheticsPlayerCache;
import com.thedrofdoctoring.synthetics.entities.linkable.DroneEntity;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ServerboundLinkedUsePacket(boolean isUseLeft) implements CustomPacketPayload {


    public static final CustomPacketPayload.Type<ServerboundLinkedUsePacket> TYPE = new CustomPacketPayload.Type<>(Synthetics.rl("linked_use_data"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundLinkedUsePacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ServerboundLinkedUsePacket::isUseLeft,
            ServerboundLinkedUsePacket::new);


    @Override
    public @NotNull Type<ServerboundLinkedUsePacket> type() {
        return TYPE;
    }


    public static void handle(ServerboundLinkedUsePacket usePacket, final IPayloadContext context){
        context.enqueueWork(() -> {
            Player player = context.player();
            SyntheticsPlayerCache cache = SyntheticsPlayerCache.get(player);
            if(cache.isNotViewingSelf && player instanceof ServerPlayer serverPlayer) {
                if(serverPlayer.getCamera() instanceof DroneEntity drone) {
                    // attacks don't have the same cooldown that a regular right-click does, so faking it
                    if(usePacket.isUseLeft && serverPlayer.tickCount - cache.placeTime > 2) {
                        cache.placeTime = serverPlayer.tickCount;
                        drone.useItemInHand(serverPlayer, InteractionHand.OFF_HAND);
                    } else if(!usePacket.isUseLeft) {
                        drone.useItemInHand(serverPlayer, InteractionHand.MAIN_HAND);
                    }

                }
            }
        });


    }
}
