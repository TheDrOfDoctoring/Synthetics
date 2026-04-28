package com.thedrofdoctoring.synthetics.capabilities;

import com.mojang.datafixers.util.Pair;
import com.thedrofdoctoring.synthetics.capabilities.interfaces.IPartManager;
import com.thedrofdoctoring.synthetics.capabilities.serialisation.ISaveData;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.types.body.installables.*;
import com.thedrofdoctoring.synthetics.core.data.types.body.parts.BodyPartType;
import com.thedrofdoctoring.synthetics.core.data.types.body.parts.BodySegmentType;
import com.thedrofdoctoring.synthetics.util.Helper;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PartManager implements ISaveData, IPartManager {
    public static final String KEY = "part_manager";

    private static final Object2ObjectMap<String, ResourceLocation> partFixerUpper = new Object2ObjectOpenHashMap<>();

    private final Object2ObjectMap<BodyPartType, BodyPart> installedParts;
    private final Object2ObjectMap<BodySegmentType, BodySegment> installedSegments;
    private final List<AppliedAugmentInstance> appliedAugments;

    private final SyntheticsPlayer player;


    public PartManager(SyntheticsPlayer player) {
        this.installedParts = new Object2ObjectOpenHashMap<>();
        this.installedSegments = new Object2ObjectOpenHashMap<>();
        this.appliedAugments = new ArrayList<>();

        this.player = player;
        setDefaultParts();
        setDefaultSegments();
    }

    public Collection<BodyPart> installedBodyParts() {
        return installedParts.values();
    }
    public Collection<BodySegment> installedSegments() {
        return installedSegments.values();
    }
    public Collection<AppliedAugmentInstance> installedAugments() {
        return List.copyOf(appliedAugments);
    }

    public boolean isBodyPartInstalled(BodyPart part) {
        return installedParts.getOrDefault(part.type().value(), null).equals(part);

    }
    public boolean isSegmentInstalled(BodySegment segment) {
        return installedSegments.getOrDefault(segment.type().value(), null).equals(segment);
    }

    @Override
    public boolean isAugmentInstalled(AppliedAugmentInstance instance) {
        return this.appliedAugments
                .stream()
                .anyMatch(augmentInstance -> augmentInstance.augment().equals(instance.augment()));
    }

    public boolean augmentSupportsBodyPart(Augment augment, BodyPart part) {
        return augment.validParts()
                .stream()
                .anyMatch(p -> p.value().equals(part));
    }

    private void replaceAugmentInstance(AppliedAugmentInstance old, AppliedAugmentInstance newInstance) {
        this.removeAugment(old);
        this.player.getComplexityManager().removePart(old);
        AbilityManager abilityManager = this.player.getAbilityManager();
        if(old.augment() != newInstance.augment()) {
            abilityManager.removeAbilities(old.augment());
            abilityManager.removeAbilities(old.appliedPart());
            abilityManager.addAbilities(newInstance.augment());
            abilityManager.addAbilities(newInstance.appliedPart());
        }
        this.addAugment(newInstance);

        int totalPowerCost = Math.max(0, this.player.getPowerManager().getTotalPowerCost() - old.augment().powerCost() + newInstance.augment().powerCost());
        this.player.getPowerManager().setTotalPowerCost(totalPowerCost);
    }

    public List<IBodyInstallable<?>> replaceBodyPart(BodyPart newPart, boolean updatePlayer) {
        List<IBodyInstallable<?>> removedInstallables = new ArrayList<>();
        BodyPart old = installedParts.put(newPart.type().value(), newPart);

        if(old != null) {
            this.player.getAbilityManager().removeAbilities(old);
            List<AppliedAugmentInstance> instancesOfPart = this.installedAugments().stream().filter(p -> p.appliedPart().type().equals(old.type())).toList();
            for(AppliedAugmentInstance instance : instancesOfPart) {
                if(augmentSupportsBodyPart(instance.augment(), newPart)) {
                    this.replaceAugmentInstance(instance, new AppliedAugmentInstance(instance.augment(), newPart));
                } else {
                    this.player.removeInstallable(instance);
                    removedInstallables.add(instance.augment());
                }
            }
            if(updatePlayer) {
                this.player.markDirtyAll();
            }
        }
        this.player.getAbilityManager().addAbilities(newPart);
        removedInstallables.add(old);
        return removedInstallables;
    }
    public List<IBodyInstallable<?>> replaceBodyPart(BodyPart newPart) {
        return replaceBodyPart(newPart, true);
    }
    public List<IBodyInstallable<?>> replaceSegment(BodySegment newSegment) {
        return replaceSegment(newSegment, true);
    }

    public List<IBodyInstallable<?>> replaceSegment(BodySegment newSegment, boolean updatePlayer) {

        BodySegment segment = installedSegments.put(newSegment.type().value(), newSegment);
        if(updatePlayer) {
            this.player.markDirtyAll();
        }
        return segment == null ? Collections.emptyList() : List.of(segment);

    }

    @Override
    public void removeAugment(AppliedAugmentInstance augment) {
        this.appliedAugments.remove(augment);
    }


    @Override
    public void addAugment(@NotNull AppliedAugmentInstance instance) {
        this.appliedAugments.add(instance);
        this.player.getComplexityManager().addPart(instance);
        this.player.getAbilityManager().addAbilities(instance.augment());
        this.player.getPowerManager().setTotalPowerCost(this.player.getPowerManager().getTotalPowerCost() + instance.augment().powerCost());
    }

    public void addAugment(@NotNull AppliedAugmentInstance augment, boolean sync) {
        addAugment(augment);
        if(sync) {
            this.player.markDirtyAll();
        }
    }

    private void setDefaultParts() {
        Optional<HolderLookup.RegistryLookup<BodyPartType>> partsOpt = player.getEntity().registryAccess().lookup(SyntheticsData.BODY_PART_TYPES);
        Optional<HolderLookup.RegistryLookup<BodyPart>> partOpt = player.getEntity().registryAccess().lookup(SyntheticsData.BODY_PARTS);

        if(partsOpt.isPresent() && partOpt.isPresent()) {
            HolderLookup.RegistryLookup<BodyPartType> typeLookup = partsOpt.get();
            HolderLookup.RegistryLookup<BodyPart> partLookup = partOpt.get();

            typeLookup.listElementIds().forEach(p -> {
                BodyPartType type = typeLookup.get(p).orElseThrow().value();
                Optional<Holder.Reference<BodyPart>> optPart = partOpt.orElseThrow().get(type.defaultPart());
                if(optPart.isPresent()) {
                    this.installedParts.put(type, partLookup.get(type.defaultPart()).orElseThrow().value());
                }
            });
        }
    }
    private void setDefaultSegments() {
        Optional<HolderLookup.RegistryLookup<BodySegmentType>> segmentTypesOpt = player.getEntity().registryAccess().lookup(SyntheticsData.BODY_SEGMENT_TYPES);
        Optional<HolderLookup.RegistryLookup<BodySegment>> segmentOpt = player.getEntity().registryAccess().lookup(SyntheticsData.BODY_SEGMENTS);

        if(segmentTypesOpt.isPresent() && segmentOpt.isPresent()) {
            HolderLookup.RegistryLookup<BodySegmentType> typeLookup = segmentTypesOpt.get();
            HolderLookup.RegistryLookup<BodySegment> partLookup = segmentOpt.get();

            typeLookup.listElementIds().forEach(p -> {
                BodySegmentType type = typeLookup.get(p).orElseThrow().value();
                Optional<Holder.Reference<BodySegment>> optSegment = segmentOpt.orElseThrow().get(type.defaultSegment());
                if(optSegment.isPresent()) {
                    this.installedSegments.put(type, partLookup.get(type.defaultSegment()).orElseThrow().value());
                }
            });
        }
    }
    public @Nullable BodyPart getDefaultPart(BodyPartType type) {
        Optional<HolderLookup.RegistryLookup<BodyPart>> partOpt = player.getEntity().registryAccess().lookup(SyntheticsData.BODY_PARTS);
        Optional<Holder.Reference<BodyPart>> optPart = partOpt.orElseThrow().get(type.defaultPart());
        return optPart.map(Holder.Reference::value).orElse(null);
    }
    public @Nullable BodySegment getDefaultSegment(BodySegmentType type) {
        Optional<HolderLookup.RegistryLookup<BodySegment>> partOpt = player.getEntity().registryAccess().lookup(SyntheticsData.BODY_SEGMENTS);
        Optional<Holder.Reference<BodySegment>> optSegment = partOpt.orElseThrow().get(type.defaultSegment());
        return optSegment.map(Holder.Reference::value).orElse(null);
    }

    public BodySegment getSegmentForPart(BodyPart part) {
        HolderSet<BodySegment> validSegments = part.validSegments();
        BodySegmentType type = validSegments.get(0).value().type().value();
        return getSegmentForType(type);
    }

    public BodySegment getSegmentForType(BodySegmentType type) {
        if(installedSegments.containsKey(type)) {
            return installedSegments.get(type);
        }
        return getDefaultSegment(type);
    }

    public BodyPart getDefaultPartForAugment(@NotNull Augment augment) {
        HolderSet<BodyPart> validParts = augment.validParts();
        BodyPartType type = validParts.get(0).value().type().value();
        return getPartForType(type);
    }
    public BodyPart getPartForType(BodyPartType type) {
        if(installedParts.containsKey(type)) {
            return installedParts.get(type);
        }
        return getDefaultPart(type);
    }

    boolean canAddAugment(AppliedAugmentInstance instance, SyntheticsPlayer syntheticsPlayer) {

        if(syntheticsPlayer.getComplexityManager().testComplexity(instance, null) != ComplexityManager.ComplexityResult.SUCCESS) {
            return false;
        }
        if(!augmentSupportsBodyPart(instance.augment(), instance.appliedPart())) {
            return false;
        }
        int onPart = 1;
        int total = 1;
        List<Augment> groupedWith = instance.augment().groupedWithList();

        for(AppliedAugmentInstance installedInstances : installedAugments()) {

            if(installedInstances.augment().equals(instance.augment()) || groupedWith.contains(installedInstances.augment())) {
                total++;
                if(installedInstances.appliedPart().equals(instance.appliedPart())) {
                    onPart++;
                }

            }
            if(total > instance.augment().maxTotal() || onPart > instance.augment().maxPerPart()) {
                return false;
            }
        }

        return true;
    }

    public static boolean isDefault(IBodyInstallable<?> installable) {
        switch (installable) {
            case BodyPart part -> {
                return part.type().value().defaultPart().location().equals(part.id());
            }
            case BodySegment segment -> {
                return segment.type().value().defaultSegment().location().equals(segment.id());
            }
            default -> {
                return false;
            }

        }
    }

    // ---------------------------------
    //          Serialisation
    // ---------------------------------



    @Override
    public CompoundTag serialiseNBT(HolderLookup.@NotNull Provider provider) {

        CompoundTag base = new CompoundTag();

        CompoundTag partsTag = new CompoundTag();
        CompoundTag segmentsTag = new CompoundTag();
        base.put("augments", serialiseAugments(provider));

        ObjectCollection<BodyPart> parts = installedParts.values();
        ObjectCollection<BodySegment> segments = installedSegments.values();

        int i = 0;
        for(BodyPart part : parts) {
            partsTag.putString(String.valueOf(i), part.id().toString());
            i++;
        }
        i = 0;
        for(BodySegment segment : segments) {
            segmentsTag.putString(String.valueOf(i), segment.id().toString());
            i++;
        }

        base.put("parts", partsTag);
        base.put("segments", segmentsTag);
        return base;
    }

    @Override
    public void deserialiseNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag parentNBT) {
        if(parentNBT.contains(nbtKey()) && parentNBT.get(nbtKey()) instanceof CompoundTag tag) {
            HolderGetter<BodyPart> partLookup = provider.lookupOrThrow(SyntheticsData.BODY_PARTS);
            HolderGetter<BodySegment> segmentLookup = provider.lookupOrThrow(SyntheticsData.BODY_SEGMENTS);

            Tag parts = tag.get("parts");
            Tag segments = tag.get("segments");
            if(parts instanceof CompoundTag nbt) {
                int size = nbt.size();
                for(int i = 0; i < size; i++) {
                    String partIDString = nbt.getString(String.valueOf(i));
                    ResourceLocation id = getPartReplacement(partIDString);

                    BodyPart part = Helper.retrieveDataObject(id, SyntheticsData.BODY_PARTS, partLookup);
                    if(part != null) {
                        replaceBodyPart(part, false);
                    }
                }
            }
            if(segments instanceof CompoundTag nbt) {
                int size = nbt.size();
                for(int i = 0; i < size; i++) {
                    String partIDString = nbt.getString(String.valueOf(i));
                    BodySegment segment = Helper.retrieveDataObject(partIDString, SyntheticsData.BODY_SEGMENTS, segmentLookup);
                    if(segment != null) {
                        replaceSegment(segment, false);
                    }
                }
            }
            deserialiseAugments(provider, tag);
        }

    }

    private CompoundTag serialiseAugments(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        int size = appliedAugments.size();
        if(size == 0) return tag;

        for(int i = 0; i < appliedAugments.size(); i++) {
            tag.putString(String.valueOf(i), appliedAugments.get(i).createSerialisationID());
        }
        return tag;
    }

    private void deserialiseAugments(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
        HolderGetter<Augment> lookup = provider.lookupOrThrow(SyntheticsData.AUGMENTS);
        HolderGetter<BodyPart> partLookup = provider.lookupOrThrow(SyntheticsData.BODY_PARTS);

        this.appliedAugments.clear();

        if(nbt.contains("augments") && nbt.get("augments") instanceof CompoundTag tag && !tag.isEmpty()) {
            int size = tag.size();
            for(int i = 0; i < size; i++) {
                String instanceIDString = tag.getString(String.valueOf(i));

                Pair<ResourceLocation, ResourceLocation> augmentInstance = AppliedAugmentInstance.augmentPartSplitIdentifiers(instanceIDString);

                Augment augment = Helper.retrieveDataObject(augmentInstance.getFirst(), SyntheticsData.AUGMENTS, lookup);
                BodyPart part = Helper.retrieveDataObject(augmentInstance.getSecond(), SyntheticsData.BODY_PARTS, partLookup);
                if(augment != null) {
                    if(part == null || !isBodyPartInstalled(part)) {
                        this.addAugment(new AppliedAugmentInstance(augment, this.getDefaultPartForAugment(augment)));
                        continue;
                    }
                    this.addAugment(new AppliedAugmentInstance(augment, part));
                }
            }
        }
    }


    public static void putPartReplacement(String oldPartName, ResourceLocation newLocation) {
        partFixerUpper.put(oldPartName, newLocation);
    }

    public static @Nullable ResourceLocation getPartReplacement(String oldPartName) {
        return partFixerUpper.getOrDefault(oldPartName, ResourceLocation.tryParse(oldPartName));
    }

    public static boolean hasPartReplacement(String oldPartName) {
        return partFixerUpper.containsKey(oldPartName);
    }

    @Override
    public String nbtKey() {
        return KEY;
    }


}
