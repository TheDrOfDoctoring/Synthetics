package com.thedrofdoctoring.synthetics;

import com.mojang.logging.LogUtils;
import com.thedrofdoctoring.synthetics.commands.SyntheticsCommands;
import com.thedrofdoctoring.synthetics.config.CommonConfig;
import com.thedrofdoctoring.synthetics.core.*;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.components.SyntheticsDataComponents;
import com.thedrofdoctoring.synthetics.core.synthetics.SyntheticAbilities;
import com.thedrofdoctoring.synthetics.menus.SyntheticsMenus;
import com.thedrofdoctoring.synthetics.networking.SyntheticsPayloads;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import org.slf4j.Logger;

@Mod(Synthetics.MODID)
public class Synthetics {
    public static final String MODID = "synthetics";
    public static final Logger LOGGER = LogUtils.getLogger();
    public Synthetics(IEventBus modEventBus, ModContainer modContainer) {

        modContainer.registerConfig(ModConfig.Type.COMMON, CommonConfig.COMMON_CONFIG);

        SyntheticAbilities.register(modEventBus);
        SyntheticsItems.register(modEventBus);
        SyntheticsBlocks.register(modEventBus);
        SyntheticsEntities.register(modEventBus);
        SyntheticsData.register(modEventBus);
        SyntheticsAttachments.register(modEventBus);
        SyntheticsBlockEntities.register(modEventBus);
        SyntheticsCommands.register(modEventBus);
        SyntheticsPayloads.register(modEventBus);
        SyntheticsDataComponents.register(modEventBus);
        SyntheticsMenus.register(modEventBus);

        modEventBus.addListener(this::registerRegistries);

    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(Synthetics.MODID, path);
    }

    public void registerRegistries(final NewRegistryEvent event) {
        event.register(SyntheticAbilities.ABILITY_REGISTRY);
    }
}
