package com.thedrofdoctoring.synthetics.advancements.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.advancements.SyntheticsAdvancementTriggers;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.types.body.parts.BodyPart;
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

public class BodyPartInstalledCriterion extends SimpleCriterionTrigger<BodyPartInstalledCriterion.TriggerInstance> {

    public void trigger(@NotNull ServerPlayer player, @NotNull BodyPart bodyPart) {
        this.trigger(player, (instance -> instance.matches(bodyPart)));
    }

    @Override
    public @NotNull Codec<BodyPartInstalledCriterion.TriggerInstance> codec() {
        return BodyPartInstalledCriterion.TriggerInstance.CODEC;
    }

    public record TriggerInstance(@NotNull Optional<ContextAwarePredicate> player,
                                  @NotNull HolderSet<BodyPart> parts) implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<BodyPartInstalledCriterion.TriggerInstance> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(BodyPartInstalledCriterion.TriggerInstance::player),
                BodyPart.SET_CODEC.fieldOf("body_parts").forGetter(BodyPartInstalledCriterion.TriggerInstance::parts)
        ).apply(inst, BodyPartInstalledCriterion.TriggerInstance::new));

        public static @NotNull Criterion<BodyPartInstalledCriterion.TriggerInstance> of(@NotNull HolderSet<BodyPart> parts) {
            return SyntheticsAdvancementTriggers.PART_INSTALLED.get().createCriterion(new BodyPartInstalledCriterion.TriggerInstance(Optional.empty(), parts));
        }

        @SafeVarargs
        public static @NotNull Criterion<BodyPartInstalledCriterion.TriggerInstance> of(@NotNull HolderLookup.Provider provider, @NotNull ResourceKey<BodyPart>... parts) {
            var lookup = provider.lookupOrThrow(SyntheticsData.BODY_PARTS);
            return of(HolderSet.direct(
                            Arrays.stream(parts)
                                    .map(lookup::getOrThrow)
                                    .toList()
                    )
            );
        }
        public static @NotNull Criterion<BodyPartInstalledCriterion.TriggerInstance> of(@NotNull HolderLookup.Provider provider, @NotNull TagKey<BodyPart> parts) {
            var lookup = provider.lookupOrThrow(SyntheticsData.BODY_PARTS);
            return of(lookup.getOrThrow(parts));
        }

        public boolean matches(@NotNull BodyPart part) {
            return this.parts.stream()
                    .map(Holder::value)
                    .anyMatch(p -> p.equals(part));
        }
    }
}

