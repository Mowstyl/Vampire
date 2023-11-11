package com.clanjhoo.vampire.listeners;

import co.aikar.commands.MessageType;
import com.clanjhoo.vampire.*;
import com.clanjhoo.vampire.keyproviders.HolyWaterMessageKeys;
import com.clanjhoo.vampire.keyproviders.SkillMessageKeys;
import com.clanjhoo.vampire.keyproviders.VampirismMessageKeys;
import com.clanjhoo.vampire.config.PluginConfig;
import com.clanjhoo.vampire.entity.VPlayer;
import com.clanjhoo.vampire.util.*;
import me.libraryaddict.disguise.events.DisguiseEvent;
import me.libraryaddict.disguise.events.UndisguiseEvent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import java.util.logging.Level;

public class ListenerMain implements Listener {
    // -------------------------------------------- //
    // INSTANCE & CONSTRUCT
    // -------------------------------------------- //

    // -------------------------------------------- //
    // FX
    // -------------------------------------------- //

    @EventHandler(priority = EventPriority.NORMAL)
    public void fxOnDeath(EntityDeathEvent event) {
        if (VampireRevamp.getVampireConfig().general.isBlacklisted(event.getEntity().getWorld()))
            return;
        // If a vampire dies ...
        if (EntityUtil.isPlayer(event.getEntity())) {
            VampireRevamp.getVPlayerManager().getDataSynchronous((uplayer) -> {
                if (uplayer.isVampire()) {
                    // ... burns up with a violent scream ;,,;
                    uplayer.runFxShriek();
                    uplayer.runFxFlameBurst();
                    uplayer.runFxSmokeBurst();
                }
            }, () -> {} , true, event.getEntity().getUniqueId());
        }
    }

