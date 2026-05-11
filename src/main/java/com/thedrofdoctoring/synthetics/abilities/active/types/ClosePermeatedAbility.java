package com.thedrofdoctoring.synthetics.abilities.active.types;

import com.thedrofdoctoring.synthetics.abilities.active.ActiveAbilityType;
import com.thedrofdoctoring.synthetics.abilities.active.StandardActiveAbility;
import com.thedrofdoctoring.synthetics.abilities.active.instances.AbilityActiveInstance;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.Ability;
import com.thedrofdoctoring.synthetics.world.data.PermeableBlocksData;
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
        ActiveAbilityType.activeAbilityDescription(ability, description);
    }
}
