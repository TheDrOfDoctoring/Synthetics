package com.thedrofdoctoring.synthetics.capabilities;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.body.abilities.IBodyInstallable;
import com.thedrofdoctoring.synthetics.body.abilities.SyntheticAbilityType;
import com.thedrofdoctoring.synthetics.body.abilities.active.SyntheticAbilityActiveInstance;
import com.thedrofdoctoring.synthetics.body.abilities.active.SyntheticActiveAbilityType;
import com.thedrofdoctoring.synthetics.body.abilities.active.SyntheticLastingAbilityType;
import com.thedrofdoctoring.synthetics.body.abilities.passive.SyntheticAbilityPassiveInstance;
import com.thedrofdoctoring.synthetics.body.abilities.passive.SyntheticPassiveAbilityType;
import com.thedrofdoctoring.synthetics.capabilities.serialisation.ISaveData;
import com.thedrofdoctoring.synthetics.capabilities.serialisation.ISyncable;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.types.body.SyntheticAbility;
import com.thedrofdoctoring.synthetics.core.synthetics.SyntheticAbilities;
import com.thedrofdoctoring.synthetics.util.Helper;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;

import java.util.*;

// Some of the active ability system handled here is partially based on Vampirism's ActionHandler, licensed under GNU LGPL. https://github.com/TeamLapen/Vampirism/blob/1.21/src/main/java/de/teamlapen/vampirism/entity/player/actions/ActionHandler.java
@SuppressWarnings("unused")
public class AbilityManager implements ISyncable {

    private final Object2ObjectMap<ResourceLocation, SyntheticAbilityPassiveInstance> passiveAbilities;
    private final Object2ObjectMap<ResourceLocation, SyntheticAbilityActiveInstance> activeAbilities;

    private final Object2ObjectMap<Holder<Attribute>, List<ResourceLocation>> attributes;
    private final Object2IntMap<ResourceLocation> cooldown;
    private final Object2IntMap<ResourceLocation> duration;


    private final SyntheticsPlayer manager;
    private boolean dirty;



    public static final String KEY = "ability_manager";

    public AbilityManager(SyntheticsPlayer manager) {
        this.passiveAbilities = new Object2ObjectOpenHashMap<>();
        this.activeAbilities = new Object2ObjectOpenHashMap<>();
        this.attributes = new Object2ObjectOpenHashMap<>();
        this.manager = manager;

        this.cooldown = new Object2IntOpenHashMap<>();
        this.duration = new Object2IntOpenHashMap<>();

    }

    public boolean isDirty() {
        return dirty;
    }

    public void markDirty() {
        this.dirty = true;
    }
    public void addAbilities(IBodyInstallable<?> holder) {
        if(holder.abilities().isPresent()) {
            HolderSet<SyntheticAbility> holderSet = holder.abilities().get();
            for(int i = 0; i < holderSet.size(); i++ ){
                SyntheticAbility ability = holderSet.get(i).value();
                SyntheticAbilityType type = ability.abilityType();
                if(type instanceof SyntheticPassiveAbilityType passive) {
                    passiveAbilities.put(ability.id(), new SyntheticAbilityPassiveInstance(passive, this.manager, (float) ability.factor(), ability.operation(), ability.id()));
                } else if(type instanceof SyntheticActiveAbilityType active) {
                    activeAbilities.put(ability.abilityType().getAbilityID(), new SyntheticAbilityActiveInstance(active, this.manager, ability.factor(), ability.options().orElseThrow(), ability.id()));
                }
            }
        }
    }

    public Collection<SyntheticAbilityActiveInstance> getActiveAbilities() {
        return this.activeAbilities.values();
    }
    public float getPercentageForAbilityTime(@NotNull SyntheticAbilityActiveInstance ability) {
        ResourceLocation id = ability.getAbility().getAbilityID();
        if (duration.containsKey(id)) {
            return duration.getInt(id) / ((float) ability.getDuration() * 20);
        }
        if (cooldown.containsKey(id)) {
            return -cooldown.getInt(id) / ((float) ability.getCooldown() * 20);
        }
        return 0f;
    }



    @SuppressWarnings("UnusedReturnValue")
    public boolean toggleAbility(SyntheticActiveAbilityType activeAbility) {

        if(!canActivate(activeAbility)) return false;
        SyntheticAbilityActiveInstance instance = this.activeAbilities.get(activeAbility.getAbilityID());


        if(isAbilityActive(activeAbility)) {
            deactivateAbility((SyntheticLastingAbilityType) activeAbility);
            return true;
        } else if(!this.isAbilityOnCooldown(activeAbility) && activeAbility.activate(manager, instance.getAbilityFactor())) {

            if(activeAbility instanceof SyntheticLastingAbilityType) {
                this.duration.put(activeAbility.getAbilityID(), instance.getDuration() * 20);
            } else {
                this.cooldown.put(activeAbility.getAbilityID(), instance.getCooldown() * 20);
            }
            this.dirty = true;
        }

        return true;
    }

