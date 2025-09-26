package com.thedrofdoctoring.synthetics.core.data;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.core.data.providers.SyntheticsAbilitiesProvider;
import com.thedrofdoctoring.synthetics.core.data.providers.SyntheticsAugmentsProvider;
import com.thedrofdoctoring.synthetics.core.data.providers.SyntheticsBodyPartsProvider;
import com.thedrofdoctoring.synthetics.core.data.providers.SyntheticsBodySegmentsProvider;
import com.thedrofdoctoring.synthetics.core.data.tags.SyntheticsTagProvider;
import com.thedrofdoctoring.synthetics.core.data.types.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

import java.util.Set;
import java.util.concurrent.CompletableFuture;


public class SyntheticsData {

    public static final ResourceKey<Registry<BodySegment>> BODY_SEGMENTS = ResourceKey.createRegistryKey(Synthetics.rl("body_segments"));
    public static final ResourceKey<Registry<BodySegmentType>> BODY_SEGMENT_TYPES = ResourceKey.createRegistryKey(Synthetics.rl("body_segment_types"));

    public static final ResourceKey<Registry<BodyPart>> BODY_PARTS = ResourceKey.createRegistryKey(Synthetics.rl("body_parts"));
    public static final ResourceKey<Registry<BodyPartType>> BODY_PART_TYPES = ResourceKey.createRegistryKey(Synthetics.rl("body_part_types"));

    public static final ResourceKey<Registry<SyntheticAugment>> AUGMENTS = ResourceKey.createRegistryKey(Synthetics.rl("augments"));
    public static final ResourceKey<Registry<SyntheticAbility>> ABILITIES = ResourceKey.createRegistryKey(Synthetics.rl("abilities"));



    public static final RegistrySetBuilder DATA_BUILDER = new RegistrySetBuilder()
            .add(BODY_SEGMENTS, SyntheticsBodySegmentsProvider::createBodySegments)
            .add(BODY_SEGMENT_TYPES, SyntheticsBodySegmentsProvider::createBodySegmentTypes)
            .add(BODY_PART_TYPES, SyntheticsBodyPartsProvider::createBodyPartTypes)
            .add(BODY_PARTS, SyntheticsBodyPartsProvider::createBodyParts)
            .add(AUGMENTS, SyntheticsAugmentsProvider::createAugments)
            .add(ABILITIES, SyntheticsAbilitiesProvider::createAbilities);


    public void registerDatapackRegistries(final DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(BODY_SEGMENT_TYPES, BodySegmentType.CODEC.codec(), BodySegmentType.CODEC.codec());
        event.dataPackRegistry(BODY_SEGMENTS, BodySegment.CODEC.codec(), BodySegment.CODEC.codec());
        event.dataPackRegistry(BODY_PART_TYPES, BodyPartType.CODEC.codec(), BodyPartType.CODEC.codec());
        event.dataPackRegistry(BODY_PARTS, BodyPart.CODEC.codec(), BodyPart.CODEC.codec());
        event.dataPackRegistry(AUGMENTS, SyntheticAugment.CODEC.codec(), SyntheticAugment.CODEC.codec());
        event.dataPackRegistry(ABILITIES, SyntheticAbility.CODEC.codec(), SyntheticAbility.CODEC.codec());
    }

    public void gatherData(final GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        DatapackBuiltinEntriesProvider provider = new DatapackBuiltinEntriesProvider(packOutput, lookupProvider, DATA_BUILDER, Set.of(Synthetics.MODID));
        generator.addProvider(event.includeServer(), provider);
        lookupProvider = provider.getRegistryProvider();
        SyntheticsTagProvider.register(generator, event, packOutput, lookupProvider, existingFileHelper);

    }

    public static void register(IEventBus bus) {
        SyntheticsData dataRegistry = new SyntheticsData();
        bus.addListener(dataRegistry::registerDatapackRegistries);
        bus.addListener(dataRegistry::gatherData);
    }
}
