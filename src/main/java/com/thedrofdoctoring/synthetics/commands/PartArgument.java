package com.thedrofdoctoring.synthetics.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.types.body.parts.BodyPart;
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

public class PartArgument implements ArgumentType<BodyPart> {

    private static final DynamicCommandExceptionType BODYPART_NOT_FOUND = new DynamicCommandExceptionType((id) -> Component.translatable("command.synthetics.argument.part_not_found", id));

    private final HolderLookup<BodyPart> registryLookup;

    private static final Collection<String> EXAMPLES = List.of("synthetics:fail_part");

    public PartArgument(CommandBuildContext context) {
        this.registryLookup = context.lookupOrThrow(SyntheticsData.BODY_PARTS);
    }

    @Override
    public BodyPart parse(StringReader reader) throws CommandSyntaxException {
        ResourceLocation location = ResourceLocation.read(reader);

        BodyPart bodyPart = Helper.retrieveDataObject(location, SyntheticsData.BODY_PARTS, registryLookup, false);
        if(bodyPart == null) throw BODYPART_NOT_FOUND.create(location);
        return bodyPart;
    }
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(registryLookup.listElements().map(p -> p.key().location().toString()), builder);
    }
    public static BodyPart getPart(@NotNull CommandContext<CommandSourceStack> context, String id) {
        return context.getArgument(id, BodyPart.class);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