    public boolean canActivate(SyntheticActiveAbilityType type) {
        if(!this.activeAbilities.containsKey(type.getAbilityID())) return false;
        if(isAbilityOnCooldown(type)) return false;
        return type.canBeUsed(manager);

    }
    public boolean isAbilityActive(SyntheticActiveAbilityType type) {
        return this.duration.containsKey(type.getAbilityID());
    }
    public boolean isAbilityOnCooldown(SyntheticActiveAbilityType type) {
        return this.cooldown.containsKey(type.getAbilityID());
    }

    public boolean hasAbility(SyntheticAbility ability) {
        return this.passiveAbilities.containsKey(ability.id()) || this.activeAbilities.containsKey(ability.id());
    }

    public void resetAll() {
        for (ResourceLocation id : duration.keySet()) {
            deactivateAbility((SyntheticLastingAbilityType) this.activeAbilities.get(id).getAbility());
        }
        this.duration.clear();
        this.cooldown.clear();
        dirty = true;
    }

    public void removeAbilities(IBodyInstallable<?> holder) {
        if(holder.abilities().isPresent()) {
            HolderSet<SyntheticAbility> holderSet = holder.abilities().get();
            for(int i = 0; i < holderSet.size(); i++ ){
                SyntheticAbility ability = holderSet.get(i).value();
                SyntheticAbilityType type = ability.abilityType();
                if(type instanceof SyntheticPassiveAbilityType) {
                    passiveAbilities.remove(ability.id());
                } else if(type instanceof SyntheticActiveAbilityType) {
                    activeAbilities.remove(ability.abilityType().getAbilityID());
                }
            }
        }
    }
    public void deactivateAbility(SyntheticLastingAbilityType type) {
        ResourceLocation id = type.getAbilityID();
        if(this.duration.containsKey(id)) {
            duration.removeInt(id);
            if(!cooldown.containsKey(id)) {
                cooldown.put(id, this.activeAbilities.get(id).getCooldown() * 20);
            }
            type.onAbilityDeactivated(manager);
            dirty = true;
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
            this.reactivateAbilities();
            this.rebuildAttributes(owner);
        }

    }

    private void rebuildAttributes(LivingEntity owner) {
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
    public boolean onTick() {
        Iterator<Object2IntMap.Entry<ResourceLocation>> cooldownIterator = cooldown.object2IntEntrySet().iterator();
        while (cooldownIterator.hasNext()) {
            Object2IntMap.Entry<ResourceLocation> entry = cooldownIterator.next();
            int cooldownTime = entry.getIntValue();
            if (cooldownTime <= 1) {
                cooldownIterator.remove();
            } else {
                entry.setValue(cooldownTime - 1);
            }
        }

        for (Object2IntMap.Entry<ResourceLocation> entry : duration.object2IntEntrySet()) {
            int newTime = entry.getIntValue() - 1;
            SyntheticLastingAbilityType lasting = (SyntheticLastingAbilityType) this.activeAbilities.get(entry.getKey()).getAbility();
            if (newTime == 0 || !this.activeAbilities.containsKey(entry.getKey())) {
                deactivateAbility(lasting);
            } else {
                if (lasting.onTick(manager, this.activeAbilities.get(entry.getKey()).getAbilityFactor())) {
                    entry.setValue(1);
                } else {
                    entry.setValue(newTime);
                }
            }

        }
        if(dirty) {
            dirty = false;
            return true;
        }
        return false;

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
                active.put(instance.getAbility().getAbilityID().toString(), data.serialiseNBT(provider));
            }
        }
        tag.put("passives", passive);
        tag.put("actives", active);
        tag.put("cooldown", serialiseTimeMap(cooldown));
        tag.put("duration", serialiseTimeMap(duration));

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

    @SuppressWarnings("LoggingSimilarMessage")
    public void deserialiseAbilities(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag tag) {

        HolderLookup.RegistryLookup<SyntheticAbility> lookup = provider.lookupOrThrow(SyntheticsData.ABILITIES);

        if(tag.contains("passives") && tag.get("passives") instanceof CompoundTag passives) {
            Set<String> keys = passives.getAllKeys();
            for(String key : keys) {
                SyntheticAbility abilityInstance = Helper.retrieveDataObject(key, SyntheticsData.ABILITIES, lookup);
                if(passives.get(key) instanceof CompoundTag passiveTag && abilityInstance != null && abilityInstance.abilityType() instanceof ISaveData data) {
                    data.deserialiseNBT(provider, passiveTag);
                } else if(abilityInstance == null) {
                    Synthetics.LOGGER.warn("Failed to deserialise ability instance{}", key);
                }

            }
        }
        if(tag.contains("actives") && tag.get("actives") instanceof CompoundTag actives) {
            Set<String> keys = actives.getAllKeys();
            for(String key : keys) {
                ResourceLocation id = ResourceLocation.tryParse(key);
                if(id == null) {
                    Synthetics.LOGGER.warn("Failed to deserialise ability {}", key);
                    continue;

                }
                SyntheticAbilityType type = SyntheticAbilities.ABILITY_REGISTRY.get(id);
                if(type instanceof SyntheticActiveAbilityType && actives.get(key) instanceof CompoundTag activeTag &&  type instanceof ISaveData data) {
                    data.deserialiseNBT(provider, activeTag);
                }
            }
        }
    }



