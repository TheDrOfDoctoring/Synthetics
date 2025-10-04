package com.thedrofdoctoring.synthetics.networking.from_client;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.capabilities.ResearchManager;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.types.research.ResearchNode;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;


public record ServerboundResearchPacket(ResourceLocation nodeID) implements CustomPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundResearchPacket> CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, ServerboundResearchPacket::nodeID,
            ServerboundResearchPacket::new);

    public static ServerboundResearchPacket create(ResearchNode node) {
        return new ServerboundResearchPacket(node.id());
    }

    @Override
    public @NotNull Type<ServerboundResearchPacket> type() {
        return TYPE;
    }

    public static final Type<ServerboundResearchPacket> TYPE = new Type<>(Synthetics.rl("research_node"));

    public static void handle(ServerboundResearchPacket packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            ResourceLocation id = packet.nodeID;
            var lookupOpt = context.player().registryAccess().lookup(SyntheticsData.RESEARCH_NODES);
            if(lookupOpt.isPresent()) {
                var objectOpt = lookupOpt.get().get(ResourceKey.create(SyntheticsData.RESEARCH_NODES, id));
                if(objectOpt.isPresent()) {
                    Holder.Reference<ResearchNode> node = objectOpt.get();
                    ResearchManager manager = SyntheticsPlayer.get(context.player()).getResearchManager();
                    boolean canResearch = manager.canResearch(node.value());
                    if(canResearch) {
                        manager.handleResearchCosts(node.value());
                        manager.addResearched(node.value());
                    }

                } else {
                    Synthetics.LOGGER.warn("Unable to retrieve node with ID {}", id);
                }
            }


        });
    }
}
