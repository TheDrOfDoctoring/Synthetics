package com.thedrofdoctoring.synthetics.core;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.components.BatteryComponentOptions;
import com.thedrofdoctoring.synthetics.core.data.components.SyntheticsDataComponents;
import com.thedrofdoctoring.synthetics.core.data.types.body.BodyPart;
import com.thedrofdoctoring.synthetics.core.data.types.body.SyntheticAugment;
import com.thedrofdoctoring.synthetics.items.BlueprintItem;
import com.thedrofdoctoring.synthetics.items.InstallableItem;
import com.thedrofdoctoring.synthetics.items.RechargeableBatteryItem;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public class SyntheticsItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Synthetics.MODID);

    private static final Set<DeferredHolder<Item, ? extends Item>> creativeTabItems = new HashSet<>();
    private static final DeferredRegister<CreativeModeTab> SYNTHETICS_CREATIVE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Synthetics.MODID);
    private static final ResourceKey<CreativeModeTab> SYNTHETIC_TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, Synthetics.rl("synthetics"));

    public static final DeferredHolder<Item, InstallableItem<SyntheticAugment>> AUGMENT_INSTALLABLE = registerInstallable("synthetic_augment_item", () -> new InstallableItem<>(SyntheticsData.AUGMENTS, SyntheticsDataComponents.AUGMENT, new Item.Properties().stacksTo(64).component(SyntheticsDataComponents.AUGMENT, Holder.direct(new SyntheticAugment(0, 0, 0, 0,null, Optional.empty(), Synthetics.rl("empty_augment"))))));
    public static final DeferredHolder<Item, InstallableItem<BodyPart>> BODY_PART_INSTALLABLE = registerInstallable("body_part_item", () -> new InstallableItem<>(SyntheticsData.BODY_PARTS, SyntheticsDataComponents.BODY_PART, new Item.Properties().stacksTo(64).component(SyntheticsDataComponents.BODY_PART, Holder.direct(new BodyPart(0, null, null, Optional.empty(), Synthetics.rl("empty_body_part"))))));
    public static final DeferredHolder<Item, RechargeableBatteryItem> MEDIUM_BATTERY = registerTab("medium_battery", () -> new RechargeableBatteryItem(new Item.Properties().stacksTo(1).component(SyntheticsDataComponents.BATTERY_OPTIONS, new BatteryComponentOptions(10000, 500, 500)).component(SyntheticsDataComponents.ENERGY_COMPONENT, 0)));
    public static final DeferredHolder<Item, BlueprintItem> BLUEPRINT = registerTab("blueprint", () -> new BlueprintItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> ARTIFICIAL_NEURON = registerTab("artificial_neuron", () -> new Item(new Item.Properties().stacksTo(64)));
    public static final DeferredHolder<Item, Item> ARTIFICIAL_CAPILLARY = registerTab("artificial_capillary", () -> new Item(new Item.Properties().stacksTo(64)));
    public static final DeferredHolder<Item, Item> ARTIFICIAL_TISSUE = registerTab("artificial_tissue", () -> new Item(new Item.Properties().stacksTo(64)));

    public static final DeferredHolder<Item, Item> CREATIVE_TAB_ICON_ITEM = register("creative_tab_icon", () -> new Item(new Item.Properties().stacksTo(1)));


    public static <T extends Item> DeferredHolder<Item, T> registerTab(final String id, final Supplier<? extends T> itemSupplier) {
        DeferredHolder<Item, T> item = ITEMS.register(id, itemSupplier);
        creativeTabItems.add(item);
        return item;
    }
    public static <T extends Item> DeferredHolder<Item, T> register(final String id, final Supplier<? extends T> itemSupplier) {
        DeferredHolder<Item, T> item = ITEMS.register(id, itemSupplier);
        return item;
    }



    public static <T extends InstallableItem<?>> DeferredHolder<Item, T> registerInstallable(final String id, final Supplier<T> itemSupplier) {
        DeferredHolder<Item, T> item = ITEMS.register(id, itemSupplier);
        return item;
    }
    public static void register(IEventBus bus) {
        ITEMS.register(bus);
        SYNTHETICS_CREATIVE_TAB.register(bus);
    }
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SYNTHETICS_TAB = SYNTHETICS_CREATIVE_TAB.register(SYNTHETIC_TAB_KEY.location().getPath(),
            () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + Synthetics.MODID))
            .icon(() -> {
                return new ItemStack(SyntheticsItems.CREATIVE_TAB_ICON_ITEM);
            })
            .displayItems(new CreativeModeTab.DisplayItemsGenerator() {
                @Override
                public void accept(CreativeModeTab.@NotNull ItemDisplayParameters itemDisplayParameters, CreativeModeTab.@NotNull Output output) {
                    creativeTabItems.forEach(item -> {
                        if(item.get() instanceof RechargeableBatteryItem battery) {
                            ItemStack emptyBattery = new ItemStack(battery);
                            ItemStack fullBattery = emptyBattery.copy();
                            emptyBattery.set(SyntheticsDataComponents.ENERGY_COMPONENT.get(), Objects.requireNonNull(emptyBattery.get(SyntheticsDataComponents.BATTERY_OPTIONS)).capacity());
                            output.accept(emptyBattery);
                            output.accept(fullBattery);

                        } else {
                            output.accept(item.get());
                        }

                    });


                    var augmentLookup = itemDisplayParameters.holders().lookupOrThrow(SyntheticsData.AUGMENTS);
                    augmentLookup.listElements().forEach(p -> {
                        ItemStack stack = new ItemStack(AUGMENT_INSTALLABLE);
                        stack.set(SyntheticsDataComponents.AUGMENT, p);
                        output.accept(stack);
                    });
                    var bodyPartLookup = itemDisplayParameters.holders().lookupOrThrow(SyntheticsData.BODY_PARTS);
                    bodyPartLookup.listElements().forEach(p -> {
                        ItemStack stack = new ItemStack(BODY_PART_INSTALLABLE);
                        stack.set(SyntheticsDataComponents.BODY_PART, p);
                        output.accept(stack);
                    });
                }
            })
            .build()
    );



}
