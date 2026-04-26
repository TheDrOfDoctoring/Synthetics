package com.thedrofdoctoring.synthetics.abilities.active;

import com.thedrofdoctoring.synthetics.abilities.AbilityData;
import com.thedrofdoctoring.synthetics.abilities.active.instances.AbilityActiveInstance;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.ActiveAbilityOptions;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public abstract class StandardActiveAbility extends ActiveAbilityType<AbilityActiveInstance<?>> {
    public StandardActiveAbility(ResourceLocation id) {
        super(id);
    }

    public Optional<AbilityActiveInstance<? extends ActiveAbilityType<?>>> createInstance(SyntheticsPlayer player, AbilityData data, ResourceLocation instanceID) {
        if(data instanceof AbilityActiveInstance.Data activeData) {
            return Optional.of(new AbilityActiveInstance<>(this, activeData, player, instanceID));
        }
        return Optional.empty();
    }

    public static AbilityActiveInstance.Data create(double factor, ActiveAbilityOptions options) {
        return new AbilityActiveInstance.Data(factor, options, Optional.empty(), Optional.empty());
    }
    public static AbilityActiveInstance.Data create(ActiveAbilityOptions options) {
        return new AbilityActiveInstance.Data(1d, options, Optional.empty(), Optional.empty());
    }

    public static AbilityActiveInstance.Data create(double factor, ActiveAbilityOptions options, String titlePathOverride) {
        return new AbilityActiveInstance.Data(factor, options, Optional.of(titlePathOverride), Optional.empty());
    }
    public static AbilityActiveInstance.Data create(double factor, ActiveAbilityOptions options, String titlePathOverride, ResourceLocation texturePathOverride) {
        return new AbilityActiveInstance.Data(factor, options, Optional.of(titlePathOverride), Optional.of(texturePathOverride));
    }
}
