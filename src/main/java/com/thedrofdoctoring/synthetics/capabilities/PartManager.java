package com.thedrofdoctoring.synthetics.capabilities;

import com.thedrofdoctoring.synthetics.capabilities.serialisation.ISaveData;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.types.*;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class PartManager implements ISaveData {
    public static final String KEY = "part_manager";
    private final Object2ObjectMap<BodyPartType, BodyPart> installedParts;
    private final Object2ObjectMap<BodySegmentType, BodySegment> installedSegments;

    private final SyntheticsPlayer player;

    public PartManager(SyntheticsPlayer player) {
        this.installedParts = new Object2ObjectOpenHashMap<>();
        this.installedSegments = new Object2ObjectOpenHashMap<>();

        this.player = player;
        setDefaultParts();
        setDefaultSegments();
    }

    public Collection<BodyPart> getInstalledParts() {
        return installedParts.values();
    }
    public Collection<BodySegment> getInstalledSegments() {
        return installedSegments.values();
    }

    public BodyPart replacePart(BodyPart newPart, boolean updatePlayer) {
        BodyPart old = installedParts.put(newPart.type().value(), newPart);
        if(updatePlayer && old != null) {
            List<AugmentInstance> instancesOfPart = this.player.getInstalledAugments().stream().filter(p -> p.appliedPart().type() == old.type()).toList();
            for(AugmentInstance instance : instancesOfPart) {
                this.player.replaceAugmentInstance(instance, new AugmentInstance(instance.augment(), newPart));
            }
            this.player.markDirty();

        }
        return old;
    }
    public BodyPart replacePart(BodyPart newPart) {
        return replacePart(newPart, true);
    }
    public BodySegment replaceSegment(BodySegment newSegment) {
        return replaceSegment(newSegment, true);
    }

    public BodySegment replaceSegment(BodySegment newSegment, boolean updatePlayer) {
        BodySegment old = installedSegments.put(newSegment.type().value(), newSegment);
        return old;

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
        HolderSet<BodySegment> validSegments = part.segment();
        BodySegmentType type = validSegments.get(0).value().type().value();
        return getSegmentForType(type);
    }

    public BodySegment getSegmentForType(BodySegmentType type) {
        if(installedSegments.containsKey(type)) {
            return installedSegments.get(type);
        }
        return getDefaultSegment(type);
    }

    public BodyPart getPartForAugment(@NotNull SyntheticAugment augment) {
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

    @Override
    public CompoundTag serialiseNBT(HolderLookup.@NotNull Provider provider) {

        CompoundTag base = new CompoundTag();

        CompoundTag partsTag = new CompoundTag();
        CompoundTag segmentsTag = new CompoundTag();

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
                    String partIDString = tag.getString(String.valueOf(i));
                    BodyPart part = Helper.retrieveDataObject(partIDString, SyntheticsData.BODY_PARTS, partLookup);
                    if(part != null) {
                        replacePart(part, false);
                    }
                }
            }
            if(segments instanceof CompoundTag nbt) {
                int size = nbt.size();
                for(int i = 0; i < size; i++) {
                    String partIDString = tag.getString(String.valueOf(i));
                    BodySegment segment = Helper.retrieveDataObject(partIDString, SyntheticsData.BODY_SEGMENTS, segmentLookup);
                    if(segment != null) {
                        replaceSegment(segment, false);
                    }
                }
            }
        }

    }

    @Override
    public String nbtKey() {
        return KEY;
    }
}
