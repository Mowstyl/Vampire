package com.clanjhoo.vampire.tasks;

import com.clanjhoo.vampire.entity.UPlayer;
import com.clanjhoo.vampire.util.EntityUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
        long now = ZonedDateTime.now().toInstant().toEpochMilli();

        // Tick each online player
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (EntityUtil.isPlayer(player)) {

                UPlayer uplayer = UPlayer.get(player);
                if (uplayer != null) {
                    try {
                        // player.sendMessage("Ticking you!");
                        uplayer.tick(now - this.getPreviousMillis());
                    } catch (NullPointerException ex) {
                        Bukkit.getServer().getLogger().log(Level.SEVERE, "While executing Vampire.TheTask, NullPointerException: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            }
        }

        this.setPreviousMillis(now);
    }
}
