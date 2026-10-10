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

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public final class ReloadArgumentType implements CustomArgumentType<ReloadArgumentType.ReloadType, String> {

    private static final DynamicCommandExceptionType ERROR_NOT_EXISTS = new DynamicCommandExceptionType(
            name -> MessageComponentSerializer
                    .message()
                    .serialize(Component.text(name + " is not a supported player class!")));

    public static ReloadArgumentType reloadClass() {
        return new ReloadArgumentType();
    }

    @Override
    public ReloadType parse(StringReader reader) throws CommandSyntaxException {
        final String strType = getNativeType().parse(reader);
        ReloadType type;
        try {
            type = ReloadType.valueOf(strType.toUpperCase());
        }
        catch (IllegalArgumentException ex) {
            throw ERROR_NOT_EXISTS.create(strType);
        }

        return type;
    }

    @Override
    public <S> ReloadType parse(StringReader reader, S source) throws CommandSyntaxException {
        return parse(reader);
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }

    @Override
    public Collection<String> getExamples() {
        return Arrays.stream(ReloadType.values()).map(Enum::name).toList();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        Arrays.stream(ReloadType.values())
                .map(Enum::name)
                .filter(entry -> entry.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                .forEach(builder::suggest);
        return builder.buildFuture();
    }

    public enum ReloadType {
        LOCALES,
        CONFIG,
        ALL
    }
}
