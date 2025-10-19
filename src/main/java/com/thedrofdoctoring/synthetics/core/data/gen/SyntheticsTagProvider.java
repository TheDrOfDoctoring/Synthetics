package com.thedrofdoctoring.synthetics.core.data.gen;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.core.SyntheticsBlocks;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.collections.BodyParts;
import com.thedrofdoctoring.synthetics.core.data.collections.BodySegments;
import com.thedrofdoctoring.synthetics.core.data.types.body.BodyPart;
import com.thedrofdoctoring.synthetics.core.data.types.body.BodySegment;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class SyntheticsTagProvider {

    public static void register(DataGenerator gen, GatherDataEvent event, PackOutput output, CompletableFuture<HolderLookup.Provider> future, ExistingFileHelper existingFileHelper) {
        SyntheticsBlockTagProvider blockTagProvider = new SyntheticsBlockTagProvider(output, future, existingFileHelper);
        gen.addProvider(event.includeServer(), new SyntheticBodyPartsTagProvider(output, future, existingFileHelper));
        gen.addProvider(event.includeServer(), new SyntheticBodySegmentsTagProvider(output, future, existingFileHelper));
        gen.addProvider(event.includeServer(), blockTagProvider);
    }
    public static class SyntheticsBlockTagProvider extends BlockTagsProvider {


        public SyntheticsBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @org.jetbrains.annotations.Nullable ExistingFileHelper existingFileHelper) {
            super(output, lookupProvider, Synthetics.MODID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.@NotNull Provider provider) {
            tag(BlockTags.MINEABLE_WITH_AXE).add(SyntheticsBlocks.RESEARCH_TABLE.get());
            tag(BlockTags.NEEDS_STONE_TOOL).add(SyntheticsBlocks.RESEARCH_TABLE.get());
            tag(BlockTags.MINEABLE_WITH_PICKAXE).add(SyntheticsBlocks.AUGMENTATION_CHAMBER.get(), SyntheticsBlocks.SYNTHETIC_FORGE.get());
            tag(BlockTags.NEEDS_IRON_TOOL).add(SyntheticsBlocks.AUGMENTATION_CHAMBER.get(), SyntheticsBlocks.SYNTHETIC_FORGE.get());
        }
    }

    public static class SyntheticBodyPartsTagProvider extends TagsProvider<BodyPart> {
        public SyntheticBodyPartsTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, SyntheticsData.BODY_PARTS, lookupProvider, Synthetics.MODID, existingFileHelper);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void addTags(HolderLookup.@NotNull Provider provider) {
            tag(BodyParts.EYES_MAIN).add(BodyParts.ORGANIC_EYES);
            tag(BodyParts.FEET_MAIN).add(BodyParts.ORGANIC_FEET, BodyParts.CYBERNETIC_FEET);
            tag(BodyParts.HEART_MAIN).add(BodyParts.ORGANIC_HEART);
            tag(BodyParts.TISSUE_MAIN).add(BodyParts.ORGANIC_TISSUE, BodyParts.CYBERNETIC_TISSUE);
            tag(BodyParts.LUNGS_MAIN).add(BodyParts.ORGANIC_LUNGS);
            tag(BodyParts.HANDS_MAIN).add(BodyParts.ORGANIC_HANDS, BodyParts.CYBERNETIC_HANDS);
            tag(BodyParts.TIBIA_MAIN).add(BodyParts.ORGANIC_TIBIA);
            tag(BodyParts.SKULL_MAIN).add(BodyParts.ORGANIC_SKULL);
            tag(BodyParts.RIBCAGE_MAIN).add(BodyParts.ORGANIC_RIBCAGE);
            tag(BodyParts.ALL_BONES).addTags(BodyParts.SKULL_MAIN, BodyParts.RIBCAGE_MAIN, BodyParts.TIBIA_MAIN);


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
