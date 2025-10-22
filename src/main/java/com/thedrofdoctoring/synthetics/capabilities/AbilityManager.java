package com.thedrofdoctoring.synthetics.capabilities;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.abilities.AbilityType;
import com.thedrofdoctoring.synthetics.abilities.IBodyInstallable;
import com.thedrofdoctoring.synthetics.abilities.active.AbilityActiveInstance;
import com.thedrofdoctoring.synthetics.abilities.active.ActiveAbilityType;
import com.thedrofdoctoring.synthetics.abilities.active.LastingAbilityType;
import com.thedrofdoctoring.synthetics.abilities.passive.IAbilityEventListener;
import com.thedrofdoctoring.synthetics.abilities.passive.instances.AbilityPassiveInstance;
import com.thedrofdoctoring.synthetics.abilities.passive.instances.AttributeAbilityInstance;
import com.thedrofdoctoring.synthetics.abilities.passive.types.PassiveAbilityType;
import com.thedrofdoctoring.synthetics.capabilities.serialisation.ISaveData;
import com.thedrofdoctoring.synthetics.capabilities.serialisation.ISyncable;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.Ability;
import com.thedrofdoctoring.synthetics.core.data.types.body.augments.Augment;
import com.thedrofdoctoring.synthetics.core.synthetics.SyntheticAbilities;
import com.thedrofdoctoring.synthetics.util.Helper;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntObjectPair;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

// Some of the active ability system handled here is partially based on Vampirism's ActionHandler, licensed under GNU LGPL. https://github.com/TeamLapen/Vampirism/blob/1.21/src/main/java/de/teamlapen/vampirism/entity/player/actions/ActionHandler.java
@SuppressWarnings("unused")
public class AbilityManager implements ISyncable {

    private final Object2ObjectMap<ResourceLocation, IntObjectPair<AbilityPassiveInstance<?>>> passiveAbilities;
    private final Object2ObjectMap<ResourceLocation, AbilityActiveInstance<?>> activeAbilities;

    private final Object2ObjectMap<Holder<Attribute>, List<ResourceLocation>> attributes;
    private final Object2IntMap<ResourceLocation> cooldown;
    private final Object2IntMap<ResourceLocation> duration;


    private final SyntheticsPlayer manager;
    private boolean dirty;
    private boolean insufficientPower;

