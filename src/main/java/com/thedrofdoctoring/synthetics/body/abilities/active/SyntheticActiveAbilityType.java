package com.thedrofdoctoring.synthetics.body.abilities.active;

import com.thedrofdoctoring.synthetics.body.abilities.SyntheticAbilityType;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.synthetics.SyntheticAbilities;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public abstract class SyntheticActiveAbilityType extends SyntheticAbilityType {

    private final ResourceLocation ID;

    public SyntheticActiveAbilityType(ResourceLocation id) {
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


}
