package com.clanjhoo.vampire.listeners;

import com.clanjhoo.vampire.VampireRevamp;
import com.clanjhoo.vampire.entity.VPlayer;
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
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Level;

public class WerewolvesSilverHook implements Listener {
    private boolean initialized = false;
    private final VampireRevamp plugin;


    public WerewolvesSilverHook(VampireRevamp plugin) {
        this.plugin = plugin;
        plugin.log(Level.INFO, "Enabled Werewolves silver compatibility!");
    }

    private boolean checkWerewolf() {
        if (initialized)
            return true;
        boolean isWerewolfEnabled = Bukkit.getPluginManager().isPluginEnabled("Werewolf");
        if (!isWerewolfEnabled) {
            plugin.log(Level.WARNING, "Werewolf plugin has been disabled. Disabled silver detection");
            HandlerList.unregisterAll(this);
        }
        initialized = true;
        return isWerewolfEnabled;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onSilverSword(EntityDamageByEntityEvent event) {
        if (!checkWerewolf())
            return;

        if (!(event.getEntity() instanceof Player))
            return;

        if (!plugin.getVampireConfig().compatibility.useWerewolfSilverSword)
            return;

        if (!EntityUtil.isPlayer(event.getEntity()))
            return;

        if (!EventUtil.isCloseCombatEvent(event))
            return;

        VPlayer vPlayer = plugin.getVPlayer((Player) event.getEntity());
        if (vPlayer == null) {
            plugin.log(Level.WARNING, "Couldn't get data of player " + event.getEntity().getName());
            return;
        }
        if (!vPlayer.isVampire())
            return;

        Entity rawDamager = EventUtil.getLiableDamager(event);
        if (!(rawDamager instanceof LivingEntity))
            return;
        LivingEntity damager = (LivingEntity) rawDamager;
        EntityEquipment damagerEquipment = damager.getEquipment();
        if (damagerEquipment == null)
            return;
        ItemStack weapon = damagerEquipment.getItemInMainHand();
        boolean isSilverSword = plugin.getWerewolvesCompat().isSilverSword(weapon);
        if (!isSilverSword)
            return;

        plugin.debugLog(Level.INFO, "Silver sword used! Scaling damage...");
        EventUtil.scaleDamage(event, plugin.getVampireConfig().compatibility.silverDamageFactor);
    }
}
