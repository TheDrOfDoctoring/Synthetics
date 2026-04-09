package com.thedrofdoctoring.synthetics.abilities.active.types;

import com.thedrofdoctoring.synthetics.abilities.active.StandardActiveAbility;
import com.thedrofdoctoring.synthetics.abilities.active.instances.AbilityActiveInstance;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.Ability;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.ActiveAbilityOptions;
import com.thedrofdoctoring.synthetics.world.data.PermeableBlocksData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class ClosePermeatedAbility extends StandardActiveAbility {
    public ClosePermeatedAbility(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean activate(SyntheticsPlayer syntheticsPlayer, AbilityActiveInstance<?> abilityData) {

        if(syntheticsPlayer.getEntity() instanceof ServerPlayer player && player.level() instanceof ServerLevel level) {
            PermeableBlocksData data = PermeableBlocksData.getData(level.getServer(), level.dimension());
            if(data != null && data.resetForUUID(level, player.getUUID())) {
                return true;
            }
        }

        syntheticsPlayer.getEntity().displayClientMessage(Component.translatable("abilities.synthetics.fail.close_permeated"), true);
        return false;
    }

    @Override
    public boolean canBeUsed(SyntheticsPlayer syntheticsPlayer) {
        return true;
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
}
