package com.clanjhoo.vampire.util;

import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class CommandUtils {

    public static final DynamicCommandExceptionType SUBCOMMAND_DOESNT_EXIST = new DynamicCommandExceptionType(
            name -> MessageComponentSerializer
                    .message()
                    .serialize(Component.text("The subcommand \"" + name + "\" does not exist!"))
    );

    public static final SimpleCommandExceptionType NO_NAME_SPECIFIED = new SimpleCommandExceptionType(
            MessageComponentSerializer
                    .message()
                    .serialize(Component.text("You have to specify a name when creating a Player entity!"))
    );

    public static final SimpleCommandExceptionType NO_FAKE_FOUND = new SimpleCommandExceptionType(
            MessageComponentSerializer
                    .message()
                    .serialize(Component.text("Couldn't find an entity with that ID!"))
    );

    public static final SimpleCommandExceptionType EXECUTOR_NOT_CONSOLE = new SimpleCommandExceptionType(
            MessageComponentSerializer
                    .message()
                    .serialize(Component.text("This command can not be run by the console!"))
    );

    public static final SimpleCommandExceptionType EXECUTOR_NOT_PLAYER = new SimpleCommandExceptionType(
            MessageComponentSerializer
                    .message()
                    .serialize(Component.text("This command can only be run by a player!"))
    );

    public static final SimpleCommandExceptionType EXECUTOR_ENTITY_NOT_PLAYER = new SimpleCommandExceptionType(
            MessageComponentSerializer
                    .message()
                    .serialize(Component.text("This command, when run by an entity, can only be run by a player!"))
    );

    public static final SimpleCommandExceptionType EXECUTOR_NOT_PLAYER_OPTIONAL = new SimpleCommandExceptionType(
            MessageComponentSerializer
                    .message()
                    .serialize(Component.text("You have to specify a target when the executor is not a player!"))
    );

    public static final SimpleCommandExceptionType EXECUTOR_NOT_ENTITY = new SimpleCommandExceptionType(
            MessageComponentSerializer
                    .message()
                    .serialize(Component.text("This command can only be run by an entity!"))
    );

    public static final SimpleCommandExceptionType EXECUTOR_NOT_LIVING = new SimpleCommandExceptionType(
            MessageComponentSerializer
                    .message()
                    .serialize(Component.text("This command can only be run by a living entity!"))
    );

    public static final SimpleCommandExceptionType EXECUTOR_NOT_LIVING_OPTIONAL = new SimpleCommandExceptionType(
            MessageComponentSerializer
                    .message()
                    .serialize(Component.text("You have to specify a target when the executor is not a living entity!"))
    );

    public static final SimpleCommandExceptionType EXECUTOR_NOT_ANIMAL_TAMER = new SimpleCommandExceptionType(
            MessageComponentSerializer
                    .message()
                    .serialize(Component.text("This command can only be run by an animal tamer!"))
    );

    public static final SimpleCommandExceptionType EXECUTOR_NOT_EQUIPMENT = new SimpleCommandExceptionType(
            MessageComponentSerializer
                    .message()
                    .serialize(Component.text("This command can only be run by an entity supporting equipment!"))
    );

    public static final SimpleCommandExceptionType EXECUTOR_NOT_HOLDING_ITEM = new SimpleCommandExceptionType(
            MessageComponentSerializer
                    .message()
                    .serialize(Component.text("This command can only be run by an entity holding an item!"))
    );

    public static final SimpleCommandExceptionType EXECUTOR_NOT_EQUIPMENT_OPTIONAL = new SimpleCommandExceptionType(
            MessageComponentSerializer
                    .message()
                    .serialize(Component.text("You have to specify a target when the executor does not support equipment!"))
    );

    public static final SimpleCommandExceptionType EXECUTOR_CANT_BE_CREATIVE = new SimpleCommandExceptionType(
            MessageComponentSerializer
                    .message()
                    .serialize(Component.text("You can't use that command while in creative gamemode!"))
    );

    public static final DynamicCommandExceptionType EXECUTOR_WRONG_CLASS = new DynamicCommandExceptionType(
            name -> MessageComponentSerializer
                    .message()
                    .serialize(Component.text("Only " + name + " can execute this command!"))
    );

    public static final SimpleCommandExceptionType ESSENTIALS_NOT_LOADED = new SimpleCommandExceptionType(
            MessageComponentSerializer
                    .message()
                    .serialize(Component.text("The abyss gazes back (please contact an admin and tell them this error)"))
    );

    public static final DynamicCommandExceptionType DATA_NOT_LOADED = new DynamicCommandExceptionType(
            name -> {
                if (name == null)
                    return MessageComponentSerializer
                            .message()
                            .serialize(Component.text("Your data couldn't be loaded. Please try again later. If the error persists contact an admin."));
                return MessageComponentSerializer
                        .message()
                        .serialize(Component.text(name + "'s data couldn't be loaded. Please try again later. If the error persists contact an admin."));
            }
    );

    public static final SimpleCommandExceptionType DATABASE_BUSY = new SimpleCommandExceptionType(
            MessageComponentSerializer
                    .message()
                    .serialize(Component.text("The database is now busy. Please contact an administrator if the error persists.", NamedTextColor.RED))
    );

    public static final DynamicCommandExceptionType ATTRIBUTE_DOES_NOT_EXIST = new DynamicCommandExceptionType(
            name -> MessageComponentSerializer
                    .message()
                    .serialize(Component.text(name + " is not a valid attribute!"))
    );

    public static final DynamicCommandExceptionType POTION_EFFECT_DOES_NOT_EXIST = new DynamicCommandExceptionType(
            name -> MessageComponentSerializer
                    .message()
                    .serialize(Component.text(name + " is not a valid potion effect type!"))
    );

    public static final DynamicCommandExceptionType BLOCK_DOES_NOT_EXIST = new DynamicCommandExceptionType(
            name -> MessageComponentSerializer
                    .message()
                    .serialize(Component.text(name + " is not a valid block!"))
    );

    public static final SimpleCommandExceptionType CHARGED_SKULL_FIREBALL = new SimpleCommandExceptionType(
            MessageComponentSerializer
                    .message()
                    .serialize(Component.text("You can't specify chargedSkull for fireballs other than wither skulls"))
    );

    public static final DynamicCommandExceptionType ENTITY_DOES_NOT_EXIST = new DynamicCommandExceptionType(
            name -> MessageComponentSerializer
                    .message()
                    .serialize(Component.text(name + " is not a valid entity type!"))
    );

    public static final DynamicCommandExceptionType NEEDED_STRICTLY_POSITIVE = new DynamicCommandExceptionType(
            name -> MessageComponentSerializer
                    .message()
                    .serialize(Component.text(name + " has to be greater than zero"))
    );

    public static final DynamicCommandExceptionType NEEDED_POSITIVE = new DynamicCommandExceptionType(
            name -> MessageComponentSerializer
                    .message()
                    .serialize(Component.text(name + " can't be negative"))
    );

    public static void sendRichMessage(@NotNull CommandSender sender, @Nullable Entity executor, @NotNull String message) {
        sender.sendRichMessage(message);
        if (executor != null && executor != sender)
            executor.sendRichMessage(message);
    }

    public static void sendMessage(@NotNull CommandSender sender, @Nullable Entity executor, @NotNull String message) {
        sender.sendMessage(message);
        if (executor != null && executor != sender)
            executor.sendMessage(message);
    }

    public static void sendMessage(@NotNull CommandSender sender, @Nullable Entity executor, @NotNull Component message) {
        sender.sendMessage(message);
        if (executor != null && executor != sender)
            executor.sendMessage(message);
    }

    public static void sendMessage(@NotNull CommandSender sender, @Nullable Entity executor, @NotNull String... messages) {
        sender.sendMessage(messages);
        if (executor != null && executor != sender)
            executor.sendMessage(messages);
    }

    public enum Sorting {
        ARBITRARY,
        NEAREST,
        FURTHEST,
        RANDOM;
    }
}