    private int timeSinceWarning;
    private static final int WARNING_TIME = 120;

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
            HolderSet<Ability> holderSet = holder.abilities().get();
            List<Ability> abilities = holderSet.stream().map(Holder::value).toList();
            addAbilities(abilities, holder);
        }
    }

    public void addAbilities(List<Ability> abilities, IBodyInstallable<?> source) {

        for (Ability ability : abilities) {
            AbilityType type = ability.abilityType();
            if (type instanceof PassiveAbilityType passive) {
                addAbility(passive, ability, hasPowerDraw(source));
            } else if (type instanceof ActiveAbilityType active) {
                addAbility(active, ability);
            }
        }

    }

    private boolean hasPowerDraw(IBodyInstallable<?> installable) {
        return installable instanceof Augment augment && augment.powerCost() > 0;
    }

    public void addAbility(ActiveAbilityType active, Ability activeAbility) {
        ResourceLocation instanceID = activeAbility.id();
        var instanceOpt = active.createInstance(this.manager, activeAbility.abilityData(), instanceID);
        instanceOpt.ifPresentOrElse(instance ->
                activeAbilities.put(activeAbility.abilityType().getAbilityID(), instance),
                () -> Synthetics.LOGGER.warn("Failed to create Active Ability Instance for ability with instance ID: {}", instanceID)
        );
    }

    public void addAbility(PassiveAbilityType passive, Ability passiveAbility, boolean hasPowerDraw) {

        int count =
                passiveAbilities
                .getOrDefault(passiveAbility.id(), IntObjectPair.of(0, null))
                .firstInt()
                + 1;
        var instanceOpt = passive.createInstance(this.manager, passiveAbility.abilityData(), passiveAbility.id(), hasPowerDraw);

        instanceOpt.ifPresentOrElse(instance -> {
            passive.onAbilityAdded(instance, count, manager);
            passiveAbilities.put(passiveAbility.id(), IntObjectPair.of(count, instance));
            }, () -> Synthetics.LOGGER.warn("Failed to create Passive Ability Instance for ability with instance ID: {}", passiveAbility.id())
        );
    }


    public Collection<AbilityActiveInstance<?>> getActiveAbilities() {
        return this.activeAbilities.values();
    }

    public Collection<AbilityPassiveInstance<?>> getPassiveAbilities() {
        return this.passiveAbilities.values().stream().map(Pair::right).collect(Collectors.toList());
    }

    public Collection<IntObjectPair<AbilityPassiveInstance<?>>> getPassiveAbilitiesPairs() {
        return this.passiveAbilities.values();
    }

    public float getPercentageForAbilityTime(@NotNull AbilityActiveInstance<?> ability) {
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
    public boolean toggleAbility(ActiveAbilityType activeAbility) {

        if(!canActivate(activeAbility)) return false;
        AbilityActiveInstance<?> instance = this.activeAbilities.get(activeAbility.getAbilityID());

        PowerManager power = this.manager.getPowerManager();

        if(isAbilityActive(activeAbility)) {
            deactivateAbility((LastingAbilityType) activeAbility);
            return true;
        } else if(hasSufficientPower(power, instance) && !this.isAbilityOnCooldown(activeAbility) && activeAbility.activate(manager, instance.factor())) {
            drainPower(power, instance);
            if(activeAbility instanceof LastingAbilityType) {
                this.duration.put(activeAbility.getAbilityID(), instance.getDuration() * 20);
            } else {
                this.cooldown.put(activeAbility.getAbilityID(), instance.getCooldown() * 20);
            }
            this.dirty = true;
        } else {
            this.manager.getEntity().playNotifySound(SoundEvents.NOTE_BLOCK_BASS.value(), SoundSource.MASTER, 0.25f, 0.75f);
        }

        return true;
    }

    private boolean hasSufficientPower(PowerManager powerManager, AbilityActiveInstance<?> abilityInstance) {
        if(!powerManager.hasSufficientPower(abilityInstance.getPowerCost())) {
            sendInsufficientPowerMessage(manager);
            return false;
        }
        return true;
    }

    private void sendInsufficientPowerMessage(SyntheticsPlayer player) {
        player.getEntity().displayClientMessage(Component.translatable("synthetics.abilities.insufficient_power").withStyle(ChatFormatting.RED), true);
    }

    private void drainPower(PowerManager powerManager, AbilityActiveInstance<?> abilityInstance) {
        powerManager.drainPower(abilityInstance.getPowerCost());
        powerManager.markDirty();
    }

    public boolean canActivate(ActiveAbilityType type) {
        if(!this.activeAbilities.containsKey(type.getAbilityID())) return false;
        if(isAbilityOnCooldown(type)) return false;
        return type.canBeUsed(manager);
    }

    public int getCountForAbility(AbilityPassiveInstance<?> instance) {
        if (this.passiveAbilities.containsKey(instance.getInstanceID())) {
            return this.passiveAbilities.get(instance.getInstanceID()).firstInt();
        }
        return 0;
    }

    public boolean isAbilityActive(ActiveAbilityType type) {
        return this.duration.containsKey(type.getAbilityID());
    }
    public boolean isAbilityOnCooldown(ActiveAbilityType type) {
        return this.cooldown.containsKey(type.getAbilityID());
    }

    public boolean hasAbility(Ability ability) {
        return this.passiveAbilities.containsKey(ability.id()) || this.activeAbilities.containsKey(ability.abilityType().getAbilityID());
    }
    public boolean isAbilityEnabled(Ability ability) {
        if(ability.abilityType() instanceof PassiveAbilityType) {
            return this.passiveAbilities.containsKey(ability.id()) && this.passiveAbilities.get(ability.id()).right().isEnabled();
        }
        return this.duration.containsKey(ability.abilityType().getAbilityID());
    }

    public void resetAll() {
        for (ResourceLocation id : duration.keySet()) {
            deactivateAbility((LastingAbilityType) this.activeAbilities.get(id).getAbility());
        }
        this.duration.clear();
        this.cooldown.clear();

        dirty = true;
    }

    public void removeAbilities(IBodyInstallable<?> holder) {
        if(holder.abilities().isPresent()) {
            HolderSet<Ability> holderSet = holder.abilities().get();
            removeAbilities(holderSet.stream().map(Holder::value).toList());
        }
    }

    public void removeAbilities(List<Ability> abilities) {
        for (Ability ability : abilities) {
            AbilityType type = ability.abilityType();
            if (type instanceof PassiveAbilityType passive) {
                removeAbility(passive, ability);
            } else if (type instanceof ActiveAbilityType active) {
                removeAbility(active, ability);
            }
        }
    }
    private void removeAbility(ActiveAbilityType active, Ability ability) {
        ResourceLocation id = ability.abilityType().getAbilityID();
        if(duration.containsKey(id)) {
            duration.put(id, 1);
        }
        activeAbilities.remove(id);
    }

    private void removeAbility(PassiveAbilityType passive, Ability ability) {
        passiveAbilities.computeIfPresent(
                ability.id(),
                (rl, pair) -> {
                    AbilityPassiveInstance<?> passiveInstance = pair.right();
                    int count = pair.leftInt() - 1;
                    passiveInstance.getAbility().onAbilityRemoved(passiveInstance, count, manager);
                    if(count > 0) {
                        return IntObjectPair.of(count, passiveInstance);
                    } else {
                        return null;
                    }
                }
        );
    }

    public void deactivateAbility(LastingAbilityType type) {
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
        this.onUpdate(true);
    }

    public void rebuildAttributes() {
        this.onUpdate(false);
    }


    private void onUpdate(boolean reactivateAbilities) {
        LivingEntity owner = this.manager.getEntity();
        if(!owner.getCommandSenderWorld().isClientSide) {
            for(var entry : this.attributes.object2ObjectEntrySet()) {
                AttributeInstance instance = owner.getAttribute(entry.getKey());
                if(instance == null) continue;
                for(ResourceLocation modifier : entry.getValue()) {
                    removeModifier(instance, modifier);
                }
            }
            if(reactivateAbilities) {
                this.reactivateAbilities();
            }
            this.rebuildAttributeInstances();
        }

    }

    private AttributeModifier getAttributeModifier(Pair<Holder<Attribute>, AttributeModifier> attributePair, int count, ResourceLocation instanceID) {
        AttributeModifier modifier = attributePair.right();
        if(count > 1) {
            // Rebuild modifier for added count
            modifier = new AttributeModifier(instanceID, modifier.amount() * count, modifier.operation());
        }
        return modifier;
    }

    private void rebuildAttribute(AttributeAbilityInstance abilityInstance, int count) {
        Optional<Pair<Holder<Attribute>, AttributeModifier>> opt = abilityInstance.getModifiedAttribute();
        if(opt.isPresent()) {
            Pair<Holder<Attribute>, AttributeModifier> attributePair = opt.get();
            AttributeInstance instance = this.manager.getEntity().getAttribute(attributePair.left());
            if(instance == null) return;
            AttributeModifier modifier = getAttributeModifier(attributePair, count, abilityInstance.getInstanceID());
            instance.addTransientModifier(modifier);
            this.attributes.compute(attributePair.left(), (key, list) -> {
                if (list == null) list = new ArrayList<>();
                list.add(modifier.id());
                return list;
            });
        }
    }

    private void rebuildAttributeInstances() {
        this.attributes.clear();
        for (IntObjectPair<AbilityPassiveInstance<?>> pairs : this.passiveAbilities.values()) {
            AbilityPassiveInstance<?> passive = pairs.right();
            if (passive instanceof AttributeAbilityInstance abilityInstance && passive.isEnabled()) {
                int count = pairs.leftInt();
                rebuildAttribute(abilityInstance, count);
            }
        }
    }


    private boolean sufficientAbilityPower(PowerManager power, AbilityPassiveInstance<?> instance) {
        //noinspection RedundantIfStatement
        if(power.getMaxPower() <= 1 && instance.hasPowerDraw()) {
            return false;
        }
        return true;
    }
    private boolean passiveIsEnabled(boolean insufficientPower, AbilityPassiveInstance<?> instance) {
        boolean shouldRebuild = false;
        if(insufficientPower && instance.isEnabled()) {
            shouldRebuild = true;
            instance.setEnabled(false);
        } else if(!insufficientPower && !instance.isEnabled()) {
            shouldRebuild = true;
            instance.setEnabled(true);
        }
        return shouldRebuild;

    }

    private boolean tickPassiveAbilities() {
        PowerManager power = this.manager.getPowerManager();
        boolean insufficientPower = false;
        boolean shouldRebuild = false;
        for (IntObjectPair<AbilityPassiveInstance<?>> pairs : this.passiveAbilities.values()) {
            AbilityPassiveInstance<?> instance = pairs.second();
            tickListener(instance, pairs.leftInt());

            if(!sufficientAbilityPower(power, instance)) {
                insufficientPower = true;
            }
            if(passiveIsEnabled(insufficientPower, instance)) {
                shouldRebuild = true;
            }
        }
        if(shouldRebuild) {
            if(insufficientPower) {
                warnForPower();
            }
        }
        this.insufficientPower = insufficientPower;
        return shouldRebuild;
    }

    private void warnForPower() {
        this.timeSinceWarning = WARNING_TIME;
        this.manager.getEntity().displayClientMessage(Component.translatable("synthetics.abilities.insufficient_augment_power").withStyle(ChatFormatting.RED), true);
    }
    private boolean tickWarningSecond() {
        this.timeSinceWarning = Math.max(0, timeSinceWarning - 1);
        return timeSinceWarning == 0;
    }

    private void tickListener(AbilityPassiveInstance<?> instance, int instanceCount) {
        if(instance.getAbility() instanceof IAbilityEventListener listener) {
            listener.onTick(instance, instanceCount, this.manager);
        }
    }

    private void tickCurrentlyActivePower() {
        PowerManager manager = this.manager.getPowerManager();
        for (Object2IntMap.Entry<ResourceLocation> entry : duration.object2IntEntrySet()) {
            AbilityActiveInstance<?> instance = this.activeAbilities.get(entry.getKey());
            drainActiveInstancePower(instance, manager);
        }
    }

    private void drainActiveInstancePower(AbilityActiveInstance<?> instance, PowerManager power) {
        int storedPower = power.getStoredPower();
        power.drainPower(instance.getPowerDrain() * 10);
        power.markDirty();
        if(instance.getPowerDrain() > storedPower) {
            this.duration.put(instance.getAbility().getAbilityID(), 1);
            Component.translatable("synthetics.abilities.insufficient_power").withStyle(ChatFormatting.RED);
        }
    }

    public void halfSecondTick() {
        tickCurrentlyActivePower();
        boolean shouldRebuild = tickPassiveAbilities();

        if(shouldRebuild) {
            this.dirty = true;
            this.onUpdate(false);
        }
        if(this.manager.getEntity().tickCount % 20 == 0 && tickWarningSecond()) {
            if(insufficientPower) {
                warnForPower();
            }
            timeSinceWarning = WARNING_TIME;
        }
    }


    private void tickCooldownTimer() {
        Iterator<Object2IntMap.Entry<ResourceLocation>> cooldownIterator = cooldown.object2IntEntrySet().iterator();
        while(cooldownIterator.hasNext()) {
            Object2IntMap.Entry<ResourceLocation> entry = cooldownIterator.next();
            int cooldownTime = entry.getIntValue();
            if (cooldownTime <= 1) {
                cooldownIterator.remove();
            } else {
                entry.setValue(cooldownTime - 1);
            }
        }
    }

    private boolean removeEntry(Object2IntMap.Entry<ResourceLocation> entry) {
        if(!this.activeAbilities.containsKey(entry.getKey())) {
            duration.removeInt(entry.getKey());
            return true;
        }
        return false;
    }

    private void tickDurationTimer() {
        for (Object2IntMap.Entry<ResourceLocation> entry : duration.object2IntEntrySet()) {
            int newTime = entry.getIntValue() - 1;

            if(removeEntry(entry)) {
                continue;
            }

            LastingAbilityType lasting = (LastingAbilityType) this.activeAbilities.get(entry.getKey()).getAbility();
            if (newTime == 0 || !this.activeAbilities.containsKey(entry.getKey())) {
                deactivateAbility(lasting);
            } else {
                if (lasting.onTick(manager, this.activeAbilities.get(entry.getKey()).factor())) {
                    entry.setValue(1);
                } else {
                    entry.setValue(newTime);
                }
            }
        }
    }

    public boolean onTick() {

        if(this.manager.getEntity().tickCount % 10 == 0) {
            this.halfSecondTick();
        }

        tickCooldownTimer();
        tickDurationTimer();

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
        for(IntObjectPair<AbilityPassiveInstance<?>> pairs : passiveAbilities.values()) {
            AbilityPassiveInstance<?> instance = pairs.right();
            if(instance instanceof ISaveData data) {
                passive.put(instance.getInstanceID().toString(), data.serialiseNBT(provider));
            }
        }
        for(AbilityActiveInstance<?> instance : activeAbilities.values()) {
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

        HolderLookup.RegistryLookup<Ability> lookup = provider.lookupOrThrow(SyntheticsData.ABILITIES);

        if(tag.contains("passives") && tag.get("passives") instanceof CompoundTag passives) {
            Set<String> keys = passives.getAllKeys();
            for(String key : keys) {
                Ability abilityInstance = Helper.retrieveDataObject(key, SyntheticsData.ABILITIES, lookup);
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
                AbilityType type = SyntheticAbilities.ABILITY_REGISTRY.get(id);
                if(type instanceof ActiveAbilityType && actives.get(key) instanceof CompoundTag activeTag &&  type instanceof ISaveData data) {
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
                AbilityActiveInstance<?> instance = this.activeAbilities.get(id);
                if(instance != null) {
                    LastingAbilityType lasting = (LastingAbilityType) instance.getAbility();
                    lasting.onRestoreActivate(manager, instance.factor());
                }

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
                    List<ResourceLocation> toDeactivate = new ArrayList<>();
                    for (Object2IntMap.Entry<ResourceLocation> active : duration.object2IntEntrySet()) {
                        String key = active.getKey().toString();
                        if (timers.contains(key)) {
                            active.setValue(timers.getInt(key));
                            timers.remove(key);
                        } else {
                            toDeactivate.add(active.getKey());
                        }
                    }
                    toDeactivate.forEach(id -> deactivateAbility((LastingAbilityType) activeAbilities.get(id).getAbility()));

                    for (String key : timers.getAllKeys()) {
                        ResourceLocation id = ResourceLocation.parse(key);
                        AbilityActiveInstance<?> instance = this.activeAbilities.get(id);
                        ActiveAbilityType ability = instance.getAbility();
                        if (ability == null) {
                            Synthetics.LOGGER.warn("Ability failed to load client side {}", id);
                        } else {
                            ability.activateClient(manager, instance.factor());
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
