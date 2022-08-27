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
import org.bukkit.inventory.ItemStack;

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
        if (!(event.getEntity() instanceof Player))
            return;

        if (!initialized) {
            if (!initialize())
                return;
            VampireRevamp.log(Level.INFO, "Enabled Werewolves silver compatibility!");
        }
        if (!VampireRevamp.getVampireConfig().compatibility.useWerewolfSilverSword)
            return;

        if (!EntityUtil.isPlayer(event.getEntity()))
            return;

        if (!EventUtil.isCloseCombatEvent(event))
            return;

        try {
            VPlayer uplayer = VampireRevamp.getVPlayerManager().tryGetDataNow(event.getEntity().getUniqueId());
            if (!uplayer.isVampire())
                return;
        }
        catch (AssertionError ex) {
            VampireRevamp.log(Level.WARNING, "Couldn't get data of player " + event.getEntity().getName());
            return;
        }

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
}
