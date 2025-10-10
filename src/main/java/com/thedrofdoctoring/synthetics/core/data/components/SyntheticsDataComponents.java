package com.thedrofdoctoring.synthetics.core.data.components;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.types.body.BodyPart;
import com.thedrofdoctoring.synthetics.core.data.types.body.SyntheticAugment;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SyntheticsDataComponents {

    public static void register(IEventBus bus) {
        DATA_COMPONENTS.register(bus);
    }

    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Synthetics.MODID);


    public static final Supplier<DataComponentType<Holder<SyntheticAugment>>> AUGMENT = DATA_COMPONENTS.registerComponentType(
            SyntheticsData.AUGMENTS.location().getPath(),
            builder -> builder
                    .persistent(SyntheticAugment.HOLDER_CODEC)
                    .networkSynchronized(ByteBufCodecs.holder(SyntheticsData.AUGMENTS, SyntheticAugment.STREAM_CODEC))
    );
    public static final Supplier<DataComponentType<Holder<BodyPart>>> BODY_PART = DATA_COMPONENTS.registerComponentType(
            SyntheticsData.BODY_PARTS.location().getPath(),
            builder -> builder
                    .persistent(BodyPart.HOLDER_CODEC)
                    .networkSynchronized(ByteBufCodecs.holder(SyntheticsData.BODY_PARTS, BodyPart.STREAM_CODEC))
    );
}
