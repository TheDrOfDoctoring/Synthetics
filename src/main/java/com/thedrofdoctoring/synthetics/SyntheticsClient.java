package com.thedrofdoctoring.synthetics;

import com.thedrofdoctoring.synthetics.client.core.SyntheticsClientManager;
import com.thedrofdoctoring.synthetics.client.core.SyntheticsClientSetup;
import com.thedrofdoctoring.synthetics.client.core.SyntheticsKeys;
import com.thedrofdoctoring.synthetics.config.ClientConfig;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = Synthetics.MODID, dist = Dist.CLIENT)
public class SyntheticsClient {

    private static SyntheticsClient INSTANCE;
    private final SyntheticsClientManager manager;

    public SyntheticsClient(IEventBus modBus, ModContainer container) {
        INSTANCE = this;
        container.registerConfig(ModConfig.Type.CLIENT, ClientConfig.CLIENT_CONFIG);
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        SyntheticsClientSetup.register(modBus);
        SyntheticsKeys.register(modBus);
        this.manager = new SyntheticsClientManager();
    }
    public static SyntheticsClient getInstance() {
        return INSTANCE;
    }

    public SyntheticsClientManager getManager() {
        return manager;
    }
}
