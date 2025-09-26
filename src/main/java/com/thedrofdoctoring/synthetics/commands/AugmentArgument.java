package com.thedrofdoctoring.synthetics.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.types.SyntheticAugment;
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

public class AugmentArgument implements ArgumentType<SyntheticAugment> {

    private static final DynamicCommandExceptionType AUGMENT_NOT_FOUND = new DynamicCommandExceptionType((id) -> Component.translatable("command.synthetics.argument.augment_not_found", id));

    private final HolderLookup<SyntheticAugment> registryLookup;

    private static final Collection<String> EXAMPLES = List.of("synthetics:fail_augment");

    public AugmentArgument(CommandBuildContext context) {
        this.registryLookup = context.lookupOrThrow(SyntheticsData.AUGMENTS);
    }

    @Override
    public SyntheticAugment parse(StringReader reader) throws CommandSyntaxException {
        ResourceLocation location = ResourceLocation.read(reader);

        SyntheticAugment augment = Helper.retrieveDataObject(location, SyntheticsData.AUGMENTS, registryLookup, false);
        if(augment == null) throw AUGMENT_NOT_FOUND.create(location);
        return augment;
    }
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(registryLookup.listElements().map(p -> p.key().location().toString()), builder);
    }
    public static SyntheticAugment getAugment(@NotNull CommandContext<CommandSourceStack> context, String id) {
        return context.getArgument(id, SyntheticAugment.class);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

}