    // -------------------------------------------- //
    // MISC
    // -------------------------------------------- //

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void blockDamage(EntityDamageEvent event) {
        if (VampireRevamp.getVampireConfig().general.isBlacklisted(event.getEntity().getWorld()))
            return;
        Entity entity = event.getEntity();
        PluginConfig conf = VampireRevamp.getVampireConfig();

        if (conf.vampire.blockDamageFrom.contains(event.getCause())) {
            if (EntityUtil.isPlayer(entity)) {
                try {
                    VPlayer uplayer = VampireRevamp.getVPlayerManager().tryGetDataNow(entity.getUniqueId());
                    if (uplayer.isVampire()) {
                        event.setCancelled(true);
                    }
                }
                catch (AssertionError ignore) {}
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void blockRegainHealth(EntityRegainHealthEvent event) {
        if (VampireRevamp.getVampireConfig().general.isBlacklisted(event.getEntity().getWorld()))
            return;
        Entity entity = event.getEntity();
        PluginConfig conf = VampireRevamp.getVampireConfig();

        if (conf.vampire.blockHealthFrom.contains(event.getRegainReason())) {
            if (EntityUtil.isPlayer(entity)) {
                try {
                    VPlayer uplayer = VampireRevamp.getVPlayerManager().tryGetDataNow(entity.getUniqueId());
                    if (uplayer.isVampire()) {
                        event.setCancelled(true);
                    }
                }
                catch (AssertionError ignore) {}
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void blockFoodChange(FoodLevelChangeEvent event) {
        if (VampireRevamp.getVampireConfig().general.isBlacklisted(event.getEntity().getWorld()))
            return;
        Entity entity = event.getEntity();
        if (EntityUtil.isPlayer(entity)) {
            try {
                VPlayer uplayer = VampireRevamp.getVPlayerManager().tryGetDataNow(entity.getUniqueId());
                if (uplayer.isVampire()) {
                    event.setFoodLevel(((Player) entity).getFoodLevel());
                    // EntityUtil.sendHealthFoodUpdatePacket((Player) entity);
                }
            }
            catch (AssertionError ignore) {}
        }
    }

    // -------------------------------------------- //
    // UPDATE
    // -------------------------------------------- //

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void updateOnJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        VampireRevamp.loadPlayerFromDB(player);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void updateOnQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        VampireRevamp.getVPlayerManager().save(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void updateOnTeleport(PlayerTeleportEvent event) {
        final Player player = event.getPlayer();
        if (EntityUtil.isPlayer(player)) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(VampireRevamp.getInstance(), () -> {
                VPlayer uplayer = VampireRevamp.getVPlayerManager().tryGetDataNow(player.getUniqueId());
                uplayer.update();
            });
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void updateOnDeath(PlayerDeathEvent event) {
        if (VampireRevamp.getVampireConfig().general.isBlacklisted(event.getEntity().getWorld()))
            return;
        // If a vampire dies ...
        Player entity = event.getEntity();
        if (EntityUtil.isPlayer(entity)) {
            VampireRevamp.getVPlayerManager().getDataSynchronous((uplayer) -> {
                if (uplayer.isVampire()) {
                    // Close down bloodlust.
                    uplayer.setRad(0);
                    uplayer.setBloodlusting(false);
                    entity.setFireTicks(0);
                }
            }, () -> {}, true, entity.getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onDisguise(DisguiseEvent event) {
        if (!VampireRevamp.getVampireConfig().vampire.batusi.preventDisguise)
            return;
        Entity aux = event.getEntity();
        if (!EntityUtil.isPlayer(aux))
            return;

        final Player player = (Player) aux;
        VPlayer vPlayer = VampireRevamp.getVPlayerManager().tryGetDataNow(player.getUniqueId());
        if (!vPlayer.isChangingDisguise())
            return;
        if (!ResourceUtil.hasPermission(player, Perm.MODE_BATUSI_DISGUISE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onUndisguise(UndisguiseEvent event) {
        if (!VampireRevamp.getVampireConfig().vampire.batusi.preventDisguise)
            return;
        Entity aux = event.getEntity();
        if (!EntityUtil.isPlayer(aux))
            return;

        final Player player = (Player) aux;
        VPlayer vPlayer = VampireRevamp.getVPlayerManager().tryGetDataNow(player.getUniqueId());
        if (!vPlayer.isChangingDisguise())
            return;
        // This event is also called when the player already has a disguise before using batusi.
        // We don't care about it in that case.
        if (!ResourceUtil.hasPermission(player, Perm.MODE_BATUSI_DISGUISE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void updateOnRespawn(PlayerRespawnEvent event) {
        if (VampireRevamp.getVampireConfig().general.isBlacklisted(event.getPlayer().getWorld()))
            return;
        // If a vampire respawns ...
        Player player = event.getPlayer();

        if (EntityUtil.isPlayer(player)) {
            VPlayer uplayer = VampireRevamp.getVPlayerManager().tryGetDataNow(player.getUniqueId());

            if (uplayer.isVampire()) {
                // ... modify food and health levels and force another speed-update.
                Bukkit.getScheduler().scheduleSyncDelayedTask(VampireRevamp.getInstance(), () -> {
                    PluginConfig conf = VampireRevamp.getVampireConfig();
                    player.setFoodLevel(conf.vampire.respawnFood);
                    player.setHealth(conf.vampire.respawnHealth);
                    EntityUtil.sendHealthFoodUpdatePacket(player);
                    uplayer.update();
                });
            }
        }
    }

    // -------------------------------------------- //
    // DROP SELF
    // -------------------------------------------- //

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void dropSelf(BlockBreakEvent event) {
        if (VampireRevamp.getVampireConfig().general.isBlacklisted(event.getPlayer().getWorld()))
            return;
        // If a non-creative player ...
        Player player = event.getPlayer();
        PluginConfig conf = VampireRevamp.getVampireConfig();
        if (EntityUtil.isPlayer(player) && player.getGameMode() != GameMode.CREATIVE) {
            // ... broke a self-dropping block ...
            Material material = event.getBlock().getType();
            if (conf.general.dropSelfMaterials.contains(material)) {
                // ... then we make it drop itself.
                event.setCancelled(true);
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(material, 1));
                event.getBlock().setType(Material.AIR);
            }
        }
    }

    // -------------------------------------------- //
    // BLOODLUST
    // -------------------------------------------- //

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void bloodlustSmokeTrail(PlayerMoveEvent event) {
        if (VampireRevamp.getVampireConfig().general.isBlacklisted(event.getPlayer().getWorld()))
            return;
        // If a noncreative player ...
        Player player = event.getPlayer();
        if (EntityUtil.isPlayer(player) && player.getGameMode() != GameMode.CREATIVE) {
            // ... moved between two blocks ...
            Block from = event.getFrom().getBlock();
            Block to = event.getTo().getBlock();
            if (!from.equals(to)) {
                VPlayer uplayer = VampireRevamp.getVPlayerManager().tryGetDataNow(player.getUniqueId());

                // ... and that player is a vampire ...
                // ... that has bloodlust on ...
                if (uplayer.isVampire() && uplayer.isBloodlusting()) {
                    // ... then spawn smoke trail.
                    PluginConfig conf = VampireRevamp.getVampireConfig();
                    Location one = event.getFrom().clone();
                    Location two = one.clone().add(0, 1, 0);
                    long count1 = MathUtil.probabilityRound(conf.vampire.bloodlust.smokes);
                    long count2 = MathUtil.probabilityRound(conf.vampire.bloodlust.smokes);
                    for (long i = count1; i > 0; i--) FxUtil.smoke(one);
                    for (long i = count2; i > 0; i--) FxUtil.smoke(two);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void bloodlustGameModeToggle(PlayerGameModeChangeEvent event) {
        if (VampireRevamp.getVampireConfig().general.isBlacklisted(event.getPlayer().getWorld()))
            return;
        // If a player enters creative or spectator mode ...
        if (event.getNewGameMode() == GameMode.CREATIVE || event.getNewGameMode() == GameMode.SPECTATOR) {
            // ... turn of bloodlust ...
            Player player = event.getPlayer();
            if (EntityUtil.isPlayer(player)) {
                VampireRevamp.getVPlayerManager().getDataSynchronous((uplayer) -> {
                    if (uplayer.isBloodlusting()) {
                        uplayer.setBloodlusting(false);
                    }
                }, () -> {}, true, player.getUniqueId());
            }
        }
    }

    // -------------------------------------------- //
    // TRUCE
    // -------------------------------------------- //

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void truceTarget(EntityTargetEvent event) {
        if (VampireRevamp.getVampireConfig().general.isBlacklisted(event.getEntity().getWorld()))
            return;
        // If a player is targeted...
        if (EntityUtil.isPlayer(event.getTarget())) {
            VampireRevamp.debugLog(Level.INFO, "Player targeted by " + event.getEntityType().name());
            Player player = (Player) event.getTarget();
            PluginConfig conf = VampireRevamp.getVampireConfig();

            // ... by creature that cares about the truce with vampires ...
            if (player != null && conf.truce.entityTypes.contains(event.getEntityType())) {
                VampireRevamp.debugLog(Level.INFO, "Entity in truce list");
                VPlayer uplayer = VampireRevamp.getVPlayerManager().tryGetDataNow(player.getUniqueId());
                // ... and that player is a vampire ...
                // ... that has not recently done something to break the truce...
                if (uplayer.isVampire() && !uplayer.truceIsBroken()) {
                    VampireRevamp.debugLog(Level.INFO, "Vampire detected, cancelling target");
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void truceDamage(EntityDamageEvent event) {
        if (VampireRevamp.getVampireConfig().general.isBlacklisted(event.getEntity().getWorld()))
            return;
        // If this is a combat event ...
        if (EventUtil.isCombatEvent(event)) {
            // ... to a creature that cares about the truce with vampires...
            PluginConfig conf = VampireRevamp.getVampireConfig();
            if (conf.truce.entityTypes.contains(event.getEntityType())) {
                // ... and the liable damager is a vampire ...
                Entity damager = EventUtil.getLiableDamager(event);

                if (EntityUtil.isPlayer(damager)) {
                    if (conf.truce.checkGamemode && ((Player) damager).getGameMode() == GameMode.CREATIVE)
                        return;


                    VampireRevamp.getVPlayerManager().getDataSynchronous((vpdamager) -> {
                        if (vpdamager.isVampire()) {
                            // Then that vampire broke the truce.
                            vpdamager.truceBreak();
                        }
                    }, () -> {}, true, damager.getUniqueId());
                }
            }
        }
    }

    // -------------------------------------------- //
    // REGEN
    // -------------------------------------------- //

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void regen(EntityDamageEvent event) {
        if (VampireRevamp.getVampireConfig().general.isBlacklisted(event.getEntity().getWorld()))
            return;
        // If the damagee is a vampire ...
        Entity entity = event.getEntity();

        if (EntityUtil.isPlayer(entity)) {
            final long timestamp = System.currentTimeMillis();
            VampireRevamp.getVPlayerManager().getDataSynchronous((vampire) -> {
                if (vampire.isVampire()) {
                    // ... mark now as lastDamageMillis
                    vampire.setLastDamageMillis(timestamp);
                }
            }, () -> {}, false, entity.getUniqueId());
        }
    }

    // -------------------------------------------- //
    // COMBAT
    // -------------------------------------------- //

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void combatVulnerability(EntityDamageEvent event) {
        if (VampireRevamp.getVampireConfig().general.isBlacklisted(event.getEntity().getWorld()))
            return;
        // If this is a close combat event ...
        if (EventUtil.isCloseCombatEvent(event)) {
            // ... where the liable damager is a human entity ...
            Entity damagerEntity = EventUtil.getLiableDamager(event);
            // ... and the damagee is a player ...
            Entity entity = event.getEntity();

            if (damagerEntity instanceof HumanEntity && EntityUtil.isPlayer(entity)) {
                HumanEntity damager = (HumanEntity) damagerEntity;
                PluginConfig conf = VampireRevamp.getVampireConfig();

                // ... and the damagee is a vampire ...
                VPlayer vampire = VampireRevamp.getVPlayerManager().tryGetDataNow(entity.getUniqueId());
                if (vampire.isVampire()) {
                    // ... and a wooden item was used ...
                    ItemStack item = EntityUtil.getWeapon(damager);
                    if (item != null && conf.vampire.holyItem.materials.contains(item.getType())) {
                        // ... Then modify damage!
                        EventUtil.setDamage(event, event.getDamage() * conf.vampire.holyItem.damageFactor);
                    }
                }
            }
        }
    }

    // -------------------------------------------- //
    // Batusi
    // -------------------------------------------- //

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void disableBatusiCheck(EntityDamageEvent event) {
        if (VampireRevamp.getVampireConfig().vampire.batusi.disableOnHit
                && EntityUtil.isPlayer(event.getEntity())) {
            Player damagee = (Player) event.getEntity();
            VampireRevamp.getVPlayerManager().getDataSynchronous((udamagee) -> {
                if (udamagee.isVampire() && udamagee.isBatusi()) {
                    udamagee.setBatusi(false, 0);
                }
            }, () -> {}, true, damagee.getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void combatStrength(EntityDamageEvent event) {
        if (VampireRevamp.getVampireConfig().general.isBlacklisted(event.getEntity().getWorld()))
            return;
        // If this is a close combat event ...
        if (EventUtil.isCloseCombatEvent(event)) {
            // ... and the liable damager is a vampire ...
            Entity damager = EventUtil.getLiableDamager(event);
            if (EntityUtil.isPlayer(damager)) {
                VPlayer vampire = VampireRevamp.getVPlayerManager().tryGetDataNow(damager.getUniqueId());

                if (vampire.isVampire()) {
                    PluginConfig conf = VampireRevamp.getVampireConfig();
                    // ... and this event isn't a forbidden mcmmo one ...
                    if (conf.compatibility.damageWithMcmmo
                            || !event.getClass().getName().equals("com.gmail.nossr50.events.fake.FakeEntityDamageByEntityEvent")) {
                        // ... Then modify damage!
                        EventUtil.scaleDamage(event, vampire.combatDamageFactor());
                    }
                }
            }
        }
    }

    // -------------------------------------------- //
    // INFECT PLAYERS
    // -------------------------------------------- //

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void infection(EntityDamageEvent event) {
        if (VampireRevamp.getVampireConfig().general.isBlacklisted(event.getEntity().getWorld()))
            return;
        // If this is a close combat event ...
        if (EventUtil.isCloseCombatEvent(event)) {
            // ... where there is one vampire and one non-vampire ...
            Entity damagee = event.getEntity();
            Entity damager = EventUtil.getLiableDamager(event);
            if (EntityUtil.isPlayer(damagee) && EntityUtil.isPlayer(damager)) {
                VampireRevamp.getVPlayerManager().getDataSynchronous((vampiree) -> {
                    final VPlayer[] vampire = {vampiree};
                    VampireRevamp.getVPlayerManager().getDataSynchronous((human) -> {
                        if ((vampire[0].isVampire() && human.isHuman())
                                || (human.isVampire() && vampire[0].isHuman())) {
                            if (human.isVampire()) {
                                VPlayer aux = vampire[0];
                                vampire[0] = human;
                                human = aux;
                            }

                            // ... and the vampire is allowed to infect through combat ...
                            // ... and the human is allowed to contract through combat ...
                            // ... Then there is a risk for infection ...
                            if (Perm.COMBAT_INFECT.has(vampire[0].getPlayer())
                                    && Perm.COMBAT_CONTRACT.has(human.getPlayer())
                                    && MathUtil.random.nextDouble() < vampire[0].combatInfectRisk()) {
                                InfectionReason reason = vampire[0].isIntending() ? InfectionReason.COMBAT_INTENDED : InfectionReason.COMBAT_MISTAKE;
                                human.addInfection(0.01D, reason, vampire[0]);
                            }
                        }
                    }, () -> {}, true, damager.getUniqueId());
                }, () -> {}, true, damagee.getUniqueId());

            }
        }
    }

    // -------------------------------------------- //
    // INFECT HORSES
    // -------------------------------------------- //

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void infectHorse(EntityDamageEvent event) {
        if (VampireRevamp.getVampireConfig().general.isBlacklisted(event.getEntity().getWorld()))
            return;
        // If this is a close combat event ...
        // ... and the vampires can infect horses...
        PluginConfig conf = VampireRevamp.getVampireConfig();
        if (EventUtil.isCloseCombatEvent(event) && conf.infection.canInfectHorses) {
            // ... where there is one vampire ...
            // ... and one is a living horse ...
            Entity damager = EventUtil.getLiableDamager(event);
            // only horses, no mules donkeys or undead ones
            if (EntityUtil.isPlayer(damager) && event.getEntity() instanceof Horse) {
                VampireRevamp.getVPlayerManager().getDataSynchronous((vampire) -> {
                    // ... and the vampire is allowed to infect through combat ...
                    if (vampire.isVampire() && Perm.COMBAT_INFECT.has(vampire.getPlayer())) {
                        Horse horse = (Horse) event.getEntity();

                        // ... Then there is a risk for infection ...
                        if (MathUtil.random.nextDouble() < vampire.combatInfectRisk()) {
                            // ... then we spawn the new horse ...
                            horse.getWorld().spawnEntity(horse.getLocation(), MathUtil.random.nextDouble() > 0.5 ? EntityType.SKELETON_HORSE : EntityType.ZOMBIE_HORSE);

                            // ... and we Thanos the old one ...
                            horse.remove();
                        }
                    }
                }, () -> {}, true, damager.getUniqueId());
            }
        }
    }

    // -------------------------------------------- //
    // SLEEP
    // -------------------------------------------- //

    @EventHandler(priority = EventPriority.NORMAL)
    public void trySleep(PlayerBedEnterEvent event) {
        if (VampireRevamp.getVampireConfig().general.isBlacklisted(event.getPlayer().getWorld()))
            return;
        Player player = event.getPlayer();
        PluginConfig conf = VampireRevamp.getVampireConfig();
        // If day sleeping is allowed for vampires ...
        if (!EntityUtil.isPlayer(player) || !conf.vampire.canSleepDaytime)
            return;

        // ... the player is a vampire ...
        VPlayer vPlayer = VampireRevamp.getVPlayerManager().tryGetDataNow(player.getUniqueId());
        if (!vPlayer.isVampire())
            return;

        // ... and tries to sleep at night or storm ...
        long time = player.getWorld().getTime();
        boolean storming = player.getWorld().hasStorm();
        VampireRevamp.log(Level.INFO, "time " + time + " " + (time >= 11834) + " storm " + storming);
        if (time >= 11834 || storming) {
            // ... we cancel
            VampireRevamp.sendMessage(event.getPlayer(),
                    MessageType.INFO,
                    VampirismMessageKeys.CANT_SLEEP);
            event.setCancelled(true);
        }
        else {
            if (!((Bed) event.getBed().getBlockData()).isOccupied())
                event.setUseBed(Event.Result.ALLOW);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void wakeUp(PlayerBedLeaveEvent event) {
        if (VampireRevamp.getVampireConfig().general.isBlacklisted(event.getPlayer().getWorld()))
            return;
        Player player = event.getPlayer();
        PluginConfig conf = VampireRevamp.getVampireConfig();
        // If day sleeping is allowed for vampires ...
        if (!EntityUtil.isPlayer(player) || !conf.vampire.canSleepDaytime)
            return;

        // ... the player is a vampire ...
        VPlayer vPlayer = VampireRevamp.getVPlayerManager().tryGetDataNow(player.getUniqueId());
        if (!vPlayer.isVampire())
            return;

        // ... and the sleeping was successful ...
        long rtime = player.getWorld().getTime();
        if (rtime != 0)
            return;

        // ... we set time to night
        player.getWorld().setTime(11834);
    }

    // -------------------------------------------- //
    // FOOD
    // -------------------------------------------- //

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void foodCake(PlayerInteractEvent event) {
        if (VampireRevamp.getVampireConfig().general.isBlacklisted(event.getPlayer().getWorld()))
            return;
        Player player = event.getPlayer();
        PluginConfig conf = VampireRevamp.getVampireConfig();
        // If cake eating is not allowed for vampires ...
        if (EntityUtil.isPlayer(player) && !conf.vampire.canEatCake) {
            // ... and the player right-clicks a cake block ...
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.CAKE) {
                // ... and the player is a vampire ...
                VPlayer uplayer = VampireRevamp.getVPlayerManager().tryGetDataNow(player.getUniqueId());
                if (uplayer.isVampire()) {
                    // ... we deny!
                    event.setCancelled(true);
                    VampireRevamp.sendMessage(player,
                            MessageType.ERROR,
                            VampirismMessageKeys.CANT_EAT_ITEM,
                            "{item}", event.getClickedBlock().getType().name());
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void foodBlood(EntityDamageEvent event) {
        if (VampireRevamp.getVampireConfig().general.isBlacklisted(event.getEntity().getWorld()))
            return;
        // If this is a close combat event ...
        // ... to a living entity ...
        if (EventUtil.isCloseCombatEvent(event) && event.getEntity() instanceof LivingEntity) {
            LivingEntity damagee = (LivingEntity) event.getEntity();
            PluginConfig conf = VampireRevamp.getVampireConfig();

            // ... of a tasty type ...
            double fullFoodQuotient = conf.fullFoodQuotient.getOrDefault(damagee.getType(), 0D);
            if (fullFoodQuotient != 0) {
                // ... that has blood left ...
                if (damagee.isValid() && !damagee.isDead() && damagee.getHealth() > 0) {
                    // ... and the liable damager is a vampire ...
                    Entity damager = EventUtil.getLiableDamager(event);
                    if (EntityUtil.isPlayer(damager)) {
                        VampireRevamp.getVPlayerManager().getDataSynchronous((vampire) -> {
                            // ... and the player is still retrievable ...
                            if (vampire.isVampire() && vampire.getPlayer() != null) {
                                // ... drink blood! ;,,;
                                double damage = event.getFinalDamage();
                                if (conf.general.useOldFoodFormula)
                                    damage = event.getDamage();
                                if (damagee.getHealth() < damage)
                                    damage = damagee.getHealth();
                                double food = damage * fullFoodQuotient;
                                if (conf.general.useOldFoodFormula)
                                    food = damage / damagee.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() * fullFoodQuotient * vampire.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();

                                vampire.addFood(food);
                            }
                        }, () -> {}, true, damager.getUniqueId());
                    }
                }
            }
        }
    }

    // -------------------------------------------- //
    // BLOOD FLASK
    // -------------------------------------------- //

    @EventHandler(priority = EventPriority.NORMAL)
    public void bloodFlaskConsume(PlayerItemConsumeEvent event) {
        if (VampireRevamp.getVampireConfig().general.isBlacklisted(event.getPlayer().getWorld()))
            return;

        ItemStack item = event.getItem();
        // If the item is a potion ...
        if (!item.getType().equals(Material.POTION))
            return;

        // ... and is a blood flask ...
        BloodFlaskUtil.BloodFlaskData bloodFlaskData = BloodFlaskUtil.getBloodFlaskData(item);
        if (bloodFlaskData == null)
            return;
        // ... get the blood amount ...
        double amount = bloodFlaskData.getAmount();

        // ... and is the blood vampiric?  ...
        boolean isVampiric = bloodFlaskData.isVampiric();

        // ... get the player ...
        VPlayer uplayer = VampireRevamp.getVPlayerManager().tryGetDataNow(event.getPlayer().getUniqueId());

        // ... are they bloodlusting? ...
        if (!uplayer.isBloodlusting()) {
            // ... calculate and add the blood amount to the player ...
            double lacking;
            if (uplayer.isVampire()) {
                // Vampires drink blood to replenish food.
                lacking = (20 - uplayer.getFood());
                if (amount > lacking) amount = Math.ceil(lacking);
                uplayer.addFood(amount);
            } else if (isVampiric) {
                // ... finally, if the player is human did they contract the dark disease from vampiric blood?
                if (uplayer.isInfected()) {
                    uplayer.addInfection(0.01D);
                } else if (event.getPlayer().hasPermission("vampire.flask.contract")
                        && MathUtil.random.nextDouble() * 20 < amount) {
                    uplayer.addInfection(0.05D, InfectionReason.FLASK, uplayer);
                }
            }
        } else {
            VampireRevamp.sendMessage(event.getPlayer(),
                    MessageType.ERROR,
                    SkillMessageKeys.FLASK_BLOODLUSTING);
            event.setCancelled(true);
        }
    }

    // -------------------------------------------- //
    // HOLY WATER
    // -------------------------------------------- //

    /**
     * You may wonder why the PotionSplashEvent was not used.
     * That event is not triggered. This potion has no vanilla effects.
     * Thus only this projectile hit event is triggered.
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void holyWater(ProjectileHitEvent event) {
        // If this projectile is a thrown potion ...
        Projectile projectile = event.getEntity();
        PluginConfig conf = VampireRevamp.getVampireConfig();
        if (!conf.general.isBlacklisted(projectile.getWorld()) &&
                projectile instanceof ThrownPotion) {
            ThrownPotion thrownPotion = (ThrownPotion) projectile;

            // ... and the potion type is holy water ...
            if (HolyWaterUtil.isHolyWater(thrownPotion)) {
                // ... who is the thrower and where did it splash? ...
                Location splashLocation = thrownPotion.getLocation();

                ProjectileSource projectileShooter = projectile.getShooter();
                if (EntityUtil.isPlayer(projectileShooter)) {
                    Player shooter = (Player) projectileShooter;

                    // ... then to all nearby players ...
                    for (Player player : splashLocation.getWorld().getPlayers()) {
                        if (EntityUtil.isPlayer(player)
                                && player.getLocation().distance(splashLocation) <= conf.holyWater.splashRadius) {
                            VampireRevamp.getVPlayerManager().getDataSynchronous((uplayer) -> {
                                VampireRevamp.sendMessage(player,
                                        MessageType.INFO,
                                        HolyWaterMessageKeys.COMMON_REACT,
                                        "{player}", shooter.getDisplayName());
                                uplayer.runFxEnderBurst();

                                // Trigger a damage event so other plugins can cancel this.
                                EntityDamageByEntityEvent triggeredEvent = new EntityDamageByEntityEvent(shooter, player, DamageCause.CUSTOM, 1D);
                                Bukkit.getPluginManager().callEvent(triggeredEvent);
                                if (!triggeredEvent.isCancelled()) {
                                    if (uplayer.isHealthy()) {
                                        VampireRevamp.sendMessage(player,
                                                MessageType.INFO,
                                                HolyWaterMessageKeys.HEALTHY_REACT);
                                    } else if (uplayer.isInfected()) {
                                        VampireRevamp.sendMessage(player,
                                                MessageType.INFO,
                                                HolyWaterMessageKeys.INFECTED_REACT);
                                        uplayer.setInfection(0);
                                        uplayer.runFxEnder();
                                    } else if (uplayer.isVampire()) {
                                        VampireRevamp.sendMessage(player,
                                                MessageType.INFO,
                                                HolyWaterMessageKeys.VAMPIRE_REACT);
                                        uplayer.addTemp(conf.holyWater.temperature);
                                        uplayer.runFxFlameBurst();
                                    }
                                }
                            }, () -> {}, true, player.getUniqueId());
                        }
                    }
                }
            }
        }
    }

    // -------------------------------------------- //
    // ALTARS
    // -------------------------------------------- //

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void altars(PlayerInteractEvent event) {
        if (VampireRevamp.getVampireConfig().general.isBlacklisted(event.getPlayer().getWorld()))
            return;
        VampireRevamp.debugLog(Level.INFO, "Interacted with something");
        // If the player right-clicked a block ...
        Action action = event.getAction();
        // ... without a placeable item in hand ...
        // ... and the event isn't fired with the off-hand ...
        if (action == Action.RIGHT_CLICK_BLOCK &&
                event.getHand() != EquipmentSlot.OFF_HAND &&
                (!VampireRevamp.getVampireConfig().altar.checkIfBlockInHand
                        || (!event.isBlockInHand() && !(event.getItem() != null && event.getItem().getType() == Material.REDSTONE)))) {
            VampireRevamp.debugLog(Level.INFO, "Without block in hand");
            // ... run altar logic.
            Player player = event.getPlayer();
            if (EntityUtil.isPlayer(player)) {
                VampireRevamp plugin = VampireRevamp.getInstance();
                VampireRevamp.debugLog(Level.INFO, "a real player");

                if (plugin.getAltarDark().evalBlockUse(event.getClickedBlock(), player) || plugin.getAltarLight().evalBlockUse(event.getClickedBlock(), player)) {
                    VampireRevamp.debugLog(Level.INFO, "and is an altar!");
                    event.setCancelled(true);
                }
            }
        }
    }
}
