package com.clanjhoo.vampire.Listeners;

import com.clanjhoo.vampire.*;
import com.clanjhoo.vampire.entity.MConf;
import com.clanjhoo.vampire.entity.UPlayer;
import com.clanjhoo.vampire.util.EventUtil;
import com.clanjhoo.vampire.util.FxUtil;
import com.clanjhoo.vampire.util.EntityUtil;
import com.clanjhoo.vampire.util.MathUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
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

public class ListenerMain implements Listener {
    // -------------------------------------------- //
    // INSTANCE & CONSTRUCT
    // -------------------------------------------- //

    private final VampireRevamp plugin;

    public ListenerMain(VampireRevamp plugin) {
        this.plugin = plugin;
    }

    // -------------------------------------------- //
    // FX
    // -------------------------------------------- //

    @EventHandler(priority = EventPriority.NORMAL)
    public void fxOnDeath(EntityDeathEvent event) {
        // If a vampire dies ...
        if (EntityUtil.isPlayer(event.getEntity())) {
            UPlayer uplayer = UPlayer.get((Player) event.getEntity());

            if (uplayer != null && uplayer.isVampire()) {
                // ... burns up with a violent scream ;,,;
                uplayer.runFxShriek();
                uplayer.runFxFlameBurst();
                uplayer.runFxSmokeBurst();
            }
        }
    }

    // -------------------------------------------- //
    // MISC
    // -------------------------------------------- //

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void blockDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        MConf mconf = plugin.mConf;

