package com.clanjhoo.vampire.entity;

import co.aikar.commands.MessageType;
import com.clanjhoo.dbhandler.annotations.DataField;
import com.clanjhoo.dbhandler.annotations.Entity;
import com.clanjhoo.dbhandler.annotations.NotNullField;
import com.clanjhoo.dbhandler.annotations.PrimaryKey;
import com.clanjhoo.vampire.InfectionReason;
import com.clanjhoo.vampire.compat.VersionCompat;
import com.clanjhoo.vampire.compat.WorldGuardCompat;
import com.clanjhoo.vampire.keyproviders.*;
import com.clanjhoo.vampire.VampireRevamp;
import com.clanjhoo.vampire.config.PluginConfig;
import com.clanjhoo.vampire.config.RadiationEffectConfig;
import com.clanjhoo.vampire.config.StateEffectConfig;
import com.clanjhoo.vampire.event.InfectionChangeEvent;
import com.clanjhoo.vampire.event.VampireTypeChangeEvent;
import com.clanjhoo.vampire.util.*;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;


@Entity(table = "vampire_data")
public class VPlayer {
    // -------------------------------------------- //
    // PERSISTENT FIELDS
    // -------------------------------------------- //

    /**
     * PERSISTENT: UniqueID of this player
     */
    @PrimaryKey
    private UUID uuid;
    /**
     * PERSISTENT: whether or not the player is a vampire
     */
    @NotNullField
    @DataField(sqltype = "BOOL")
    private boolean vampire = false;
    /**
     * PERSISTENT: whether or not the player is a nosferatu (higher tier)
     */
    @NotNullField
    @DataField(sqltype = "BOOL")
    private boolean nosferatu = false;
    /**
     * PERSISTENT: degree of infection [0, 1]. 0 means no infection, 1 turns the player into a vampire
     */
    @NotNullField
    private double infection = 0;
    /**
     * PERSISTENT: how was this player infected
     */
    @DataField(sqltype = "VARCHAR(16)")
    private String reason;
    /**
     * PERSISTENT: UUID of the player who infected this player
     */
    private UUID makerUUID;
    /**
     * PERSISTENT: whether or not the vampire is trying to infect others in combat on purpose or not
     */
    @NotNullField
    @DataField(sqltype = "BOOL")
    private boolean intending = false;
    /**
     * PERSISTENT: whether or not to apply night vision effects to a vampire
     */
    @NotNullField
    @DataField(sqltype = "BOOL")
    private boolean usingNightVision = false;

    /**
     * TRANSIENT: set of regions this vampire is allowed to enter (worldguard enabled)
     */
    private final transient Set<String> allowedRegions = new ConcurrentSkipListSet<>();
    /**
     * TRANSIENT: whether or not the vampire is in bloodlust mode
     */
    private transient boolean bloodlusting = false;
    /**
     * TRANSIENT: Had enabled flight
     */
    private transient boolean hadFlight = false;
    /**
     * TRANSIENT: the irradiation for the player.
     */
    private transient double rad = 0;
    /**
     * TRANSIENT: the temperature of the player [0, 1]
     */
    private transient double temp = 0;
    /**
     * TRANSIENT: the food accumulator
     */
    private transient double foodRem = 0;
    /**
     * TRANSIENT: timestamp of the last time the player received damage
     */
    private transient long lastDamageMillis = 0;
    /**
     * TRANSIENT: timestamp of the last time the player used shriek
     */
    private transient long lastShriekMillis = 0;
    /**
     * TRANSIENT: milliseconds to wait before repeating shriek message
     */
    private transient long lastShriekWaitMessageMillis = 0;
    /**
     * TRANSIENT: when will the truce be restored
     */
    private transient long truceRestoreTimestamp = 0;
    /**
     * TRANSIENT: the player who offered the blood in a trade
     */
    private transient VPlayer tradeOfferedFrom = null;
    /**
     * TRANSIENT: the amount of blood offered in a trade
     */
    private transient double tradeOfferedAmount = 0;
    /**
     * TRANSIENT: timestamp of the moment the trade started
     */
    private transient long tradeOfferedAtMillis = 0;
    /**
     * TRANSIENT FX: smoke
     */
    private transient long fxSmokeMillis = 0;
    /**
     * TRANSIENT FX: ender
     */
    private transient long fxEnderMillis = 0;
    /**
     * TRANSIENT: is this player is changing its appearance due to batusi
     */
    private transient boolean isDisguising = false;
    /**
     * TRANSIENT: this is the result of the last raytrace
     */
    private transient RayTraceResult lastRayTrace = null;
    /**
     * TRANSIENT:
     */
    private final transient VampireRevamp plugin;
    private final transient EntityUtil entityUtil;
    private final transient RingUtil ringUtil;
    private final transient SunUtil sunUtil;


    // -------------------------------------------- //
    // CONSTRUCTORS
    // -------------------------------------------- //

    public VPlayer() {
        this.vampire = false;
        this.nosferatu = false; // You can't be nosferatu without being vampire
        this.infection = 0;
        this.reason = null;
        this.makerUUID = null;
        this.intending = false;
        this.usingNightVision = false;
        plugin = VampireRevamp.getInstance();
        entityUtil = new EntityUtil(plugin);
        ringUtil = new RingUtil(plugin);
        sunUtil = new SunUtil(plugin);
    }

    public void invite(String regionName) {
        allowedRegions.add(regionName);
    }

    public void expel(String regionName) {
        allowedRegions.remove(regionName);
    }

    // -------------------------------------------- //
    // Getters and setters
    // -------------------------------------------- //

    public void setLastRayTrace(RayTraceResult result) {
        lastRayTrace = result;
    }

    public RayTraceResult getLastRayTrace() {
        return lastRayTrace;
    }

    public boolean isVampire() {
        return this.vampire;
    }

    // Shortcut
    public boolean isHuman() {
        return !this.isVampire();
    }

