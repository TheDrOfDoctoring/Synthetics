package com.thedrofdoctoring.synthetics.networking.from_client;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.networking.from_server.ClientboundPlayerUpdatePacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public class ServerboundRequestUpdatePacket implements CustomPacketPayload {
    private static ServerboundRequestUpdatePacket INSTANCE;

    private ServerboundRequestUpdatePacket() {
    }
    public static ServerboundRequestUpdatePacket getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new ServerboundRequestUpdatePacket();
        }
        return INSTANCE;
    }
    public static final CustomPacketPayload.Type<ServerboundRequestUpdatePacket> TYPE = new CustomPacketPayload.Type<>(Synthetics.rl("request_update"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundRequestUpdatePacket> CODEC = StreamCodec.unit(getInstance());

    @NotNull
    public Type<ServerboundRequestUpdatePacket> type() {
        return TYPE;
    }

    public static void handle(ServerboundRequestUpdatePacket __, final IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientboundPlayerUpdatePacket update = ClientboundPlayerUpdatePacket.create(context.player(), SyntheticsPlayer.get(context.player()).serialiseNBT(context.player().level().registryAccess()), true);
            context.reply(update);
        });
    }
}