        if (mconf.getBlockDamageFrom().contains(event.getCause())) {
            if (EntityUtil.isPlayer(entity)) {
                UPlayer uplayer = UPlayer.get((Player) entity);

                if (uplayer != null && uplayer.isVampire()) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void blockRegainHealth(EntityRegainHealthEvent event) {
        Entity entity = event.getEntity();
        MConf mconf = plugin.mConf;

        if (mconf.getBlockHealthFrom().contains(event.getRegainReason())) {
            if (EntityUtil.isPlayer(entity)) {
                UPlayer uplayer = UPlayer.get((Player) entity);

                if (uplayer != null && uplayer.isVampire()) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void blockFoodChange(FoodLevelChangeEvent event) {
        Entity entity = event.getEntity();
        if (EntityUtil.isPlayer(entity)) {
            UPlayer uplayer = UPlayer.get((Player) entity);

            if (uplayer != null && uplayer.isVampire()) {
                event.setCancelled(true);
                EntityUtil.sendHealthFoodUpdatePacket((Player) entity);
            }
        }
    }

    // -------------------------------------------- //
    // UPDATE
    // -------------------------------------------- //

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void updateOnJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        if (EntityUtil.isPlayer(player)) {
            UPlayer uplayer = UPlayer.get(player);

            if (uplayer == null) {
                uplayer = plugin.uPlayerColl.add(event.getPlayer());
            }
            uplayer.update();
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void updateOnQuit(PlayerQuitEvent event) {
        plugin.uPlayerColl.savePlayer(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void updateOnTeleport(PlayerTeleportEvent event) {
        final Player player = event.getPlayer();
        if (EntityUtil.isPlayer(player)) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    UPlayer uplayer = UPlayer.get(player);
                    uplayer.update();
                }
            });
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void updateOnDeath(EntityDeathEvent event) {
        // If a vampire dies ...
        LivingEntity entity = event.getEntity();

        if (EntityUtil.isPlayer(entity)) {
            UPlayer uplayer = UPlayer.get((Player) entity);

            if (uplayer != null && uplayer.isVampire()) {
                // Close down bloodlust.
                uplayer.setRad(0);
                uplayer.setTemp(0);
                uplayer.setBloodlusting(false);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void updateOnRespawn(PlayerRespawnEvent event) {
        // If a vampire respawns ...
        Player player = event.getPlayer();

        if (EntityUtil.isPlayer(player)) {
            UPlayer uplayer = UPlayer.get(player);

            if (uplayer != null && uplayer.isVampire()) {
                // ... modify food and health levels and force another speed-update.
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        MConf mconf = plugin.mConf;
                        player.setFoodLevel(mconf.getUpdateRespawnFood());
                        player.setHealth((double) mconf.getUpdateRespawnHealth());
                        EntityUtil.sendHealthFoodUpdatePacket(player);
                        uplayer.update();
                    }
                });
            }
        }
    }

    private void updateNameColor(Player player) {
        if (EntityUtil.isPlayer(player)) {
            MConf mconf = plugin.mConf;
            if (mconf.isUpdateNameColor()) {
                UPlayer uplayer = UPlayer.get(player);

                if (uplayer != null && uplayer.isVampire()) {
                    player.setDisplayName(mconf.getUpdateNameColorTo().toString() + ChatColor.stripColor(player.getDisplayName()));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void updateNameColor(AsyncPlayerChatEvent event) {
        updateNameColor(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void updateNameColor(PlayerJoinEvent event) {
        updateNameColor(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void updateNameColor(PlayerTeleportEvent event) {
        updateNameColor(event.getPlayer());
    }


    // -------------------------------------------- //
    // DROP SELF
    // -------------------------------------------- //

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void dropSelf(BlockBreakEvent event) {
        // If a non-creative player ...
        Player player = event.getPlayer();
        MConf mconf = plugin.mConf;
        if (EntityUtil.isPlayer(player) && player.getGameMode() != GameMode.CREATIVE) {
            // ... broke a self-dropping block ...
            Material material = event.getBlock().getType();
            if (mconf.getDropSelfMaterials().contains(material)) {
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
        // If a noncreative player ...
        Player player = event.getPlayer();
        if (EntityUtil.isPlayer(player) && player.getGameMode() != GameMode.CREATIVE) {
            // ... moved between two blocks ...
            Block from = event.getFrom().getBlock();
            Block to = event.getTo().getBlock();
            if (!from.equals(to)) {
                UPlayer uplayer = UPlayer.get(player);

                // ... and that player is a vampire ...
                // ... that has bloodlust on ...
                if (uplayer != null && uplayer.isVampire() && uplayer.isBloodlusting()) {
                    // ... then spawn smoke trail.
                    MConf mconf = plugin.mConf;
                    Location one = event.getFrom().clone();
                    Location two = one.clone().add(0, 1, 0);
                    long count1 = MathUtil.probabilityRound(mconf.getBloodlustSmokes());
                    long count2 = MathUtil.probabilityRound(mconf.getBloodlustSmokes());
                    for (long i = count1; i > 0; i--) FxUtil.smoke(one);
                    for (long i = count2; i > 0; i--) FxUtil.smoke(two);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void bloodlustGameModeToggle(PlayerGameModeChangeEvent event) {
        // If a player enters creative or spectator mode ...
        if (event.getNewGameMode() == GameMode.CREATIVE || event.getNewGameMode() == GameMode.SPECTATOR) {
            // ... turn of bloodlust ...
            Player player = event.getPlayer();
            if (EntityUtil.isPlayer(player)) {
                UPlayer uplayer = UPlayer.get(player);

                if (uplayer != null && uplayer.isBloodlusting()) {
                    uplayer.setBloodlusting(false);
                }
            }
        }
    }

    // -------------------------------------------- //
    // TRUCE
    // -------------------------------------------- //

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void truceTarget(EntityTargetEvent event) {
        // If a player is targeted...
        if (EntityUtil.isPlayer(event.getTarget())) {
            Player player = (Player) event.getTarget();
            MConf mconf = plugin.mConf;

            // ... by creature that cares about the truce with vampires ...
            if (mconf.getTruceEntityTypes().contains(event.getEntityType())) {
                UPlayer uplayer = UPlayer.get(player);

                // ... and that player is a vampire ...
                // ... that has not recently done something to break the truce...
                if (uplayer != null && uplayer.isVampire() && !uplayer.truceIsBroken()) {
                    /*
                    // ... then if the player is a ghast target nothing ...
                    if (event.getEntityType() == EntityType.GHAST) {
                        event.setTarget(null);
                    } else {
                        // ... otherwise cancel the event.
                        event.setCancelled(true);
                    }
                    */
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void truceDamage(EntityDamageEvent event) {
        // If this is a combat event ...
        if (EventUtil.isCombatEvent(event)) {
            // ... to a creature that cares about the truce with vampires...
            MConf mconf = plugin.mConf;
            if (mconf.getTruceEntityTypes().contains(event.getEntityType())) {
                // ... and the liable damager is a vampire ...
                Entity damager = EventUtil.getLiableDamager(event);

                if (EntityUtil.isPlayer(damager)) {
                    UPlayer vpdamager = UPlayer.get((Player) damager);

                    if (vpdamager != null && vpdamager.isVampire()) {
                        // Then that vampire broke the truce.
                        vpdamager.truceBreak();
                    }
                }
            }
        }
    }

    // -------------------------------------------- //
    // REGEN
    // -------------------------------------------- //

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void regen(EntityDamageEvent event) {
        // If the damagee is a vampire ...
        Entity entity = event.getEntity();

        if (EntityUtil.isPlayer(entity)) {
            UPlayer vampire = UPlayer.get((Player) entity);

            if (vampire != null && vampire.isVampire()) {
                // ... mark now as lastDamageMillis
                vampire.setLastDamageMillis(System.currentTimeMillis());
            }
        }
    }

    // -------------------------------------------- //
    // COMBAT
    // -------------------------------------------- //

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void combatVulnerability(EntityDamageEvent event) {
        // If this is a close combat event ...
        if (EventUtil.isCloseCombatEvent(event)) {
            // ... where the liable damager is a human entity ...
            Entity damagerEntity = EventUtil.getLiableDamager(event);
            // ... and the damagee is a player ...
            Entity entity = event.getEntity();

            if (damagerEntity instanceof HumanEntity && EntityUtil.isPlayer(entity)) {
                HumanEntity damager = (HumanEntity) damagerEntity;
                MConf mconf = plugin.mConf;

                // ... and the damagee is a vampire ...
                UPlayer vampire = UPlayer.get((Player) entity);
                if (vampire != null && vampire.isVampire()) {
                    // ... and a wooden item was used ...
                    ItemStack item = EntityUtil.getWeapon(damager);
                    if (item != null && mconf.getCombatWoodMaterials().contains(item.getType())) {
                        // ... Then modify damage!
                        EventUtil.setDamage(event, mconf.getCombatWoodDamage());
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void combatStrength(EntityDamageEvent event) {
        // If this is a close combat event ...
        if (EventUtil.isCloseCombatEvent(event)) {
            // ... and the liable damager is a vampire ...
            Entity damager = EventUtil.getLiableDamager(event);
            if (EntityUtil.isPlayer(damager)) {
                UPlayer vampire = UPlayer.get((Player) damager);

                if (vampire != null && vampire.isVampire()) {
                    // ... and this event isn't a forbidden mcmmo one ...
                    if (plugin.mConf.isCombatDamageFactorWithMcmmoAbilities()
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
        // If this is a close combat event ...
        if (EventUtil.isCloseCombatEvent(event)) {
            // ... where there is one vampire and one non-vampire ...
            Entity damagee = event.getEntity();
            Entity damager = EventUtil.getLiableDamager(event);
            if (EntityUtil.isPlayer(damagee) && EntityUtil.isPlayer(damager)) {
                UPlayer vampire = UPlayer.get((Player) damagee);
                UPlayer human = UPlayer.get((Player) damager);
                if (vampire != null && human != null
                        && ((vampire.isVampire() && human.isHuman())
                        || (human.isVampire() && vampire.isHuman()))) {
                    if (human.isVampire()) {
                        UPlayer aux = vampire;
                        vampire = human;
                        human = aux;
                    }

                    // ... and the vampire is allowed to infect through combat ...
                    // ... and the human is allowed to contract through combat ...
                    // ... Then there is a risk for infection ...
                    if (Perm.COMBAT_INFECT.has(vampire.getPlayer())
                            && Perm.COMBAT_CONTRACT.has(human.getPlayer())
                            && MathUtil.random.nextDouble() < vampire.combatInfectRisk()) {
                        InfectionReason reason = vampire.isIntending() ? InfectionReason.COMBAT_INTENDED : InfectionReason.COMBAT_MISTAKE;
                        human.addInfection(0.01D, reason, vampire);
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
        // If this is a close combat event ...
        // ... and the vampires can infect horses...
        MConf mconf = plugin.mConf;
        if (EventUtil.isCloseCombatEvent(event) && mconf.isCanInfectHorses()) {
            // ... where there is one vampire ...
            // ... and one is a living horse ...
            Entity damager = EventUtil.getLiableDamager(event);
            // only horses, no mules donkeys or undead ones
            if (EntityUtil.isPlayer(damager) && event.getEntity() instanceof Horse) {
                UPlayer vampire = UPlayer.get((Player) damager);
                // ... and the vampire is allowed to infect through combat ...
                if (vampire != null && vampire.isVampire() && Perm.COMBAT_INFECT.has(vampire.getPlayer())) {
                    Horse horse = (Horse) event.getEntity();

                    // ... Then there is a risk for infection ...
                    if (MathUtil.random.nextDouble() < vampire.combatInfectRisk()) {
                        // ... then we spawn the new horse ...
                        horse.getWorld().spawnEntity(horse.getLocation(), MathUtil.random.nextDouble() > 0.5 ? EntityType.SKELETON_HORSE : EntityType.ZOMBIE_HORSE);

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

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void foodCake(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        MConf mconf = plugin.mConf;
        // If cake eating is not allowed for vampires ...
        if (EntityUtil.isPlayer(player) && !mconf.isFoodCakeAllowed()) {
            // .. and the player right-clicks a cake block ...
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.CAKE) {
                // ... and the player is a vampire ...
                UPlayer uplayer = UPlayer.get(player);
                if (uplayer != null && uplayer.isVampire()) {
                    // ... we deny!
                    event.setCancelled(true);
                    uplayer.msg(plugin.mLang.foodCantEat, "cake");
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void foodBlood(EntityDamageEvent event) {
        // If this is a close combat event ...
        // ... to a living entity ...
        if (EventUtil.isCloseCombatEvent(event) && event.getEntity() instanceof LivingEntity) {
            LivingEntity damagee = (LivingEntity) event.getEntity();
            MConf mconf = plugin.mConf;

            // ... of a tasty type ...
            Double fullFoodQuotient = mconf.getEntityTypeFullFoodQuotient().get(damagee.getType());
            if (fullFoodQuotient != null && fullFoodQuotient > 0) {
                // ... that has blood left ...
                if (damagee != null && damagee.isValid() && !damagee.isDead() && damagee.getHealth() > 0) {
                    // ... and the liable damager is a vampire ...
                    Entity damager = EventUtil.getLiableDamager(event);
                    if (EntityUtil.isPlayer(damager)) {
                        UPlayer vampire = UPlayer.get((Player) damager);
                        // ... and the player is still retrievable ...
                        if (vampire != null && vampire.isVampire() && vampire.getPlayer() != null) {
                            // ... drink blood! ;,,;
                            double damage = event.getDamage();
                            if (damagee.getHealth() < damage) damage = damagee.getHealth();
                            double food = damage / damagee.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() * fullFoodQuotient * vampire.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();

                            vampire.getFood().add(food);
                        }
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
        ItemStack item = event.getItem();
        // If the item is a potion ...
        // ... and is a blood flask ...
        if (item.getType().equals(Material.POTION) && BloodFlaskUtil.isBloodFlask(item)) {
            // ... get the blood amount ...
            double amount = BloodFlaskUtil.getBloodFlaskAmount(item);

            // ... and is the blood vampiric?  ...
            boolean isVampiric = BloodFlaskUtil.isBloodFlaskVampiric(item);

            // ... get the player ...
            UPlayer uplayer = UPlayer.get(event.getPlayer());

            if (uplayer != null) {
                // ... are they bloodlusting? ...
                if (!uplayer.isBloodlusting()) {
                    // ... calculate and add the blood amount to the player ...
                    double lacking;
                    if (uplayer.isVampire()) {
                        // Vampires drink blood to replenish food.
                        lacking = (20 - uplayer.getFood().get());
                        if (amount > lacking) amount = lacking;
                        uplayer.getFood().add(amount);
                    } else if (isVampiric) {
                        // ... finally, if the player is human did they contract the dark disease from vampiric blood?
                        if (uplayer.isInfected()) {
                            uplayer.addInfection(0.01D);
                        } else if (MathUtil.random.nextDouble() * 20 < amount) {
                            uplayer.addInfection(0.05D, InfectionReason.FLASK, uplayer);
                        }
                    }
                } else {
                    uplayer.msg(plugin.mLang.flaskBloodlusting);
                    event.setCancelled(true);
                }
            }
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
        MConf mconf = plugin.mConf;
        if (projectile instanceof ThrownPotion) {
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
                                && player.getLocation().distance(splashLocation) <= mconf.getHolyWaterSplashRadius()) {
                            UPlayer uplayer = UPlayer.get(player);
                            uplayer.msg(plugin.mLang.holyWaterCommon, shooter.getDisplayName());
                            uplayer.runFxEnderBurst();

                            // Trigger a damage event so other plugins can cancel this.
                            EntityDamageByEntityEvent triggeredEvent = new EntityDamageByEntityEvent(shooter, player, DamageCause.CUSTOM, 1D);
                            Bukkit.getPluginManager().callEvent(triggeredEvent);
                            if (!triggeredEvent.isCancelled()) {
                                if (uplayer.isHealthy()) {
                                    uplayer.msg(plugin.mLang.holyWaterHealthy);
                                } else if (uplayer.isInfected()) {
                                    uplayer.msg(plugin.mLang.holyWaterInfected);
                                    uplayer.setInfection(0);
                                    uplayer.runFxEnder();
                                } else if (uplayer.isVampire()) {
                                    uplayer.msg(plugin.mLang.holyWaterVampire);
                                    uplayer.addTemp(mconf.getHolyWaterTemp());
                                    uplayer.runFxFlameBurst();
                                }
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

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void altars(PlayerInteractEvent event) {
        // If the player right-clicked a block ...
        Action action = event.getAction();
        // ... without a placeable item in hand ...
        // ... and the event isn't fired with the off-hand ...
        if (action == Action.RIGHT_CLICK_BLOCK && !event.isBlockInHand() && event.getHand() != EquipmentSlot.OFF_HAND) {
            // ... run altar logic.
            Player player = event.getPlayer();
            if (EntityUtil.isPlayer(player)) {
                MConf mconf = plugin.mConf;

                if (mconf.getAltarDark().evalBlockUse(event.getClickedBlock(), player) || mconf.getAltarLight().evalBlockUse(event.getClickedBlock(), player)) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
