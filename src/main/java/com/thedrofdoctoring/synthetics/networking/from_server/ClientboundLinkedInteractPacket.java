package com.thedrofdoctoring.synthetics.networking.from_server;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.blocks.entities.linkables.LinkableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ClientboundLinkedInteractPacket(BlockPos pos) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientboundLinkedInteractPacket> TYPE = new Type<>(Synthetics.rl("linked_interact"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundLinkedInteractPacket> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ClientboundLinkedInteractPacket::pos,
            ClientboundLinkedInteractPacket::new
    );


    @Override
    public @NotNull CustomPacketPayload.Type<ClientboundLinkedInteractPacket> type() {
        return TYPE;
    }

    public static void handle(ClientboundLinkedInteractPacket packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            BlockEntity be = context.player().level().getBlockEntity(packet.pos);
            if(be instanceof LinkableBlockEntity linkable) {
                linkable.onLinkedInteract(context.player());
            }
        });
    }
}
