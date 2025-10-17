package com.thedrofdoctoring.synthetics.core.synthetics;

import com.mojang.serialization.MapCodec;
import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.body.abilities.AbilityType;
import com.thedrofdoctoring.synthetics.body.abilities.active.AbilityActiveInstance;
import com.thedrofdoctoring.synthetics.body.abilities.active.types.LeapAbility;
import com.thedrofdoctoring.synthetics.body.abilities.active.types.WallClimbAbility;
import com.thedrofdoctoring.synthetics.body.abilities.passive.instances.AbilityData;
import com.thedrofdoctoring.synthetics.body.abilities.passive.instances.AttributeAbilityInstance;
import com.thedrofdoctoring.synthetics.body.abilities.passive.instances.GenericPassiveAbilityInstance;
import com.thedrofdoctoring.synthetics.body.abilities.passive.types.AttributeAbilityType;
import com.thedrofdoctoring.synthetics.body.abilities.passive.types.BatteryAbilityType;
import com.thedrofdoctoring.synthetics.body.abilities.passive.types.PassiveAbilityType;
import com.thedrofdoctoring.synthetics.body.abilities.passive.types.generators.FoodGeneratorAbility;
import com.thedrofdoctoring.synthetics.body.abilities.passive.types.generators.SolarGeneratorAbility;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

@SuppressWarnings("unused")
public class SyntheticAbilities {

    public static final ResourceKey<Registry<AbilityType>> ABILITY_REGISTRY_KEY = ResourceKey.createRegistryKey(Synthetics.rl("ability_types"));
    public static final Registry<AbilityType> ABILITY_REGISTRY = new RegistryBuilder<>(ABILITY_REGISTRY_KEY)
            .sync(true)
            .defaultKey(Synthetics.rl("none"))
            .create();


    public static final ResourceKey<Registry<MapCodec<? extends AbilityData>>> ABILITY_DATA_TYPE_KEY = ResourceKey.createRegistryKey(Synthetics.rl("ability_data_type"));
    public static final Registry<MapCodec<? extends AbilityData>> ABILITY_DATA_TYPE_REGISTRY = new RegistryBuilder<>(ABILITY_DATA_TYPE_KEY)
            .sync(true)
            .defaultKey(Synthetics.rl("none"))
            .create();


    public static final ResourceKey<Registry<StreamCodec<? super RegistryFriendlyByteBuf, ? extends AbilityData>>> ABILITY_DATA_STREAM_TYPE_KEY = ResourceKey.createRegistryKey(Synthetics.rl("ability_data_type_stream"));
    public static final Registry<StreamCodec<? super RegistryFriendlyByteBuf, ? extends AbilityData>> ABILITY_DATA_STREAM_TYPE_REGISTRY = new RegistryBuilder<>(ABILITY_DATA_STREAM_TYPE_KEY)
            .sync(true)
            .defaultKey(Synthetics.rl("none"))
            .create();

    public static final DeferredRegister<StreamCodec<? super RegistryFriendlyByteBuf, ? extends AbilityData>> ABILITY_DATA_STREAM = DeferredRegister.create(ABILITY_DATA_STREAM_TYPE_KEY, Synthetics.MODID);


    public static final DeferredRegister<AbilityType> ABILITIES = DeferredRegister.create(ABILITY_REGISTRY, Synthetics.MODID);
    public static final DeferredRegister<MapCodec<? extends AbilityData>> ABILITY_DATA = DeferredRegister.create(ABILITY_DATA_TYPE_REGISTRY, Synthetics.MODID);
    public static final DeferredRegister<StreamCodec<? super RegistryFriendlyByteBuf, ? extends AbilityData>> ABILITY_STREAM_DATA = DeferredRegister.create(ABILITY_DATA_STREAM_TYPE_REGISTRY, Synthetics.MODID);


