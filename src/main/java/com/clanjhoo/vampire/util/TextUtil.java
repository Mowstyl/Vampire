package com.clanjhoo.vampire.util;

import co.aikar.commands.CommandHelp;
import co.aikar.commands.RegisteredCommand;
import com.clanjhoo.vampire.keyproviders.CommandMessageKeys;
import com.clanjhoo.vampire.Perm;
import com.clanjhoo.vampire.VampireRevamp;
import com.clanjhoo.vampire.entity.VPlayer;
import com.clanjhoo.vampire.keyproviders.GrammarMessageKeys;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class TextUtil {
    private final static Map<String, TextUtil> instances = new ConcurrentHashMap<>(1);
    public static final Pattern PATTERN_NEWLINE = Pattern.compile("\\r?\\n");

    private final VampireRevamp plugin;
    private final ResourceUtil resUtil;


    private TextUtil(VampireRevamp plugin) {
        this.plugin = plugin;
        resUtil = ResourceUtil.get(plugin);
    }

    public static TextUtil get(VampireRevamp plugin) {
        return instances.computeIfAbsent(plugin.getName(), (k) -> new TextUtil(plugin));
    }

    public static Component capitalizeFirst(@NotNull Component text) {
        GsonComponentSerializer serializer = GsonComponentSerializer.gson();
        String rawText = serializer.serialize(text);
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonText = jsonParser.parse(rawText);
        return serializer.deserialize(capitalizeFirst(jsonText).toString());
    }

    private static JsonElement capitalizeFirst(JsonElement jsonText) {
        if (!jsonText.isJsonObject()) {
            String aux = capitalizeFirst(jsonText.getAsString());
            if (aux.contains(" "))
                aux = "\"" + aux + "\"";
            JsonParser jsonParser = new JsonParser();
            return jsonParser.parse(aux);
        }
        JsonObject root = jsonText.getAsJsonObject();
        if (root.has("text")) {
            String dieText = root.get("text").getAsString();
            if (!dieText.isEmpty()) {
                root.addProperty("text", capitalizeFirst(dieText));
                return root;
            }
        }
        if (root.has("extra")) {
            JsonElement extra = capitalizeFirst(root.getAsJsonArray("extra").get(0));
            root.getAsJsonArray("extra").set(0, extra);
            return root;
        }
        throw new IllegalArgumentException("Unknown JSON structure, please contact the dev");
    }

    public static String capitalizeFirst(@NotNull String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    public static ArrayList<String> wrap(final String string) {
        if (string == null) throw new NullPointerException("string");
        return new ArrayList<>(Arrays.asList(PATTERN_NEWLINE.split(string)));
    }

    public static ArrayList<String> wrap(final Collection<String> strings) {
        ArrayList<String> ret = new ArrayList<>();
        for (String string : strings) {
            ret.addAll(wrap(string));
        }
        return ret;
    }

    public Component getPlayerInfoHeader(boolean isVampire, boolean isNosferatu, Component playerName, CommandSender sender) {
        Component start = Component.text("_______.[ ", NamedTextColor.GOLD);

        Component playerStr = plugin.getMessage(sender, GrammarMessageKeys.PLAYER);
        playerStr = capitalizeFirst(playerStr)
                .append(Component.text(" "))
                .color(isVampire
                        ? NamedTextColor.RED
                        : isNosferatu
                            ? NamedTextColor.DARK_RED
                            : NamedTextColor.DARK_GREEN);

        Component end = Component.text(" ].___________", NamedTextColor.GOLD);

        return start
                .append(playerStr)
                .append(playerName)
                .append(end);
    }

    public static Component getPluginDescriptionHeader() {
        return Component.text("_______.[ ", NamedTextColor.GOLD)
                .append(Component.text("Plugin Version & Information", NamedTextColor.DARK_GREEN))
                .append(Component.text(" ].___________", NamedTextColor.GOLD));
    }

    public static List<Component> getPluginDescription(Plugin plugin) {
        List<Component> description = new ArrayList<>(6);

        description.add(getPluginDescriptionHeader());

        description.add(Component.text("Name: ", NamedTextColor.LIGHT_PURPLE)
                .append(Component.text(plugin.getDescription().getName(), NamedTextColor.AQUA)));

        description.add(Component.text("Version: ", NamedTextColor.LIGHT_PURPLE)
                .append(Component.text(plugin.getDescription().getVersion(), NamedTextColor.AQUA)));

        String rawWebsite = plugin.getDescription().getDescription();
        if (rawWebsite != null) {
            description.add(Component.text("Website: ", NamedTextColor.LIGHT_PURPLE)
                    .append(Component.text(rawWebsite, NamedTextColor.AQUA)
                            .clickEvent(ClickEvent.openUrl(rawWebsite))));
        }

        description.add(Component.text("Authors: ", NamedTextColor.LIGHT_PURPLE)
                .append(Component.text(
                        String.join(", ", plugin.getDescription().getAuthors()),
                        NamedTextColor.AQUA)));

        String rawDescription = plugin.getDescription().getDescription();
        if (rawDescription != null) {
            description.add(Component.text("Description: ", NamedTextColor.LIGHT_PURPLE)
                    .append(Component.text(rawDescription, NamedTextColor.YELLOW)));
        }

        return description;
    }

    public Component getCommandHelp(String command, RegisteredCommand<?> regCommand, CommandSender sender, int requireVampire) {
        String commandStr = "/v";
        boolean isSuggestion = true;

        command = command.toLowerCase();
        if (command.equals("set")) {
            isSuggestion = false;
        }

        if (command.equals("vampire") || command.equals("nosferatu") || command.equals("infection") || command.equals("food") || command.equals("health")) {
            commandStr += " set";
        }
        String fullCowl = commandStr + " " + command;
        ClickEvent clickEv = isSuggestion
                ? ClickEvent.suggestCommand(fullCowl)
                : ClickEvent.runCommand(fullCowl);
        HoverEvent<Component> hoverEv = HoverEvent.showText(suggestCommandTxt(fullCowl, isSuggestion));

        Component cmd = Component.text(commandStr, NamedTextColor.AQUA);

        Component params = Component.text(" " + command);
        if (sender instanceof Player) {
            VPlayer vPlayer = plugin.getVPlayer(((Player) sender));
            int vampireLevel = (vPlayer == null || vPlayer.isHuman()) ? 0 : (vPlayer.isNosferatu() ? 2 : 1);
            boolean hasRequiredVLevel = requireVampire <= vampireLevel;
            if (!command.equals("vampire") && !command.equals("nosferatu")) {
                Perm permission = Perm.getPermFromString(command);
                if (permission == null) {
                    params = params.color(NamedTextColor.DARK_RED);
                } else if (resUtil.hasPermission(sender, permission) && hasRequiredVLevel) {
                    params = params.color(NamedTextColor.AQUA);
                } else if (resUtil.hasPermission(sender, permission)) {
                    params = params.color(NamedTextColor.YELLOW);
                } else {
                    params = params.color(NamedTextColor.RED);
                }
            } else {
                Perm permission1 = Perm.getPermFromString(command + " on");
                Perm permission2 = Perm.getPermFromString(command + " off");
                if (permission1 == null || permission2 == null) {
                    params = params.color(NamedTextColor.DARK_RED);
                } else if (resUtil.hasPermission(sender, permission1) && resUtil.hasPermission(sender, permission2)) {
                    params = params.color(NamedTextColor.AQUA);
                } else if (resUtil.hasPermission(sender, permission1) || resUtil.hasPermission(sender, permission2)) {
                    params = params.color(NamedTextColor.YELLOW);
                } else {
                    params = params.color(NamedTextColor.RED);
                }
            }
        }

        Component extra;
        if (!command.equals("set")) {
            extra = Component.text(" " + regCommand.getSyntaxText(), NamedTextColor.DARK_AQUA)
                    .append(Component.text(" "))
                    .append(plugin.getMessage(sender,
                                    CommandMessageKeys.getProviderFromCommand(fullCowl.substring(3)))
                                    .color(NamedTextColor.YELLOW));
        }
        else {
            extra = Component.text(" set player attributes",NamedTextColor.YELLOW);
        }

        return cmd
                .append(params)
                .append(extra)
                .clickEvent(clickEv)
                .hoverEvent(hoverEv);
    }

    public static Component suggestCommandTxt(String command, boolean isSuggestion) {
        Component hovers = isSuggestion
                ? Component.text("Suggest: ")
                : Component.text("Command: ");
        hovers = hovers.color(NamedTextColor.LIGHT_PURPLE)
                .append(Component.text(command, NamedTextColor.AQUA));

        return hovers;
    }

    public Component getHelpHeader(CommandHelp help, int maxPages, String command, CommandSender sender) {
        Component start = Component.text("_______.[ ", NamedTextColor.GOLD);

        Component helpHeader = plugin.getMessage(sender,
                        CommandMessageKeys.COMMAND_HELP_HEADER,
                        "{command}", command)
                .color(NamedTextColor.DARK_GREEN);

        Component prev = Component.text("[<]");
        if (help.getPage() == 1) {
            prev = prev.color(NamedTextColor.GRAY);
        }
        else {
            String sComm = "/v ? " + (help.getPage() - 1);
            prev = prev.color(NamedTextColor.AQUA)
                    .clickEvent(ClickEvent.runCommand(sComm))
                    .hoverEvent(HoverEvent.showText(suggestCommandTxt(sComm, false)));
        }

        Component numPages = Component.text(
                " " + help.getPage() + "/" + maxPages + " ",
                NamedTextColor.GOLD);

        Component next = Component.text("[>]");
        if (help.getPage() == maxPages) {
            next = next.color(NamedTextColor.GRAY);
        }
        else {
            String sComm = "/v h " + (help.getPage() + 1);
            next = next.color(NamedTextColor.AQUA)
                    .clickEvent(ClickEvent.runCommand(sComm))
                    .hoverEvent(HoverEvent.showText(suggestCommandTxt(sComm, false)));
        }

        Component end = Component.text(" ].__________", NamedTextColor.GOLD);

        return start
                .append(helpHeader)
                .append(prev)
                .append(numPages)
                .append(next)
                .append(end);
    }
}
