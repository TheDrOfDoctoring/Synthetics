package com.thedrofdoctoring.synthetics.networking.from_client;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.blocks.entities.linkables.LinkableBlockEntity;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.synthetics.SyntheticAbilities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ServerboundLinkedInteractPacket(BlockPos pos) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ServerboundLinkedInteractPacket> TYPE = new Type<>(Synthetics.rl("server_linked_interact"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundLinkedInteractPacket> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ServerboundLinkedInteractPacket::pos,
            ServerboundLinkedInteractPacket::new
    );


    @Override
    public @NotNull CustomPacketPayload.Type<ServerboundLinkedInteractPacket> type() {
        return TYPE;
    }

    public static void handle(ServerboundLinkedInteractPacket packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if(!SyntheticsPlayer.get(context.player()).getAbilityManager().hasAbilityType(SyntheticAbilities.VIEW_LINKED_MENU.get())) {
                return;
            }

            BlockEntity be = context.player().level().getBlockEntity(packet.pos);
            if(be instanceof LinkableBlockEntity linkable) {
                linkable.onLinkedInteract(context.player());
            }
        });
    }
}
