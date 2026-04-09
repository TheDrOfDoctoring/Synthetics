package com.thedrofdoctoring.synthetics.abilities.passive.types;

import com.thedrofdoctoring.synthetics.abilities.AbilityData;
import com.thedrofdoctoring.synthetics.abilities.passive.IAbilityEventListener;
import com.thedrofdoctoring.synthetics.abilities.passive.instances.HealingAbilityInstance;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.Ability;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;

public class HealingAbilityType extends PassiveAbilityType<HealingAbilityInstance> implements IAbilityEventListener<HealingAbilityInstance> {
    public HealingAbilityType(ResourceLocation id) {
        super(id);
    }

    @Override
    public void onTick(HealingAbilityInstance instance, int instanceCount, SyntheticsPlayer player) {
        if(player.getEntity().tickCount % instance.factor() == 0 && player.getEntity().getHealth() < player.getEntity().getMaxHealth()) {
            player.getEntity().heal(instance.healingAmount() * instanceCount);
            player.getPowerManager().drainPower(instance.powerDraw() * instanceCount);
        }

    }

    @Override
    public void addDescriptionInfo(Ability ability, List<Component> description) {
        if(ability.abilityData() instanceof HealingAbilityInstance.Data data) {
            ChatFormatting formatting = ability.abilityNature().defaultColour();
            description.add(Component.translatable("abilities.synthetics.description.time_per_heal", data.factor()).withStyle(formatting));
            description.add(Component.translatable("abilities.synthetics.description.healing_amount", data.healingAmount()).withStyle(formatting));
            description.add(Component.translatable("abilities.synthetics.description.power_draw", data.powerDraw()).withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public Optional<HealingAbilityInstance> createInstance(SyntheticsPlayer player, AbilityData data, ResourceLocation instanceID, boolean powerDraw) {
        if(data instanceof HealingAbilityInstance.Data healingData) {
            return Optional.of(new HealingAbilityInstance(this, player, healingData, instanceID, powerDraw));
        }
        return Optional.empty();
    }

    public static HealingAbilityInstance.Data create(int timePerHeal, int powerDraw, float healingAmount) {
        return new HealingAbilityInstance.Data(timePerHeal, powerDraw, healingAmount);
    }
}
