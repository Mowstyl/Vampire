package com.clanjhoo.vampire.listeners;

import com.clanjhoo.vampire.VampireRevamp;
import com.clanjhoo.vampire.entity.UPlayer;
import org.bukkit.entity.Phantom;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.io.Serializable;

public class PhantomListener implements Listener {
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
                UPlayer uPlayer = VampireRevamp.getPlayerCollection().getDataNow(new Serializable[]{phan.getSpawningEntity()});
                if (uPlayer.isVampire() && !uPlayer.truceIsBroken()) {
                    e.setCancelled(true);
                }
            }
        }
    }
}
