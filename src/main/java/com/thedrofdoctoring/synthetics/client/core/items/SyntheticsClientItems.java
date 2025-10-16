package com.thedrofdoctoring.synthetics.client.core.items;

import com.thedrofdoctoring.synthetics.body.abilities.IBodyInstallable;
import com.thedrofdoctoring.synthetics.capabilities.PowerManager;
import com.thedrofdoctoring.synthetics.core.SyntheticsItems;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.collections.Augments;
import com.thedrofdoctoring.synthetics.core.data.components.SyntheticsDataComponents;
import com.thedrofdoctoring.synthetics.core.data.types.body.BodyPart;
import com.thedrofdoctoring.synthetics.core.data.types.body.SyntheticAbility;
import com.thedrofdoctoring.synthetics.core.data.types.body.SyntheticAugment;
import com.thedrofdoctoring.synthetics.core.synthetics.SyntheticAbilities;
import com.thedrofdoctoring.synthetics.items.InstallableItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ModelEvent;
import org.jetbrains.annotations.NotNull;

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
            if(installable instanceof SyntheticAugment augment) {
                tooltips.add(Component.translatable("tooltips.synthetics.augment_complexity", augment.complexity()).withStyle(ChatFormatting.RED));
                tooltips.add(Component.translatable("tooltips.synthetics.body_part", augment.validParts().get(0).value().title()).withStyle(ChatFormatting.BLUE));
            }
            if(installable instanceof BodyPart part) {
                tooltips.add(Component.translatable("tooltips.synthetics.max_complexity", part.maxComplexity()).withStyle(ChatFormatting.BLUE));
            }
            if(installable.abilities().isPresent()) {
                for(Holder<SyntheticAbility> ability : installable.abilities().get()) {
                    if(ability.value().abilityType().equals(SyntheticAbilities.BATTERY.get())) {
                        tooltips.add(Component.translatable("text.synthetics.augmentation_power_storage", (int) ability.value().factor() * PowerManager.BATTERY_STORAGE_BASE).withStyle(ChatFormatting.BLUE));
                    }
                }
            }

        } else {
            tooltips.add(Component.translatable("text.synthetics.info_shift").withStyle(ChatFormatting.GRAY));
        }
    }

}
