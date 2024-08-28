package com.clanjhoo.vampire.listeners;

import com.clanjhoo.vampire.VampireRevamp;
import com.clanjhoo.vampire.compat.WorldGuardCompat;
import com.clanjhoo.vampire.entity.VPlayer;
import com.clanjhoo.vampire.util.EntityUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;


public class EntryVampiresListener implements Listener {

    private final VampireRevamp plugin;


    public EntryVampiresListener(VampireRevamp plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVampireEntry(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        VPlayer vPlayer = plugin.getVPlayer(player);
        if (vPlayer == null || !vPlayer.isVampire()) {
            return;
        }

        // Haven't moved from the block the player was standing
        Location from = event.getFrom();
        Location to = event.getTo();
        if (EntityUtil.toBlockVector(to).subtract(EntityUtil.toBlockVector(from)).lengthSquared() == 0) {
            return;
        }
        WorldGuardCompat wgc = plugin.getWorldGuardCompat();
        boolean allowedFrom = wgc.canVampiresEnter(player, from);
        boolean allowedTo = wgc.canVampiresEnter(player, from);
        if (allowedFrom && !allowedTo) {
            event.setCancelled(true);
        }
    }
}
