package com.clanjhoo.vampire.tasks;

import com.clanjhoo.vampire.VampireRevamp;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;


public class BatTask implements Runnable {

    private final VampireRevamp plugin;
    private final Map<UUID, Location> lastLocationMap;

    public BatTask(VampireRevamp plugin) {
        this.plugin = plugin;
        lastLocationMap = new HashMap<>();
    }

    @Override
    public void run() {
        for (UUID puuid : plugin.batmap.keySet()) {
            Player player = Bukkit.getPlayer(puuid);

            if (player != null) {
                Location lastLocation = lastLocationMap.put(player.getUniqueId(), player.getLocation());
                Iterator<LivingEntity> iterator = plugin.batmap.get(puuid).iterator();
                while (iterator.hasNext()) {
                    LivingEntity entity = iterator.next();

                    if (entity.isValid() && !entity.isDead()) {
                        // Getting the vector and setting the bat's velocity, You can make a BukkitRunnable and run this code every tick
                        Vector velocity = player.getEyeLocation().toVector().subtract(entity.getLocation().toVector()).normalize();

                        // To make the bat move faster, you can simply multiply the vector
                        if (lastLocation != null) {
                            try {
                                velocity.multiply(Math.max(1, lastLocation.distance(player.getLocation())));
                            }
                            catch (IllegalArgumentException ignore) {}
                        }

                        if (velocity.getX() > Double.MAX_VALUE) {
                            velocity.setX(Double.MAX_VALUE);
                        }
                        if (velocity.getY() > Double.MAX_VALUE) {
                            velocity.setY(Double.MAX_VALUE);
                        }
                        if (velocity.getZ() > Double.MAX_VALUE) {
                            velocity.setZ(Double.MAX_VALUE);
                        }

                        try {
                            entity.setVelocity(velocity);
                        }
                        catch (IllegalArgumentException ignore) {}
                    }
                    else {
                        iterator.remove();
                    }
                }
            }
            else {
                for (LivingEntity entity : plugin.batmap.get(puuid)) {
                    plugin.batEnabled.remove(puuid);
                    plugin.bats.remove(entity);
                    if (entity.isValid() && !entity.isDead()) {
                        entity.remove();
                    }
                }
                plugin.batmap.remove(puuid);
            }
        }
    }

}
