package com.thedrofdoctoring.synthetics.items;

import com.thedrofdoctoring.synthetics.body.abilities.IBodyInstallable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class InstallableItem<T extends IBodyInstallable<T>> extends Item {

    private final ResourceKey<Registry<T>> type;
    private final Supplier<DataComponentType<T>> componentType;


    public InstallableItem(ResourceKey<Registry<T>> type, Supplier<DataComponentType<T>> componentType, Properties properties) {
        super(properties);
        this.type = type;
        this.componentType = componentType;
    }

    public ResourceKey<Registry<T>> getType() {
        return type;
    }

    public T getInstallableComponent(ItemStack stack) {

        return stack.get(componentType);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {

        IBodyInstallable<T> component = stack.get(componentType);
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
}
