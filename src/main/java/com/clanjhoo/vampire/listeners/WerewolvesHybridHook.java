package com.clanjhoo.vampire.listeners;

import com.clanjhoo.vampire.VampireRevamp;
import com.clanjhoo.vampire.entity.VPlayer;
import com.clanjhoo.vampire.event.InfectionChangeEvent;
import com.clanjhoo.vampire.event.VampireTypeChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import us.rfsmassacre.Werewolf.Events.WerewolfInfectionEvent;
import us.rfsmassacre.Werewolf.WerewolfAPI;

import java.util.logging.Level;


public class WerewolvesHybridHook implements Listener {
    private boolean initialized = false;
    private final VampireRevamp plugin;


    public WerewolvesHybridHook(VampireRevamp plugin) {
        this.plugin = plugin;
        plugin.log(Level.INFO, "Enabled Werewolves hybrid compatibility!");
    }

    private boolean checkWerewolf() {
        if (initialized)
            return true;
        boolean isWerewolfEnabled = Bukkit.getPluginManager().isPluginEnabled("Werewolf");
        if (!isWerewolfEnabled) {
            plugin.log(Level.WARNING, "Werewolf plugin has been disabled. Disabled hybrid prevention");
            HandlerList.unregisterAll(this);
        }
        initialized = true;
        return isWerewolfEnabled;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInfectionChange(InfectionChangeEvent event) {
        if (!checkWerewolf())
            return;

        if (!plugin.getVampireConfig().compatibility.preventWerewolfHybrids)
            return;

        VPlayer uplayer = event.getVPlayer();
        if (uplayer == null) {
            event.setCancelled(true);
            return;
        }

        Player player = uplayer.getPlayer();
        if (player == null) {
            event.setCancelled(true);
            return;
        }

        plugin.debugLog(Level.INFO, "Infection: " + event.getInfection());

        if (WerewolfAPI.isWerewolf(player) && event.getInfection() != 0) {
            plugin.debugLog(Level.INFO, "Cancelled!");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onTypeChange(VampireTypeChangeEvent event) {
        if (!checkWerewolf())
            return;

        if (!plugin.getVampireConfig().compatibility.preventWerewolfHybrids)
            return;

        VPlayer uplayer = event.getVPlayer();
        if (uplayer == null) {
            event.setCancelled(true);
            return;
        }

        Player player = uplayer.getPlayer();
        if (player == null) {
            event.setCancelled(true);
            return;
        }

        plugin.debugLog(Level.INFO, "Vampire: " + event.isVampire());

        if (WerewolfAPI.isWerewolf(player) && event.isVampire()) {
            plugin.debugLog(Level.INFO, "Cancelled!");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onWerewolfInfection(WerewolfInfectionEvent event) {
        if (!checkWerewolf())
            return;

        if (!plugin.getVampireConfig().compatibility.preventWerewolfHybrids)
            return;

        Player player = event.getPlayer();
        if (player == null) {
            event.setCancelled(true);
            return;
        }

        VPlayer vPlayer = plugin.getVPlayer(player);
        if (vPlayer == null) {
            player.sendMessage("You seem resistant to werewolf infection somehow... Please contact an admin if this error persists.");
            plugin.log(Level.WARNING, "Couldn't get data of player " + player.getName());
            event.setCancelled(true);
        } else if (vPlayer.isUnhealthy()) {
            event.setCancelled(true);
        }
    }
}
