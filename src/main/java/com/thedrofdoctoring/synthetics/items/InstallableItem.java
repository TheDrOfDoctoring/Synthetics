package com.thedrofdoctoring.synthetics.items;

import com.thedrofdoctoring.synthetics.abilities.IBodyInstallable;
import com.thedrofdoctoring.synthetics.client.core.items.SyntheticsClientItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class InstallableItem<T extends IBodyInstallable<T>> extends Item {

    private final ResourceKey<Registry<T>> type;
    private final Supplier<DataComponentType<Holder<T>>> componentType;


    public InstallableItem(ResourceKey<Registry<T>> type, Supplier<DataComponentType<Holder<T>>> componentType, Properties properties) {
        super(properties);
        this.type = type;
        this.componentType = componentType;
    }

    public ResourceKey<Registry<T>> getType() {
        return type;
    }

    public T getInstallableComponent(ItemStack stack) {
        Holder<T> holder = stack.get(componentType);
        if(holder == null) {
            return null;
        }
        return holder.value();
    }
    public Holder<T> getInstallableComponentHolder(ItemStack stack) {
        return stack.get(componentType);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {

        IBodyInstallable<T> component = getInstallableComponent(stack);
        if(component != null) {
            MutableComponent mutable = Component.empty();
            mutable.append(component.typeTitleID()).withStyle();
            mutable.append(": ");
            mutable.withStyle(ChatFormatting.AQUA);
            MutableComponent mutable2 = Component.empty();
            mutable2.append(component.title()).withStyle(ChatFormatting.WHITE);
            mutable.append(mutable2);
            return mutable;
        }
        return super.getName(stack);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if(context.level() == null) return;
        if(Objects.requireNonNull(context.level()).isClientSide) {
            SyntheticsClientItems.handleInstallableHoverText(stack, context, tooltipComponents, tooltipFlag);
        }
    }
}
