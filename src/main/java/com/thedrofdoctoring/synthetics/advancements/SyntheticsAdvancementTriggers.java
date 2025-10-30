package com.thedrofdoctoring.synthetics.advancements;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.advancements.criterion.*;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SyntheticsAdvancementTriggers {

    private static final DeferredRegister<CriterionTrigger<?>> TRIGGERS = DeferredRegister.create(Registries.TRIGGER_TYPE, Synthetics.MODID);

    public static final DeferredHolder<CriterionTrigger<?>, ResearchCriterion> RESEARCH_UNLOCKED = TRIGGERS.register("research_unlocked", ResearchCriterion::new);
    public static final DeferredHolder<CriterionTrigger<?>, BodySegmentInstalledCriterion> SEGMENT_INSTALLED = TRIGGERS.register("segment_installed", BodySegmentInstalledCriterion::new);
    public static final DeferredHolder<CriterionTrigger<?>, BodyPartInstalledCriterion> PART_INSTALLED = TRIGGERS.register("body_part_installed", BodyPartInstalledCriterion::new);
    public static final DeferredHolder<CriterionTrigger<?>, AugmentInstalledCriterion> AUGMENT_INSTALLED = TRIGGERS.register("augment_installed", AugmentInstalledCriterion::new);
    public static final DeferredHolder<CriterionTrigger<?>, GenericSyntheticsCriterion> GENERIC = TRIGGERS.register("generic_criteria", GenericSyntheticsCriterion::new);


    public static void register(IEventBus bus) {
        TRIGGERS.register(bus);
    }
}
