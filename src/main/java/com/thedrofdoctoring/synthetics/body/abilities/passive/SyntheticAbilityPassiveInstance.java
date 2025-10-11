package com.thedrofdoctoring.synthetics.body.abilities.passive;

import com.thedrofdoctoring.synthetics.body.abilities.SyntheticAbilityInstance;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.Optional;

public class SyntheticAbilityPassiveInstance extends SyntheticAbilityInstance<SyntheticPassiveAbilityType> {

    private final AttributeModifier.Operation operation;
    private boolean hasPowerDraw;
    private boolean isEnabled = true;

    private static final Pair<Holder<Attribute>, AttributeModifier> EMPTY_PAIR = Pair.of(null, null);

    public SyntheticAbilityPassiveInstance(SyntheticPassiveAbilityType ability, SyntheticsPlayer player, float abilityFactor, AttributeModifier.Operation operation, ResourceLocation instanceID, boolean powerDraw) {
        super(ability, player, abilityFactor, instanceID);
        this.operation = operation;
        this.hasPowerDraw = powerDraw;
    }
    public SyntheticAbilityPassiveInstance(SyntheticPassiveAbilityType ability, SyntheticsPlayer player, float abilityFactor, ResourceLocation instanceID, boolean powerDraw) {
        super(ability, player, abilityFactor, instanceID);
        this.operation = null;
        this.hasPowerDraw = powerDraw;
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

    public Optional<Pair<Holder<Attribute>, AttributeModifier>> getModifiedAttribute(LivingEntity entity) {
        if(operation == null) return Optional.empty();
        if(this.ability.getModifiedAttribute().isPresent()) {
            Holder<Attribute> attribute = this.ability.getModifiedAttribute().get();
            return Optional.of(Pair.of(attribute, new AttributeModifier(instanceID, abilityFactor, operation)));
        }
        return Optional.empty();
    }
}
