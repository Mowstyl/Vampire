package com.clanjhoo.vampire.util;

import co.aikar.commands.CommandHelp;
import co.aikar.commands.RegisteredCommand;
import com.clanjhoo.vampire.entity.UPlayerColl;
import com.clanjhoo.vampire.keyproviders.CommandMessageKeys;
import com.clanjhoo.vampire.Perm;
import com.clanjhoo.vampire.VampireRevamp;
import com.clanjhoo.vampire.entity.UPlayer;
import com.clanjhoo.vampire.keyproviders.GrammarMessageKeys;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public class TextUtil {
    public static final Pattern PATTERN_NEWLINE = Pattern.compile("\\r?\\n");

    // -------------------------------------------- //
    // SPLIT AT LINEBREAKS
    // -------------------------------------------- //

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

    public static BaseComponent[] getPlayerInfoHeader(boolean isVampire, boolean isNosferatu, String playerName, CommandSender sender) {
        BaseComponent[] comps = new BaseComponent[4];

        comps[0] = new TextComponent("_______.[ ");
        comps[0].setColor(ChatColor.GOLD);

        String playerStr = VampireRevamp.getMessage(sender, GrammarMessageKeys.PLAYER);
        playerStr = playerStr.substring(0, 1).toUpperCase() + playerStr.substring(1);
        comps[1] = new TextComponent(playerStr + " ");
        if (!isVampire)
            comps[1].setColor(ChatColor.DARK_GREEN);
        else if (!isNosferatu)
            comps[1].setColor(ChatColor.RED);
        else
            comps[1].setColor(ChatColor.DARK_RED);

        comps[2] = new TextComponent(playerName);

        comps[3] = new TextComponent(" ].___________");
        comps[3].setColor(ChatColor.GOLD);

        return comps;
    }

    public static BaseComponent[] getPluginDescriptionHeader() {
        BaseComponent[] comps = new BaseComponent[3];

        comps[0] = new TextComponent("_______.[ ");
        comps[0].setColor(ChatColor.GOLD);

        comps[1] = new TextComponent("Plugin Version & Information");
        comps[1].setColor(ChatColor.DARK_GREEN);

        comps[2] = new TextComponent(" ].___________");
        comps[2].setColor(ChatColor.GOLD);

        return comps;
    }

    public static List<BaseComponent[]> getPluginDescription(Plugin plugin) {
        List<BaseComponent[]> comps = new ArrayList<>();
        comps.add(getPluginDescriptionHeader());

        BaseComponent[] name = new BaseComponent[2];
        name[0] = new TextComponent("Name: ");
        name[0].setColor(ChatColor.LIGHT_PURPLE);
        name[1] = new TextComponent(plugin.getDescription().getName());
        name[1].setColor(ChatColor.AQUA);
        comps.add(name);

        BaseComponent[] version = new BaseComponent[2];
        version[0] = new TextComponent("Version: ");
        version[0].setColor(ChatColor.LIGHT_PURPLE);
        version[1] = new TextComponent(plugin.getDescription().getVersion());
        version[1].setColor(ChatColor.AQUA);
        comps.add(version);

        BaseComponent[] website = new BaseComponent[2];
        website[0] = new TextComponent("Website: ");
        website[0].setColor(ChatColor.LIGHT_PURPLE);
        website[1] = new TextComponent(plugin.getDescription().getWebsite());
        website[1].setColor(ChatColor.AQUA);
        website[1].setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, plugin.getDescription().getWebsite()));
        comps.add(website);

        BaseComponent[] authors = new BaseComponent[2];
        authors[0] = new TextComponent("Authors: ");
        authors[0].setColor(ChatColor.LIGHT_PURPLE);
        authors[1] = new TextComponent(String.join(", ", plugin.getDescription().getAuthors()));
        authors[1].setColor(ChatColor.AQUA);
        comps.add(authors);

        BaseComponent[] description = new BaseComponent[2];
        description[0] = new TextComponent("Description: ");
        description[0].setColor(ChatColor.LIGHT_PURPLE);
        description[1] = new TextComponent(plugin.getDescription().getDescription());
        description[1].setColor(ChatColor.YELLOW);
        comps.add(description);

        return comps;
    }

    public static BaseComponent[] getCommandHelp(String command, RegisteredCommand regCommand, CommandSender sender, int requireVampire) {
        BaseComponent[] comps;
        String commandStr = "/v";
        boolean isSuggestion = true;
        int size = 4;

        command = command.toLowerCase();
        if (command.equals("set")) {
            isSuggestion = false;
            size = 3;
        }
        comps = new BaseComponent[size];

        if (command.equals("vampire") || command.equals("nosferatu") || command.equals("infection") || command.equals("food") || command.equals("health")) {
            commandStr += " set";
        }
        String fullCowl = commandStr + " " + command;
        ClickEvent clickEv = new ClickEvent(isSuggestion ? ClickEvent.Action.SUGGEST_COMMAND : ClickEvent.Action.RUN_COMMAND, fullCowl);
        HoverEvent hoverEv = new HoverEvent(HoverEvent.Action.SHOW_TEXT, suggestCommandTxt(fullCowl, isSuggestion));

        comps[0] = new TextComponent(commandStr);
        comps[0].setColor(ChatColor.AQUA);
        comps[0].setClickEvent(clickEv);
        comps[0].setHoverEvent(hoverEv);

        comps[1] = new TextComponent(" " + command);
        if (sender instanceof Player) {
            UPlayer uplayer = UPlayerColl.get(((Player) sender).getUniqueId());
            boolean hasRequiredVLevel = requireVampire == 0 || uplayer.isNosferatu() || (uplayer.isVampire() && requireVampire == 1);
            if (!command.equals("vampire") && !command.equals("nosferatu")) {
                Perm permission = Perm.getPermFromString(command);
                if (permission == null) {
                    comps[1].setColor(ChatColor.DARK_RED);
                } else if (permission.has(sender) && hasRequiredVLevel) {
                    comps[1].setColor(ChatColor.AQUA);
                } else if (permission.has(sender)) {
                    comps[1].setColor(ChatColor.YELLOW);
                } else {
                    comps[1].setColor(ChatColor.RED);
                }
            } else {
                Perm permission1 = Perm.getPermFromString(command + " on");
                Perm permission2 = Perm.getPermFromString(command + " off");
                if (permission1 == null || permission2 == null) {
                    comps[1].setColor(ChatColor.DARK_RED);
                } else if (permission1.has(sender) && permission2.has(sender)) {
                    comps[1].setColor(ChatColor.AQUA);
                } else if (permission1.has(sender) || permission2.has(sender)) {
                    comps[1].setColor(ChatColor.YELLOW);
                } else {
                    comps[1].setColor(ChatColor.RED);
                }
            }
        }
        comps[1].setClickEvent(clickEv);
        comps[1].setHoverEvent(hoverEv);

        if (!command.equals("set")) {
            comps[2] = new TextComponent(" " + regCommand.getSyntaxText());
            comps[2].setColor(ChatColor.DARK_AQUA);

            comps[3] = new TextComponent(" " + VampireRevamp.getMessage(sender,
                    CommandMessageKeys.getProviderFromCommand(fullCowl.substring(3))));
            comps[3].setColor(ChatColor.YELLOW);
            comps[3].setClickEvent(clickEv);
            comps[3].setHoverEvent(hoverEv);
        }
        else {
            comps[2] = new TextComponent(" set player attributes");
            comps[2].setColor(ChatColor.YELLOW);
        }
        comps[2].setClickEvent(clickEv);
        comps[2].setHoverEvent(hoverEv);

        return comps;
    }

    public static BaseComponent[] suggestCommandTxt(String command, boolean isSuggestion) {
        BaseComponent[] hovers = new BaseComponent[2];
        if (isSuggestion)
            hovers[0] = new TextComponent("Suggest: ");
        else
            hovers[0] = new TextComponent("Command: ");
        hovers[0].setColor(ChatColor.LIGHT_PURPLE);
        hovers[1] = new TextComponent(command);
        hovers[1].setColor(ChatColor.AQUA);
        return hovers;
    }

    public static BaseComponent[] getHelpHeader(CommandHelp help, int maxPages, String command, CommandSender sender) {
        BaseComponent[] comps = new BaseComponent[6];

        comps[0] = new TextComponent("_______.[ ");
        comps[0].setColor(ChatColor.GOLD);

        String helpHeader = VampireRevamp.getMessage(sender, CommandMessageKeys.COMMAND_HELP_HEADER);
        helpHeader = helpHeader.replace("{command}", command);
        comps[1] = new TextComponent(helpHeader);
        comps[1].setColor(ChatColor.DARK_GREEN);

        comps[2] = new TextComponent("[<]");
        if (help.getPage() == 1) {
            comps[2].setColor(ChatColor.GRAY);
        }
        else {
            comps[2].setColor(ChatColor.AQUA);
            String sComm = "/v ? " + (help.getPage() - 1);
            comps[2].setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, sComm));
            comps[2].setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, suggestCommandTxt(sComm, false)));
        }

        comps[3] = new TextComponent(" " + help.getPage() + "/" + maxPages + " ");
        comps[3].setColor(ChatColor.GOLD);

        comps[4] = new TextComponent("[>]");
        if (help.getPage() == maxPages) {
            comps[4].setColor(ChatColor.GRAY);
        }
        else {
            comps[4].setColor(ChatColor.AQUA);
            String sComm = "/v h " + (help.getPage() + 1);
            comps[4].setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, sComm));
            comps[4].setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, suggestCommandTxt(sComm, false)));
        }

        comps[5] = new TextComponent(" ].__________");
        comps[5].setColor(ChatColor.GOLD);

        return comps;
    }
}
