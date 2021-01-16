package com.clanjhoo.vampire.listeners;

import com.clanjhoo.vampire.VampireRevamp;
import com.clanjhoo.vampire.entity.UPlayer;
import com.clanjhoo.vampire.entity.UPlayerColl;
import com.clanjhoo.vampire.event.InfectionChangeEvent;
import com.clanjhoo.vampire.event.VampireTypeChangeEvent;
import com.clanjhoo.vampire.util.EntityUtil;
import com.clanjhoo.vampire.util.EventUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import us.rfsmassacre.Werewolf.Events.WerewolfInfectionEvent;
import us.rfsmassacre.Werewolf.WerewolfAPI;

import java.util.logging.Level;

public class WerewolvesSilverHook implements Listener {
    private boolean initialized = false;

    private boolean initialize() {
        boolean isWerewolfEnabled = Bukkit.getPluginManager().isPluginEnabled("Werewolf");
        if (!isWerewolfEnabled)
            HandlerList.unregisterAll(this);
        initialized = true;
        return isWerewolfEnabled;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onSilverSword(EntityDamageByEntityEvent event) {
        if (!initialized) {
            if (!initialize())
                return;
            VampireRevamp.log(Level.INFO, "Enabled Werewolves silver compatibility!");
        }
        if (!VampireRevamp.getVampireConfig().compatibility.useWerewolfSilverSword)
            return;

        if (!EventUtil.isCloseCombatEvent(event))
            return;

        if (!EntityUtil.isPlayer(event.getEntity()))
            return;

        UPlayer uplayer = UPlayerColl.get(event.getEntity().getUniqueId());
        if (!uplayer.isVampire())
            return;

        Entity rawDamager = EventUtil.getLiableDamager(event);
        if (!(rawDamager instanceof LivingEntity))
            return;

        LivingEntity damager = (LivingEntity) rawDamager;

        if (damager.getEquipment() == null)
            return;
        ItemStack weapon = damager.getEquipment().getItemInMainHand();
        boolean isSilverSword = VampireRevamp.getWerewolvesCompat().isSilverSword(weapon);
        if (!isSilverSword)
            return;

        VampireRevamp.debugLog(Level.INFO, "Silver sword used! Scaling damage...");
        EventUtil.scaleDamage(event, VampireRevamp.getVampireConfig().compatibility.silverDamageFactor);
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

        VampireRevamp.debugLog(Level.INFO, "Vampire: " + event.isVampire());

        if (WerewolfAPI.isWerewolf(player) && event.isVampire()) {
            VampireRevamp.debugLog(Level.INFO, "Cancelled!");
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

        UPlayer uplayer = UPlayerColl.get(player.getUniqueId());
        if (uplayer.isUnhealthy()) {
            event.setCancelled(true);
        }
    }
}
