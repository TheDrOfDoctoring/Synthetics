package com.thedrofdoctoring.synthetics.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.thedrofdoctoring.synthetics.Synthetics;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SyntheticsCommands {

    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, Synthetics.MODID);
    public static final DeferredHolder<ArgumentTypeInfo<?, ?>, ArgumentTypeInfo<?, ?>> AUGMENTS = COMMAND_ARGUMENT_TYPES.register("synthetic_augments", () -> ArgumentTypeInfos.registerByClass(AugmentArgument.class, SingletonArgumentInfo.contextAware(AugmentArgument::new)));
    public static final DeferredHolder<ArgumentTypeInfo<?, ?>, ArgumentTypeInfo<?, ?>> PARTS = COMMAND_ARGUMENT_TYPES.register("body_parts", () -> ArgumentTypeInfos.registerByClass(PartArgument.class, SingletonArgumentInfo.contextAware(PartArgument::new)));
    public static final DeferredHolder<ArgumentTypeInfo<?, ?>, ArgumentTypeInfo<?, ?>> RESEARCH = COMMAND_ARGUMENT_TYPES.register("research_nodes", () -> ArgumentTypeInfos.registerByClass(ResearchArgument.class, SingletonArgumentInfo.contextAware(ResearchArgument::new)));

    public static void register(IEventBus bus) {
        COMMAND_ARGUMENT_TYPES.register(bus);
        SyntheticsCommands commands = new SyntheticsCommands();
        NeoForge.EVENT_BUS.addListener(commands::onCommandsRegister);
    }

    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal(Synthetics.MODID).then(SyntheticsAugmentCommand.register(buildContext)));
        dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal(Synthetics.MODID).then(SyntheticsPartCommand.register(buildContext)));
        dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal(Synthetics.MODID).then(SyntheticsResearchCommand.register(buildContext)));
        dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal(Synthetics.MODID).then(SyntheticsEnergyCommand.register(buildContext)));
        dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal(Synthetics.MODID).then(SyntheticsCooldownsCommand.register(buildContext)));



    }
    private void onCommandsRegister(final RegisterCommandsEvent event) {
        registerCommands(event.getDispatcher(), event.getBuildContext());
    }

}
