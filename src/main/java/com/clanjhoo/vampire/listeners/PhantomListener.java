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
            if (phan.getSpawningEntity() != null) {
                Player player = Bukkit.getPlayer(phan.getSpawningEntity());
                VPlayer vPlayer = plugin.getVPlayer(player);
                if (vPlayer != null && vPlayer.isVampire() && !vPlayer.truceIsBroken(System.currentTimeMillis())) {
                    e.setCancelled(true);
                }
            }
        }
    }
}
