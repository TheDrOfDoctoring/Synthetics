package com.thedrofdoctoring.synthetics.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.types.research.ResearchNode;
import com.thedrofdoctoring.synthetics.util.Helper;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ResearchArgument implements ArgumentType<ResearchNode> {

    private static final DynamicCommandExceptionType RESEARCH_NOT_FOUND = new DynamicCommandExceptionType((id) -> Component.translatable("command.synthetics.argument.research_not_found", id));

    private final HolderLookup<ResearchNode> registryLookup;

    private static final Collection<String> EXAMPLES = List.of("synthetics:no_node");

    public ResearchArgument(CommandBuildContext context) {
        this.registryLookup = context.lookupOrThrow(SyntheticsData.RESEARCH_NODES);
    }

    @Override
    public ResearchNode parse(StringReader reader) throws CommandSyntaxException {
        ResourceLocation location = ResourceLocation.read(reader);

        ResearchNode research = Helper.retrieveDataObject(location, SyntheticsData.RESEARCH_NODES, registryLookup, false);
        if(research == null) throw RESEARCH_NOT_FOUND.create(location);
        return research;
    }
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(registryLookup.listElements().map(p -> p.key().location().toString()), builder);
    }
    public static ResearchNode getResearch(@NotNull CommandContext<CommandSourceStack> context, String id) {
        return context.getArgument(id, ResearchNode.class);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
