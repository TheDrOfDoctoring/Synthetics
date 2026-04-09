package com.thedrofdoctoring.synthetics.networking.from_client;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.abilities.active.ActiveAbilityType;
import com.thedrofdoctoring.synthetics.capabilities.AbilityManager;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.Ability;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ServerboundActivateAbilityPacket(Holder<Ability> ability) implements CustomPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundActivateAbilityPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.holder(SyntheticsData.ABILITIES, Ability.STREAM_CODEC), ServerboundActivateAbilityPacket::ability,
            ServerboundActivateAbilityPacket::new
    );

    @Override
    public @NotNull Type<ServerboundActivateAbilityPacket> type() {
        return TYPE;
    }

    public static final Type<ServerboundActivateAbilityPacket> TYPE = new Type<>(Synthetics.rl("activate_ability"));

    public static void handle(ServerboundActivateAbilityPacket abilityPacket, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if(!abilityPacket.ability.isBound()) return;

            Ability ability = abilityPacket.ability.value();
            if(ability.abilityType() instanceof ActiveAbilityType<?> type) {
                AbilityManager manager = SyntheticsPlayer.get(context.player()).getAbilityManager();
                if(manager.canActivate(ability, type)) {
                    manager.toggleAbility(ability, type);
                } else {
                    context.player().displayClientMessage(Component.translatable("text.synthetics.ability_unavailable").withStyle(ChatFormatting.RED), true);
                }

            } else {
                Synthetics.LOGGER.warn("Retrieved non-active ability {} from activate ability packet", ability.abilityType().getAbilityID().toString());
            }

        });
    }
}
