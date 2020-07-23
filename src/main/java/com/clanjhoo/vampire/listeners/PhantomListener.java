package com.clanjhoo.vampire.listener;

import com.clanjhoo.vampire.VampireRevamp;
import com.clanjhoo.vampire.entity.UPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Phantom;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class PhantomListener implements Listener {
    // -------------------------------------------- //
    // INSTANCE & CONSTRUCT
    // -------------------------------------------- //

    private boolean trucePhantoms;

    public PhantomListener() {
        trucePhantoms = VampireRevamp.getVampireConfig().truce.entityTypes.contains(EntityType.PHANTOM);
    }

    // -------------------------------------------- //
    // PHANTOMS
    // -------------------------------------------- //

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPhantomSpawn(CreatureSpawnEvent e) {
        if ((e.getEntity() instanceof Phantom) && e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
            Phantom phan = (Phantom) e.getEntity();
            UPlayer uPlayer = UPlayer.get(phan.getSpawningEntity());
            if (trucePhantoms && uPlayer.isVampire() && !uPlayer.truceIsBroken()) {
                e.setCancelled(true);
            }
        }
    }
}
