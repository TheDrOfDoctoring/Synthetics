package com.thedrofdoctoring.synthetics.core.synthetics;

import com.mojang.serialization.MapCodec;
import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.abilities.AbilityType;
import com.thedrofdoctoring.synthetics.abilities.active.instances.AbilityActiveInstance;
import com.thedrofdoctoring.synthetics.abilities.active.instances.FlamethrowerAbilityInstance;
import com.thedrofdoctoring.synthetics.abilities.active.types.FlamethrowerAbility;
import com.thedrofdoctoring.synthetics.abilities.active.types.LeapAbility;
import com.thedrofdoctoring.synthetics.abilities.active.types.WallClimbAbility;
import com.thedrofdoctoring.synthetics.abilities.passive.instances.AbilityData;
import com.thedrofdoctoring.synthetics.abilities.passive.instances.AttributeAbilityInstance;
import com.thedrofdoctoring.synthetics.abilities.passive.instances.GenericPassiveAbilityInstance;
import com.thedrofdoctoring.synthetics.abilities.passive.types.AttributeAbilityType;
import com.thedrofdoctoring.synthetics.abilities.passive.types.BatteryAbilityType;
import com.thedrofdoctoring.synthetics.abilities.passive.types.PassiveAbilityType;
import com.thedrofdoctoring.synthetics.abilities.passive.types.generators.FoodGeneratorAbility;
import com.thedrofdoctoring.synthetics.abilities.passive.types.generators.SolarGeneratorAbility;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.function.Supplier;

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

    static {
        registerType("attribute",
                () -> AttributeAbilityInstance.AttributeAbilityData.CODEC,
                () -> AttributeAbilityInstance.AttributeAbilityData.STREAM_CODEC
        );
        registerType("generic_passive_ability",
                () -> GenericPassiveAbilityInstance.Data.CODEC,
                () -> GenericPassiveAbilityInstance.Data.STREAM_CODEC
        );
        registerType("generic_active_ability",
                () -> AbilityActiveInstance.Data.CODEC,
                () -> AbilityActiveInstance.Data.STREAM_CODEC
        );
        registerType("flamethrower_ability",
                () -> FlamethrowerAbilityInstance.Data.CODEC,
                () -> FlamethrowerAbilityInstance.Data.STREAM_CODEC
        );

    }

    public static final DeferredHolder<AbilityType, AttributeAbilityType> ATTRIBUTE_ABILITY = ABILITIES.register("attribute_type", AttributeAbilityType::new);
    public static final DeferredHolder<AbilityType, BatteryAbilityType> BATTERY = ABILITIES.register("battery", BatteryAbilityType::new);
    public static final DeferredHolder<AbilityType, SolarGeneratorAbility> SOLAR_GENERATOR = ABILITIES.register("solar_generator", SolarGeneratorAbility::new);
    public static final DeferredHolder<AbilityType, PassiveAbilityType> UNDERWATER_VISION = ABILITIES.register("underwater_vision", PassiveAbilityType::new);
    public static final DeferredHolder<AbilityType, FoodGeneratorAbility> FOOD_GENERATOR = ABILITIES.register("food_generator", FoodGeneratorAbility::new);

    public static final DeferredHolder<AbilityType, LeapAbility> LEAP = ABILITIES.register("leap", LeapAbility::new);
    public static final DeferredHolder<AbilityType, WallClimbAbility> WALL_CLIMB = ABILITIES.register("wall_climb", WallClimbAbility::new);
    public static final DeferredHolder<AbilityType, FlamethrowerAbility> FLAMETHROWER = ABILITIES.register("flamethrower", FlamethrowerAbility::new);


    private static void registerType(String id, Supplier<MapCodec<? extends AbilityData>> codec, Supplier<StreamCodec<? super RegistryFriendlyByteBuf, ? extends AbilityData>> streamCodec) {
        ABILITY_DATA.register(id, codec);
        ABILITY_STREAM_DATA.register(id, streamCodec);
    }

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
