package com.thedrofdoctoring.synthetics.advancements.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.advancements.SyntheticsAdvancementTriggers;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.types.body.parts.BodySegment;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

public class BodySegmentInstalledCriterion extends SimpleCriterionTrigger<BodySegmentInstalledCriterion.TriggerInstance> {

    public void trigger(@NotNull ServerPlayer player, @NotNull BodySegment bodySegment) {
        this.trigger(player, (instance -> instance.matches(bodySegment)));
    }

    @Override
    public @NotNull Codec<BodySegmentInstalledCriterion.TriggerInstance> codec() {
        return BodySegmentInstalledCriterion.TriggerInstance.CODEC;
    }

    public record TriggerInstance(@NotNull Optional<ContextAwarePredicate> player, @NotNull HolderSet<BodySegment> segments) implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<BodySegmentInstalledCriterion.TriggerInstance> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf( "player").forGetter(BodySegmentInstalledCriterion.TriggerInstance::player),
                BodySegment.SET_CODEC.fieldOf("body_segments").forGetter(BodySegmentInstalledCriterion.TriggerInstance::segments)
        ).apply(inst, BodySegmentInstalledCriterion.TriggerInstance::new));

        public static @NotNull Criterion<BodySegmentInstalledCriterion.TriggerInstance> of(@NotNull HolderSet<BodySegment> segments) {
            return SyntheticsAdvancementTriggers.SEGMENT_INSTALLED.get().createCriterion(new BodySegmentInstalledCriterion.TriggerInstance(Optional.empty(), segments));
        }

        @SafeVarargs
        public static @NotNull Criterion<BodySegmentInstalledCriterion.TriggerInstance> of(@NotNull HolderLookup.Provider provider, @NotNull ResourceKey<BodySegment>... segments) {
            var lookup = provider.lookupOrThrow(SyntheticsData.BODY_SEGMENTS);
            return of(HolderSet.direct(
                            Arrays.stream(segments)
                                    .map(lookup::getOrThrow)
                                    .toList()
                    )
            );
        }
        public static @NotNull Criterion<BodySegmentInstalledCriterion.TriggerInstance> of(@NotNull HolderLookup.Provider provider, @NotNull TagKey<BodySegment> segments) {
            var lookup = provider.lookupOrThrow(SyntheticsData.BODY_SEGMENTS);
            return of(lookup.getOrThrow(segments));
        }

        public boolean matches(@NotNull BodySegment segment) {
            return this.segments.stream()
                    .map(Holder::value)
                    .anyMatch(seg -> seg.equals(segment));
        }
    }
}
