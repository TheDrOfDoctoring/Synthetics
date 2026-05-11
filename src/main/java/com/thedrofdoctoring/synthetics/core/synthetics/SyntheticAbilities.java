package com.thedrofdoctoring.synthetics.core.synthetics;

import com.mojang.serialization.MapCodec;
import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.abilities.AbilityData;
import com.thedrofdoctoring.synthetics.abilities.AbilityType;
import com.thedrofdoctoring.synthetics.abilities.active.instances.*;
import com.thedrofdoctoring.synthetics.abilities.active.types.*;
import com.thedrofdoctoring.synthetics.abilities.passive.instances.*;
import com.thedrofdoctoring.synthetics.abilities.passive.types.*;
import com.thedrofdoctoring.synthetics.abilities.passive.types.generators.FatConverterAbility;
import com.thedrofdoctoring.synthetics.abilities.passive.types.generators.FoodGeneratorAbility;
import com.thedrofdoctoring.synthetics.abilities.passive.types.generators.ShockAbsorberAbility;
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
        registerType("effect_amplifier_ability",
                () -> EffectAbilityInstance.Data.CODEC,
                () -> EffectAbilityInstance.Data.STREAM_CODEC
        );
        registerType("passive_healing_ability",
                () -> HealingAbilityInstance.Data.CODEC,
                () -> HealingAbilityInstance.Data.STREAM_CODEC
        );
        registerType("damage_modifier_ability",
                () -> DamageResistanceAbilityInstance.Data.CODEC,
                () -> DamageResistanceAbilityInstance.Data.STREAM_CODEC
        );
        registerType("lasting_effect_ability",
                () -> LastingEffectAbilityInstance.Data.CODEC,
                () -> LastingEffectAbilityInstance.Data.STREAM_CODEC
        );
        registerType("harpoon_ability",
                () -> HarpoonAbilityInstance.Data.CODEC,
                () -> HarpoonAbilityInstance.Data.STREAM_CODEC
        );
        registerType("permeator_ability",
                () -> BlockPermeatorAbilityInstance.Data.CODEC,
                () -> BlockPermeatorAbilityInstance.Data.STREAM_CODEC
        );
        registerType("block_highlight_ability",
                () -> BlockHighlightAbilityInstance.Data.CODEC,
                () -> BlockHighlightAbilityInstance.Data.STREAM_CODEC
        );
        registerType("lasting_attribute_ability",
                () -> LastingAttributeAbilityInstance.Data.CODEC,
                () -> LastingAttributeAbilityInstance.Data.STREAM_CODEC
        );
        registerType("passive_invisibility_ability",
                () -> PassiveInvisibilityInstance.Data.CODEC,
                () -> PassiveInvisibilityInstance.Data.STREAM_CODEC
        );
        registerType("entity_highlight_ability",
                () -> EntityHighlightAbilityInstance.Data.CODEC,
                () -> EntityHighlightAbilityInstance.Data.STREAM_CODEC
        );
        registerType("blasting_ability",
                () -> BlastingAbilityInstance.Data.CODEC,
                () -> BlastingAbilityInstance.Data.STREAM_CODEC
        );
        registerType("shock_ability",
                () -> ShockAbilityInstance.Data.CODEC,
                () -> ShockAbilityInstance.Data.STREAM_CODEC
        );

    }

    public static final DeferredHolder<AbilityType, AttributeAbilityType> ATTRIBUTE_ABILITY = ABILITIES.register("attribute_type", AttributeAbilityType::new);
    public static final DeferredHolder<AbilityType, BatteryAbilityType> BATTERY = ABILITIES.register("battery", BatteryAbilityType::new);
    public static final DeferredHolder<AbilityType, SolarGeneratorAbility> SOLAR_GENERATOR = ABILITIES.register("solar_generator", SolarGeneratorAbility::new);
    public static final DeferredHolder<AbilityType, StandardPassiveAbility> UNDERWATER_VISION = ABILITIES.register("underwater_vision", StandardPassiveAbility::new);
    public static final DeferredHolder<AbilityType, FoodGeneratorAbility> FOOD_GENERATOR = ABILITIES.register("food_generator", FoodGeneratorAbility::new);
    public static final DeferredHolder<AbilityType, EffectAbilityType> EFFECT_AMPLIFIER = ABILITIES.register("effect_amplifier", EffectAbilityType::new);
    public static final DeferredHolder<AbilityType, DamageResistanceAbilityType> DAMAGE_MODIFIER = ABILITIES.register("damage_modifier", DamageResistanceAbilityType::new);
    public static final DeferredHolder<AbilityType, HealingAbilityType> HEALING = ABILITIES.register("healing", HealingAbilityType::new);
    public static final DeferredHolder<AbilityType, StandardPassiveAbility> DRONE_LINK = ABILITIES.register("drone_link", StandardPassiveAbility::new);
    public static final DeferredHolder<AbilityType, StandardPassiveAbility> PERMEABLE_LINK = ABILITIES.register("permeable_link", StandardPassiveAbility::new);
    public static final DeferredHolder<AbilityType, ShockAbsorberAbility> SHOCK_ABSORBER = ABILITIES.register("shock_absorber", ShockAbsorberAbility::new);
    public static final DeferredHolder<AbilityType, PassiveInvisibilityType> PASSIVE_INVISIBILITY = ABILITIES.register("passive_invisibility", PassiveInvisibilityType::new);
    public static final DeferredHolder<AbilityType, StandardPassiveAbility> FLIGHT_COUNT = ABILITIES.register("flight_count", StandardPassiveAbility::new);
    public static final DeferredHolder<AbilityType, FatConverterAbility> FAT_CONVERSION = ABILITIES.register("fat_conversion", FatConverterAbility::new);


    public static final DeferredHolder<AbilityType, LeapAbility> LEAP = ABILITIES.register("leap", LeapAbility::new);
    public static final DeferredHolder<AbilityType, WallClimbAbility> WALL_CLIMB = ABILITIES.register("wall_climb", WallClimbAbility::new);
    public static final DeferredHolder<AbilityType, ViewLinkedAbility> TOGGLE_VIEW_LINKED = ABILITIES.register("toggle_view_linked", ViewLinkedAbility::new);
    public static final DeferredHolder<AbilityType, InteractLinkedAbility> INTERACT_LINKED = ABILITIES.register("interact_linked", InteractLinkedAbility::new);
    public static final DeferredHolder<AbilityType, FlamethrowerAbility> FLAMETHROWER = ABILITIES.register("flamethrower", FlamethrowerAbility::new);
    public static final DeferredHolder<AbilityType, RepulsorAbility> REPULSOR = ABILITIES.register("repulsor", RepulsorAbility::new);
    public static final DeferredHolder<AbilityType, ViewLinkedMenuAbility> VIEW_LINKED_MENU = ABILITIES.register("view_linked_menu", ViewLinkedMenuAbility::new);
    public static final DeferredHolder<AbilityType, InvisibilityAbility> INVISIBILITY = ABILITIES.register("invisibility", InvisibilityAbility::new);
    public static final DeferredHolder<AbilityType, LastingEffectAbility> LASTING_EFFECT = ABILITIES.register("lasting_effect", LastingEffectAbility::new);
    public static final DeferredHolder<AbilityType, HarpoonAbility> HARPOON = ABILITIES.register("harpoon", HarpoonAbility::new);
    public static final DeferredHolder<AbilityType, BlockPermeatorAbility> PERMEATOR = ABILITIES.register("permeator", BlockPermeatorAbility::new);
    public static final DeferredHolder<AbilityType, ClosePermeatedAbility> CLOSE_PERMEATED = ABILITIES.register("close_permeated", ClosePermeatedAbility::new);
    public static final DeferredHolder<AbilityType, InteractLinkedAbility> TELEPORT_LINKED = ABILITIES.register("teleport_linked", InteractLinkedAbility::new);
    public static final DeferredHolder<AbilityType, BlockHighlightAbility> BLOCK_HIGHLIGHT = ABILITIES.register("block_highlight", BlockHighlightAbility::new);
    public static final DeferredHolder<AbilityType, TeleportAbility> TELEPORT = ABILITIES.register("teleport", TeleportAbility::new);
    public static final DeferredHolder<AbilityType, LastingAttributeAbility> LASTING_ATTRIBUTE = ABILITIES.register("lasting_attribute", LastingAttributeAbility::new);
    public static final DeferredHolder<AbilityType, PositionLockAbility> POSITION_LOCK = ABILITIES.register("position_lock", PositionLockAbility::new);
    public static final DeferredHolder<AbilityType, EntityHighlightAbility> ENTITY_HIGHLIGHT = ABILITIES.register("entity_highlight", EntityHighlightAbility::new);
    public static final DeferredHolder<AbilityType, RocketFlightAbility> ROCKET_FLIGHT = ABILITIES.register("rocket_flight", RocketFlightAbility::new);
    public static final DeferredHolder<AbilityType, RocketFlightAugAbility> ROCKET_AUG_FLIGHT = ABILITIES.register("rocket_aug_flight", RocketFlightAugAbility::new);
    public static final DeferredHolder<AbilityType, BrushAbility> BRUSH_ABILITY = ABILITIES.register("brush", BrushAbility::new);
    public static final DeferredHolder<AbilityType, WaterwalkingAbility> WATER_WALKING = ABILITIES.register("water_walking", WaterwalkingAbility::new);
    public static final DeferredHolder<AbilityType, ItemMagnetAbility> ITEM_MAGNET = ABILITIES.register("item_magnet", ItemMagnetAbility::new);
    public static final DeferredHolder<AbilityType, BlastingAbility> BLASTING = ABILITIES.register("blasting", BlastingAbility::new);
    public static final DeferredHolder<AbilityType, ShockAbility> SHOCKING = ABILITIES.register("shocking", ShockAbility::new);



    private static <T extends AbilityData> void registerType(String id, Supplier<MapCodec<T>> codec, Supplier<StreamCodec<? super RegistryFriendlyByteBuf, T>> streamCodec) {
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
