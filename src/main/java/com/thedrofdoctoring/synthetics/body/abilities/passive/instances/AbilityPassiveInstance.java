package com.thedrofdoctoring.synthetics.body.abilities.passive.instances;

import com.thedrofdoctoring.synthetics.body.abilities.AbilityInstance;
import com.thedrofdoctoring.synthetics.body.abilities.passive.types.PassiveAbilityType;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import net.minecraft.resources.ResourceLocation;

public abstract class AbilityPassiveInstance<T extends PassiveAbilityType> extends AbilityInstance<T> {

    private final AbilityData data;
    private final boolean hasPowerDraw;
    private boolean isEnabled = true;

    public AbilityPassiveInstance(T ability, SyntheticsPlayer player, AbilityData data, ResourceLocation instanceID, boolean powerDraw) {
        super(ability, player, data, instanceID);
        this.hasPowerDraw = powerDraw;
        this.data = data;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public boolean hasPowerDraw() {
        return hasPowerDraw;
    }

    public AbilityData data() {
        return data;
    }



}
