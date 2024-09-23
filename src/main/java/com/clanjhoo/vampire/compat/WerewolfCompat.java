package com.clanjhoo.vampire.compat;

import com.clanjhoo.vampire.VampireRevamp;
import com.clanjhoo.vampire.listeners.WerewolvesHybridHook;
import com.clanjhoo.vampire.listeners.WerewolvesSilverHook;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.logging.Level;

public final class WerewolfCompat {
    private boolean isHybridProtEnabled;
    private boolean isSilverEnabled;
    private WerewolvesHybridHook hybridListener = null;
    private WerewolvesSilverHook silverListener = null;
    private Method isHumanPlayer = null;
    private Method isWerewolfPlayer = null;
    private Method isAlphaPlayer = null;
    private Method isWerewolfUUID = null;
    private Method isAlphaUUID = null;
    private Method getWerewolfItemID = null;
    private final VampireRevamp plugin;


    public WerewolfCompat(VampireRevamp plugin) {
        this.plugin = plugin;
        boolean hybridProtEnabled = plugin.getVampireConfig().compatibility.preventWerewolfHybrids;
        boolean silverEnabled = plugin.getVampireConfig().compatibility.useWerewolfSilverSword;
        if (hybridProtEnabled) {
            try {
                Class<?> wwAPI = Class.forName("us.rfsmassacre.Werewolf.WerewolfAPI");
                isHumanPlayer = wwAPI.getMethod("isHuman", Player.class);
                isWerewolfPlayer = wwAPI.getMethod("isWerewolf", Player.class);
                isAlphaPlayer = wwAPI.getMethod("isAlpha", Player.class);
                isWerewolfUUID = wwAPI.getMethod("isWerewolf", UUID.class);
                isAlphaUUID = wwAPI.getMethod("isAlpha", UUID.class);
                getWerewolfItemID = wwAPI.getMethod("getWerewolfItemID", ItemStack.class);
            }
            catch (ClassNotFoundException | NoSuchMethodException ex) {
                hybridProtEnabled = false;
                silverEnabled = false;
            }
        }

        isHybridProtEnabled = hybridProtEnabled;

        isSilverEnabled = silverEnabled && getWerewolfItemID != null;

        if (isHybridProtEnabled || isSilverEnabled) {
            plugin.log(Level.INFO, "Werewolves found! Enabling werewolves compatibility...");

            if (isHybridProtEnabled) {
                plugin.log(Level.INFO, "Enabling hybrid prevention...");
                hybridListener = new WerewolvesHybridHook(plugin);
                Bukkit.getPluginManager().registerEvents(hybridListener, plugin);
            }

            if (isSilverEnabled) {
                plugin.log(Level.INFO, "Enabling silver detection...");
                silverListener = new WerewolvesSilverHook(plugin);
                Bukkit.getPluginManager().registerEvents(silverListener, plugin);
            }

            plugin.log(Level.INFO, "Done!");
        }
    }

    private void disableWWHybridProt() {
        plugin.log(Level.INFO, "Disabling Werewolves hybrid protection hook...");
        isHybridProtEnabled = false;
        HandlerList.unregisterAll(hybridListener);
        plugin.log(Level.INFO, "Done!");
    }

    private void disableSilverHook() {
        plugin.log(Level.INFO, "Disabling Werewolves silver detection hook...");
        isSilverEnabled = false;
        HandlerList.unregisterAll(silverListener);
        plugin.log(Level.INFO, "Done!");
    }

    public void disable() {
        plugin.log(Level.INFO, "Disabling werewolves compatibility!");
        disableWWHybridProt();
        disableSilverHook();
    }

    public boolean isSilverSword(ItemStack item) {
        boolean result = false;
        if (isSilverEnabled) {
            try {
                if (item != null && item.getType() != Material.AIR) {
                    String itemID = (String) getWerewolfItemID.invoke(null, item);
                    result = itemID != null && itemID.equals("SILVER_SWORD");
                }
            } catch (IllegalAccessException | InvocationTargetException ex) {
                plugin.log(Level.WARNING, "Error while calling WerewolfAPI!");
                ex.printStackTrace();
                disableSilverHook();
            }
        }
        return result;
    }

    public boolean isHuman(Player var0) {
        boolean result = true;
        if (isHybridProtEnabled) {
            try {
                result = (boolean) isHumanPlayer.invoke(null, var0);
            } catch (IllegalAccessException | InvocationTargetException ex) {
                plugin.log(Level.WARNING, "Error while calling WerewolfAPI!");
                ex.printStackTrace();
                disableWWHybridProt();
            }
        }
        return result;
    }

    public boolean isWerewolf(Player var0) {
        boolean result = false;
        if (isHybridProtEnabled) {
            try {
                result = (boolean) isWerewolfPlayer.invoke(null, var0);
            } catch (IllegalAccessException | InvocationTargetException ex) {
                plugin.log(Level.WARNING, "Error while calling WerewolfAPI!");
                ex.printStackTrace();
                disableWWHybridProt();
            }
        }
        return result;
    }

    public boolean isAlpha(Player var0) {
        boolean result = false;
        if (isHybridProtEnabled) {
            try {
                result = (boolean) isAlphaPlayer.invoke(null, var0);
            } catch (IllegalAccessException | InvocationTargetException ex) {
                plugin.log(Level.WARNING, "Error while calling WerewolfAPI!");
                ex.printStackTrace();
                disableWWHybridProt();
            }
        }
        return result;
    }

    public boolean isWerewolf(UUID var0) {
        boolean result = false;
        if (isHybridProtEnabled) {
            try {
                result = (boolean) isWerewolfUUID.invoke(null, var0);
            } catch (IllegalAccessException | InvocationTargetException ex) {
                plugin.log(Level.WARNING, "Error while calling WerewolfAPI!");
                ex.printStackTrace();
                disableWWHybridProt();
            }
        }
        return result;
    }

    public boolean isAlpha(UUID var0) {
        boolean result = false;
        if (isHybridProtEnabled) {
            try {
                result = (boolean) isAlphaUUID.invoke(null, var0);
            } catch (IllegalAccessException | InvocationTargetException ex) {
                plugin.log(Level.WARNING, "Error while calling WerewolfAPI!");
                ex.printStackTrace();
                disableWWHybridProt();
            }
        }
        return result;
    }
}
