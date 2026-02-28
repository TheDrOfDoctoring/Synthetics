package com.thedrofdoctoring.synthetics.capabilities.cache;

import com.thedrofdoctoring.synthetics.abilities.passive.instances.AbilityPassiveInstance;
import com.thedrofdoctoring.synthetics.abilities.passive.instances.EffectAbilityInstance;
import com.thedrofdoctoring.synthetics.capabilities.AbilityManager;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;

import java.util.Map;

public class EffectAmplifierCache {

    private final Map<Holder<MobEffect>, Double> effectToAmplifierMod = new Object2DoubleOpenHashMap<>();

    public void rebuild(SyntheticsPlayer player) {
        effectToAmplifierMod.clear();
        AbilityManager abilityManager = player.getAbilityManager();
        for(AbilityPassiveInstance<?> instance : abilityManager.getPassiveAbilities()) {
            if(instance instanceof EffectAbilityInstance effectInstance && effectInstance.isEnabled()) {
                effectInstance.effects().forEach(effect -> {
                    if(effectToAmplifierMod.containsKey(effect)) {
                        effectToAmplifierMod.put(effect, effectToAmplifierMod.get(effect) + effectInstance.factor());
                    } else {
                        effectToAmplifierMod.put(effect, effectInstance.factor());
                    }
                });
            }
        }
    }

    public int getAmplificationModifier(Holder<MobEffect> effect) {
        return (int) Math.round(effectToAmplifierMod.getOrDefault(effect, 0d));
    }

}
