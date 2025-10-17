package com.thedrofdoctoring.synthetics.body.abilities.active;

import com.thedrofdoctoring.synthetics.body.abilities.AbilityType;
import com.thedrofdoctoring.synthetics.body.abilities.passive.instances.AbilityData;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.data.types.body.ActiveAbilityOptions;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public abstract class ActiveAbilityType extends AbilityType {

    private final ResourceLocation ID;

    public ActiveAbilityType(ResourceLocation id) {
        this.ID = id;
    }

    @Override
    public ResourceLocation getAbilityID() {
        return ID;
    }

    public abstract boolean activate(SyntheticsPlayer syntheticsPlayer, double abilityFactor);
    public abstract void onAbilityDeactivated(SyntheticsPlayer syntheticsPlayer);
    public abstract void activateClient(SyntheticsPlayer syntheticsPlayer, double abilityFactor);

    public abstract boolean canBeUsed(SyntheticsPlayer syntheticsPlayer);

    public Optional<AbilityActiveInstance<? extends ActiveAbilityType>> createInstance(SyntheticsPlayer player, AbilityData data, ResourceLocation instanceID) {
        if(data instanceof AbilityActiveInstance.ActiveAbilityData activeData) {
            return Optional.of(new AbilityActiveInstance<>(this, activeData, player, instanceID));
        }
        return Optional.empty();
    }

    public static AbilityActiveInstance.ActiveAbilityData create(double factor, ActiveAbilityOptions options) {
        return new AbilityActiveInstance.ActiveAbilityData(factor, options);
    }

}