    public static final DeferredHolder<MapCodec<? extends AbilityData>, MapCodec<AttributeAbilityInstance.AttributeAbilityData>> ATTRIBUTE_TYPE =
            ABILITY_DATA.register("attribute", () -> AttributeAbilityInstance.AttributeAbilityData.CODEC);
    public static final DeferredHolder<StreamCodec<? super RegistryFriendlyByteBuf, ? extends AbilityData>, StreamCodec<? super RegistryFriendlyByteBuf, AttributeAbilityInstance.AttributeAbilityData>> ATTRIBUTE_STREAM_TYPE =
            ABILITY_DATA_STREAM.register("attribute", () -> AttributeAbilityInstance.AttributeAbilityData.STREAM_CODEC);

    public static final DeferredHolder<MapCodec<? extends AbilityData>, MapCodec<GenericPassiveAbilityInstance.Data>> GENERIC_TYPE =
            ABILITY_DATA.register("generic_passive_ability", () -> GenericPassiveAbilityInstance.Data.CODEC);
    public static final DeferredHolder<StreamCodec<? super RegistryFriendlyByteBuf, ? extends AbilityData>, StreamCodec<? super RegistryFriendlyByteBuf, GenericPassiveAbilityInstance.Data>> GENERIC_STREAM_TYPE =
            ABILITY_DATA_STREAM.register("generic_passive_ability", () -> GenericPassiveAbilityInstance.Data.STREAM_CODEC);

    public static final DeferredHolder<MapCodec<? extends AbilityData>, MapCodec<AbilityActiveInstance.ActiveAbilityData>> ACTIVE_GENERIC_TYPE =
            ABILITY_DATA.register("generic_active_ability", () -> AbilityActiveInstance.ActiveAbilityData.CODEC);
    public static final DeferredHolder<StreamCodec<? super RegistryFriendlyByteBuf, ? extends AbilityData>, StreamCodec<? super RegistryFriendlyByteBuf, AbilityActiveInstance.ActiveAbilityData>> ACTIVE_GENERIC_STREAM_TYPE =
            ABILITY_DATA_STREAM.register("generic_active_ability", () -> AbilityActiveInstance.ActiveAbilityData.STREAM_CODEC);

    //TODO:
    // Abilities replaced with ability types, eg attribute modifier ability, damage resistance, with the specified modified attribute in the ability data gen.

    public static final DeferredHolder<AbilityType, AttributeAbilityType> ATTRIBUTE_ABILITY = ABILITIES.register("attribute_type", AttributeAbilityType::new);
    public static final DeferredHolder<AbilityType, BatteryAbilityType> BATTERY = ABILITIES.register("battery", BatteryAbilityType::new);
    public static final DeferredHolder<AbilityType, SolarGeneratorAbility> SOLAR_GENERATOR = ABILITIES.register("solar_generator", SolarGeneratorAbility::new);
    public static final DeferredHolder<AbilityType, PassiveAbilityType> UNDERWATER_VISION = ABILITIES.register("underwater_vision", PassiveAbilityType::new);
    public static final DeferredHolder<AbilityType, FoodGeneratorAbility> FOOD_GENERATOR = ABILITIES.register("food_generator", FoodGeneratorAbility::new);


    public static final DeferredHolder<AbilityType, LeapAbility> LEAP = ABILITIES.register("leap", LeapAbility::new);
    public static final DeferredHolder<AbilityType, WallClimbAbility> WALL_CLIMB = ABILITIES.register("wall_climb", WallClimbAbility::new);



    public static void register(IEventBus bus) {
        ABILITIES.register(bus);
        ABILITY_DATA.register(bus);
        ABILITY_DATA_STREAM.register(bus);
    }
    public static void registerRegistries(NewRegistryEvent event) {
        event.register(ABILITY_REGISTRY);
        event.register(ABILITY_DATA_TYPE_REGISTRY);
        event.register(ABILITY_DATA_STREAM_TYPE_REGISTRY);

    }
}
