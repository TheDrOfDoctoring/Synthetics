package com.thedrofdoctoring.synthetics.core.synthetics;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.body.abilities.SyntheticAbilityType;
import com.thedrofdoctoring.synthetics.body.abilities.active.types.LeapAbility;
import com.thedrofdoctoring.synthetics.body.abilities.active.types.WallClimbAbility;
import com.thedrofdoctoring.synthetics.body.abilities.passive.SyntheticPassiveAbilityType;
import com.thedrofdoctoring.synthetics.body.abilities.passive.generators.FoodGeneratorAbility;
import com.thedrofdoctoring.synthetics.body.abilities.passive.generators.SolarGeneratorAbility;
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

    //TODO:
    // Abilities replaced with ability types, eg attribute modifier ability, damage resistance, with the specified modified attribute in the ability data gen.

    public static final DeferredHolder<SyntheticAbilityType, SyntheticPassiveAbilityType> FALL_DAMAGE_ABILITY = ABILITIES.register("fall_damage", (id) -> new SyntheticPassiveAbilityType(Attributes.FALL_DAMAGE_MULTIPLIER, id));
    public static final DeferredHolder<SyntheticAbilityType, SyntheticPassiveAbilityType> SAFE_FALL_ABILITY = ABILITIES.register("safe_fall_distance", (id) -> new SyntheticPassiveAbilityType(Attributes.SAFE_FALL_DISTANCE, id));
    public static final DeferredHolder<SyntheticAbilityType, SyntheticPassiveAbilityType> ATTACK_DAMAGE = ABILITIES.register("attack_damage", (id) -> new SyntheticPassiveAbilityType(Attributes.ATTACK_DAMAGE, id));
    public static final DeferredHolder<SyntheticAbilityType, SyntheticPassiveAbilityType> ATTACK_KNOCKBACK = ABILITIES.register("attack_knockback", (id) -> new SyntheticPassiveAbilityType(Attributes.ATTACK_KNOCKBACK, id));
    public static final DeferredHolder<SyntheticAbilityType, SyntheticPassiveAbilityType> ARMOUR = ABILITIES.register("armour", (id) -> new SyntheticPassiveAbilityType(Attributes.ARMOR, id));
    public static final DeferredHolder<SyntheticAbilityType, SyntheticPassiveAbilityType> ARMOUR_TOUGHNESS = ABILITIES.register("armour_toughness", (id) -> new SyntheticPassiveAbilityType(Attributes.ARMOR_TOUGHNESS, id));
    public static final DeferredHolder<SyntheticAbilityType, SyntheticPassiveAbilityType> MAX_HEALTH = ABILITIES.register("max_health", (id) -> new SyntheticPassiveAbilityType(Attributes.MAX_HEALTH, id));
    public static final DeferredHolder<SyntheticAbilityType, SyntheticPassiveAbilityType> JUMP_HEIGHT = ABILITIES.register("jump_height", (id) -> new SyntheticPassiveAbilityType(Attributes.JUMP_STRENGTH, id));
    public static final DeferredHolder<SyntheticAbilityType, SyntheticPassiveAbilityType> BLOCK_INTERACTION_RANGE = ABILITIES.register("block_interaction_range", (id) -> new SyntheticPassiveAbilityType(Attributes.BLOCK_INTERACTION_RANGE, id));
    public static final DeferredHolder<SyntheticAbilityType, SyntheticPassiveAbilityType> ENTITY_INTERACTION_RANGE = ABILITIES.register("entity_interaction_range", (id) -> new SyntheticPassiveAbilityType(Attributes.ENTITY_INTERACTION_RANGE, id));
    public static final DeferredHolder<SyntheticAbilityType, SyntheticPassiveAbilityType> MOVEMENT_SPEED = ABILITIES.register("movement_speed", (id) -> new SyntheticPassiveAbilityType(Attributes.MOVEMENT_SPEED, id));
    public static final DeferredHolder<SyntheticAbilityType, SyntheticPassiveAbilityType> STEP_HEIGHT = ABILITIES.register("step_height", (id) -> new SyntheticPassiveAbilityType(Attributes.STEP_HEIGHT, id));
    public static final DeferredHolder<SyntheticAbilityType, SyntheticPassiveAbilityType> ATTACK_SPEED = ABILITIES.register("attack_speed", (id) -> new SyntheticPassiveAbilityType(Attributes.ATTACK_SPEED, id));
    public static final DeferredHolder<SyntheticAbilityType, SyntheticPassiveAbilityType> OXYGEN_BONUS = ABILITIES.register("oxygen_bonus", (id) -> new SyntheticPassiveAbilityType(Attributes.OXYGEN_BONUS, id));
    public static final DeferredHolder<SyntheticAbilityType, SyntheticPassiveAbilityType> SWIM_SPEED = ABILITIES.register("swim_speed", (id) -> new SyntheticPassiveAbilityType(Attributes.WATER_MOVEMENT_EFFICIENCY, id));

    public static final DeferredHolder<SyntheticAbilityType, SyntheticPassiveAbilityType> BATTERY = ABILITIES.register("battery", SyntheticPassiveAbilityType::new);
    public static final DeferredHolder<SyntheticAbilityType, SyntheticPassiveAbilityType> SOLAR_GENERATOR = ABILITIES.register("solar_generator", SolarGeneratorAbility::new);
    public static final DeferredHolder<SyntheticAbilityType, SyntheticPassiveAbilityType> UNDERWATER_VISION = ABILITIES.register("underwater_vision", SyntheticPassiveAbilityType::new);
    public static final DeferredHolder<SyntheticAbilityType, SyntheticPassiveAbilityType> FOOD_GENERATOR = ABILITIES.register("food_generator", FoodGeneratorAbility::new);


    public static final DeferredHolder<SyntheticAbilityType, LeapAbility> LEAP = ABILITIES.register("leap", LeapAbility::new);
    public static final DeferredHolder<SyntheticAbilityType, WallClimbAbility> WALL_CLIMB = ABILITIES.register("wall_climb", WallClimbAbility::new);



    public static void register(IEventBus bus) {
        ABILITIES.register(bus);
    }
}
