package com.thedrofdoctoring.synthetics.networking.from_server;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.SyntheticsClient;
import com.thedrofdoctoring.synthetics.client.core.SyntheticsClientManager;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.types.research.ResearchNode;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public class ClientboundUpdateResearchNodesPacket implements CustomPacketPayload{


    private static ClientboundUpdateResearchNodesPacket INSTANCE;

    private ClientboundUpdateResearchNodesPacket() {
    }
    public static ClientboundUpdateResearchNodesPacket getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new ClientboundUpdateResearchNodesPacket();
        }
        return INSTANCE;
    }
    public static final CustomPacketPayload.Type<ClientboundUpdateResearchNodesPacket> TYPE = new CustomPacketPayload.Type<>(Synthetics.rl("update_research"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundUpdateResearchNodesPacket> CODEC = StreamCodec.unit(getInstance());

    @NotNull
    public CustomPacketPayload.Type<ClientboundUpdateResearchNodesPacket> type() {
        return TYPE;
    }

    public static void handle(ClientboundUpdateResearchNodesPacket __, final IPayloadContext context) {
        context.enqueueWork(() -> {
            SyntheticsClientManager manager = SyntheticsClient.getInstance().getManager();
            HolderLookup.RegistryLookup<ResearchNode> nodes = context.player().level().registryAccess().lookupOrThrow(SyntheticsData.RESEARCH_NODES);
            manager.setResearch(nodes.listElements().map(Holder.Reference::value).toList());
        });
    }
}
