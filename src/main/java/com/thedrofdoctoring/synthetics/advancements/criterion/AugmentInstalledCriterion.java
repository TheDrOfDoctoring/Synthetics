package com.thedrofdoctoring.synthetics.advancements.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.advancements.SyntheticsAdvancementTriggers;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.types.body.augments.Augment;
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

public class AugmentInstalledCriterion extends SimpleCriterionTrigger<AugmentInstalledCriterion.TriggerInstance> {

    public void trigger(@NotNull ServerPlayer player, @NotNull Augment augment) {
        this.trigger(player, (instance -> instance.matches(augment)));
    }

    @Override
    public @NotNull Codec<AugmentInstalledCriterion.TriggerInstance> codec() {
        return AugmentInstalledCriterion.TriggerInstance.CODEC;
    }

    public record TriggerInstance(@NotNull Optional<ContextAwarePredicate> player,
                                  @NotNull HolderSet<Augment> augments) implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<AugmentInstalledCriterion.TriggerInstance> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(AugmentInstalledCriterion.TriggerInstance::player),
                Augment.SET_CODEC.fieldOf("augments").forGetter(AugmentInstalledCriterion.TriggerInstance::augments)
        ).apply(inst, AugmentInstalledCriterion.TriggerInstance::new));

        public static @NotNull Criterion<AugmentInstalledCriterion.TriggerInstance> of(@NotNull HolderSet<Augment> augments) {
            return SyntheticsAdvancementTriggers.AUGMENT_INSTALLED.get().createCriterion(new AugmentInstalledCriterion.TriggerInstance(Optional.empty(), augments));
        }

        @SafeVarargs
        public static @NotNull Criterion<AugmentInstalledCriterion.TriggerInstance> of(@NotNull HolderLookup.Provider provider, @NotNull ResourceKey<Augment>... augments) {
            var lookup = provider.lookupOrThrow(SyntheticsData.AUGMENTS);
            return of(HolderSet.direct(
                            Arrays.stream(augments)
                                    .map(lookup::getOrThrow)
                                    .toList()
                    )
            );
        }
        public static @NotNull Criterion<AugmentInstalledCriterion.TriggerInstance> of(@NotNull HolderLookup.Provider provider, @NotNull TagKey<Augment> augments) {
            var lookup = provider.lookupOrThrow(SyntheticsData.AUGMENTS);
            return of(lookup.getOrThrow(augments));
        }

        public boolean matches(@NotNull Augment augment) {
            return this.augments.stream()
                    .map(Holder::value)
                    .anyMatch(p -> p.equals(augment));
        }
    }
}
