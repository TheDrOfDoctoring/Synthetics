package com.thedrofdoctoring.synthetics.core.data.providers;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.core.SyntheticsBlocks;
import com.thedrofdoctoring.synthetics.core.SyntheticsItems;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.collections.Augments;
import com.thedrofdoctoring.synthetics.core.data.collections.BodyParts;
import com.thedrofdoctoring.synthetics.core.data.collections.BodySegments;
import com.thedrofdoctoring.synthetics.core.data.collections.tags.AugmentTags;
import com.thedrofdoctoring.synthetics.core.data.collections.tags.SyntheticsBlockTags;
import com.thedrofdoctoring.synthetics.core.data.collections.tags.SyntheticsEntityTags;
import com.thedrofdoctoring.synthetics.core.data.collections.tags.SyntheticsItemTags;
import com.thedrofdoctoring.synthetics.core.data.types.body.installables.Augment;
import com.thedrofdoctoring.synthetics.core.data.types.body.installables.BodyPart;
import com.thedrofdoctoring.synthetics.core.data.types.body.installables.BodySegment;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
        gen.addProvider(event.includeServer(), new SyntheticsAugmentTagProvider(output, future, existingFileHelper));
        gen.addProvider(event.includeServer(), new SyntheticBodySegmentsTagProvider(output, future, existingFileHelper));
        gen.addProvider(event.includeServer(), new SyntheticsEntityTypeTagProvider(output, future, existingFileHelper));
        gen.addProvider(event.includeServer(), blockTagProvider);
        gen.addProvider(event.includeServer(), new SyntheticsItemTagProvider(output, future, blockTagProvider.contentsGetter(), existingFileHelper));

    }

    public static class SyntheticsItemTagProvider extends ItemTagsProvider {


        public SyntheticsItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, lookupProvider, blockTags, Synthetics.MODID, existingFileHelper);
        }


        @Override
        protected void addTags(HolderLookup.@NotNull Provider provider) {
            tag(SyntheticsItemTags.IRON_GEARS).add(SyntheticsItems.IRON_GEAR.get());
            tag(SyntheticsItemTags.VALID_DRONE).add(Items.FLINT_AND_STEEL, Items.FIREWORK_ROCKET);
        }
    }

    public static class SyntheticsAugmentTagProvider extends TagsProvider<Augment> {

        protected SyntheticsAugmentTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, SyntheticsData.AUGMENTS, lookupProvider, Synthetics.MODID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.@NotNull Provider provider) {
            tag(AugmentTags.DAMPENERS).add(Augments.BASIC_INERTIAL_DAMPENERS, Augments.CYBERNETIC_INERTIAL_DAMPENERS, Augments.MECHANICAL_INERTIAL_DAMPENERS);
        }
    }

    public static class SyntheticsBlockTagProvider extends BlockTagsProvider {


        public SyntheticsBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @org.jetbrains.annotations.Nullable ExistingFileHelper existingFileHelper) {
            super(output, lookupProvider, Synthetics.MODID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.@NotNull Provider provider) {
            tag(BlockTags.MINEABLE_WITH_AXE).add(SyntheticsBlocks.RESEARCH_TABLE.get());
            tag(BlockTags.NEEDS_STONE_TOOL).add(SyntheticsBlocks.RESEARCH_TABLE.get());
            tag(BlockTags.MINEABLE_WITH_PICKAXE).add(SyntheticsBlocks.TELEPORTER_LINKABLE_BLOCK.get(), SyntheticsBlocks.AUGMENTATION_CHAMBER.get(), SyntheticsBlocks.SYNTHETIC_FORGE.get(), SyntheticsBlocks.REDSTONE_LINKABLE_BLOCK.get(), SyntheticsBlocks.CAMERA_LINKABLE_BLOCK.get());
            tag(BlockTags.NEEDS_IRON_TOOL).add(SyntheticsBlocks.TELEPORTER_LINKABLE_BLOCK.get(), SyntheticsBlocks.AUGMENTATION_CHAMBER.get(), SyntheticsBlocks.SYNTHETIC_FORGE.get(), SyntheticsBlocks.REDSTONE_LINKABLE_BLOCK.get(), SyntheticsBlocks.CAMERA_LINKABLE_BLOCK.get());
            tag(SyntheticsBlockTags.PERMEATOR_BLACKLIST).addTag(BlockTags.WITHER_IMMUNE).addTag(BlockTags.AIR).addTag(BlockTags.REPLACEABLE).add(SyntheticsBlocks.PERMEABLE_BLOCK.value(), SyntheticsBlocks.PERMEABLE_LINKABLE_BLOCK.get());
            tag(SyntheticsBlockTags.ORE_DOWSING).addTag(BlockTags.IRON_ORES).addTag(BlockTags.GOLD_ORES).addTag(BlockTags.DIAMOND_ORES).addTag(BlockTags.REDSTONE_ORES).add(Blocks.ANCIENT_DEBRIS);
        }
    }

    public static class SyntheticBodyPartsTagProvider extends TagsProvider<BodyPart> {
        public SyntheticBodyPartsTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, SyntheticsData.BODY_PARTS, lookupProvider, Synthetics.MODID, existingFileHelper);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void addTags(HolderLookup.@NotNull Provider provider) {
            tag(BodyParts.EYES_MAIN).add(BodyParts.ORGANIC_LEFT_EYE, BodyParts.ORGANIC_RIGHT_EYE, BodyParts.CYBERNETIC_LEFT_EYE, BodyParts.CYBERNETIC_RIGHT_EYE);
            tag(BodyParts.FEET_MAIN).add(BodyParts.ORGANIC_LEFT_FOOT, BodyParts.ORGANIC_RIGHT_FOOT, BodyParts.CYBERNETIC_LEFT_FOOT, BodyParts.CYBERNETIC_RIGHT_FOOT);
            tag(BodyParts.HEART_MAIN).add(BodyParts.ORGANIC_HEART, BodyParts.MECHANICAL_HEART, BodyParts.CYBERNETIC_HEART);
            tag(BodyParts.TISSUE_MAIN).add(BodyParts.ORGANIC_TISSUE, BodyParts.CYBERNETIC_TISSUE);
            tag(BodyParts.LUNGS_MAIN).add(BodyParts.ORGANIC_LUNGS);
            tag(BodyParts.NON_ORGANIC_HANDS).add(BodyParts.MECHANICAL_LEFT_HAND, BodyParts.MECHANICAL_RIGHT_HAND, BodyParts.CYBERNETIC_LEFT_HAND, BodyParts.CYBERNETIC_RIGHT_HAND);
            tag(BodyParts.HANDS_MAIN).add(BodyParts.ORGANIC_LEFT_HAND, BodyParts.ORGANIC_RIGHT_HAND, BodyParts.MECHANICAL_RIGHT_HAND, BodyParts.MECHANICAL_LEFT_HAND, BodyParts.CYBERNETIC_LEFT_HAND, BodyParts.CYBERNETIC_RIGHT_HAND);
            tag(BodyParts.TIBIA_MAIN).add(BodyParts.ORGANIC_TIBIA);
            tag(BodyParts.SKULL_MAIN).add(BodyParts.ORGANIC_SKULL);
            tag(BodyParts.RIBCAGE_MAIN).add(BodyParts.ORGANIC_RIBCAGE, BodyParts.ARMOURED_RIBCAGE, BodyParts.SYNTHETIC_RIBCAGE);
            tag(BodyParts.BRAINS_MAIN).add(BodyParts.ORGANIC_BRAIN, BodyParts.CYBERNETIC_BRAIN);
            tag(BodyParts.ARM_MUSCLE_MAIN).add(BodyParts.ORGANIC_ARM_MUSCLE);
            tag(BodyParts.STOMACH_MAIN).add(BodyParts.ORGANIC_STOMACH);


            tag(BodyParts.ALL_BONES).addTags(BodyParts.SKULL_MAIN, BodyParts.RIBCAGE_MAIN, BodyParts.TIBIA_MAIN);
            tag(BodyParts.ALL_MUSCLES).addTags(BodyParts.ARM_MUSCLE_MAIN);


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

    public static class SyntheticsEntityTypeTagProvider extends EntityTypeTagsProvider {
        public SyntheticsEntityTypeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
            super(output, lookupProvider, Synthetics.MODID, existingFileHelper);
        }

        public void addTags(HolderLookup.@NotNull Provider lookupProvider) {
            tag(SyntheticsEntityTags.COMMON_ENTITY_HIGHLIGHT_BLACKLIST).add(EntityType.ENDER_DRAGON);
        }
    }

}
