package com.clanjhoo.vampire.tasks;

import com.clanjhoo.vampire.VampireRevamp;
import com.clanjhoo.vampire.entity.VPlayer;
import com.clanjhoo.vampire.util.SunUtil;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;


public class RaytracingTask implements Runnable {
    private final VampireRevamp plugin;
    private final SunUtil sunUtil;
    private BukkitTask currentTask;

    // -------------------------------------------- //
    // INSTANCE & CONSTRUCT
    // -------------------------------------------- //
    public RaytracingTask(VampireRevamp plugin) {
        this.plugin = plugin;
        sunUtil = SunUtil.get(plugin);
    }

    private void rayTraceEveryone() {
        // Raytrace for all vampire players
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.isOnline())
                continue;
            VPlayer vPlayer = plugin.getVPlayer(player);
            if (vPlayer == null || vPlayer.isHuman())
                continue;
            World playerWorld = player.getWorld();
            Location startLocation = player.getLocation();
            // Angle between the Sun and the Y axis on the XY plane
            double angleWithY = -sunUtil.calcSunAngle(player.getWorld(), player);
            Vector direction = new Vector(Math.sin(angleWithY), Math.cos(angleWithY), 0);
            vPlayer.setLastRayTrace(
                    playerWorld.rayTraceBlocks(startLocation, direction, 64, FluidCollisionMode.ALWAYS, true));
        }
        currentTask = null;
    }

    public void stop() {
        if (currentTask != null)
            currentTask.cancel();
    }

    // -------------------------------------------- //
    // OVERRIDE: MODULO REPEAT TASK
    // -------------------------------------------- //
    @Override
    public void run() {
        if (currentTask == null)
            currentTask = Bukkit.getScheduler().runTaskAsynchronously(plugin, this::rayTraceEveryone);
    }
}
