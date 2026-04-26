package com.thedrofdoctoring.synthetics.abilities.active.types;

import com.thedrofdoctoring.synthetics.abilities.active.StandardLastingAbility;
import com.thedrofdoctoring.synthetics.abilities.active.instances.AbilityActiveInstance;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.capabilities.cache.SyntheticsPlayerCache;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.Ability;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.ActiveAbilityOptions;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class WaterwalkingAbility extends StandardLastingAbility {
    public WaterwalkingAbility(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean onTick(SyntheticsPlayer syntheticsPlayer, AbilityActiveInstance<?> instance) {
        return false;
    }

    @Override
    public void onRestoreActivate(SyntheticsPlayer syntheticsPlayer, AbilityActiveInstance<?> instance) {
        activate(syntheticsPlayer);
    }

    @Override
    public void onAbilityDeactivated(SyntheticsPlayer syntheticsPlayer, AbilityActiveInstance<?> instance) {
        SyntheticsPlayerCache.get(syntheticsPlayer.getEntity()).isWaterWalking = false;
    }

    @Override
    public void activateClient(SyntheticsPlayer syntheticsPlayer, AbilityActiveInstance<?> instance) {
        activate(syntheticsPlayer);
    }

    @Override
    public boolean activate(SyntheticsPlayer syntheticsPlayer, AbilityActiveInstance<?> abilityData) {
        activate(syntheticsPlayer);
        return true;
    }

    private void activate(SyntheticsPlayer player) {
        SyntheticsPlayerCache.get(player.getEntity()).isWaterWalking = true;
    }

    @Override
    public void addDescriptionInfo(Ability ability, List<Component> description) {
        if (ability.abilityData() instanceof AbilityActiveInstance.Data activeData) {
            ActiveAbilityOptions options = activeData.options();
            description.add(Component.translatable("abilities.synthetics.description.cooldown", options.cooldown()).withStyle(ChatFormatting.BLUE));
            if (options.duration() > 0) {
                description.add(Component.translatable("abilities.synthetics.description.duration", options.duration()).withStyle(ChatFormatting.BLUE));
            }
            if (options.powerCost() > 0) {
                description.add(Component.translatable("abilities.synthetics.description.power_cost", options.powerCost()).withStyle(ChatFormatting.BLUE));

            }
            if (options.powerDrain() > 0) {
                description.add(Component.translatable("abilities.synthetics.description.power_drain", options.powerDrain()).withStyle(ChatFormatting.BLUE));
            }
        }
    }

    @Override
    public boolean canBeUsed(SyntheticsPlayer syntheticsPlayer) {
        return true;
    }
}
