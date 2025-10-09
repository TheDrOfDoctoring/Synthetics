package com.thedrofdoctoring.synthetics.menus;

import com.thedrofdoctoring.synthetics.Synthetics;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SyntheticsMenus {

    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, Synthetics.MODID);


    public static final DeferredHolder<MenuType<?>, MenuType<AugmentationChamberMenu>> AUGMENTATION_CHAMBER = MENUS.register("augmentation_chamber", () -> create(AugmentationChamberMenu::new));

    private static <T extends AbstractContainerMenu> MenuType<T> create(MenuType.MenuSupplier<T> supplier) {
        return new MenuType<>(supplier, FeatureFlags.DEFAULT_FLAGS);
    }
    public static void register(IEventBus bus) {
        MENUS.register(bus);
    }


}
