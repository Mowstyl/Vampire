package com.clanjhoo.vampire.tasks;

import com.clanjhoo.vampire.VampireRevamp;
import com.clanjhoo.vampire.entity.UPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.logging.Level;

public class TheTask implements Runnable {
    // -------------------------------------------- //
    // INSTANCE & CONSTRUCT
    // -------------------------------------------- //

    public TheTask() {
        this.previousMillis = ZonedDateTime.now().toInstant().toEpochMilli();
    }


    // -------------------------------------------- //
    // OVERRIDE: MODULO REPEAT TASK
    // -------------------------------------------- //


    // When did the last invocation occur?
    private long previousMillis;

    public long getPreviousMillis() {
        return this.previousMillis;
    }

    public void setPreviousMillis(long previousMillis) {
        this.previousMillis = previousMillis;
    }

    @Override
    public void run() {
        long now = System.currentTimeMillis();

        // Tick each online player
        for (Player player : Bukkit.getOnlinePlayers()) {
            boolean success = VampireRevamp.getPlayerCollection().getDataSynchronous(new Serializable[]{player.getUniqueId()}, (uPlayer) -> {
                try {
                    // VampireRevamp.debugLog(Level.INFO, "Ticking " + player.getName());
                    uPlayer.tick(now - this.getPreviousMillis());
                } catch (NullPointerException ex) {
                    VampireRevamp.log(Level.SEVERE, "While executing Vampire.TheTask: " + ex.getMessage());
                    ex.printStackTrace();
                }
            },
                () -> VampireRevamp.log(Level.WARNING, "Couldn't find data for player " + player.getName() + " while executing TheTask."),
                false);
            if (!success) {
                VampireRevamp.log(Level.WARNING, "Couldn't schedule tick for player " + player.getName() + " while executing TheTask.");
            }
        }

        this.setPreviousMillis(now);
    }
}
