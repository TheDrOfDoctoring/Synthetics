package com.thedrofdoctoring.synthetics.core;

import com.thedrofdoctoring.synthetics.Synthetics;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class SyntheticsItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Synthetics.MODID);

    private static final Set<DeferredHolder<Item, ? extends Item>> creativeTabItems = new HashSet<>();
    private static final DeferredRegister<CreativeModeTab> SYNTHETICS_CREATIVE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Synthetics.MODID);
    private static final ResourceKey<CreativeModeTab> SYNTHETIC_TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, Synthetics.rl("synthetics"));

    public static <T extends Item> DeferredHolder<Item, T> register(final String id, final Supplier<? extends T> itemSupplier) {
        DeferredHolder<Item, T> item = ITEMS.register(id, itemSupplier);
        creativeTabItems.add(item);
        return item;
    }
    public static void register(IEventBus bus) {
        ITEMS.register(bus);
        SYNTHETICS_CREATIVE_TAB.register(bus);
    }
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SYNTHETICS_TAB = SYNTHETICS_CREATIVE_TAB.register(SYNTHETIC_TAB_KEY.location().getPath(), () -> CreativeModeTab.builder().displayItems(
                    (pParameters, pOutput) -> creativeTabItems.forEach(item -> pOutput.accept(item.get())))
            .title(Component.translatable("itemGroup." + Synthetics.MODID))
            .icon(() -> new ItemStack(Items.IRON_INGOT))
            .build()
    );


}
