package com.thedrofdoctoring.synthetics.client.core.items;

import com.thedrofdoctoring.synthetics.capabilities.PowerManager;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.Ability;
import com.thedrofdoctoring.synthetics.core.data.types.body.installables.Augment;
import com.thedrofdoctoring.synthetics.core.data.types.body.installables.BodyPart;
import com.thedrofdoctoring.synthetics.core.data.types.body.installables.BodySegment;
import com.thedrofdoctoring.synthetics.core.data.types.body.installables.IBodyInstallable;
import com.thedrofdoctoring.synthetics.core.data.types.body.parts.BodyPartType;
import com.thedrofdoctoring.synthetics.core.data.types.body.parts.BodySegmentType;
import com.thedrofdoctoring.synthetics.core.synthetics.SyntheticAbilities;
import com.thedrofdoctoring.synthetics.items.InstallableItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ModelEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

public class SyntheticsClientItems {

    public void registerGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register(SyntheticItemModelLoader.SyntheticGeometryLoader.ID, SyntheticItemModelLoader.SyntheticGeometryLoader.INSTANCE);
    }

    public static void register(IEventBus bus) {
        SyntheticsClientItems clientItems = new SyntheticsClientItems();
        bus.addListener(clientItems::registerGeometryLoaders);
    }

    public static void handleInstallableHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext ignoredContext, @NotNull List<Component> tooltips, @NotNull TooltipFlag flag) {
        if(flag.hasShiftDown()) {
            InstallableItem<?> item = (InstallableItem<?>) stack.getItem();
            IBodyInstallable<?> installable = item.getInstallableComponent(stack);
            switch(installable) {
                case Augment     augment -> addAugmentTooltip(tooltips, augment);
                case BodyPart    part    -> addPartTooltip(tooltips, part);
                case BodySegment segment -> addSegmentTooltip(tooltips, segment);
                default -> {}
            }
            if(installable.abilities().isPresent()) {
                for(Holder<Ability> ability : installable.abilities().get()) {
                    if(ability.value().abilityType().equals(SyntheticAbilities.BATTERY.get())) {
                        tooltips.add(Component.translatable("text.synthetics.augmentation_power_storage", (int) ability.value().abilityData().factor() * PowerManager.BATTERY_STORAGE_BASE).withStyle(ChatFormatting.BLUE));
                    }
                }
            }

        } else {
            tooltips.add(Component.translatable("text.synthetics.info_shift").withStyle(ChatFormatting.GRAY));
        }
    }

    private static void addAugmentTooltip(List<Component> tooltips, Augment augment) {
        tooltips.add(Component.translatable("text.synthetics.augmentation.max_total", augment.maxTotal()).withStyle(ChatFormatting.BLUE));
        tooltips.add(Component.translatable("text.synthetics.augmentation.max_per_part", augment.maxPerPart()).withStyle(ChatFormatting.BLUE));
        HashMap<Holder<BodyPartType>, BodyPart> validTypes = new HashMap<>(augment.validParts().size());
        for(Holder<BodyPart> validPart : augment.validParts()) {
            if(!validTypes.containsKey(validPart.value().type())) {
                validTypes.put(validPart.value().type(), validPart.value());
            }
        }
        StringBuilder str = new StringBuilder();
        for(BodyPart part : validTypes.values()) {
            if(str.isEmpty()) {
                str.append(part.title().getString());
            } else {
                str.append(", ");
                str.append(part.title().getString());
            }

        }
        tooltips.add(Component.translatable("tooltips.synthetics.body_part", str.toString()).withStyle(ChatFormatting.BLUE));
        tooltips.add(Component.translatable("tooltips.synthetics.augment_complexity", augment.complexity()).withStyle(ChatFormatting.RED));
    }
    private static void addPartTooltip(List<Component> tooltip, BodyPart part) {
        tooltip.add(Component.translatable("tooltips.synthetics.max_complexity", part.maxComplexity()).withStyle(ChatFormatting.BLUE));
        HashMap<Holder<BodySegmentType>, BodySegment> validTypes = new HashMap<>(part.validSegments().size());
        for(Holder<BodySegment> validSegment : part.validSegments()) {
            if(!validTypes.containsKey(validSegment.value().type())) {
                validTypes.put(validSegment.value().type(), validSegment.value());
            }
        }
        StringBuilder str = new StringBuilder();
        for(BodySegment segment : validTypes.values()) {
            if(str.isEmpty()) {
                str.append(segment.title().getString());
            } else {
                str.append(", ");
                str.append(segment.title().getString());
            }
        }
        tooltip.add(Component.translatable("tooltips.synthetics.body_segment", str.toString()).withStyle(ChatFormatting.BLUE));
    }
    private static void addSegmentTooltip(List<Component> tooltip, BodySegment segment) {
        tooltip.add(Component.translatable("tooltips.synthetics.max_complexity", segment.maxComplexity()).withStyle(ChatFormatting.BLUE));
    }

}
