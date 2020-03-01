package com.clanjhoo.vampire.util;

import co.aikar.commands.CommandHelp;
import co.aikar.commands.RegisteredCommand;
import com.clanjhoo.vampire.Perm;
import com.clanjhoo.vampire.entity.UPlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtil {
    public static final Map<String, String> parseReplacements;
    public static final Pattern parsePattern;

    public static final Pattern PATTERN_NEWLINE = Pattern.compile("\\r?\\n");

    static {
        // Create the parse replacements map
        parseReplacements = new HashMap<>();

        // Color by name
        parseReplacements.put("<empty>", "");
        parseReplacements.put("<black>", "\u00A70");
        parseReplacements.put("<navy>", "\u00A71");
        parseReplacements.put("<green>", "\u00A72");
        parseReplacements.put("<teal>", "\u00A73");
        parseReplacements.put("<red>", "\u00A74");
        parseReplacements.put("<purple>", "\u00A75");
        parseReplacements.put("<gold>", "\u00A76");
        parseReplacements.put("<orange>", "\u00A76");
        parseReplacements.put("<silver>", "\u00A77");
        parseReplacements.put("<gray>", "\u00A78");
        parseReplacements.put("<grey>", "\u00A78");
        parseReplacements.put("<blue>", "\u00A79");
        parseReplacements.put("<lime>", "\u00A7a");
        parseReplacements.put("<aqua>", "\u00A7b");
        parseReplacements.put("<rose>", "\u00A7c");
        parseReplacements.put("<pink>", "\u00A7d");
        parseReplacements.put("<yellow>", "\u00A7e");
        parseReplacements.put("<white>", "\u00A7f");
        parseReplacements.put("<magic>", "\u00A7k");
        parseReplacements.put("<bold>", "\u00A7l");
        parseReplacements.put("<strong>", "\u00A7l");
        parseReplacements.put("<strike>", "\u00A7m");
        parseReplacements.put("<strikethrough>", "\u00A7m");
        parseReplacements.put("<under>", "\u00A7n");
        parseReplacements.put("<underline>", "\u00A7n");
        parseReplacements.put("<italic>", "\u00A7o");
        parseReplacements.put("<em>", "\u00A7o");
        parseReplacements.put("<reset>", "\u00A7r");

        // Color by semantic functionality
        parseReplacements.put("<l>", "\u00A72");
        parseReplacements.put("<logo>", "\u00A72");
        parseReplacements.put("<a>", "\u00A76");
        parseReplacements.put("<art>", "\u00A76");
        parseReplacements.put("<n>", "\u00A77");
        parseReplacements.put("<notice>", "\u00A77");
        parseReplacements.put("<i>", "\u00A7e");
        parseReplacements.put("<info>", "\u00A7e");
        parseReplacements.put("<g>", "\u00A7a");
        parseReplacements.put("<good>", "\u00A7a");
        parseReplacements.put("<b>", "\u00A7c");
        parseReplacements.put("<bad>", "\u00A7c");

        parseReplacements.put("<k>", "\u00A7b");
        parseReplacements.put("<key>", "\u00A7b");

        parseReplacements.put("<v>", "\u00A7d");
        parseReplacements.put("<value>", "\u00A7d");
        parseReplacements.put("<h>", "\u00A7d");
        parseReplacements.put("<highlight>", "\u00A7d");

        parseReplacements.put("<c>", "\u00A7b");
        parseReplacements.put("<command>", "\u00A7b");
        parseReplacements.put("<p>", "\u00A73");
        parseReplacements.put("<parameter>", "\u00A73");
        parseReplacements.put("&&", "&");
        parseReplacements.put("§§", "§");

        // Color by number/char
        for (int i = 48; i <= 122; i++) {
            char c = (char) i;
            parseReplacements.put("§" + c, "\u00A7" + c);
            parseReplacements.put("&" + c, "\u00A7" + c);
            if (i == 57) i = 96;
        }

        // Build the parse pattern and compile it
        StringBuilder patternStringBuilder = new StringBuilder();
        for (String find : parseReplacements.keySet()) {
            patternStringBuilder.append('(');
            patternStringBuilder.append(Pattern.quote(find));
            patternStringBuilder.append(")|");
        }
        String patternString = patternStringBuilder.toString();
        patternString = patternString.substring(0, patternString.length() - 1); // Remove the last |
        parsePattern = Pattern.compile(patternString);
    }

    // -------------------------------------------- //
    // PARSE
    // -------------------------------------------- //

    public static String parse(@Nonnull String string) {
        StringBuffer ret = new StringBuffer();
        Matcher matcher = parsePattern.matcher(string);

        while (matcher.find()) {
            matcher.appendReplacement(ret, parseReplacements.get(matcher.group(0)));
        }
        matcher.appendTail(ret);

        return ret.toString();
    }

    public static String parse(String string, Object... args) {
        return String.format(parse(string), args);
    }

    public static List<String> parse(Collection<String> strings) {
        List<String> ret = new ArrayList<>(strings.size());
        for (String string : strings) {
            ret.add(parse(string));
        }
        return ret;
    }

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

    // -------------------------------------------- //
    // Parse and Wrap combo
    // -------------------------------------------- //

    public static ArrayList<String> parseWrap(final String string) {
        return wrap(parse(string));
    }

    public static ArrayList<String> parseWrap(final Collection<String> strings) {
        return wrap(parse(strings));
    }

    public static String implode(final Object[] list, final String glue, final String format) {
        StringBuilder ret = new StringBuilder();

        for (int i = 0; i < list.length; i++) {
            Object item = list[i];
            String str = (item == null ? "NULL" : item.toString());

            if (i != 0) {
                ret.append(glue);
            }
            if (format != null) {
                ret.append(String.format(format, str));
            } else {
                ret.append(str);
            }
        }

        return ret.toString();
    }

    public static String implode(final Object[] list, final String glue) {
        return implode(list, glue, null);
    }

    public static String implode(final Collection<?> coll, final String glue, final String format) {
        return implode(coll.toArray(new Object[0]), glue, format);
    }

    public static String implode(final Collection<?> coll, final String glue) {
        return implode(coll, glue, null);
    }

    public static String implodeCommaAndDot(final Collection<?> objects, final String format, final String comma, final String and, final String dot) {
        if (objects.size() == 0) return "";
        if (objects.size() == 1) {
            return implode(objects, comma, format);
        }

        List<Object> ourObjects = new ArrayList<>(objects);

        String lastItem = ourObjects.get(ourObjects.size() - 1).toString();
        String nextToLastItem = ourObjects.get(ourObjects.size() - 2).toString();
        if (format != null) {
            lastItem = String.format(format, lastItem);
            nextToLastItem = String.format(format, nextToLastItem);
        }
        String merge = nextToLastItem + and + lastItem;
        ourObjects.set(ourObjects.size() - 2, merge);
        ourObjects.remove(ourObjects.size() - 1);

        return implode(ourObjects, comma, format) + dot;
    }

    public static String implodeCommaAndDot(final Collection<?> objects, final String comma, final String and, final String dot) {
        return implodeCommaAndDot(objects, null, comma, and, dot);
    }

    public static String implodeCommaAnd(final Collection<?> objects, final String comma, final String and) {
        return implodeCommaAndDot(objects, comma, and, "");
    }

    public static String implodeCommaAndDot(final Collection<?> objects, final String color) {
        return implodeCommaAndDot(objects, color + ", ", color + " and ", color + ".");
    }

    public static String implodeCommaAnd(final Collection<?> objects, final String color) {
        return implodeCommaAndDot(objects, color + ", ", color + " and ", "");
    }

    public static String implodeCommaAndDot(final Collection<?> objects) {
        return implodeCommaAndDot(objects, "");
    }

    public static String implodeCommaAnd(final Collection<?> objects) {
        return implodeCommaAnd(objects, "");
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

    public static BaseComponent[] getCommandHelp(String command, RegisteredCommand regCommand, CommandSender sender, List<String> aliases, int requireVampire) {
        BaseComponent[] comps;
        String commandStr = "/v";
        boolean isSuggestion = true;

        command = command.toLowerCase();
        if (!command.equals("set")) {
            comps = new BaseComponent[4];
        }
        else {
            comps = new BaseComponent[3];
            isSuggestion = false;
        }

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

        comps[1] = new TextComponent(" " + String.join(",", aliases));
        if (sender instanceof Player) {
            UPlayer uplayer = UPlayer.get(((Player) sender).getUniqueId());
            boolean hasRequiredVLevel = requireVampire == 0 || (uplayer != null && (uplayer.isNosferatu() || (uplayer.isVampire() && requireVampire == 1)));
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

            comps[3] = new TextComponent(" " + regCommand.getHelpText());
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

    public static BaseComponent[] getHelpHeader(CommandHelp help, int maxPages, String command) {
        BaseComponent[] comps = new BaseComponent[6];

        comps[0] = new TextComponent("_______.[ ");
        comps[0].setColor(ChatColor.GOLD);

        comps[1] = new TextComponent("Help for command \"" + command + "\" ");
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
