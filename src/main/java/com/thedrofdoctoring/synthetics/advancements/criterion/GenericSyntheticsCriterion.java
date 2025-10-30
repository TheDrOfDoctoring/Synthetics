package com.thedrofdoctoring.synthetics.advancements.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.advancements.SyntheticsAdvancementTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class GenericSyntheticsCriterion extends SimpleCriterionTrigger<GenericSyntheticsCriterion.TriggerInstance> {

    public void trigger(@NotNull ServerPlayer player, Trigger trigger) {
        this.trigger(player, (instance) -> instance.matches(trigger));
    }

    @Override
    public @NotNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }


    public record TriggerInstance(@NotNull Optional<ContextAwarePredicate> player, @NotNull Trigger trigger) implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                StringRepresentable.fromEnum(Trigger::values).fieldOf("trigger").forGetter(TriggerInstance::trigger)
        ).apply(inst, TriggerInstance::new));

        public static @NotNull Criterion<TriggerInstance> of(@NotNull Trigger trigger) {
            return SyntheticsAdvancementTriggers.GENERIC.get().createCriterion(new TriggerInstance(Optional.empty(), trigger));
        }


        boolean matches(Trigger trigger) {
            return this.trigger == trigger;
        }
    }

    public enum Trigger implements StringRepresentable {
        INSTALLED_ANY_AUGMENT("installed_any_augment"),
        INSTALLED_ANY_PART("installed_any_part"),
        INSTALLED_ANY_SEGMENT("installed_any_segment"),
        ANY_RESEARCH("any_research");


        private final String name;

        Trigger(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }
    }
}
