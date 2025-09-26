package com.thedrofdoctoring.synthetics.core.data.tags;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.collections.BodyParts;
import com.thedrofdoctoring.synthetics.core.data.collections.BodySegments;
import com.thedrofdoctoring.synthetics.core.data.types.BodyPart;
import com.thedrofdoctoring.synthetics.core.data.types.BodySegment;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class SyntheticsTagProvider {

    public static void register(DataGenerator gen, GatherDataEvent event, PackOutput output, CompletableFuture<HolderLookup.Provider> future, ExistingFileHelper existingFileHelper) {
        gen.addProvider(event.includeServer(), new SyntheticBodyPartsTagProvider(output, future, existingFileHelper));
        gen.addProvider(event.includeServer(), new SyntheticBodySegmentsTagProvider(output, future, existingFileHelper));
    }

    public static class SyntheticBodyPartsTagProvider extends TagsProvider<BodyPart> {
        public SyntheticBodyPartsTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, SyntheticsData.BODY_PARTS, lookupProvider, Synthetics.MODID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.@NotNull Provider provider) {
            tag(BodyParts.EYES_MAIN).add(BodyParts.ORGANIC_EYES, BodyParts.CYBERNETIC_EYES);
            tag(BodyParts.FEET_MAIN).add(BodyParts.ORGANIC_FEET);
        }
    }
    public static class SyntheticBodySegmentsTagProvider extends TagsProvider<BodySegment> {
        public SyntheticBodySegmentsTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, SyntheticsData.BODY_SEGMENTS, lookupProvider, Synthetics.MODID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.@NotNull Provider provider) {
            tag(BodySegments.TORSO_MAIN).add(BodySegments.ORGANIC_TORSO);
            tag(BodySegments.ARMS_MAIN).add(BodySegments.ORGANIC_ARMS);
            tag(BodySegments.LOWER_BODY_MAIN).add(BodySegments.ORGANIC_LOWER_BODY);
            tag(BodySegments.HEAD_MAIN).add(BodySegments.ORGANIC_HEAD);

        }
    }
}
