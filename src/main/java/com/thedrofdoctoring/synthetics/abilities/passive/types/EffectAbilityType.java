package com.thedrofdoctoring.synthetics.abilities.passive.types;

import com.thedrofdoctoring.synthetics.abilities.passive.instances.AbilityData;
import com.thedrofdoctoring.synthetics.abilities.passive.instances.EffectAbilityInstance;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.capabilities.cache.EffectAmplifierCache;
import com.thedrofdoctoring.synthetics.capabilities.cache.SyntheticsPlayerCache;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.Ability;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Optional;

public class EffectAbilityType extends PassiveAbilityType<EffectAbilityInstance> {
    public EffectAbilityType(ResourceLocation id) {
        super(id);
    }

    public static final int IMMUNITY_FACTOR = -99;

    @Override
    public void onAbilityRemoved(EffectAbilityInstance instance, int instanceCount, SyntheticsPlayer player) {
        super.onAbilityRemoved(instance, instanceCount, player);
        rebuildPlayerCache(player);
    }

    @Override
    public void onAbilityAdded(EffectAbilityInstance instance, int instanceCount, SyntheticsPlayer player) {
        super.onAbilityAdded(instance, instanceCount, player);
        rebuildPlayerCache(player);
    }

    @Override
    public Component title(AbilityData data) {
        if(data.factor() < IMMUNITY_FACTOR + 1) {
            return Component.translatable("abilities.synthetics.effect_immunity");
        }
        return super.title(data);
    }

    @Override
    public Optional<EffectAbilityInstance> createInstance(SyntheticsPlayer player, AbilityData data, ResourceLocation instanceID, boolean powerDraw) {
        if(data instanceof EffectAbilityInstance.Data effectData) {
            return Optional.of(new EffectAbilityInstance(this, player, effectData, instanceID, powerDraw));
        }
        return Optional.empty();
    }

    @Override
    public void addDescriptionInfo(Ability ability, List<Component> description) {
        if(ability.abilityData().factor() > IMMUNITY_FACTOR + 1) {
            super.addDescriptionInfo(ability, description);
        }
        if(ability.abilityData() instanceof EffectAbilityInstance.Data data) {
            data.effects().forEach(effect -> description.add(Component.translatable("ability.synthetics.effect.desc", effect.value().getDisplayName()).withStyle(ability.abilityNature().defaultColour())));
        }
    }

    public static EffectAbilityInstance.Data create(double factor, HolderSet<MobEffect> effects) {
        return new EffectAbilityInstance.Data(factor, effects);
    }
    @SafeVarargs
    public static EffectAbilityInstance.Data create(double factor, Holder<MobEffect>... effects) {
        return new EffectAbilityInstance.Data(factor, HolderSet.direct(effects));
    }

    private void rebuildPlayerCache(SyntheticsPlayer player) {
        SyntheticsPlayerCache.get(player.getEntity()).effectCache.rebuild(player);
    }

    public static EffectAmplifierCache getEffectCache(Player player) {
        return SyntheticsPlayerCache.get(player).effectCache;
    }
}
