package com.clanjhoo.vampire.cmd.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.kyori.adventure.text.Component;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public final class YesNoArgumentType implements CustomArgumentType<Boolean, String> {

    private static final DynamicCommandExceptionType ERROR_NOT_EXISTS = new DynamicCommandExceptionType(
            name -> MessageComponentSerializer
                    .message()
                    .serialize(Component.text(name + " is not yes or no!")));

    public static YesNoArgumentType yesNoClass() {
        return new YesNoArgumentType();
    }

    @Override
    public Boolean parse(StringReader reader) throws CommandSyntaxException {
        boolean result;
        final String strType = getNativeType().parse(reader).toLowerCase();

        if (strType.equalsIgnoreCase("yes"))
            result = true;
        else if (strType.equalsIgnoreCase("no"))
            result = false;
        else
            throw ERROR_NOT_EXISTS.create(strType);

        return result;
    }

    @Override
    public <S> Boolean parse(StringReader reader, S source) throws CommandSyntaxException {
        return parse(reader);
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }

    @Override
    public Collection<String> getExamples() {
        return List.of("yes", "no");
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        Stream.of("yes", "no")
                .filter(entry -> entry.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                .forEach(builder::suggest);
        return builder.buildFuture();
    }
}
