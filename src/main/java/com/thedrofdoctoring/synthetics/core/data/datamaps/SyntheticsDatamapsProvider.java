package com.thedrofdoctoring.synthetics.core.data.datamaps;

import com.thedrofdoctoring.synthetics.core.data.collections.BodyPartTypes;
import com.thedrofdoctoring.synthetics.core.data.types.body.parts.BodyPartType;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class SyntheticsDatamapsProvider extends DataMapProvider {

    public static void register(DataGenerator gen, GatherDataEvent event, CompletableFuture<HolderLookup.Provider> future, PackOutput output) {
        gen.addProvider(event.includeServer(), new SyntheticsDatamapsProvider(output, future));
    }


    public SyntheticsDatamapsProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pRegistries) {
        super(pOutput, pRegistries);
    }

    @Override
    protected void gather(HolderLookup.@NotNull Provider provider) {
        gatherPartTypeScales(builder(SyntheticsDatamaps.PART_TYPE_DISPLAY_SCALE));
    }
    protected void gatherPartTypeScales(Builder<Double, BodyPartType> partTypeBuilder) {

        partTypeBuilder.add(BodyPartTypes.LEFT_EYE, 0.75d, false);
        partTypeBuilder.add(BodyPartTypes.RIGHT_EYE, 0.75d, false);

    }
}
