package com.clanjhoo.vampire.listener;

import com.clanjhoo.vampire.entity.UPlayer;
import com.clanjhoo.vampire.event.InfectionChangeEvent;
import com.clanjhoo.vampire.event.VampireTypeChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import us.rfsmassacre.Werewolf.Events.WerewolfInfectionEvent;
import us.rfsmassacre.Werewolf.WerewolfAPI;

public class WerewolvesHook implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInfectionChange(InfectionChangeEvent event) {
        UPlayer uplayer = event.getUplayer();
        if (uplayer == null) {
            event.setCancelled(true);
            return;
        }

        Player player = uplayer.getPlayer();
        if (player == null) {
            event.setCancelled(true);
            return;
        }

        if (WerewolfAPI.isWerewolf(player) && event.getInfection() != 0) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onTypeChange(VampireTypeChangeEvent event) {
        UPlayer uplayer = event.getUplayer();
        if (uplayer == null) {
            event.setCancelled(true);
            return;
        }

        Player player = uplayer.getPlayer();
        if (player == null) {
            event.setCancelled(true);
            return;
        }

        if (WerewolfAPI.isWerewolf(player) && event.isVampire()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onWerewolfInfection(WerewolfInfectionEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            event.setCancelled(true);
            return;
        }

        UPlayer uplayer = UPlayer.get(player);
        if (uplayer == null) {
            return;
        }

        if (uplayer.isUnhealthy()) {
            event.setCancelled(true);
        }
    }
}
