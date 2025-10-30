package com.thedrofdoctoring.synthetics.advancements.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.advancements.SyntheticsAdvancementTriggers;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.types.research.ResearchNode;
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

public class ResearchCriterion extends SimpleCriterionTrigger<ResearchCriterion.TriggerInstance> {

    public void trigger(@NotNull ServerPlayer player, @NotNull ResearchNode node) {
        this.trigger(player, (instance -> instance.matches(node)));
    }

    @Override
    public @NotNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public record TriggerInstance(@NotNull Optional<ContextAwarePredicate> player, @NotNull HolderSet<ResearchNode> research) implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf( "player").forGetter(TriggerInstance::player),
                ResearchNode.SET_CODEC.fieldOf("research").forGetter(TriggerInstance::research)
        ).apply(inst, TriggerInstance::new));

        public static @NotNull Criterion<TriggerInstance> of(@NotNull HolderSet<ResearchNode> research) {
            return SyntheticsAdvancementTriggers.RESEARCH_UNLOCKED.get().createCriterion(new TriggerInstance(Optional.empty(), research));
        }
        @SafeVarargs
        public static @NotNull Criterion<TriggerInstance> of(@NotNull HolderLookup.Provider provider, @NotNull ResourceKey<ResearchNode>... research) {
            var lookup = provider.lookupOrThrow(SyntheticsData.RESEARCH_NODES);
            return of(HolderSet.direct(
                    Arrays.stream(research)
                    .map(lookup::getOrThrow)
                    .toList()
                    )
            );
        }
        public static @NotNull Criterion<TriggerInstance> of(@NotNull HolderLookup.Provider provider, @NotNull TagKey<ResearchNode> research) {
            var lookup = provider.lookupOrThrow(SyntheticsData.RESEARCH_NODES);
            return of(lookup.getOrThrow(research));
        }

        public boolean matches(@NotNull ResearchNode research) {
            return this.research.stream()
                    .map(Holder::value)
                    .anyMatch(node -> node.equals(research));
        }
    }
}
