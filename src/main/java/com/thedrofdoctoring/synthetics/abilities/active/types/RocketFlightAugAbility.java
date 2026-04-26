package com.thedrofdoctoring.synthetics.abilities.active.types;

import com.thedrofdoctoring.synthetics.abilities.active.instances.AbilityActiveInstance;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.Ability;
import com.thedrofdoctoring.synthetics.core.synthetics.SyntheticAbilities;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class RocketFlightAugAbility extends RocketFlightAbility {
    public RocketFlightAugAbility(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean activate(SyntheticsPlayer syntheticsPlayer, AbilityActiveInstance<?> abilityData) {
        var pairOpt = SyntheticsPlayer.get(syntheticsPlayer.getEntity()).getAbilityManager().getPassiveAbilitiesPairs()
                .stream()
                .filter(pair -> pair.second().type().equals(SyntheticAbilities.FLIGHT_COUNT.get()))
                .findFirst();
        boolean isAvailable = pairOpt.map(pair -> pair.leftInt() >= 2).orElse(false);
        if(!isAvailable) {
            syntheticsPlayer.getEntity().displayClientMessage(Component.translatable("abilities.synthetics.rocket_aug_flight.insufficient").withStyle(ChatFormatting.RED), true);
            return false;
        }
        return super.activate(syntheticsPlayer, abilityData);
    }

    @Override
    public void addDescriptionInfo(Ability ability, List<Component> description) {
        description.add(Component.translatable("abilities.synthetics.description.rocket_aug_flight").withStyle(ChatFormatting.RED));
        super.addDescriptionInfo(ability, description);
    }

}