    public void setVampire(boolean val) {
        if (this.vampire != val) {
            VampireTypeChangeEvent event = new VampireTypeChangeEvent(val, this);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                this.vampire = val;
                this.setNosferatu(false);
                if (!val) {
                    this.setMaker(null);
                    this.setReason(null);
                }
                this.setBloodlusting(false);
                this.setIntending(false);
                this.setUsingNightVision(false);
                this.setInfection(0);

                Player player = Bukkit.getPlayer(uuid);
                if (plugin.permissionGroupEnabled())
                    plugin.setVampireGroup(player, val);
                if (player != null) {
                    PluginConfig conf = plugin.getVampireConfig();
                    if (this.vampire) {
                        plugin.sendMessage(player,
                                MessageType.INFO,
                                VampirismMessageKeys.TURNED_VAMPIRE);
                        this.runFxShriek();
                        this.runFxSmokeBurst();
                        this.runFxSmoke();

                        conf.potionEffects.human.removePotionEffects(player);
                        // player.setSleepingIgnored(true);
                    } else {
                        plugin.sendMessage(player,
                                MessageType.INFO,
                                VampirismMessageKeys.CURED_VAMPIRE);
                        this.runFxEnder();

                        // player.setSleepingIgnored(false);
                        conf.potionEffects.nosferatu.removePotionEffects(player);
                        conf.potionEffects.vampire.removePotionEffects(player);
                    }

                    this.update();
                }
            }
        }
        else {
            this.setInfection(0);
        }
    }

    public void setNosferatu(boolean val) {
        this.nosferatu = val;
    }

    public boolean isNosferatu() {
        return this.isVampire() && this.nosferatu;
    }

    public double getInfection() {
        return this.infection;
    }

    public boolean isInfected() {
        return this.infection > 0D;
    }

    public void setInfection(double val) {
        if (this.infection != val) {
            // Call event
            InfectionChangeEvent event = new InfectionChangeEvent(val, this);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                val = event.getInfection();

                if (val >= 1D) {
                    this.setVampire(true);
                } else if (val <= 0D) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null) {
                        if (this.infection > 0D && !this.isVampire()) {
                            plugin.sendMessage(player,
                                    MessageType.INFO,
                                    InfectionMessageKeys.CURED);
                        }
                        this.infection = 0D;
                        plugin.getVampireConfig().potionEffects.infected.removePotionEffects(player);
                    }
                } else {
                    this.infection = val;
                }
                this.update();
            }
        }
    }

    public void addInfection(double val) {
        this.setInfection(this.getInfection() + val);
    }

    public void addInfection(double val, InfectionReason reason, VPlayer maker) {
        addInfection(val, reason, maker == null ? null : maker.getUuid());
    }

    public void addInfection(double val, InfectionReason reason, UUID makerUUID) {
        Player player = Bukkit.getPlayer(uuid);
        if (!vampire) {
            this.setReason(reason);
            this.setMakerUUID(makerUUID);

            String parent = null;
            if (reason.isMaker()) {
                parent = getMakerName();
            }
            if (parent == null || parent.isEmpty()) {
                parent = "someone";
            }

            // plugin.log(this.getReasonDesc(false));
            if (reason.isNoticeable())
                plugin.sendMessage(player,
                        MessageType.INFO,
                        reason.getDescKey(),
                        new Tuple<>("{player}", plugin.getMessage(player, GrammarMessageKeys.YOU)),
                        new Tuple<>("{to_be_past}", plugin.getMessage(player, GrammarMessageKeys.TO_BE_2ND_PAST)),
                        new Tuple<>("{parent}", Component.text(parent)));
            this.addInfection(val);
        }
    }

    // healthy and unhealthy - Fake field shortcuts
    public boolean isHealthy() {
        return !this.isVampire() && !this.isInfected();
    }

    public boolean isUnhealthy() {
        return !this.isHealthy();
    }

    public InfectionReason getReason() {
        return InfectionReason.fromName(reason);
    }

    public void setReason(InfectionReason reason) {
        this.reason = reason == null ? null : reason.name();
    }

    public UUID getMakerUUID() {
        return this.makerUUID;
    }

    public void setMakerUUID(UUID makerUUID) {
        this.makerUUID = makerUUID;
    }

    @Nullable
    public String getMakerName() {
        if (makerUUID == null)
            return null;
        return Bukkit.getOfflinePlayer(this.makerUUID).getName();
    }

    public void setMaker(VPlayer val) {
        this.setMakerUUID(val == null ? null : val.getUuid());
    }

    public boolean isIntending() {
        return this.intending;
    }

    public void setIntending(boolean val) {
        this.intending = val;
        Player p = Bukkit.getPlayer(uuid);

        if (p == null) {
            plugin.log(Level.WARNING, "An offline player is trying to infect on intend!");
            return;
        }
        if (!plugin.getVampireConfig().vampire.intend.enabled) {
            Component intendAction = plugin.getMessage(p, GrammarMessageKeys.INTEND);
            plugin.sendMessage(p,
                    MessageType.ERROR,
                    CommandMessageKeys.DISABLED_ACTION,
                    new Tuple<>("{action}", intendAction));
            return;
        }

        Component on = plugin.getMessage(p, GrammarMessageKeys.ON);
        Component off = plugin.getMessage(p, GrammarMessageKeys.OFF);

        plugin.sendMessage(p,
                MessageType.INFO,
                CommandMessageKeys.SHOW_INTENT,
                new Tuple<>("{enabled}", isIntending() ? on : off),
                new Tuple<>("{percent}", Component.text(String.format("%.1f", combatInfectRisk() * 100))));
    }

    public boolean isBloodlusting() {
        return this.bloodlusting;
    }

    public void setBloodlusting(boolean val) {
        plugin.debugLog(Level.INFO, "Changing bloodlust");
        Player me = Bukkit.getPlayer(uuid);
        if (me == null) {
            plugin.log(Level.WARNING, "An offline player is trying to bloodlust!");
            return;
        }
        PluginConfig conf = plugin.getVampireConfig();

        Component bloodlustAction = plugin.getMessage(me, GrammarMessageKeys.BLOODLUST);
        bloodlustAction = TextUtil.capitalizeFirst(bloodlustAction);

        if (this.bloodlusting == val) {
            // No real change - just view the info.
            plugin.debugLog(Level.INFO, "This is not a change!");
            Component on = plugin.getMessage(me, GrammarMessageKeys.ON);
            Component off = plugin.getMessage(me, GrammarMessageKeys.OFF);
            plugin.sendMessage(me,
                    MessageType.INFO,
                    GrammarMessageKeys.X_IS_Y,
                    new Tuple<>("{key}", bloodlustAction),
                    new Tuple<>("{value}", val ? on : off));
            return;
        }

        if (val) { // Enabling bloodlust
            // There are a few rules to when you can turn it on:
            plugin.debugLog(Level.INFO, "Za warudo has changed!");
            if (!this.isVampire()) {
                plugin.debugLog(Level.INFO, "Non non non again!");
                Component vampireType = plugin.getMessage(me, GrammarMessageKeys.VAMPIRE_TYPE);
                plugin.sendMessage(me,
                        MessageType.ERROR,
                        GrammarMessageKeys.ONLY_TYPE_CAN_ACTION,
                        new Tuple<>("{vampire_type}", vampireType),
                        new Tuple<>("{action}", bloodlustAction));
            } else if (this.getFood() != null && this.getFood() < conf.vampire.bloodlust.minFood) {
                plugin.debugLog(Level.INFO, "Too hungry!");
                plugin.sendMessage(me,
                        MessageType.ERROR,
                        SkillMessageKeys.BLOODLUST_LOW_FOOD);
            } else if (conf.vampire.bloodlust.checkGamemode &&
                            (me.getGameMode() == GameMode.CREATIVE ||
                             me.getGameMode() == GameMode.SPECTATOR)) { // or offline :P but offline players wont see the message
                plugin.debugLog(Level.INFO, "Le bad gamemode!");
                plugin.sendMessage(me,
                        MessageType.ERROR,
                        SkillMessageKeys.BLOODLUST_GAMEMODE_CHECK);
            } else if (!conf.vampire.bloodlust.enabled) {
                plugin.debugLog(Level.INFO, "Bloodlust config disabled!");
                plugin.sendMessage(me,
                        MessageType.ERROR,
                        CommandMessageKeys.DISABLED_ACTION,
                        new Tuple<>("{action}", bloodlustAction));
            } else {
                this.bloodlusting = true;
                plugin.debugLog(Level.INFO, "enabling bloodlust");
                this.update();
                plugin.debugLog(Level.INFO, "updated!");
                Component on = plugin.getMessage(me, GrammarMessageKeys.ON);
                plugin.sendMessage(me,
                        MessageType.INFO,
                        CommandMessageKeys.SHOW_BLOODLUST,
                        new Tuple<>("{bloodlust}", bloodlustAction),
                        new Tuple<>("{enabled}", on),
                        new Tuple<>("{percent}", Component.text(String.format("%.1f", this.combatDamageFactor() * 100))));
                plugin.debugLog(Level.INFO, "sent message to " + me);
            }
        }
        else { // Disabling bloodlust
            this.bloodlusting = false;
            conf.potionEffects.bloodlust.removePotionEffects(me);
            this.update();
            Component off = plugin.getMessage(me, GrammarMessageKeys.OFF);
            plugin.sendMessage(me,
                    MessageType.INFO,
                    CommandMessageKeys.SHOW_BLOODLUST,
                    new Tuple<>("{bloodlust}", bloodlustAction),
                    new Tuple<>("{enabled}", off),
                    new Tuple<>("{percent}", Component.text(String.format("%.1f", this.combatDamageFactor() * 100))));
        }
    }

    public boolean isUsingNightVision() {
        return this.usingNightVision;
    }

    public void setUsingNightVision(boolean val) {
        Player me = Bukkit.getPlayer(uuid);
        if (me == null) {
            plugin.log(Level.WARNING, "An offline player is trying to use nightvision!");
            return;
        }

        // If an actual change is being made ...
        if (this.usingNightVision != val) {
            PluginConfig conf = plugin.getVampireConfig();
            // ... do change stuff ...
            if (conf.vampire.nightvision.enabled) {
                this.usingNightVision = val;

                // ... remove the nightvision potion effects ...
                conf.potionEffects.nightvision.removePotionEffects(me);

                // ... trigger a potion effect update ...
                this.update();

                Component onString = val ?
                        plugin.getMessage(me, GrammarMessageKeys.ON) :
                        plugin.getMessage(me, GrammarMessageKeys.OFF);

                plugin.sendMessage(me,
                        MessageType.INFO,
                        CommandMessageKeys.SHOW_NIGHTVISION,
                        new Tuple<>("{enabled}", onString));
            }
            else {
                Component nightvisionAction = plugin.getMessage(me, GrammarMessageKeys.NIGHTVISION);
                plugin.sendMessage(me,
                        MessageType.ERROR,
                        CommandMessageKeys.DISABLED_ACTION,
                        new Tuple<>("{action}", nightvisionAction));
            }
        }
    }

    @NotNull
    public UUID getUuid() {
        return uuid;
    }

    @Nullable
    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public double getRad() {
        return this.rad;
    }

    public void setRad(double rad) {
        this.rad = rad;
    }

    public double getTemp() {
        return this.temp;
    }

    public void setTemp(double val) {
        this.temp = MathUtil.limitNumber(val, 0D, 1D);
    }

    public void addTemp(double val) {
        this.setTemp(this.getTemp() + val);
    }

    public Double getFood() {
        Double food = null;
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            food = this.foodRem + player.getFoodLevel();
        }
        return food;
    }

    public Integer addFood(double amount) {
        Integer added = null;
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            this.foodRem += amount;
            int diff = (int) this.foodRem;
            this.foodRem -= diff;
            int current = player.getFoodLevel();
            int next = current + diff;
            player.setFoodLevel(next);
            added = next - current;
        }
        return added;
    }

    public long getLastDamageMillis() {
        return this.lastDamageMillis;
    }

    public void setLastDamageMillis(long lastDamageMillis) {
        if (lastDamageMillis > this.lastDamageMillis)
            this.lastDamageMillis = lastDamageMillis;
    }

    public long getLastShriekMillis() {
        return this.lastShriekMillis;
    }

    public void setLastShriekMillis(long lastShriekMillis) {
        if (lastShriekMillis > this.lastShriekMillis)
            this.lastShriekMillis = lastShriekMillis;
    }

    public long getLastShriekWaitMessageMillis() {
        return this.lastShriekWaitMessageMillis;
    }

    public void setLastShriekWaitMessageMillis(long lastShriekWaitMessageMillis) {
        if (lastShriekWaitMessageMillis > this.lastShriekWaitMessageMillis)
            this.lastShriekWaitMessageMillis = lastShriekWaitMessageMillis;
    }

    public long getFxSmokeMillis() {
        return this.fxSmokeMillis;
    }

    public void setFxSmokeMillis(long fxSmokeMillis) {
        this.fxSmokeMillis = fxSmokeMillis;
    }

    public void runFxSmoke() {
        this.fxSmokeMillis = 20L * 1000L;
    }

    public long getFxEnderMillis() {
        return this.fxEnderMillis;
    }

    public void getFxEnderMillis(long fxEnderMillis) {
        this.fxEnderMillis = fxEnderMillis;
    }

    public void runFxEnder() {
        this.fxEnderMillis = 10L * 1000L;
    }

    // FX: Shriek
    public void runFxShriek() {
        Player me = Bukkit.getPlayer(uuid);
        if (me != null) {
            Location location = me.getLocation();
            World world = location.getWorld();
            world.playEffect(location, Effect.GHAST_SHRIEK, 0);
        }
    }

    // FX: SmokeBurst
    public void runFxSmokeBurst() {
        Player me = Bukkit.getPlayer(uuid);
        if (me != null) {
            double dcount = plugin.getVampireConfig().specialEffects.smokeBurstCount;
            long lcount = MathUtil.probabilityRound(dcount);
            for (long i = lcount; i > 0; i--) FxUtil.smoke(me);
        }
    }

    // FX: EnderBurst
    public void runFxEnderBurst() {
        Player me = Bukkit.getPlayer(uuid);
        if (me != null) {
            double dcount = plugin.getVampireConfig().specialEffects.enderBurstCount;
            long lcount = MathUtil.probabilityRound(dcount);
            for (long i = lcount; i > 0; i--) FxUtil.ender(me, 0);
        }
    }

    // FX: FlameBurst
    public void runFxFlameBurst() {
        Player me = Bukkit.getPlayer(uuid);
        if (me != null) {
            double dcount = plugin.getVampireConfig().specialEffects.flameBurstCount;
            long lcount = MathUtil.probabilityRound(dcount);
            for (long i = lcount; i > 0; i--) FxUtil.flame(me);
        }
    }

    // -------------------------------------------- //
    // SHRIEK
    // -------------------------------------------- //

    public void shriek() {
        // You must be online to shriek
        Player me = Bukkit.getPlayer(uuid);
        if (me == null) {
            plugin.log(Level.WARNING, "An offline player is trying to shriek!");
            return;
        }

        if (!plugin.getVampireConfig().vampire.intend.enabled) {
            Component shriekAction = plugin.getMessage(me, GrammarMessageKeys.SHRIEK);
            plugin.sendMessage(me,
                    MessageType.ERROR,
                    CommandMessageKeys.DISABLED_ACTION,
                    new Tuple<>("{action}", shriekAction));
            return;
        }

        // You must be a vampire to shriek
        if (this.isVampire()) {
            PluginConfig conf = plugin.getVampireConfig();
            long now = ZonedDateTime.now().toInstant().toEpochMilli();

            long millisSinceLastShriekWaitMessage = now - this.lastShriekWaitMessageMillis;
            if (millisSinceLastShriekWaitMessage >= conf.vampire.shriek.waitMessageCooldownMillis) {
                long millisSinceLastShriek = now - this.lastShriekMillis;
                long millisToWait = conf.vampire.shriek.cooldownMillis - millisSinceLastShriek;

                if (millisToWait > 0) {
                    long secondsToWait = (long) Math.ceil(millisToWait / 1000D);
                    plugin.sendMessage(me,
                            MessageType.ERROR,
                            SkillMessageKeys.SHRIEK_WAIT,
                            "{seconds}", String.format("%d", secondsToWait));
                    this.lastShriekWaitMessageMillis = now;
                } else {
                    this.runFxShriek();
                    this.runFxSmokeBurst();
                    this.lastShriekMillis = now;
                }
            }
        } else {
            Component vampireType = plugin.getMessage(me, GrammarMessageKeys.VAMPIRE_TYPE);
            Component shriekAction = plugin.getMessage(me, GrammarMessageKeys.SHRIEK);
            plugin.sendMessage(me,
                    MessageType.ERROR,
                    GrammarMessageKeys.ONLY_TYPE_CAN_ACTION,
                    new Tuple<>("{vampire_type}", vampireType),
                    new Tuple<>("{action}", shriekAction));
        }
    }

    public void setBatusi(boolean activate, int numberOfBats) {
        if (activate)
            enableBatusi(numberOfBats, false);
        else
            disableBatusi(false);
    }

    public boolean isBatusi() {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null)
            return false;
        return plugin.batEnabled.getOrDefault(player.getUniqueId(), false);
    }

    private void enableBatusi(int numberOfBats, boolean silent) {
        Player me = Bukkit.getPlayer(uuid);
        if (me == null) {
            plugin.log(Level.WARNING, "An offline player is trying to batusi!");
            return;
        }

        PluginConfig conf = plugin.getVampireConfig();

        if (!conf.vampire.batusi.enabled) {
            Component batusiAction = plugin.getMessage(me, GrammarMessageKeys.BATUSI);
            plugin.sendMessage(me,
                    MessageType.ERROR,
                    CommandMessageKeys.DISABLED_ACTION,
                    new Tuple<>("{action}", batusiAction));
            return;
        }

        CommandMessageKeys messageKey = CommandMessageKeys.BATUSI_ALREADY_USED;
        if (!plugin.batEnabled.getOrDefault(me.getUniqueId(), false)) {
            plugin.debugLog(Level.INFO, "Enabling batusi!");
            entityUtil.spawnBats(me, numberOfBats);
            plugin.debugLog(Level.INFO, "Bats spawned!");
            messageKey = CommandMessageKeys.BATUSI_TOGGLED_ON;
            this.hadFlight = me.getAllowFlight();
            plugin.batEnabled.put(me.getUniqueId(), true);
        }
        if (plugin.isDisguiseEnabled()) {
            this.isDisguising = true;
            DisguiseUtil.disguiseBat(me);
            this.isDisguising = false;
            plugin.debugLog(Level.INFO, "Disguised enabled!");
        }
        if (conf.vampire.batusi.enableFlight) {
            me.setAllowFlight(true);
            me.setFlying(true);
            plugin.debugLog(Level.INFO, "Flight enabled!");
        }
        if (!silent)
            plugin.sendMessage(me, MessageType.INFO, messageKey);
        plugin.debugLog(Level.INFO, "Batusi message sent!");
    }

    private void disableBatusi(boolean silent) {
        Player sender = Bukkit.getPlayer(uuid);

        if (sender == null || !plugin.batEnabled.getOrDefault(sender.getUniqueId(), false))
            return;

        try {
            entityUtil.despawnBats(sender);
            if (plugin.isDisguiseEnabled()) {
                this.isDisguising = true;
                DisguiseUtil.undisguise(sender);
                this.isDisguising = false;
            }
            if (plugin.getVampireConfig().vampire.batusi.enableFlight) {
                sender.setAllowFlight(this.hadFlight && sender.getAllowFlight());
                sender.setFlying(sender.getAllowFlight() && sender.isFlying());
            }
            plugin.batEnabled.put(sender.getUniqueId(), false);
            if (!silent)
                plugin.sendMessage(sender,
                        MessageType.INFO,
                        CommandMessageKeys.BATUSI_TOGGLED_OFF);
        }
        catch (Exception ex) {
            plugin.sendMessage(sender,
                    MessageType.INFO,
                    CommandMessageKeys.BATUSI_ERROR);
            plugin.getLogger().log(Level.WARNING, "Error while removing bat cloud!: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public boolean isChangingDisguise() {
        return this.isDisguising;
    }

    // -------------------------------------------- //
    // UPDATE
    // -------------------------------------------- //

    public void update() {
        Player player = Bukkit.getPlayer(uuid);
        PluginConfig conf = plugin.getVampireConfig();

        if (player != null) {
            if (!conf.general.isBlacklisted(player.getWorld())) {
                this.updatePotionEffects();
            }
            else {
                this.setBloodlusting(false);
                this.setUsingNightVision(false);
                this.setIntending(false);
                this.setBatusi(false, 0);
                this.setRad(0);
                this.setTemp(0);
            }
        }
        plugin.debugLog(Level.INFO, "Updated");
    }

    public void updateBatusiOnTeleport() {
        if (this.isBatusi()) {
            AtomicInteger aliveBats = new AtomicInteger(0);
            plugin.batmap.get(uuid).forEach((le) -> {
                if (le.isDead())
                    return;
                le.remove();
                plugin.bats.remove(le);
                aliveBats.getAndIncrement();
            });
            plugin.batmap.remove(uuid);
            disableBatusi(true);
            Bukkit.getScheduler().runTaskLater(
                    plugin,
                    () -> enableBatusi(aliveBats.get(), true),
                    1);
        }
    }

    public boolean canHaveNosferatuEffects() {
        PluginConfig conf = plugin.getVampireConfig();
        return this.isNosferatu() &&
                (!conf.radiation.removeBuffs.enabled ||
                 !conf.radiation.removeBuffs.affectNosferatu ||
                 this.getTemp() <= conf.radiation.removeBuffs.temperature);
    }

    public boolean canHaveVampireEffects() {
        PluginConfig conf = plugin.getVampireConfig();
        return this.isVampire() &&
                (!conf.radiation.removeBuffs.enabled ||
                        this.getTemp() <= conf.radiation.removeBuffs.temperature);
    }

    // -------------------------------------------- //
    // UPDATE > POTION EFFECTS
    // -------------------------------------------- //

    public void updatePotionEffects() {
        // plugin.debugLog(Level.INFO, "Updating potion effects...");
        // Find the player and their conf
        Player player = Bukkit.getPlayer(uuid);
        if (player == null || player.isDead()) {
            return;
        }
        PluginConfig conf = plugin.getVampireConfig();
        final int targetDuration = conf.potionEffects.seconds * 20;

        // TODO: I made this dirty fix for lower tps.
        // TODO: The real solution is to tick based on millis and not ticks.

        List<StateEffectConfig> effectConfs = new LinkedList<>();
        effectConfs.add(conf.potionEffects.vampire);
        effectConfs.add(conf.potionEffects.bloodlust);
        effectConfs.add(conf.potionEffects.human);
        effectConfs.add(conf.potionEffects.infected);
        effectConfs.add(conf.potionEffects.nightvision);
        effectConfs.add(conf.potionEffects.nosferatu);
        Collections.sort(effectConfs);

        for (StateEffectConfig effectConf : effectConfs) {
            //plugin.debugLog(Level.INFO, "Group: " + effectConf.toString());
            if (effectConf.getPassesChecks().apply(this)) {
                //plugin.debugLog(Level.INFO, "Passes!");
                effectConf.addPotionEffects(player, targetDuration);
            }
        }

        if (isVampire()) {
            List<RadiationEffectConfig> debuffConfs = conf.radiation.effects;

            for (RadiationEffectConfig debuffConf : debuffConfs) {
                if (this.getTemp() >= debuffConf.temperature &&
                        ((debuffConf.affectNosferatu && this.isNosferatu()) ||
                        !debuffConf.affectNosferatu && !this.isNosferatu())) {
                    player.addPotionEffect(new PotionEffect(debuffConf.type, debuffConf.ticks, debuffConf.strength, true, false));
                }
            }
        }
    }

    // -------------------------------------------- //
    // TICK
    // -------------------------------------------- //

    public boolean isWearingRing(@NotNull Player player) {
        PluginConfig conf = plugin.getVampireConfig();
        if (conf.radiation.radiationRingEnabled)
            return ringUtil.isSunRing(player.getInventory().getItemInOffHand());
        return false;
    }

    public void tick(long millis) {
        Player player = Bukkit.getPlayer(uuid);
        PluginConfig conf = plugin.getVampireConfig();
        if (player != null && player.getGameMode() != GameMode.SPECTATOR && !conf.general.isBlacklisted(player.getWorld())) {
            if (!isWearingRing(player)) {
                this.tickRadTemp(millis);
            }
            this.tickInfection(millis);
            this.tickRegen(millis);
            this.tickBloodlust(millis);
            this.tickPotionEffects(millis);
            this.tickEffects(millis);
        }
    }

    public void tickRadTemp(long millis) {
        // Update rad and temp
        Player me = Bukkit.getPlayer(uuid);
        if (me != null) {
            PluginConfig conf = plugin.getVampireConfig();
            if (me.getGameMode() != GameMode.CREATIVE
                    && me.getGameMode() != GameMode.SPECTATOR
                    && this.isVampire() && !me.isDead()) {
                double irradiation = 0;
                boolean irradiationEnabled = true;

                if (plugin.isWorldGuardEnabled()) {
                    WorldGuardCompat wg = plugin.getWorldGuardCompat();
                    irradiationEnabled = wg.isIrradiationEnabled(me, me.getLocation());
                }

                if (irradiationEnabled) {
                    irradiation = sunUtil.calcPlayerIrradiation(this, me);
                }

                this.rad = conf.radiation.baseRadiation + irradiation;
                double tempDelta = conf.radiation.tempPerRadAndMilli * this.rad * millis;
                this.addTemp(tempDelta);
            }
            else {
                this.rad = 0;
                this.temp = 0;
            }
        }
    }

    public void tickInfection(long millis) {
        Player me = Bukkit.getPlayer(uuid);
        if (this.isInfected() && me != null) {
            int indexOld = this.infectionGetMessageIndex();
            PluginConfig conf = plugin.getVampireConfig();
            this.addInfection(millis * conf.infection.amountPerMilli);
            int indexNew = this.infectionGetMessageIndex();

            if (!this.isVampire() && indexOld != indexNew) {
                if (conf.infection.progressDamage != 0)
                    me.damage(conf.infection.progressDamage);
                if (conf.infection.progressNauseaTicks > 0)
                    FxUtil.ensure(plugin.getVersionCompat().getPotionEffectByName("nausea"), me, conf.infection.progressNauseaTicks);

                plugin.sendMessage(me,
                        MessageType.INFO,
                        InfectionMessageKeys.getFeeling(indexNew));
                plugin.sendMessage(me,
                        MessageType.INFO,
                        InfectionMessageKeys.getHint(ThreadLocalRandom.current().nextInt(InfectionMessageKeys.getMaxHint())));
            }
        }
    }

    public int infectionGetMessageIndex() {
        return (int) ((InfectionMessageKeys.getMaxFeeling() + 1) * this.getInfection()) - 1;
    }

    public void tickRegen(long millis) {
        Player me = Bukkit.getPlayer(uuid);
        if (me == null) {
            return;
        }
        PluginConfig conf = plugin.getVampireConfig();
        boolean enabled = (this.isVampire() && conf.vampire.regen.enabled) ||
                          (this.isNosferatu() && conf.vampire.regenNosferatu.enabled);
        boolean buffsActive = !conf.radiation.removeBuffs.enabled ||
                                this.getTemp() < conf.radiation.removeBuffs.temperature ||
                                (this.isNosferatu() && !conf.radiation.removeBuffs.affectNosferatu);
        double minFood = this.isNosferatu() ? conf.vampire.regenNosferatu.minFood : conf.vampire.regen.minFood;
        int delayMillis = this.isNosferatu() ? conf.vampire.regenNosferatu.delayMillis : conf.vampire.regen.delayMillis;
        double foodPerMilli = this.isNosferatu() ? conf.vampire.regenNosferatu.foodPerMilli : conf.vampire.regen.foodPerMilli;
        double healthPerFood = this.isNosferatu() ? conf.vampire.regenNosferatu.healthPerFood : conf.vampire.regen.healthPerFood;

        AttributeInstance maxHealthAttr = me.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        double maxHealth = 20;
        if (maxHealthAttr != null)
            maxHealth = maxHealthAttr.getValue();
        if (enabled
                && maxHealthAttr != null
                && buffsActive
                && me.getGameMode() != GameMode.CREATIVE
                && me.getGameMode() != GameMode.SPECTATOR
                && !me.isDead()
                && me.getHealth() < maxHealth
                && this.getFood() >= minFood) {
            long millisSinceLastDamage = ZonedDateTime.now().toInstant().toEpochMilli() - this.lastDamageMillis;
            if (millisSinceLastDamage >= delayMillis) {
                double foodDiff = this.addFood(-foodPerMilli * millis);
                double healthTarget = me.getHealth() - foodDiff * healthPerFood;

                healthTarget = MathUtil.limitNumber(healthTarget, 0D, maxHealth);
                me.setHealth(healthTarget);
            }
        }
    }

    public void tickBloodlust(long millis) {
        Player me = Bukkit.getPlayer(uuid);
        if (me == null) {
            return;
        }
        PluginConfig conf = plugin.getVampireConfig();
        if (this.isVampire() && this.isBloodlusting()
                && !me.isDead()
                && me.getGameMode() != GameMode.CREATIVE
                && me.getGameMode() != GameMode.SPECTATOR) {
            this.addFood(millis * conf.vampire.bloodlust.foodPerMilli);
            if (this.getFood() < conf.vampire.bloodlust.minFood)
                this.setBloodlusting(false);
        }
    }

    public void tickPotionEffects(long millis) {
        // TODO: Will update too often!?
        this.updatePotionEffects();
    }

    public void tickEffects(long millis) {
        Player me = Bukkit.getPlayer(uuid);
        if (me != null && !me.isDead()
                && me.getGameMode() != GameMode.CREATIVE
                && me.getGameMode() != GameMode.SPECTATOR) {
            PluginConfig conf = plugin.getVampireConfig();

            // FX: Smoke
            if (this.fxSmokeMillis > 0) {
                this.fxSmokeMillis -= millis;
                double dcount = conf.specialEffects.smokePerMilli * millis;
                long lcount = MathUtil.probabilityRound(dcount);
                for (long i = lcount; i > 0; i--)
                    FxUtil.smoke(me);
            }

            // FX: Ender
            if (this.fxEnderMillis > 0) {
                this.fxEnderMillis -= millis;
                double dcount = conf.specialEffects.enderPerMilli * millis;
                long lcount = MathUtil.probabilityRound(dcount);
                for (long i = lcount; i > 0; i--)
                    FxUtil.ender(me, conf.specialEffects.enderRandomMaxLen);
            }

            // Vampire sun reactions
            if (this.isVampire()) {
                // Buffs
                if (conf.radiation.burn.enabled &&
                        (!this.isNosferatu() || conf.radiation.burn.affectNosferatu)) {
                    if (this.getTemp() > conf.radiation.burn.temperature)
                        FxUtil.ensureBurn(me, conf.radiation.burn.ticks);
                }

                // Fx
                double dsmokes = conf.radiation.smokesPerTempAndMilli * this.temp * millis;
                long lsmokes = MathUtil.probabilityRound(dsmokes);
                for (long i = lsmokes; i > 0; i--)
                    FxUtil.smoke(me);

                double dflames = conf.radiation.flamesPerTempAndMilli * this.temp * millis;
                long lflames = MathUtil.probabilityRound(dflames);
                for (long i = lflames; i > 0; i--)
                    FxUtil.flame(me);
            }
        }
    }

    // -------------------------------------------- //
    // TRADE
    // -------------------------------------------- //

    @SuppressWarnings("deprecation")
    public void tradeAccept() {
        Player me = Bukkit.getPlayer(uuid);
        if (me != null) {
            VPlayer vyou = this.tradeOfferedFrom;
            Player you = vyou == null ? null : vyou.getPlayer();
            PluginConfig conf = plugin.getVampireConfig();
            Component yourName = you == null ? null : Component.text(you.getDisplayName());

            // Any offer available?
            if (you == null || ZonedDateTime.now().toInstant().toEpochMilli() - this.tradeOfferedAtMillis > conf.trade.offerToleranceMillis) {
                plugin.sendMessage(me,
                        MessageType.ERROR,
                        TradingMessageKeys.ACCEPT_NONE);
            } // Standing close enough?
            else if (!this.withinDistanceOf(vyou, conf.trade.offerMaxDistance)) {
                plugin.sendMessage(me,
                        MessageType.ERROR,
                        TradingMessageKeys.NOT_CLOSE,
                        new Tuple<>("{player}", yourName));
            } else {
                double amount = this.tradeOfferedAmount;

                // Enough blood?
                double enough = 0;
                if (conf.general.vampiresUseFoodAsBlood && vyou.isVampire()) {
                    // blood is only food for vampires
                    enough = vyou.getFood();
                } else {
                    // but blood is health for humans
                    enough = you.getHealth();
                }

                if (this.tradeOfferedAmount > enough) {
                    plugin.sendMessage(you,
                            MessageType.ERROR,
                            TradingMessageKeys.LACKING_OUT);
                    plugin.sendMessage(me,
                            MessageType.ERROR,
                            TradingMessageKeys.LACKING_IN,
                            new Tuple<>("{player}", yourName));
                } else {
                    Component merName = Component.text(me.getDisplayName());
                    Component amountComp = Component.text(String.format("%.1f", amount));
                    // Transfer blood (food for vampires, life for humans)
                    if (conf.general.vampiresUseFoodAsBlood && vyou.isVampire()) {
                        vyou.addFood(-amount);
                    } else {
                        you.damage(amount);
                    }

                    this.addFood(amount * conf.trade.percentage);

                    // Risk infection/boost infection
                    if (vyou.isVampire() && !this.isVampire()) {
                        if (this.isInfected()) {
                            this.addInfection(0.01D);
                        }
                        else if (me.hasPermission("vampire.trade.contract")
                                && ThreadLocalRandom.current().nextDouble() * 20 < amount) {
                            this.addInfection(0.05D, InfectionReason.TRADE, vyou);
                        }
                    }
                    // Trader Messages
                    plugin.sendMessage(you,
                            MessageType.INFO,
                            TradingMessageKeys.TRANSFER_OUT,
                            new Tuple<>("{player}", merName),
                            new Tuple<>("{amount}", amountComp));
                    plugin.sendMessage(me,
                            MessageType.INFO,
                            TradingMessageKeys.TRANSFER_IN,
                            new Tuple<>("{player}", yourName),
                            new Tuple<>("{amount}", amountComp));

                    // Who noticed?
                    Location tradeLocation = me.getLocation();
                    World tradeWorld = tradeLocation.getWorld();
                    Location l1 = me.getEyeLocation();
                    Location l2 = you.getEyeLocation();
                    for (Player player : tradeWorld.getPlayers()) {
                        if (player.getLocation().distance(tradeLocation) <= conf.trade.visualDistance) {
                            player.playEffect(l1, Effect.POTION_BREAK, 5);
                            player.playEffect(l2, Effect.POTION_BREAK, 5);
                            if (!player.equals(me) && !player.equals(you)) {
                                plugin.sendMessage(player,
                                        MessageType.INFO,
                                        TradingMessageKeys.SEEN,
                                        new Tuple<>("{player}", merName),
                                        new Tuple<>("{source}", yourName));
                            }
                        }
                    }

                    // Reset trade memory
                    this.tradeOfferedFrom = null;
                    this.tradeOfferedAtMillis = 0;
                    this.tradeOfferedAmount = 0;
                }
            }
        }
    }

    public void tradeOffer(Player sender, VPlayer vyou, double amount) {
        Player you = vyou.getPlayer();
        Player me = Bukkit.getPlayer(uuid);
        if (you != null && me != null) {
            PluginConfig conf = plugin.getVampireConfig();
            Component yourName = Component.text(you.getDisplayName());
            if (!this.withinDistanceOf(vyou, conf.trade.offerMaxDistance)) {
                plugin.sendMessage(me,
                        MessageType.INFO,
                        TradingMessageKeys.NOT_CLOSE,
                        new Tuple<>("{player}", yourName));
            } else if (me.equals(you)) {
                plugin.sendMessage(me,
                        MessageType.INFO,
                        TradingMessageKeys.SELF);
                FxUtil.ensure(plugin.getVersionCompat().getPotionEffectByName("nausea"), me, 12 * 20);
            } else {
                Component merName = Component.text(me.getDisplayName());
                Component amountComp = Component.text(String.format("%.1f", amount));
                vyou.tradeOfferedFrom = this;
                vyou.tradeOfferedAtMillis = ZonedDateTime.now().toInstant().toEpochMilli();
                vyou.tradeOfferedAmount = amount;

                plugin.sendMessage(me,
                        MessageType.INFO,
                        TradingMessageKeys.OFFER_OUT,
                        new Tuple<>("{player}", yourName),
                        new Tuple<>("{amount}", amountComp));

                plugin.sendMessage(you,
                        MessageType.INFO,
                        TradingMessageKeys.OFFER_IN,
                        new Tuple<>("{player}", merName),
                        new Tuple<>("{amount}", amountComp));

                plugin.sendMessage(you,
                        MessageType.INFO,
                        TradingMessageKeys.ACCEPT_HELP);
            }
        }
        else {
            plugin.sendMessage(sender,
                    MessageType.ERROR,
                    CommandMessageKeys.DATA_NOT_FOUND);
        }
    }

    public boolean withinDistanceOf(VPlayer vyou, double maxDistance) {
        boolean res = false;
        Player me = Bukkit.getPlayer(uuid);
        Player you = vyou.getPlayer();
        if (you != null && me != null && !me.isDead() && !you.isDead()) {
            Location l1 = me.getLocation();
            Location l2 = you.getLocation();
            if (l1.getWorld().equals(l2.getWorld()) && l1.distance(l2) <= maxDistance)
                res = true;
        }
        return res;
    }

    // -------------------------------------------- //
    // TRUCE
    // -------------------------------------------- //

    public void updateTruce(long now, long before) {
        if (this.isVampire() && this.truceIsBroken(before) && !this.truceIsBroken(now)) {
            this.truceRestore();
        }
    }

    public boolean truceIsBroken(long when) {
        return when < truceRestoreTimestamp;
    }

    public void truceBreak(long when) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            if (!this.truceIsBroken(when)) {
                plugin.sendMessage(player,
                        MessageType.INFO,
                        VampirismMessageKeys.TRUCE_BROKEN);
            }
            this.setTruceRestoreTimestamp(when);
        }
    }

    public void truceRestore() {
        Player me = Bukkit.getPlayer(uuid);
        if (me != null) {
            plugin.sendMessage(me,
                    MessageType.INFO,
                    VampirismMessageKeys.TRUCE_RESTORED);
            VersionCompat vCompat = plugin.getVersionCompat();
            // Untarget the player.
            me.getWorld().getNearbyEntities(
                    me.getLocation(),
                    50,
                    50,
                    50,
                    (e) -> e.getType() != EntityType.WITHER
                            && vCompat.isUndead(e.getType())
                            && e instanceof Creature
                            && me.equals(((Creature) e).getTarget()))
                    .forEach((le) -> ((Creature) le).setTarget(null));
        }
    }

    private void setTruceRestoreTimestamp(long timeBroken) {
        long aux = timeBroken + plugin.getVampireConfig().truce.breakMillis;
        if (truceRestoreTimestamp < aux) {
            this.truceRestoreTimestamp = aux;
        }
    }

    // -------------------------------------------- //
    // COMBAT
    // -------------------------------------------- //

    public double combatDamageFactor() {
        double damageFactor = 0D;
        Player me = Bukkit.getPlayer(uuid);

        if (me != null) {
            PluginConfig conf = plugin.getVampireConfig();
            if (this.isBloodlusting())
                damageFactor = conf.vampire.bloodlust.damageFactor;
            else
                damageFactor = conf.vampire.damageFactor;
        }

        return damageFactor;
    }

    public double combatInfectRisk() {
        double infectRisk = 0D;

        Player me = Bukkit.getPlayer(uuid);
        if (me != null) {
            if (this.isVampire()) {
                PluginConfig conf = plugin.getVampireConfig();
                if (this.isIntending())
                    infectRisk = conf.vampire.intend.infectionChance;
                else
                    infectRisk = conf.infection.chance;
            }
        }

        return infectRisk;
    }
}
