package com.thedrofdoctoring.synthetics.abilities.passive.types;

import com.thedrofdoctoring.synthetics.abilities.passive.IAbilityEventListener;
import com.thedrofdoctoring.synthetics.abilities.passive.instances.AbilityData;
import com.thedrofdoctoring.synthetics.abilities.passive.instances.DamageResistanceAbilityInstance;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.Ability;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class DamageResistanceAbilityType extends PassiveAbilityType<DamageResistanceAbilityInstance> implements IAbilityEventListener<DamageResistanceAbilityInstance> {

    public DamageResistanceAbilityType(ResourceLocation id) {
        super(id);
    }

    @Override
    public void addDescriptionInfo(Ability ability, List<Component> description) {
        super.addDescriptionInfo(ability, description);
        if (ability.abilityData() instanceof DamageResistanceAbilityInstance.Data data) {
            data.damageTypes().stream().forEach(
                    type -> {
                        if(type.getKey() != null) description.add(Component.translatable("abilities.synthetics.damage.desc", type.getKey().location().toString()).withStyle(ability.abilityNature().defaultColour()));
                    });
        }
    }

    // maybe cache this like effects are cached
    @Override
    public void onDamage(LivingDamageEvent.Pre event, DamageResistanceAbilityInstance instance, int instanceCount, SyntheticsPlayer player) {
        boolean shouldModify = instance.damageTypes()
                .stream()
                .anyMatch(type -> type.getKey() != null && event.getSource().is(type.getKey()));
        if (shouldModify) {
            float damageModifier;
            if(instance.factor() >= 1) {
                damageModifier = (float) (1 + ((instance.factor() - 1) * instanceCount));
            } else {
                damageModifier = (float) (1 - ((1 - instance.factor()) * instanceCount));
            }
            event.setNewDamage(event.getOriginalDamage() * (damageModifier));
        }
    }

    public static DamageResistanceAbilityInstance.Data create(double factor, HolderSet<DamageType> damageTypes) {
        return new DamageResistanceAbilityInstance.Data(factor, damageTypes);
    }

    @SafeVarargs
    public static DamageResistanceAbilityInstance.Data create(double factor, Holder<DamageType>... damageTypes) {
        return new DamageResistanceAbilityInstance.Data(factor, HolderSet.direct(damageTypes));
    }
    @SafeVarargs
    public static DamageResistanceAbilityInstance.Data create(BootstrapContext<?> context, double factor, ResourceKey<DamageType>... damageTypes) {
        return new DamageResistanceAbilityInstance.Data(factor, HolderSet.direct(Arrays.stream(damageTypes).map(key -> context.lookup(Registries.DAMAGE_TYPE).getOrThrow(key)).toList()));
    }

    public static DamageResistanceAbilityInstance.Data create(BootstrapContext<?> context, double factor, TagKey<DamageType> damageTypes) {
        return new DamageResistanceAbilityInstance.Data(factor, context.lookup(Registries.DAMAGE_TYPE).getOrThrow(damageTypes));
    }

    @Override
    public Optional<DamageResistanceAbilityInstance> createInstance(SyntheticsPlayer player, AbilityData data, ResourceLocation instanceID, boolean powerDraw) {
        if (data instanceof DamageResistanceAbilityInstance.Data instData) {
            return Optional.of(new DamageResistanceAbilityInstance(this, player, instData, instanceID, powerDraw));
        }
        return Optional.empty();
    }
}
