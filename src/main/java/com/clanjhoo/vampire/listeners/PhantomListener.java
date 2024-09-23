package com.clanjhoo.vampire.listeners;

import com.clanjhoo.vampire.VampireRevamp;
import com.clanjhoo.vampire.entity.VPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.time.ZonedDateTime;
import java.util.UUID;

public class PhantomListener implements Listener {

    private final VampireRevamp plugin;


    public PhantomListener(VampireRevamp plugin) {
        this.plugin = plugin;
    }
    // -------------------------------------------- //
    // INSTANCE & CONSTRUCT
    // -------------------------------------------- //

    // -------------------------------------------- //
    // PHANTOMS
    // -------------------------------------------- //

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPhantomSpawn(CreatureSpawnEvent e) {
        if ((e.getEntity() instanceof Phantom) && e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
            Phantom phan = (Phantom) e.getEntity();
            UUID playerUUID = phan.getSpawningEntity();
            if (playerUUID != null) {
                Player player = Bukkit.getPlayer(playerUUID);
                VPlayer vPlayer = plugin.getVPlayer(player);
                long now = ZonedDateTime.now().toInstant().toEpochMilli();
                if (vPlayer != null && vPlayer.isVampire() && !vPlayer.truceIsBroken(now)) {
                    e.setCancelled(true);
                }
            }
        }
    }
}
