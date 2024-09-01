package com.clanjhoo.vampire.tasks;

import com.clanjhoo.vampire.VampireRevamp;
import com.clanjhoo.vampire.entity.VPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.ZonedDateTime;


public class TheTask implements Runnable {

    private final VampireRevamp plugin;
    // -------------------------------------------- //
    // INSTANCE & CONSTRUCT
    // -------------------------------------------- //

    public TheTask(VampireRevamp plugin) {
        this.plugin = plugin;
        this.previousMillis = ZonedDateTime.now().toInstant().toEpochMilli();
    }


    // -------------------------------------------- //
    // OVERRIDE: MODULO REPEAT TASK
    // -------------------------------------------- //


    // When did the last invocation occur?
    private long previousMillis;

    @Override
    public void run() {
        long now = ZonedDateTime.now().toInstant().toEpochMilli();

        // Tick each online player
        for (Player player : Bukkit.getOnlinePlayers()) {
            VPlayer vPlayer = plugin.getVPlayer(player);
            if (vPlayer == null)
                continue;
            // VampireRevamp.debugLog(Level.INFO, "Ticking " + player.getName());
            vPlayer.tick(now - previousMillis);
            vPlayer.updateTruce(now, previousMillis);
        }

        previousMillis = now;
    }
}
