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

    private boolean initialize() {
        boolean isWerewolfEnabled = Bukkit.getPluginManager().isPluginEnabled("Werewolf");
        if (!isWerewolfEnabled)
            HandlerList.unregisterAll(this);
        initialized = true;
        return isWerewolfEnabled;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInfectionChange(InfectionChangeEvent event) {
        if (!initialized) {
            if (!initialize())
                return;
            VampireRevamp.log(Level.INFO, "Enabled Werewolves hybrid compatibility!");
        }
        if (!VampireRevamp.getVampireConfig().compatibility.preventWerewolfHybrids)
            return;

        VPlayer uplayer = event.getUplayer();
        if (uplayer == null) {
            event.setCancelled(true);
            return;
        }

        Player player = uplayer.getPlayer();
        if (player == null) {
            event.setCancelled(true);
            return;
        }

        VampireRevamp.debugLog(Level.INFO, "Infection: " + event.getInfection());

        if (WerewolfAPI.isWerewolf(player) && event.getInfection() != 0) {
            VampireRevamp.debugLog(Level.INFO, "Cancelled!");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onTypeChange(VampireTypeChangeEvent event) {
        if (!initialized) {
            if (!initialize())
                return;
            VampireRevamp.log(Level.INFO, "Enabled Werewolves hybrid compatibility!");
        }
        if (!VampireRevamp.getVampireConfig().compatibility.preventWerewolfHybrids)
            return;

        VPlayer uplayer = event.getUplayer();
        if (uplayer == null) {
            event.setCancelled(true);
            return;
        }

        Player player = uplayer.getPlayer();
        if (player == null) {
            event.setCancelled(true);
            return;
        }

        VampireRevamp.debugLog(Level.INFO, "Vampire: " + event.isVampire());

        if (WerewolfAPI.isWerewolf(player) && event.isVampire()) {
            VampireRevamp.debugLog(Level.INFO, "Cancelled!");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onWerewolfInfection(WerewolfInfectionEvent event) {
        if (!initialized) {
            if (!initialize())
                return;
            VampireRevamp.log(Level.INFO, "Enabled Werewolves hybrid compatibility!");
        }
        if (!VampireRevamp.getVampireConfig().compatibility.preventWerewolfHybrids)
            return;

        Player player = event.getPlayer();
        if (player == null) {
            event.setCancelled(true);
            return;
        }

        try {
            VPlayer uplayer = VampireRevamp.getVPlayerManager().tryGetDataNow(player.getUniqueId());
            if (uplayer.isUnhealthy()) {
                event.setCancelled(true);
            }
        }
        catch (AssertionError ex) {
            player.sendMessage("You seem resistant to werewolf infection somehow... Please contact an admin if this error persists.");
            VampireRevamp.log(Level.WARNING, "Couldn't get data of player " + player.getName());
            event.setCancelled(true);
        }
    }
}
