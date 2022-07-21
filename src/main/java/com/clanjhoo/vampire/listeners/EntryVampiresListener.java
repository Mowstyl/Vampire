package com.clanjhoo.vampire.listeners;

import com.clanjhoo.vampire.VampireAPI;
import com.clanjhoo.vampire.VampireRevamp;
import com.clanjhoo.vampire.compat.WorldGuardCompat;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;


public class EntryVampiresListener implements Listener {
    @EventHandler(priority = EventPriority.NORMAL)
    public void onVampireEntry(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        // Haven't moved from the block the player was standing
        if (from.toBlockLocation().toVector().subtract(to.toBlockLocation().toVector()).lengthSquared() == 0) {
            return;
        }
        WorldGuardCompat wgc = VampireRevamp.getWorldGuardCompat();
        Player player = event.getPlayer();
        boolean allowedFrom = wgc.canVampiresEnter(player, from);
        boolean allowedTo = wgc.canVampiresEnter(player, from);
        if (allowedFrom && !allowedTo) {
            if (VampireAPI.isVampire(player)) {
                event.setCancelled(true);
            }
        }
    }
}
