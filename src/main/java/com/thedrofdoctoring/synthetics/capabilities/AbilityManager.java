package com.thedrofdoctoring.synthetics.capabilities;

import com.thedrofdoctoring.synthetics.body.abilities.SyntheticAbilityType;
import com.thedrofdoctoring.synthetics.body.abilities.active.SyntheticAbilityActiveInstance;
import com.thedrofdoctoring.synthetics.body.abilities.active.SyntheticActiveAbilityType;
import com.thedrofdoctoring.synthetics.body.abilities.passive.SyntheticAbilityPassiveInstance;
import com.thedrofdoctoring.synthetics.body.abilities.passive.SyntheticPassiveAbilityType;
import com.thedrofdoctoring.synthetics.capabilities.serialisation.ISaveData;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.types.SyntheticAbility;
import com.thedrofdoctoring.synthetics.core.data.types.SyntheticAugment;
import com.thedrofdoctoring.synthetics.util.Helper;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class AbilityManager implements ISaveData {

    private final Object2ObjectMap<ResourceLocation, SyntheticAbilityPassiveInstance> passiveAbilities;
    private final Object2ObjectMap<ResourceLocation, SyntheticAbilityActiveInstance> activeAbilities;

    private final Object2ObjectMap<Holder<Attribute>, List<ResourceLocation>> attributes;

    private final SyntheticsPlayer manager;

    public static final String KEY = "ability_manager";

    public AbilityManager(SyntheticsPlayer manager) {
        this.passiveAbilities = new Object2ObjectOpenHashMap<>();
        this.activeAbilities = new Object2ObjectOpenHashMap<>();
        this.attributes = new Object2ObjectOpenHashMap<>();
        this.manager = manager;
    }
    public void addAugment(SyntheticAugment augment) {
        for(int i = 0; i < augment.abilities().size(); i++ ){
            SyntheticAbility ability = augment.abilities().get(i).value();
            SyntheticAbilityType type = ability.abilityType();
            if(type instanceof SyntheticPassiveAbilityType passive) {
                passiveAbilities.put(ability.id(), new SyntheticAbilityPassiveInstance(passive, this.manager, ability.factor(), ability.operation(), ability.id()));
            } else if(type instanceof SyntheticActiveAbilityType active) {
                activeAbilities.put(ability.id(), new SyntheticAbilityActiveInstance(active, this.manager, ability.id()));
            }
        }

    }
    public void removeAugment(SyntheticAugment augment) {

        for(int i = 0; i < augment.abilities().size(); i++ ){
            SyntheticAbility ability = augment.abilities().get(i).value();
            SyntheticAbilityType type = ability.abilityType();
            if(type instanceof SyntheticPassiveAbilityType passive) {
                passiveAbilities.remove(ability.id());
            } else if(type instanceof SyntheticActiveAbilityType active) {
                activeAbilities.remove(ability.id());
            }
        }
    }

    public void onUpdate() {
        LivingEntity owner = this.manager.getEntity();
        if(!owner.getCommandSenderWorld().isClientSide) {
            for(var entry : this.attributes.object2ObjectEntrySet()) {
                AttributeInstance instance = owner.getAttribute(entry.getKey());
                if(instance == null) continue;
                for(ResourceLocation modifier : entry.getValue()) {
                    removeModifier(instance, modifier);
                }
            }
            rebuildList(owner);
        }

    }

    private void rebuildList(LivingEntity owner) {
        this.attributes.clear();
        for(SyntheticAbilityPassiveInstance passive : this.passiveAbilities.values()) {
            Optional<Pair<Holder<Attribute>, AttributeModifier>> opt = passive.getModifiedAttribute(owner);
            if(opt.isPresent()) {
                Pair<Holder<Attribute>, AttributeModifier> attributePair = opt.get();
                AttributeInstance instance = owner.getAttribute(attributePair.left());
                if(instance == null) continue;
                instance.addTransientModifier(attributePair.right());
                if(this.attributes.containsKey(attributePair.left())) {
                    List<ResourceLocation> list = this.attributes.get(attributePair.left());
                    list.add(attributePair.right().id());
                    this.attributes.put(attributePair.left(), list);

                } else {
                    List<ResourceLocation> list = new ArrayList<>();
                    list.add(attributePair.right().id());
                    this.attributes.put(attributePair.left(), list);
                }
            }
        }
    }
    public void onTick() {

    }

    public void removeModifier(@NotNull AttributeInstance att, @NotNull ResourceLocation location) {
        AttributeModifier m = att.getModifier(location);
        if (m != null) {
            att.removeModifier(m);
        }
    }

    @Override
    public CompoundTag serialiseNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        CompoundTag passive = new CompoundTag();
        CompoundTag active = new CompoundTag();
        for(SyntheticAbilityPassiveInstance instance : passiveAbilities.values()) {
            if(instance instanceof ISaveData data) {
                passive.put(instance.getInstanceID().toString(), data.serialiseNBT(provider));
            }
        }
        for(SyntheticAbilityActiveInstance instance : activeAbilities.values()) {
            if(instance instanceof ISaveData data) {
                active.put(instance.getInstanceID().toString(), data.serialiseNBT(provider));
            }
        }
        tag.put("passives", passive);
        tag.put("actives", active);

        tag.put("attributes", serialiseAttributes());

        return tag;
    }

    public void deserialiseAttributes(@NotNull CompoundTag nbt) {
        this.attributes.clear();
        if (nbt.contains("attributes") && nbt.get("attributes") instanceof CompoundTag tag) {
            Set<String> keys = tag.getAllKeys();
            for(String attributeKey : keys) {
                ResourceLocation location = ResourceLocation.tryParse(attributeKey);
                if(location != null && BuiltInRegistries.ATTRIBUTE.getHolder(location).isPresent()) {
                    Holder<Attribute> attributeHolder = BuiltInRegistries.ATTRIBUTE.getHolder(location).get();
                    List<ResourceLocation> instances = new ArrayList<>();

                    if(nbt.contains(attributeKey) && nbt.get(attributeKey) instanceof CompoundTag keyTag) {
                        for(int i = 0; i < keyTag.size(); i++ ) {
                            String id = keyTag.getString(String.valueOf(i));
                            ResourceLocation modifier = ResourceLocation.tryParse(id);
                            if(modifier != null) {
                                instances.add(modifier);
                            }
                        }
                    }
                    this.attributes.put(attributeHolder, instances);
                }


            }


        }
    }

    public CompoundTag serialiseAttributes() {
        CompoundTag attributes = new CompoundTag();
        for(var mapEntry : this.attributes.object2ObjectEntrySet()) {
            CompoundTag attribute = new CompoundTag();
            ResourceKey<Attribute> key = mapEntry.getKey().getKey();
            int i = 0;
            for(ResourceLocation location : mapEntry.getValue()) {
                i++;
                attribute.putString(String.valueOf(i), location.toString());
            }
            if(key != null) {
                attributes.put(key.location().toString(), attribute);
            }
        }

        return attributes;

    }


    @Override
    public void deserialiseNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
        if(nbt.contains(nbtKey()) && nbt.get(nbtKey()) instanceof CompoundTag tag) {
            HolderLookup.RegistryLookup<SyntheticAbility> lookup = provider.lookupOrThrow(SyntheticsData.ABILITIES);

            if(tag.contains("passives") && tag.get("passives") instanceof CompoundTag passives) {
                Set<String> keys = passives.getAllKeys();
                for(String key : keys) {
                    SyntheticAbility abilityInstance = Helper.retrieveDataObject(key, SyntheticsData.ABILITIES, lookup);
                    if(passives.get(key) instanceof CompoundTag passiveTag && abilityInstance != null && abilityInstance.abilityType() instanceof ISaveData data) {
                        data.deserialiseNBT(provider, passiveTag);
                    }

                }
            }
            if(tag.contains("actives") && tag.get("actives") instanceof CompoundTag actives) {
                Set<String> keys = actives.getAllKeys();
                for(String key : keys) {
                    SyntheticAbility abilityInstance = Helper.retrieveDataObject(key, SyntheticsData.ABILITIES, lookup);
                    if(actives.get(key) instanceof CompoundTag activeTag && abilityInstance != null && abilityInstance.abilityType() instanceof ISaveData data) {
                        data.deserialiseNBT(provider, activeTag);
                    }
                }
            }

        }
        if(!this.manager.getEntity().getCommandSenderWorld().isClientSide) {
            deserialiseAttributes(nbt);
        }

    }
    public void clear() {
        this.passiveAbilities.clear();
        this.activeAbilities.clear();
    }

    public String nbtKey() {
        return KEY;
    }

}
