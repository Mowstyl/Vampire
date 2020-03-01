package com.clanjhoo.vampire.util;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class IdUtil {
    private static Map<String, CommandSender> registryIdToSender = new ConcurrentHashMap<>();

    // -------------------------------------------- //
    // CONSTANTS
    // -------------------------------------------- //
    // Taken directly from my own imagination!

    public final static String IDPREFIX = "@";
    public final static String CONSOLE_ID = IDPREFIX+"console";

    // -------------------------------------------- //
    // IS VALID PLAYER NAME
    // -------------------------------------------- //

    // The regex for a valid minecraft player name.
    public final static Pattern PATTERN_PLAYER_NAME = Pattern.compile("^[a-zA-Z0-9_]{2,16}$");

    public static boolean isValidPlayerName(String string)
    {
        return PATTERN_PLAYER_NAME.matcher(string).matches();
    }

    // -------------------------------------------- //
    // UUID
    // -------------------------------------------- //

    public static UUID asUuid(String string)
    {
        // Null
        if (string == null) throw new NullPointerException("string");

        // Avoid Exception
        if (string.length() != 36) return null;

        // Try
        try
        {
            return UUID.fromString(string);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public static boolean isUuid(String string)
    {
        return asUuid(string) != null;
    }

    // -------------------------------------------- //
    // ID TYPE CHECKING
    // -------------------------------------------- //

    public static boolean isPlayerId(String string)
    {
        // NOTE: Assuming all custom ids isn't a valid player name or id.
        return IdUtil.isValidPlayerName(string) || IdUtil.isUuid(string);
    }

    public static boolean isPlayer(Object senderObject)
    {
        String id = IdUtil.getId(senderObject);
        if (id == null) return false;
        return isPlayerId(id);
    }

    public static boolean isConsoleId(String string)
    {
        return CONSOLE_ID.equals(string);
    }

    public static boolean isConsole(Object senderObject)
    {
        String id = IdUtil.getId(senderObject);
        if (id == null) return false;
        return isConsoleId(id);
    }

    // -------------------------------------------- //
    // GET AS
    // -------------------------------------------- //

    public static CommandSender getAsSender(Object object)
    {
        if (!(object instanceof CommandSender)) return null;
        return (CommandSender) object;
    }

    public static Player getAsPlayer(Object object)
    {
        if (!(object instanceof Player)) return null;
        return (Player) object;
    }

    public static ConsoleCommandSender getAsConsole(Object object)
    {
        if (!(object instanceof ConsoleCommandSender)) return null;
        return (ConsoleCommandSender) object;
    }

    public static ConsoleCommandSender getConsole()
    {
        return Bukkit.getConsoleSender();
    }

    public static Player getPlayer(Object senderObject)
    {
        return getAsPlayer(getSender(senderObject));
    }

    public static CommandSender getSender(Object senderObject)
    {
        // Null Return
        if (senderObject == null) throw new NullPointerException("senderObject");

        // Already Done
        if (senderObject instanceof CommandSender) return (CommandSender) senderObject;

        // Console Type
        // Handled at "Already Done"

        // Console Id/Name
        if (CONSOLE_ID.equals(senderObject)) return getConsole();

        // Player
        // Handled at "Already Done"

        // CommandSender
        // Handled at "Already Done"

        // OfflinePlayer
        if (senderObject instanceof OfflinePlayer)
        {
            return getSender(((OfflinePlayer) senderObject).getUniqueId());
        }

        // UUID
        if (senderObject instanceof UUID)
        {
            // Attempt finding player
            UUID uuid = (UUID)senderObject;
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) return player;

            // Otherwise assume registered sender
            return registryIdToSender.get(uuid.toString());
        }

        // String
        if (senderObject instanceof String)
        {
            // Recurse as UUID
            String string = (String)senderObject;
            UUID uuid = IdUtil.asUuid(string);
            if (uuid != null) return getSender(uuid);

            // Registry
            CommandSender sender = registryIdToSender.get(string);
            if (sender != null) return sender;

            // Bukkit API
            return Bukkit.getPlayerExact(string);
        }

        // Return Null
        return null;
    }

    public static UUID getUuid(Object senderObject)
    {
        // Null Return
        if (senderObject == null) throw new NullPointerException("senderObject");

        // Already Done
        if (senderObject instanceof UUID) return (UUID)senderObject;

        // Console Type
        if (senderObject instanceof ConsoleCommandSender) return null;

        // Console Id/Name
        if (CONSOLE_ID.equals(senderObject)) return null;

        // Player
        if (senderObject instanceof Player) return ((Player)senderObject).getUniqueId();

        // CommandSender
        if (senderObject instanceof CommandSender)
        {
            CommandSender sender = (CommandSender)senderObject;
            String id = sender.getName();
            return IdUtil.asUuid(id);
        }

        // OfflinePlayer
        if (senderObject instanceof OfflinePlayer) return ((OfflinePlayer) senderObject).getUniqueId();

        // UUID
        // Handled at "Already Done"

        // String
        if (senderObject instanceof String)
        {
            // Is UUID
            String string = (String)senderObject;
            UUID uuid = IdUtil.asUuid(string);
            if (uuid != null) return uuid;

            // Is Name
            // Handled at "Data"
        }

        // Return Null
        return null;
    }

    // This method always returns null or a lower case String.
    public static String getId(Object senderObject)
    {
        // Null Return
        if (senderObject == null) throw new NullPointerException("senderObject");

        // Already Done
        if (senderObject instanceof String && IdUtil.isUuid((String)senderObject)) return (String)senderObject;

        // Console
        // Handled at "Command Sender"

        // Console Id/Name
        if (CONSOLE_ID.equals(senderObject)) return CONSOLE_ID;

        // Player
        // Handled at "Command Sender"

        // Command Sender
        if (senderObject instanceof CommandSender) return getIdFromSender((CommandSender) senderObject);

        // OfflinePlayer
        // Handled at "Data"

        // UUID
        if (senderObject instanceof UUID) return getIdFromUuid((UUID) senderObject);

        // Return Null
        return null;
    }

    public static String getIdFromSender(CommandSender sender)
    {
        if (sender instanceof Player)
        {
            try
            {
                return ((Player) sender).getUniqueId().toString();
            }
            catch (UnsupportedOperationException e)
            {
                // ProtocolLib: The method getUniqueId is not supported for temporary players.
                return null;
            }
        }
        if (sender instanceof ConsoleCommandSender) return CONSOLE_ID;

        // We blacklist all entities other than players.
        // This is because all entities are CommandSenders since Minecraft 1.9.
        // We do not want Arrows with id "arrow" in the database.
        if (sender instanceof Entity) return null;

        return sender.getName().toLowerCase();
    }

    public static String getIdFromUuid(UUID uuid)
    {
        return uuid.toString();
    }

    public static String getName(Object senderObject)
    {
        // Null Return
        if (senderObject == null) throw new NullPointerException("senderObject");

        // Already Done
        // Handled at "Data" (not applicable - names can look differently)

        // Console
        // Handled at "Command Sender"

        // Console Id/Name
        if (CONSOLE_ID.equals(senderObject)) return CONSOLE_ID;

        // Player
        // Handled at "Command Sender"

        // CommandSender
        if (senderObject instanceof CommandSender) return getNameFromSender((CommandSender)senderObject);

        // UUID
        // Handled at "Data".

        // String
        // Handled at "Data"
        // Note: We try to use stored data to fix the capitalization!

        // TryFix Behavior
        // Note: We try to use stored data to fix the capitalization!
        if (senderObject instanceof String) return (String)senderObject;

        // Return Null
        return null;
    }

    public static OfflinePlayer getOfflinePlayer(Object senderObject)
    {
        // Null Return
        if (senderObject == null) throw new NullPointerException("senderObject");

        // Already done
        if (senderObject instanceof OfflinePlayer) return (OfflinePlayer) senderObject;

        //
        UUID uuid = getUuid(senderObject);
        if (uuid == null) return null;

        return Bukkit.getOfflinePlayer(uuid);
    }

    public static String getNameFromSender(CommandSender sender)
    {
        if (sender instanceof ConsoleCommandSender) return CONSOLE_ID;
        return sender.getName();
    }
}