    @Override
    public void deserialiseNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
        if(nbt.contains(nbtKey()) && nbt.get(nbtKey()) instanceof CompoundTag tag) {
            deserialiseAbilities(provider, tag);
            if(!this.manager.getEntity().getCommandSenderWorld().isClientSide) {
                deserialiseAttributes(tag);

            }
            this.duration.clear();
            if(tag.contains("duration", CompoundTag.TAG_COMPOUND)) {
                this.deserialiseTimeMap((CompoundTag) tag.get("duration"), duration);
            }
            this.cooldown.clear();

            if(tag.contains("cooldown", CompoundTag.TAG_COMPOUND)) {
                this.deserialiseTimeMap((CompoundTag) tag.get("cooldown"), cooldown);
            }

        }

    }

    public void reactivateAbilities() {
        if(!this.manager.getEntity().getCommandSenderWorld().isClientSide) {
            for (ResourceLocation id : duration.keySet()) {
                SyntheticAbilityActiveInstance instance = this.activeAbilities.get(id);
                SyntheticLastingAbilityType lasting = (SyntheticLastingAbilityType) instance.getAbility();
                lasting.onRestoreActivate(manager, instance.getAbilityFactor());
            }
        }
    }

    public void clear() {
        this.passiveAbilities.clear();
        this.activeAbilities.clear();
    }

    private void deserialiseTimeMap(@NotNull CompoundTag nbt, @NotNull Object2IntMap<ResourceLocation> map) {
        for (String key : nbt.getAllKeys()) {
            ResourceLocation id = ResourceLocation.tryParse(key);
            if (id == null || !this.activeAbilities.containsKey(id)) {
                Synthetics.LOGGER.warn("Failed to load timer for ability {}", id);
            } else {
                map.put(id, nbt.getInt(key));
            }
        }
    }

    private @NotNull CompoundTag serialiseTimeMap(@NotNull Object2IntMap<ResourceLocation> map) {
        CompoundTag nbt = new CompoundTag();
        var set = map.object2IntEntrySet();
        for (Object2IntMap.Entry<ResourceLocation> entry : set) {
            nbt.putInt(entry.getKey().toString(), entry.getIntValue());
        }
        return nbt;
    }



    @Override
    public CompoundTag serialiseUpdateNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.put("cooldown", serialiseTimeMap(cooldown));
        tag.put("duration", serialiseTimeMap(duration));

        return tag;
    }

    @Override
    public void deserialiseUpdateNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
        if(nbt.contains(nbtKey(), CompoundTag.TAG_COMPOUND)) {
            CompoundTag tag = (CompoundTag) nbt.get(nbtKey());
            if(tag == null) return;
            if(tag.contains("duration", CompoundTag.TAG_COMPOUND)) {
                CompoundTag timers = (CompoundTag) tag.get("duration");
                assert timers != null;
                if (this.manager.getEntity().getCommandSenderWorld().isClientSide) {
                    for (Object2IntMap.Entry<ResourceLocation> active : duration.object2IntEntrySet()) {
                        String key = active.getKey().toString();
                        if (timers.contains(key)) {
                            active.setValue(timers.getInt(key));
                            timers.remove(key);
                        } else {
                            deactivateAbility((SyntheticLastingAbilityType) activeAbilities.get(active.getKey()).getAbility());
                        }
                    }
                    for (String key : timers.getAllKeys()) {
                        ResourceLocation id = ResourceLocation.parse(key);
                        SyntheticAbilityActiveInstance instance = this.activeAbilities.get(id);
                        SyntheticActiveAbilityType ability = instance.getAbility();
                        if (ability == null) {
                            Synthetics.LOGGER.warn("Ability failed to load client side {}", id);
                        } else {
                            ability.activateClient(manager, instance.getAbilityFactor());
                            duration.put(id, timers.getInt(key));
                        }
                    }
                }
            }
            if (tag.contains("cooldown", Tag.TAG_COMPOUND)) {
                cooldown.clear();
                deserialiseTimeMap(tag.getCompound("cooldown"), cooldown);
            }
        }


    }
    public String nbtKey() {
        return KEY;
    }
}
