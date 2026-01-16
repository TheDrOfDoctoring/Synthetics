package com.thedrofdoctoring.synthetics.networking.from_client;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.networking.from_server.ClientboundLevelUUIDPacket;
import com.thedrofdoctoring.synthetics.networking.from_server.ClientboundPlayerUpdatePacket;
import com.thedrofdoctoring.synthetics.world.data.IDSavedData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

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
            MinecraftServer server = context.player().level().getServer();
            if(server != null) {
                UUID uuid = IDSavedData.getData(server);
                ClientboundLevelUUIDPacket uuidPacket = new ClientboundLevelUUIDPacket(uuid);
                context.reply(uuidPacket);
            }

            context.reply(update);
        });
    }
}
