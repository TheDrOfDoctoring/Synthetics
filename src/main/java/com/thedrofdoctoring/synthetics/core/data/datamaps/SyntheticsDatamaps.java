package com.thedrofdoctoring.synthetics.core.data.datamaps;

import com.mojang.serialization.Codec;
import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.types.body.parts.BodyPartType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

public class SyntheticsDatamaps {

    private SyntheticsDatamaps() {}

    public static final DataMapType<BodyPartType, Double> PART_TYPE_DISPLAY_SCALE = DataMapType.builder(
            Synthetics.rl("part_type_display_scale"),
            SyntheticsData.BODY_PART_TYPES,
            Codec.DOUBLE
    ).synced(Codec.DOUBLE, true).build();

    public static void register(IEventBus bus) {
        SyntheticsDatamaps dataMaps = new SyntheticsDatamaps();
        bus.addListener(dataMaps::registerDataMapTypes);
    }

    public void registerDataMapTypes(final RegisterDataMapTypesEvent event) {
        event.register(SyntheticsDatamaps.PART_TYPE_DISPLAY_SCALE);
    }
}
