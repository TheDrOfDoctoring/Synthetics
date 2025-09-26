package com.thedrofdoctoring.synthetics.core.synthetics;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.body.abilities.SyntheticAbilityType;
import com.thedrofdoctoring.synthetics.body.abilities.passive.SyntheticPassiveAbilityType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;


public class SyntheticAbilities {

    public static final ResourceKey<Registry<SyntheticAbilityType>> ABILITY_REGISTRY_KEY = ResourceKey.createRegistryKey(Synthetics.rl("ability_types"));
    public static final Registry<SyntheticAbilityType> ABILITY_REGISTRY = new RegistryBuilder<>(ABILITY_REGISTRY_KEY)
            .sync(true)
            .defaultKey(Synthetics.rl("none"))
            .create();

    public static final DeferredRegister<SyntheticAbilityType> ABILITIES = DeferredRegister.create(ABILITY_REGISTRY, Synthetics.MODID);

    public static final DeferredHolder<SyntheticAbilityType, SyntheticPassiveAbilityType> FALL_DAMAGE_ABILITY = ABILITIES.register("fall_damage", (id) -> new SyntheticPassiveAbilityType(Attributes.FALL_DAMAGE_MULTIPLIER, id));
    public static final DeferredHolder<SyntheticAbilityType, SyntheticPassiveAbilityType> SAFE_FALL_ABILITY = ABILITIES.register("safe_fall_distance", (id) -> new SyntheticPassiveAbilityType(Attributes.SAFE_FALL_DISTANCE, id));


    public static void register(IEventBus bus) {
        ABILITIES.register(bus);
    }
}
