package com.thedrofdoctoring.synthetics.capabilities;

import com.thedrofdoctoring.synthetics.capabilities.serialisation.ISyncable;
import com.thedrofdoctoring.synthetics.core.data.types.body.*;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
@SuppressWarnings("unused")
public class ComplexityManager implements ISyncable {

    private static final String KEY = "complexity_manager";

    private final SyntheticsPlayer player;

    private final Object2IntMap<ResourceLocation> totalBodyPartComplexity;
    private final Object2IntMap<ResourceLocation> totalBodySegmentComplexity;

    private int totalComplexity;

    public ComplexityManager(SyntheticsPlayer player) {
        this.totalBodyPartComplexity = new Object2IntArrayMap<>();
        this.totalBodySegmentComplexity = new Object2IntArrayMap<>(5);
        this.player = player;
    }
    public void addPart(AugmentInstance instance) {
        int addedComplexity = instance.augment().complexity();
        addComplexity(addedComplexity, instance.appliedPart());

    }

    public void removePart(AugmentInstance instance) {
        int addedComplexity = -instance.augment().complexity();
        addComplexity(addedComplexity, instance.appliedPart());
    }

    public void addComplexity(int complexity, BodyPart part) {
        this.totalComplexity = Math.max(0, complexity + totalComplexity);

        int currentPartComplexity = totalBodyPartComplexity.getInt(part.id());
        totalBodyPartComplexity.put(part.type().value().id(), Math.max(0, currentPartComplexity + complexity));

        ResourceLocation segmentID = this.player.getPartManager().getSegmentForPart(part).type().value().id();
        int currentSegmentComplexity = totalBodyPartComplexity.getInt(segmentID);
        totalBodySegmentComplexity.put(segmentID, Math.max(0, currentSegmentComplexity + complexity));
    }
    public ComplexityResult testComplexity(AugmentInstance newInstance, @Nullable AugmentInstance removed) {
        int addedComplexity = newInstance.augment().complexity();

        if(removed != null) {
            addedComplexity = addedComplexity - removed.augment().complexity();
        }
        boolean failedPartCheck = false;
        int currentPartComplexity = totalBodyPartComplexity.getInt(newInstance.appliedPart().type().value().id());
        BodySegment segment = this.player.getPartManager().getSegmentForPart(newInstance.appliedPart());
        int currentSegmentComplexity = totalBodySegmentComplexity.getInt(segment.type().value().id());
        if(currentPartComplexity + addedComplexity > newInstance.appliedPart().maxComplexity()) {
            failedPartCheck = true;
        }
        if(currentSegmentComplexity + addedComplexity > segment.maxComplexity()) {
            if(failedPartCheck) {
                return ComplexityResult.FAIL_BOTH;
            }
            return ComplexityResult.FAIL_SEGMENT;
        } else if(failedPartCheck) {
            return ComplexityResult.FAIL_BODY_PART;
        }

        return ComplexityResult.SUCCESS;
    }
    public ComplexityPairs getNewComplexity(AugmentInstance newInstance, @Nullable AugmentInstance removed) {
        int addedComplexity = newInstance.augment().complexity();

        if(removed != null) {
            addedComplexity = addedComplexity - removed.augment().complexity();
        }
        boolean failedPartCheck = false;
        int newPartComplexity = totalBodyPartComplexity.getInt(newInstance.appliedPart().type().value().id()) + addedComplexity;
        BodySegment segment = this.player.getPartManager().getSegmentForPart(newInstance.appliedPart());
        int newSegmentComplexity = totalBodySegmentComplexity.getInt(segment.type().value().id()) + addedComplexity;
        return new ComplexityPairs(newInstance.appliedPart(), segment, newPartComplexity, newSegmentComplexity);
    }


    public int getTotalComplexity() {
        return this.totalComplexity;
    }

    public int getTotalPartComplexity(BodyPart part) {
        return getTotalPartComplexity(part.type().value().id());
    }
    public int getTotalSegmentComplexity(BodySegment segment) {
        return getTotalSegmentComplexity(segment.type().value().id());
    }
    public int getTotalPartComplexity(BodyPartType part) {
        return getTotalPartComplexity(part.id());
    }
    public int getTotalSegmentComplexity(BodySegmentType segment) {
        return getTotalSegmentComplexity(segment.id());
    }


    public int getTotalPartComplexity(ResourceLocation part) {
        if(totalBodyPartComplexity.containsKey(part)) {
            return totalBodyPartComplexity.getInt(part);
        }
        return 0;
    }
    public int getTotalSegmentComplexity(ResourceLocation segment) {
        if (totalBodySegmentComplexity.containsKey(segment)) {
            return totalBodySegmentComplexity.getInt(segment);
        }
        return 0;
    }

    @Override
    public CompoundTag serialiseNBT(HolderLookup.@NotNull Provider provider) {
        return new CompoundTag();
    }

    @Override
    public void deserialiseNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag parentNBT) {
        this.totalBodySegmentComplexity.clear();
        this.totalBodyPartComplexity.clear();
        this.totalComplexity = 0;
        List<AugmentInstance> augments = this.player.getInstalledAugments();
        for(AugmentInstance instance : augments) {
            addPart(instance);
        }
    }

    @Override
    public String nbtKey() {
        return KEY;
    }

    @Override
    public CompoundTag serialiseUpdateNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("total_complexity", totalComplexity);
        List<AugmentInstance> augments = this.player.getInstalledAugments();
        CompoundTag segments = new CompoundTag();
        for(AugmentInstance instance : augments) {
            BodyPart part = instance.appliedPart();
            BodySegment segment = this.player.getPartManager().getSegmentForPart(part);
            segments.putInt(segment.type().value().id().toString(), totalBodySegmentComplexity.getOrDefault(segment.type().value().id(), 0));
        }
        tag.put("segment_complexity", segments);
        return tag;
    }

    @Override
    public void deserialiseUpdateNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag parentNBT) {
        if(parentNBT.contains(nbtKey()) && parentNBT.get(nbtKey()) instanceof CompoundTag tag) {
            this.totalComplexity = tag.getInt("total_complexity");
            Tag segments = tag.get("segment_complexity");
            if(segments instanceof CompoundTag nbt) {
                Set<String> keys = nbt.getAllKeys();
                for(String key : keys) {
                    ResourceLocation id = ResourceLocation.tryParse(key);
                    this.totalBodySegmentComplexity.put(id, nbt.getInt(key));
                }
            }

        }

    }
    public enum ComplexityResult {
        FAIL_BODY_PART,
        FAIL_SEGMENT,
        FAIL_BOTH,
        SUCCESS
    }

    public record ComplexityPairs(BodyPart part, BodySegment segment, int partComplexity, int segmentComplexity) {}
}
