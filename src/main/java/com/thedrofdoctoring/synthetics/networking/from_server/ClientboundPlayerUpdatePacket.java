package com.thedrofdoctoring.synthetics.networking.from_server;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.SyntheticsAttachments;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ClientboundPlayerUpdatePacket(int entityID, CompoundTag data, boolean updatingSelf) implements CustomPacketPayload {

    public static final Type<ClientboundPlayerUpdatePacket> TYPE = new Type<>(Synthetics.rl("update_player"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundPlayerUpdatePacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ClientboundPlayerUpdatePacket::entityID,
            ByteBufCodecs.COMPOUND_TAG, ClientboundPlayerUpdatePacket::data,
            ByteBufCodecs.BOOL, ClientboundPlayerUpdatePacket::updatingSelf,
            ClientboundPlayerUpdatePacket::new
    );

    public static ClientboundPlayerUpdatePacket create(Player player, CompoundTag data) {
        return create(player, data, true);
    }
    public static ClientboundPlayerUpdatePacket create(Player player, CompoundTag data, boolean updatingSelf) {

        return new ClientboundPlayerUpdatePacket(player.getId(), data, updatingSelf);
    }


    @Override
    public @NotNull Type<ClientboundPlayerUpdatePacket> type() {
        return TYPE;
    }

    public static void handle(ClientboundPlayerUpdatePacket packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            Level level = player.level();
            if(packet.updatingSelf()) {
                updateData(player, packet.data(), true);
            } else {
                Entity entity = level.getEntity(packet.entityID());
                if(entity == null) {
                    Synthetics.LOGGER.error("Could not find updated entity {}", packet.entityID());
                    return;
                }
                updateData(entity, packet.data, false);
            }

        });
    }
    private static void updateData(Entity entity, CompoundTag data, boolean self) {
        if(self) {
            entity.getData(SyntheticsAttachments.SYNTHETICS_MANAGER).deserialiseNBT(entity.registryAccess(), data);
        } else {
            entity.getData(SyntheticsAttachments.SYNTHETICS_MANAGER).deserialiseUpdateNBT(entity.registryAccess(), data);

        }
    }
}
