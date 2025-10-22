package com.thedrofdoctoring.synthetics.networking.from_server;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.SyntheticsClient;
import com.thedrofdoctoring.synthetics.client.core.SyntheticsClientManager;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.types.body.parts.BodyPartType;
import com.thedrofdoctoring.synthetics.core.data.types.research.ResearchNode;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public class ClientboundUpdateDataCachePacket implements CustomPacketPayload{


    private static ClientboundUpdateDataCachePacket INSTANCE;

    private ClientboundUpdateDataCachePacket() {
    }
    public static ClientboundUpdateDataCachePacket getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new ClientboundUpdateDataCachePacket();
        }
        return INSTANCE;
    }
    public static final CustomPacketPayload.Type<ClientboundUpdateDataCachePacket> TYPE = new CustomPacketPayload.Type<>(Synthetics.rl("update_datapack_packet"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundUpdateDataCachePacket> CODEC = StreamCodec.unit(getInstance());

    @NotNull
    public CustomPacketPayload.Type<ClientboundUpdateDataCachePacket> type() {
        return TYPE;
    }

    @SuppressWarnings("unused")
    public static void handle(ClientboundUpdateDataCachePacket __, final IPayloadContext context) {
        context.enqueueWork(() -> {
            SyntheticsClientManager manager = SyntheticsClient.getInstance().getManager();
            HolderLookup.RegistryLookup<ResearchNode> nodes = context.player().level().registryAccess().lookupOrThrow(SyntheticsData.RESEARCH_NODES);
            HolderLookup.RegistryLookup<BodyPartType> bodyParts = context.player().level().registryAccess().lookupOrThrow(SyntheticsData.BODY_PART_TYPES);
            manager.updateResearch(nodes);
            manager.updatePartTypes(bodyParts);
        });
    }
}
