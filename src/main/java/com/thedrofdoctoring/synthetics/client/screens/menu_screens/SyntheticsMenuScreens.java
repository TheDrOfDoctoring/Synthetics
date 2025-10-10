package com.thedrofdoctoring.synthetics.client.screens.menu_screens;

import com.thedrofdoctoring.synthetics.client.screens.menu_screens.augmentation_chamber.AugmentationChamberScreen;
import com.thedrofdoctoring.synthetics.menus.SyntheticsMenus;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class SyntheticsMenuScreens {

    public void registerScreens(RegisterMenuScreensEvent event) {

        event.register(SyntheticsMenus.AUGMENTATION_CHAMBER.get(), AugmentationChamberScreen::new);
        event.register(SyntheticsMenus.SYNTHETIC_FORGE.get(), SyntheticForgeScreen::new);

    }
    public static void register(IEventBus bus) {
        SyntheticsMenuScreens menuScreens = new SyntheticsMenuScreens();
        bus.addListener(menuScreens::registerScreens);
    }
}
