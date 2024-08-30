package com.clanjhoo.vampire.listeners;

import co.aikar.commands.MessageType;
import com.clanjhoo.dbhandler.data.LoadResult;
import com.clanjhoo.vampire.*;
import com.clanjhoo.vampire.event.VampireLoadedEvent;
import com.clanjhoo.vampire.keyproviders.HolyWaterMessageKeys;
import com.clanjhoo.vampire.keyproviders.SkillMessageKeys;
import com.clanjhoo.vampire.keyproviders.VampirismMessageKeys;
import com.clanjhoo.vampire.config.PluginConfig;
import com.clanjhoo.vampire.entity.VPlayer;
import com.clanjhoo.vampire.util.*;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.BlockProjectileSource;
import org.bukkit.projectiles.ProjectileSource;

import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;


public class ListenerMain implements Listener {
    private final VampireRevamp plugin;
    private final ResourceUtil resUtil;
    private final BloodFlaskUtil bloodUtil;
    private final HolyWaterUtil holyUtil;

    // -------------------------------------------- //
    // INSTANCE & CONSTRUCT
    // -------------------------------------------- //

    public ListenerMain(VampireRevamp plugin) {
        this.plugin = plugin;
        resUtil = new ResourceUtil(plugin);
        bloodUtil = new BloodFlaskUtil(plugin);
        holyUtil = new HolyWaterUtil(plugin);
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onDataLoad(VampireLoadedEvent ev) {
        if (ev.getResult() == LoadResult.SUCCESS) {
            VPlayer vPlayer = ev.getData();
            assert vPlayer != null;
            plugin.storeVPlayer(vPlayer);
            Player p = vPlayer.getPlayer();
            if (p != null)
                plugin.setVampireGroup(p, vPlayer.isVampire());
        }
        else {
            UUID uuid = (UUID) ev.getKeys().get(0);
            OfflinePlayer oPlayer = Bukkit.getOfflinePlayer(uuid);
            String name = oPlayer.getName();
            if (name != null)
                plugin.log(Level.WARNING,
                        "There was an error while loading data for player " + oPlayer.getName() + ".");
            else
                plugin.log(Level.WARNING,
                        "There was an error while loading data for player with uuid " + uuid + ".");
            Exception ex = ev.getException();
            assert ex != null;
            ex.printStackTrace();
        }
    }

    // -------------------------------------------- //
    // FX
    // -------------------------------------------- //

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void fxOnDeath(EntityDeathEvent event) {
        if (plugin.getVampireConfig().general.isBlacklisted(event.getEntity().getWorld()))
            return;
        // If a vampire dies ...
        if (EntityUtil.isPlayer(event.getEntity())) {
            VPlayer vPlayer = plugin.getVPlayer((Player) event.getEntity());
            if (vPlayer == null) {
                plugin.log(Level.WARNING, "There was an error while loading " + event.getEntity().getName() + " data");
                return;
            }
            if (vPlayer.isVampire()) {
                // ... burns up with a violent scream ;,,;
                vPlayer.runFxShriek();
                vPlayer.runFxFlameBurst();
                vPlayer.runFxSmokeBurst();
            }
        }
    }

    // -------------------------------------------- //
    // MISC
    // -------------------------------------------- //

    @EventHandler(priority = EventPriority.LOW)
    public void blockDamage(EntityDamageEvent event) {
        if (plugin.getVampireConfig().general.isBlacklisted(event.getEntity().getWorld()))
            return;
        Entity entity = event.getEntity();
        PluginConfig conf = plugin.getVampireConfig();

        if (conf.vampire.blockDamageFrom.contains(event.getCause())) {
            if (EntityUtil.isPlayer(entity)) {
                VPlayer vPlayer = plugin.getVPlayer((Player) entity);
                if (vPlayer == null)
                    return;
                if (vPlayer.isVampire()) {
                    event.setDamage(0);
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void blockRegainHealth(EntityRegainHealthEvent event) {
        if (plugin.getVampireConfig().general.isBlacklisted(event.getEntity().getWorld()))
            return;
        Entity entity = event.getEntity();
        PluginConfig conf = plugin.getVampireConfig();

        if (conf.vampire.blockHealthFrom.contains(event.getRegainReason())) {
            if (EntityUtil.isPlayer(entity)) {
                VPlayer vPlayer = plugin.getVPlayer((Player) entity);
                if (vPlayer == null)
                    return;
                if (vPlayer.isVampire()) {
                    event.setAmount(0);
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void blockFoodChange(FoodLevelChangeEvent event) {
        if (plugin.getVampireConfig().general.isBlacklisted(event.getEntity().getWorld()))
            return;
        Entity entity = event.getEntity();
        if (EntityUtil.isPlayer(entity)) {
            VPlayer vPlayer = plugin.getVPlayer((Player) entity);
            if (vPlayer == null)
                return;
            if (vPlayer.isVampire()) {
                event.setFoodLevel(((Player) entity).getFoodLevel());
                event.setCancelled(true);
                // EntityUtil.sendHealthFoodUpdatePacket((Player) entity);
            }
        }
    }

    // -------------------------------------------- //
    // UPDATE
    // -------------------------------------------- //

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void updateOnJoin(PlayerJoinEvent event) {
        plugin.loadVPlayerFromDB(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void updateOnQuit(PlayerQuitEvent event) {
        plugin.saveVPlayer(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void updateOnTeleport(PlayerTeleportEvent event) {
        final Player player = event.getPlayer();
        if (EntityUtil.isPlayer(player)) {
            VPlayer vPlayer = plugin.getVPlayer(player);
            if (vPlayer == null) {
                plugin.log(Level.WARNING, "There was an error while loading " + player.getName() + " data");
                return;
            }
            vPlayer.update();
            vPlayer.updateBatusiOnTeleport();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void updateOnDeath(PlayerDeathEvent event) {
        if (plugin.getVampireConfig().general.isBlacklisted(event.getEntity().getWorld()))
            return;
        // If a vampire dies ...
        Player entity = event.getEntity();
        if (EntityUtil.isPlayer(entity)) {
            VPlayer vPlayer = plugin.getVPlayer(entity);
            if (vPlayer == null) {
                plugin.log(Level.WARNING, "There was an error while loading " + entity.getName() + " data");
                return;
            }
            if (vPlayer.isVampire()) {
                // Close down bloodlust.
                vPlayer.setRad(0);
                vPlayer.setBloodlusting(false);
                entity.setFireTicks(0);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void updateOnRespawn(PlayerRespawnEvent event) {
        if (plugin.getVampireConfig().general.isBlacklisted(event.getPlayer().getWorld()))
            return;
        // If a vampire respawns ...
        Player player = event.getPlayer();

        if (EntityUtil.isPlayer(player)) {
            VPlayer vPlayer = plugin.getVPlayer(player);
            if (vPlayer == null)
                return;
            // ... modify food and health levels and force another speed-update.
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (vPlayer.isVampire()) {
                    PluginConfig conf = plugin.getVampireConfig();
                    player.setFoodLevel(conf.vampire.respawnFood);
                    player.setHealth(conf.vampire.respawnHealth);
                    EntityUtil.sendHealthFoodUpdatePacket(player);
                }
                vPlayer.update();
            });
        }
    }

    // -------------------------------------------- //
    // DROP SELF
    // -------------------------------------------- //

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void dropSelf(BlockBreakEvent event) {
        if (plugin.getVampireConfig().general.isBlacklisted(event.getPlayer().getWorld()))
            return;
        // If a non-creative player ...
        Player player = event.getPlayer();
        PluginConfig conf = plugin.getVampireConfig();
        if (EntityUtil.isPlayer(player) && player.getGameMode() != GameMode.CREATIVE) {
            // ... broke a self-dropping block ...
            Material material = event.getBlock().getType();
            if (conf.general.dropSelfMaterials.contains(material)) {
                // ... then we make it drop itself.
                event.setCancelled(true);
                event.getBlock().setType(Material.AIR);
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(material, 1));
            }
        }
    }

    // -------------------------------------------- //
    // BLOODLUST
    // -------------------------------------------- //

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void bloodlustSmokeTrail(PlayerMoveEvent event) {
        if (plugin.getVampireConfig().general.isBlacklisted(event.getPlayer().getWorld()))
            return;
        // If a noncreative player ...
        Player player = event.getPlayer();
        if (!EntityUtil.isPlayer(player)
                || player.getGameMode() == GameMode.CREATIVE
                || player.getGameMode() == GameMode.SPECTATOR)
            return;
        // ... moved between two blocks ...
        Block from = event.getFrom().getBlock();
        Block to = event.getTo().getBlock();
        if (from.equals(to))
            return;
        // ... and that player is a vampire ...
        VPlayer vPlayer = plugin.getVPlayer(player);
        if (vPlayer == null)
            return;
        // ... that has bloodlust on ...
        if (vPlayer.isVampire() && vPlayer.isBloodlusting()) {
            // ... then spawn smoke trail.
            PluginConfig conf = plugin.getVampireConfig();
            Location one = event.getFrom().clone();
            Location two = one.clone().add(0, 1, 0);
            long count1 = MathUtil.probabilityRound(conf.vampire.bloodlust.smokes);
            long count2 = MathUtil.probabilityRound(conf.vampire.bloodlust.smokes);
            for (long i = count1; i > 0; i--) FxUtil.smoke(one);
            for (long i = count2; i > 0; i--) FxUtil.smoke(two);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void bloodlustGameModeToggle(PlayerGameModeChangeEvent event) {
        if (plugin.getVampireConfig().general.isBlacklisted(event.getPlayer().getWorld()))
            return;
        // If a player enters creative or spectator mode ...
        if (event.getNewGameMode() == GameMode.CREATIVE || event.getNewGameMode() == GameMode.SPECTATOR) {
            // ... turn of bloodlust ...
            Player player = event.getPlayer();
            if (EntityUtil.isPlayer(player)) {
                VPlayer vPlayer = plugin.getVPlayer(player);
                if (vPlayer == null)
                    return;
                if (vPlayer.isBloodlusting()) {
                    vPlayer.setBloodlusting(false);
                }
            }
        }
    }

    // -------------------------------------------- //
    // TRUCE
    // -------------------------------------------- //

    @EventHandler(priority = EventPriority.LOW)
    public void truceTarget(EntityTargetEvent event) {
        if (plugin.getVampireConfig().general.isBlacklisted(event.getEntity().getWorld()))
            return;
        // If a player is targeted...
        if (EntityUtil.isPlayer(event.getTarget())) {
            plugin.debugLog(Level.INFO, "Player targeted by " + event.getEntityType().name());
            Player player = (Player) event.getTarget();

            // ... by creature that cares about the truce with vampires ...
            if (player != null && event.getEntity() instanceof Creature) {
                Creature creat = (Creature) event.getEntity();
                if (creat.getCategory() != EntityCategory.UNDEAD || creat.getType() == EntityType.WITHER)
                    return;
                plugin.debugLog(Level.INFO, "Entity in truce list");
                VPlayer vPlayer = plugin.getVPlayer(player);
                if (vPlayer == null)
                    return;
                // ... and that player is a vampire ...
                // ... that has not recently done something to break the truce...
                if (vPlayer.isVampire() && !vPlayer.truceIsBroken(ZonedDateTime.now().toInstant().toEpochMilli())) {
                    plugin.debugLog(Level.INFO, "Vampire detected, cancelling target");
                    event.setTarget(null);
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void truceDamage(EntityDamageEvent event) {
        if (plugin.getVampireConfig().general.isBlacklisted(event.getEntity().getWorld()))
            return;
        // If this is a combat event ...
        if (EventUtil.isCombatEvent(event)) {
            // ... to a creature that cares about the truce with vampires...
            PluginConfig conf = plugin.getVampireConfig();
            if (!conf.truce.entityTypes.contains(event.getEntityType()))
                return;
            // ... and the liable damager is a vampire ...
            Entity damager = EventUtil.getLiableDamager(event);

            if (EntityUtil.isPlayer(damager)) {
                if (conf.truce.checkGamemode && ((Player) damager).getGameMode() == GameMode.CREATIVE)
                    return;
                long now = ZonedDateTime.now().toInstant().toEpochMilli();
                VPlayer vpDamager = plugin.getVPlayer((Player) damager);
                if (vpDamager == null) {
                    plugin.log(Level.WARNING, "There was an error while loading " + damager.getName() + " data");
                    return;
                }
                if (vpDamager.isVampire()) {
                    // Then that vampire broke the truce.
                    vpDamager.truceBreak(now);
                }
            }
        }
    }

    // -------------------------------------------- //
    // REGEN
    // -------------------------------------------- //

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void regen(EntityDamageEvent event) {
        if (plugin.getVampireConfig().general.isBlacklisted(event.getEntity().getWorld()))
            return;
        // If the damagee is a vampire ...
        Entity entity = event.getEntity();

        if (EntityUtil.isPlayer(entity)) {
            final long timestamp = ZonedDateTime.now().toInstant().toEpochMilli();
            VPlayer vPlayer = plugin.getVPlayer((Player) entity);
            if (vPlayer == null) {
                plugin.log(Level.WARNING, "There was an error while loading " + entity.getName() + " data");
                return;
            }
            if (vPlayer.isVampire()) {
                // ... mark now as lastDamageMillis
                vPlayer.setLastDamageMillis(timestamp);
            }
        }
    }

    // -------------------------------------------- //
    // COMBAT
    // -------------------------------------------- //

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void combatVulnerability(EntityDamageEvent event) {
        if (plugin.getVampireConfig().general.isBlacklisted(event.getEntity().getWorld()))
            return;
        // If this is a close combat event ...
        if (EventUtil.isCloseCombatEvent(event)) {
            // ... where the liable damager is a human entity ...
            Entity damagerEntity = EventUtil.getLiableDamager(event);
            // ... and the damagee is a player ...
            Entity entity = event.getEntity();

            if (damagerEntity instanceof HumanEntity && EntityUtil.isPlayer(entity)) {
                HumanEntity damager = (HumanEntity) damagerEntity;
                PluginConfig conf = plugin.getVampireConfig();
                // ... and the damagee is a vampire ...
                VPlayer vPlayer = plugin.getVPlayer((Player) entity);
                if (vPlayer == null)
                    return;
                if (vPlayer.isVampire()) {
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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void disableBatusiCheck(EntityDamageEvent event) {
        if (plugin.getVampireConfig().vampire.batusi.disableOnHit
                && EntityUtil.isPlayer(event.getEntity())) {
            Player damagee = (Player) event.getEntity();
            VPlayer vDamagee = plugin.getVPlayer(damagee);
            if (vDamagee == null) {
                plugin.log(Level.WARNING, "There was an error while loading " + damagee.getName() + " data");
                return;
            }
            if (vDamagee.isVampire() && vDamagee.isBatusi()) {
                vDamagee.setBatusi(false, 0);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void combatStrength(EntityDamageEvent event) {
        if (plugin.getVampireConfig().general.isBlacklisted(event.getEntity().getWorld()))
            return;
        // If this is a close combat event ...
        if (EventUtil.isCloseCombatEvent(event)) {
            // ... and the liable damager is a vampire ...
            Entity damager = EventUtil.getLiableDamager(event);
            if (EntityUtil.isPlayer(damager)) {
                VPlayer vPlayer = plugin.getVPlayer((Player) damager);
                if (vPlayer == null)
                    return;
                if (vPlayer.isVampire()) {
                    PluginConfig conf = plugin.getVampireConfig();
                    // ... and this event isn't a forbidden mcmmo one ...
                    if (conf.compatibility.damageWithMcmmo
                            || !event.getClass().getName().equals("com.gmail.nossr50.events.fake.FakeEntityDamageByEntityEvent")) {
                        // ... Then modify damage!
                        EventUtil.scaleDamage(event, vPlayer.combatDamageFactor());
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
        if (plugin.getVampireConfig().general.isBlacklisted(event.getEntity().getWorld()))
            return;
        // If this is a close combat event ...
        if (EventUtil.isCloseCombatEvent(event)) {
            // ... where there is one vampire and one non-vampire ...
            Entity damagee = event.getEntity();
            Entity damager = EventUtil.getLiableDamager(event);
            if (EntityUtil.isPlayer(damagee) && EntityUtil.isPlayer(damager)) {
                Player vampire = (Player) damagee;
                VPlayer vampiree = plugin.getVPlayer((Player) damagee);
                if (vampiree == null) {
                    plugin.log(Level.WARNING, "There was an error while loading " + damagee.getName() + " data");
                    return;
                }
                Player human = (Player) damager;
                VPlayer humanee = plugin.getVPlayer((Player) damager);
                if (humanee == null) {
                    plugin.log(Level.WARNING, "There was an error while loading " + damager.getName() + " data");
                    return;
                }
                if ((vampiree.isVampire() && humanee.isHuman())
                        || (humanee.isVampire() && vampiree.isHuman())) {
                    if (humanee.isVampire()) {
                        Player aux = vampire;
                        vampire = human;
                        human = aux;
                        VPlayer auxx = vampiree;
                        vampiree = humanee;
                        humanee = auxx;
                    }
                    // ... and the vampire is allowed to infect through combat ...
                    // ... and the human is allowed to contract through combat ...
                    // ... Then there is a risk for infection ...
                    if (resUtil.hasPermission(vampire, Perm.COMBAT_INFECT)
                            && resUtil.hasPermission(human, Perm.COMBAT_CONTRACT)
                            && ThreadLocalRandom.current().nextDouble() < vampiree.combatInfectRisk()) {
                        InfectionReason reason = vampiree.isIntending() ? InfectionReason.COMBAT_INTENDED : InfectionReason.COMBAT_MISTAKE;
                        humanee.addInfection(0.01D, reason, vampiree);
                    }
                }
            }
        }
    }

    // -------------------------------------------- //
    // INFECT HORSES
    // -------------------------------------------- //

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void infectHorse(EntityDamageEvent event) {
        if (plugin.getVampireConfig().general.isBlacklisted(event.getEntity().getWorld()))
            return;
        // If this is a close combat event ...
        // ... and the vampires can infect horses...
        PluginConfig conf = plugin.getVampireConfig();
        if (EventUtil.isCloseCombatEvent(event) && conf.infection.canInfectHorses) {
            // ... where there is one vampire ...
            // ... and one is a living horse ...
            Entity damager = EventUtil.getLiableDamager(event);
            // only horses, no mules donkeys or undead ones
            if (EntityUtil.isPlayer(damager) && event.getEntity() instanceof Horse) {
                VPlayer vPlayer = plugin.getVPlayer((Player) damager);
                if (vPlayer == null) {
                    plugin.log(Level.WARNING, "There was an error while loading " + damager.getName() + " data");
                    return;
                }
                // ... and the vampire is allowed to infect through combat ...
                if (vPlayer.isVampire() && resUtil.hasPermission(damager, Perm.COMBAT_INFECT)) {
                    Horse horse = (Horse) event.getEntity();
                    // If the horse has already died we exit
                    if (!horse.isValid() || horse.isDead()) {
                        return;
                    }
                    // ... Then there is a risk for infection ...
                    if (ThreadLocalRandom.current().nextDouble() < vPlayer.combatInfectRisk()) {
                        // ... then we spawn the new horse ...
                        horse.getWorld().spawnEntity(horse.getLocation(), ThreadLocalRandom.current().nextDouble() >= conf.infection.zombieHorseChance ? EntityType.SKELETON_HORSE : EntityType.ZOMBIE_HORSE);

                        // ... and we Thanos the old one ...
                        horse.remove();
                    }
                }
            }
        }
    }

    // -------------------------------------------- //
    // FOOD
    // -------------------------------------------- //

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void foodCake(PlayerInteractEvent event) {
        if (plugin.getVampireConfig().general.isBlacklisted(event.getPlayer().getWorld()))
            return;
        Player player = event.getPlayer();
        PluginConfig conf = plugin.getVampireConfig();
        // If cake eating is not allowed for vampires ...
        if (EntityUtil.isPlayer(player) && !conf.vampire.canEatCake) {
            // ... and the player right-clicks a cake block ...
            Block clicked = event.getClickedBlock();
            if (clicked != null
                    && event.getAction() == Action.RIGHT_CLICK_BLOCK
                    && clicked.getType() == Material.CAKE) {
                // ... and the player is a vampire ...
                VPlayer vPlayer = plugin.getVPlayer(player);
                if (vPlayer == null || !vPlayer.isVampire())
                    return;
                // ... we deny!
                event.setCancelled(true);
                plugin.sendMessage(player,
                        MessageType.ERROR,
                        VampirismMessageKeys.CANT_EAT_ITEM,
                        "{item}", clicked.getType().name());
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void foodBlood(EntityDamageEvent event) {
        if (plugin.getVampireConfig().general.isBlacklisted(event.getEntity().getWorld()))
            return;
        // If this is a close combat event ...
        // ... to a living entity ...
        if (EventUtil.isCloseCombatEvent(event) && event.getEntity() instanceof LivingEntity) {
            LivingEntity damagee = (LivingEntity) event.getEntity();
            PluginConfig conf = plugin.getVampireConfig();
            // ... of a tasty type ...
            double fullFoodQuotient = conf.fullFoodQuotient.getOrDefault(damagee.getType(), 0D);
            if (fullFoodQuotient != 0) {
                // ... that has blood left ...
                if (damagee.isValid() && !damagee.isDead() && damagee.getHealth() > 0) {
                    // ... and the liable damager is a vampire ...
                    Entity damager = EventUtil.getLiableDamager(event);
                    if (EntityUtil.isPlayer(damager)) {
                        VPlayer vPlayer = plugin.getVPlayer((Player) damager);
                        if (vPlayer == null) {
                            plugin.log(Level.WARNING, "There was an error while loading " + damager.getName() + " data");
                            return;
                        }
                        // ... and the player is still retrievable ...
                        if (vPlayer.isVampire()) {
                            // ... drink blood! ;,,;
                            double damage = event.getFinalDamage();
                            if (conf.general.useOldFoodFormula)
                                damage = event.getDamage();
                            if (damagee.getHealth() < damage)
                                damage = damagee.getHealth();

                            double food = damage * fullFoodQuotient;
                            if (conf.general.useOldFoodFormula) {
                                AttributeInstance maxHealthDamagee = damagee.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                                double baseMaxHealthDamagee = 20;
                                if (maxHealthDamagee != null)
                                    baseMaxHealthDamagee = maxHealthDamagee.getBaseValue();
                                AttributeInstance maxHealthDamager = damagee.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                                double baseMaxHealthDamager = 20;
                                if (maxHealthDamager != null)
                                    baseMaxHealthDamager = maxHealthDamager.getBaseValue();
                                food = damage / baseMaxHealthDamagee * fullFoodQuotient * baseMaxHealthDamager;
                            }
                            vPlayer.addFood(food);
                        }
                    }
                }
            }
        }
    }

    // -------------------------------------------- //
    // BLOOD FLASK
    // -------------------------------------------- //

    @EventHandler(priority = EventPriority.LOW)
    public void bloodFlaskConsume(PlayerItemConsumeEvent event) {
        if (plugin.getVampireConfig().general.isBlacklisted(event.getPlayer().getWorld()))
            return;

        ItemStack item = event.getItem();
        // If the item is a potion ...
        if (!item.getType().equals(Material.POTION))
            return;

        // ... and is a blood flask ...
        BloodFlaskUtil.BloodFlaskData bloodFlaskData = bloodUtil.getBloodFlaskData(item);
        if (bloodFlaskData == null)
            return;
        // ... get the blood amount ...
        double amount = bloodFlaskData.getAmount();

        // ... and is the blood vampiric?  ...
        boolean isVampiric = bloodFlaskData.isVampiric();

        // ... get the player ...
        VPlayer vPlayer = plugin.getVPlayer(event.getPlayer());
        if (vPlayer == null)
            return;

        // ... are they bloodlusting? ...
        if (!vPlayer.isBloodlusting()) {
            // ... calculate and add the blood amount to the player ...
            double lacking;
            if (vPlayer.isVampire()) {
                // Vampires drink blood to replenish food.
                lacking = (20 - vPlayer.getFood());
                if (amount > lacking) amount = Math.ceil(lacking);
                vPlayer.addFood(amount);
            } else if (isVampiric) {
                // ... finally, if the player is human did they contract the dark disease from vampiric blood?
                if (vPlayer.isInfected()) {
                    vPlayer.addInfection(0.01D);
                } else if (event.getPlayer().hasPermission("vampire.flask.contract")
                        && ThreadLocalRandom.current().nextDouble() * 20 < amount) {
                    vPlayer.addInfection(0.05D, InfectionReason.FLASK, bloodFlaskData.getOwner());
                }
            }
        } else {
            plugin.sendMessage(event.getPlayer(),
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
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void holyWater(ProjectileHitEvent event) {
        // If this projectile is a thrown potion ...
        Projectile projectile = event.getEntity();
        PluginConfig conf = plugin.getVampireConfig();
        if (!conf.general.isBlacklisted(projectile.getWorld()) &&
                projectile instanceof ThrownPotion) {
            // ... and the potion type is holy water ...
            if (holyUtil.isHolyWater((ThrownPotion) projectile)) {
                ProjectileSource shooter = projectile.getShooter();
                Component displayName = null;
                if (shooter instanceof Player) {
                    displayName = Component.text(((Player) shooter).getDisplayName());
                }
                else if (shooter instanceof BlockProjectileSource) {
                    displayName = Component.text(((BlockProjectileSource) shooter).getBlock().getType().name());
                }
                else if (shooter instanceof Entity) {
                    displayName = Component.text(((Entity) shooter).getName());
                }
                else if (shooter != null) {
                    displayName = Component.text("???");
                }
                // ... who is the thrower and where did it splash? ...
                Location splashLocation = projectile.getLocation();
                // ... then to all nearby players ...
                for (Player player : splashLocation.getWorld().getPlayers()) {
                    if (EntityUtil.isPlayer(player)
                            && player.getLocation().distance(splashLocation) <= conf.holyWater.splashRadius) {
                        VPlayer vPlayer = plugin.getVPlayer(player);
                        if (vPlayer == null) {
                            plugin.log(Level.WARNING, "There was an error while loading " + player.getName() + " data");
                            return;
                        }
                        if (displayName != null) {
                            plugin.sendMessage(player,
                                    MessageType.INFO,
                                    HolyWaterMessageKeys.COMMON_REACT,
                                    new Tuple<>("{player}", displayName));
                        }
                        vPlayer.runFxEnderBurst();

                        // Trigger a damage event so other plugins can cancel this.
                        EntityDamageEvent triggeredEvent = plugin.getVersionCompat().getProjectileDamageEvent(player, projectile, 1D);
                        Bukkit.getPluginManager().callEvent(triggeredEvent);
                        if (!triggeredEvent.isCancelled()) {
                            if (vPlayer.isHealthy()) {
                                plugin.sendMessage(player,
                                        MessageType.INFO,
                                        HolyWaterMessageKeys.HEALTHY_REACT);
                            } else if (vPlayer.isInfected()) {
                                plugin.sendMessage(player,
                                        MessageType.INFO,
                                        HolyWaterMessageKeys.INFECTED_REACT);
                                vPlayer.setInfection(0);
                                vPlayer.runFxEnder();
                            } else if (vPlayer.isVampire()) {
                                plugin.sendMessage(player,
                                        MessageType.INFO,
                                        HolyWaterMessageKeys.VAMPIRE_REACT);
                                vPlayer.addTemp(conf.holyWater.temperature);
                                vPlayer.runFxFlameBurst();
                            }
                        }
                    }
                }
            }
        }
    }

    // -------------------------------------------- //
    // ALTARS
    // -------------------------------------------- //

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void altars(PlayerInteractEvent event) {
        if (plugin.getVampireConfig().general.isBlacklisted(event.getPlayer().getWorld()))
            return;
        plugin.debugLog(Level.INFO, "Interacted with something");
        // If the player right-clicked a block ...
        Action action = event.getAction();
        // ... without a placeable item in hand ...
        // ... and the event isn't fired with the off-hand ...
        ItemStack item = event.getItem();
        if (action == Action.RIGHT_CLICK_BLOCK &&
                event.getHand() != EquipmentSlot.OFF_HAND &&
                (!plugin.getVampireConfig().altar.checkIfBlockInHand
                        || (!event.isBlockInHand() && !(item != null && item.getType() == Material.REDSTONE)))) {
            plugin.debugLog(Level.INFO, "Without block in hand");
            // ... run altar logic.
            Player player = event.getPlayer();
            if (EntityUtil.isPlayer(player)) {
                plugin.debugLog(Level.INFO, "a real player");

                if (plugin.getAltarDark().evalBlockUse(event.getClickedBlock(), player) || plugin.getAltarLight().evalBlockUse(event.getClickedBlock(), player)) {
                    plugin.debugLog(Level.INFO, "and is an altar!");
                    event.setCancelled(true);
                }
            }
        }
    }
}
