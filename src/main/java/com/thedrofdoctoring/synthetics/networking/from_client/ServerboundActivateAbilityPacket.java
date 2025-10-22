package com.thedrofdoctoring.synthetics.networking.from_client;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.abilities.AbilityType;
import com.thedrofdoctoring.synthetics.abilities.active.ActiveAbilityType;
import com.thedrofdoctoring.synthetics.capabilities.AbilityManager;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ServerboundActivateAbilityPacket(AbilityType ability) implements CustomPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundActivateAbilityPacket> CODEC = StreamCodec.composite(
            AbilityType.STREAM_CODEC, ServerboundActivateAbilityPacket::ability,
            ServerboundActivateAbilityPacket::new
    );

    @Override
    public @NotNull Type<ServerboundActivateAbilityPacket> type() {
        return TYPE;
    }

    public static final Type<ServerboundActivateAbilityPacket> TYPE = new Type<>(Synthetics.rl("activate_ability"));

    public static void handle(ServerboundActivateAbilityPacket abilityPacket, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if(abilityPacket.ability instanceof ActiveAbilityType active) {
                AbilityManager manager = SyntheticsPlayer.get(context.player()).getAbilityManager();
                if(manager.canActivate(active)) {
                    manager.toggleAbility(active);
                }

            } else {
                Synthetics.LOGGER.warn("Retrieved non-active ability {} from activate ability packet", abilityPacket.ability().getAbilityID().toString());
            }

        });
    }
}
