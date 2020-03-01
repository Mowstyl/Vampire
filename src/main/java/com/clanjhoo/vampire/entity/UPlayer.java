package com.clanjhoo.vampire.entity;

import com.clanjhoo.vampire.InfectionReason;
import com.clanjhoo.vampire.VampireRevamp;
import com.clanjhoo.vampire.accumulator.UPlayerFoodAccumulator;
import com.clanjhoo.vampire.event.EventVampirePlayerInfectionChange;
import com.clanjhoo.vampire.event.EventVampirePlayerVampireChange;
import com.clanjhoo.vampire.util.*;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.UUID;

public class UPlayer {
    // -------------------------------------------- //
    // PERSISTENT FIELDS
    // -------------------------------------------- //

    /**
     * PERSISTENT: whether or not the player is a vampire
     */
    private boolean vampire = false;
    /**
     * PERSISTENT: whether or not the player is a nosferatu (higher tier)
     */
    private boolean nosferatu = false;
    /**
     * PERSISTENT: degree of infection [0, 1]. 0 means no infection, 1 turns the player into a vampire
     */
    private double infection = 0;
    /**
     * PERSISTENT:
     */
    private InfectionReason reason;
    /**
     * PERSISTENT: UUID of the player who infected this player
     */
    private UUID makerUUID;
    /**
     * PERSISTENT: whether or not the vampire is trying to infect others in combat on purpose or not
     */
    private boolean intending = false;
    /**
     * TRANSIENT: whether or not the vampire is in bloodlust mode
     */
    private transient boolean bloodlusting = false;
    /**
     * PERSISTENT: whether or not to apply night vision effects to a vampire
     */
    private boolean usingNightVision = false;
    /**
     * TRANSIENT: UUID of the player
     */
    private transient UUID playerUUID = null;
    /**
     * TRANSIENT: UUID of the player
     */
    private transient Player player = null;
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
    private transient UPlayerFoodAccumulator food = new UPlayerFoodAccumulator(this);
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
     * TRANSIENT: milliseconds to wait before restoring the truce
     */
    private transient long truceBreakMillisLeft = 0;
    /**
     * TRANSIENT: the player who offered the blood in a trade
     */
    private transient UPlayer tradeOfferedFrom = null;
    /**
     * TRANSIENT: the amount of blood offered in a trade
     */
    private transient double tradeOfferedAmount = 0;
    /**
     * TRANSIENT: timestamp of the moment the trade started
     */
    private transient long tradeOfferedAtMillis = 0;
    /**
     * TRANSIENT: permission assignments
     */
    private transient PermissionAttachment permA = null;
    /**
     * TRANSIENT FX: smoke
     */
    private transient long fxSmokeMillis = 0;
    /**
     * TRANSIENT FX: ender
     */
    private transient long fxEnderMillis = 0;

    /**
     * TRANSIENT: plugin
     */
    public static VampireRevamp plugin;


    // -------------------------------------------- //
    // CONSTRUCTORS
    // -------------------------------------------- //

    public UPlayer(boolean vampire, boolean nosferatu, double infection, InfectionReason reason,
                   UUID makerUUID, boolean intending, boolean usingNightVision) {
        this.vampire = vampire || nosferatu;
        this.nosferatu = this.vampire && nosferatu; // You can't be nosferatu without being vampire
        this.infection = infection;
        this.reason = reason;
        this.makerUUID = makerUUID;
        this.intending = vampire && intending;
        this.usingNightVision = vampire && usingNightVision;
    }

    // -------------------------------------------- //
    // META
    // -------------------------------------------- //

    public static UPlayer get(UUID uuid) {
        UPlayer uPlayer = null;

        if (uuid != null)
            uPlayer = plugin.uPlayerColl.get(uuid);

        return uPlayer;
    }

    public static UPlayer get(Player player) {
        UPlayer uPlayer = null;

        if (player != null)
            uPlayer = get(player.getUniqueId());

        return uPlayer;
    }

    // -------------------------------------------- //
    // Getters and setters
    // -------------------------------------------- //

    public boolean isVampire() {
        return this.vampire;
    }

    // Shortcut
    public boolean isHuman() {
        return !this.isVampire();
    }

    public void setVampire(boolean val) {
        this.setInfection(0);

        if (this.vampire != val) {
            EventVampirePlayerVampireChange event = new EventVampirePlayerVampireChange(val, this);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                this.vampire = val;

                if (this.vampire) {
                    this.msg(plugin.mLang.vampireTrue);
                    this.runFxShriek();
                    this.runFxSmokeBurst();
                    this.runFxSmoke();

                    Player player = this.getPlayer();
                    if (player != null) {
                        MConf mconf = plugin.mConf;
                        mconf.getEffectConfHuman().removePotionEffects(player);
                        player.setSleepingIgnored(true);
                    }
                } else {
                    this.msg(plugin.mLang.vampireFalse);
                    this.runFxEnder();
                    this.setNosferatu(false);
                    this.setMaker(null);
                    this.setReason(null);
                    this.setBloodlusting(false);
                    this.setIntending(false);
                    this.setUsingNightVision(false);
                    player.setSleepingIgnored(false);

                    Player player = this.getPlayer();
                    if (player != null) {
                        MConf mconf = plugin.mConf;
                        mconf.getEffectConfVampire().removePotionEffects(player);
                    }
                }

                this.update();
            }
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
            EventVampirePlayerInfectionChange event = new EventVampirePlayerInfectionChange(val, this);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                val = event.getInfection();

                if (val >= 1D) {
                    this.setVampire(true);
                } else if (val <= 0D) {
                    if (this.infection > 0D && !this.isVampire()) {
                        this.msg(plugin.mLang.infectionCured);
                    }
                    this.infection = 0D;

                    Player player = this.getPlayer();
                    if (player != null) {
                        MConf mconf = plugin.mConf;
                        mconf.getEffectConfInfected().removePotionEffects(player);
                    }
                } else {
                    this.infection = val;
                }
                this.updatePotionEffects();
            }
        }
    }

    public void addInfection(double val) {
        this.setInfection(this.getInfection() + val);
    }

    public void addInfection(double val, InfectionReason reason, UPlayer maker) {
        if (!vampire) {
            this.setReason(reason);
            this.setMakerUUID(maker == null ? null : maker.getPlayerUUID());
            plugin.log(this.getReasonDesc(false));
            if (reason.isNoticeable())
                this.msg(this.getReasonDesc(true));
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
        return reason == null ? InfectionReason.UNKNOWN : reason;
    }

    public void setReason(InfectionReason reason) {
        this.reason = reason;
    }

    public String getReasonDesc(boolean self) {
        return this.getReason().getDesc(this, self);
    }

    public UUID getMakerUUID() {
        return this.makerUUID;
    }

    public void setMakerUUID(UUID makerUUID) {
        this.makerUUID = makerUUID;
    }

    public UPlayer getMaker() {
        return plugin.uPlayerColl.get(this.makerUUID);
    }

    public void setMaker(UPlayer val) {
        this.setMakerUUID(val == null ? null : val.getPlayerUUID());
    }

    public boolean isIntending() {
        return this.intending;
    }

    public void setIntending(boolean val) {
        this.intending = val;
        this.msg(this.intendMsg());
    }

    public String intendMsg() {
        return plugin.mLang.boolIsY("Infect intent", this.isIntending()) + " " + plugin.mLang.quotaIsPercent("Combat infect risk", this.combatInfectRisk());
    }

    public boolean isBloodlusting() {
        return this.bloodlusting;
    }

    public void setBloodlusting(boolean val) {
        if (this.bloodlusting == val) {
            // No real change - just view the info.
            this.msg(plugin.mLang.boolIsY("Bloodlust", val));
        } else {
            Player me = this.getPlayer();
            if (me != null) {
                if (val) {
                    // There are a few rules to when you can turn it on:
                    if (!this.isVampire()) {
                        msg(plugin.mLang.onlyVampsCanX, "use bloodlust");
                    } else if (this.getFood() != null && this.getFood().get() < plugin.mConf.getBloodlustMinFood()) {
                        msg("<b>Your food is too low for bloodlust.");
                    } else if (me.getGameMode() == GameMode.CREATIVE
                            || me.getGameMode() == GameMode.SPECTATOR) {
                        msg("<b>You can't use bloodlust while in Creative or Spectator Mode."); // or offline :P but offline players wont see the message
                    } else {
                        this.bloodlusting = true;
                        this.update();
                        this.msg(this.bloodlustMsg());
                    }
                } else {
                    MConf mconf = plugin.mConf;
                    mconf.getEffectConfBloodlust().removePotionEffects(me);
                    this.bloodlusting = false;
                    this.update();
                    this.msg(this.bloodlustMsg());
                }
            }
        }
    }

    public String bloodlustMsg() {
        return plugin.mLang.boolIsY("Bloodlust", this.isBloodlusting()) + " " + plugin.mLang.quotaIsPercent("combat damage", this.combatDamageFactor());
    }

    public boolean isUsingNightVision() {
        return this.usingNightVision;
    }

    public void setUsingNightVision(boolean val) {
        // If an actual change is being made ...
        if (this.usingNightVision != val) {
            // ... do change stuff ...
            this.usingNightVision = val;

            // ... remove the nightvision potion effects ...
            Player me = this.getPlayer();
            if (me != null) {
                MConf mconf = plugin.mConf;
                mconf.getEffectConfNightvision().removePotionEffects(me);
            }

            // ... trigger a potion effect update ...
            this.updatePotionEffects();
            this.msg(this.usingNightVisionMsg());
        }
    }

    public String usingNightVisionMsg() {
        return plugin.mLang.boolIsY("Nightvision", this.isUsingNightVision());
    }

    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    public void setPlayerUUID(UUID playerUUID) {
        this.playerUUID = playerUUID;

        if (playerUUID != null) {
            this.player = Bukkit.getPlayer(playerUUID);
        } else {
            this.player = null;
        }
    }

    public Player getPlayer() {
        Player p = null;

        if (this.player != null) {
            p = this.player;
        } else {
            p = Bukkit.getPlayer(this.playerUUID);
        }

        return p;
    }

    public OfflinePlayer getOfflinePlayer() {
        OfflinePlayer p = null;

        if (this.player != null) {
            p = this.player;
        } else {
            p = Bukkit.getOfflinePlayer(this.playerUUID);
        }

        return p;
    }

    public void setPlayer(Player player) {
        this.player = player;
        if (player != null) {
            this.playerUUID = player.getUniqueId();
        } else {
            this.playerUUID = null;
        }
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

    public UPlayerFoodAccumulator getFood() {
        if (this.food == null)
            this.food = new UPlayerFoodAccumulator(this);
        return this.food;
    }

    public long getLastDamageMillis() {
        return this.lastDamageMillis;
    }

    public void setLastDamageMillis(long lastDamageMillis) {
        this.lastDamageMillis = lastDamageMillis;
    }

    public long getLastShriekMillis() {
        return this.lastShriekMillis;
    }

    public void setLastShriekMillis(long lastShriekMillis) {
        this.lastShriekMillis = lastShriekMillis;
    }

    public long getLastShriekWaitMessageMillis() {
        return this.lastShriekWaitMessageMillis;
    }

    public void setLastShriekWaitMessageMillis(long lastShriekWaitMessageMillis) {
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
        Player me = this.getPlayer();
        if (me != null) {
            Location location = me.getLocation();
            World world = location.getWorld();
            world.playEffect(location, Effect.GHAST_SHRIEK, 0);
        }
    }

    // FX: SmokeBurst
    public void runFxSmokeBurst() {
        Player me = this.getPlayer();
        if (me != null) {
            double dcount = plugin.mConf.getFxSmokeBurstCount();
            long lcount = MathUtil.probabilityRound(dcount);
            for (long i = lcount; i > 0; i--) FxUtil.smoke(me);
        }
    }

    // FX: EnderBurst
    public void runFxEnderBurst() {
        Player me = this.getPlayer();
        if (me != null) {
            double dcount = plugin.mConf.getFxEnderBurstCount();
            long lcount = MathUtil.probabilityRound(dcount);
            for (long i = lcount; i > 0; i--) FxUtil.ender(me, 0);
        }
    }

    // FX: FlameBurst
    public void runFxFlameBurst() {
        Player me = this.getPlayer();
        if (me != null) {
            double dcount = plugin.mConf.getFxFlameBurstCount();
            long lcount = MathUtil.probabilityRound(dcount);
            for (long i = lcount; i > 0; i--) FxUtil.flame(me);
        }
    }

    // -------------------------------------------- //
    // SHRIEK
    // -------------------------------------------- //

    public void shriek() {
        // You must be online to shriek
        Player me = this.getPlayer();
        if (me != null) {
            MConf mconf = plugin.mConf;

            // You must be a vampire to shriek
            if (this.isVampire()) {
                long now = System.currentTimeMillis();

                long millisSinceLastShriekWaitMessage = now - this.lastShriekWaitMessageMillis;
                if (millisSinceLastShriekWaitMessage >= mconf.getShriekWaitMessageCooldownMillis()) {
                    long millisSinceLastShriek = now - this.lastShriekMillis;
                    long millisToWait = mconf.getShriekCooldownMillis() - millisSinceLastShriek;

                    if (millisToWait > 0) {
                        long secondsToWait = (long) Math.ceil(millisToWait / 1000D);
                        this.msg(plugin.mLang.shriekWait, secondsToWait);
                        this.lastShriekWaitMessageMillis = now;
                    } else {
                        this.runFxShriek();
                        this.runFxSmokeBurst();
                        this.lastShriekMillis = now;
                    }
                }
            } else {
                msg(plugin.mLang.onlyVampsCanX, "shriek");
            }
        }
    }

    public void msg(String message) {
        this.getPlayer().sendMessage(TextUtil.parse(message));
    }

    public void msg(String message, Object... args) {
        this.getPlayer().sendMessage(TextUtil.parse(message, args));
    }

    // -------------------------------------------- //
    // UPDATE
    // -------------------------------------------- //

    public void update() {
        //this.updatePermissions();
        this.updatePotionEffects();
    }

    // -------------------------------------------- //
    // UPDATE > PERMISSONS
    // -------------------------------------------- //

    /*
    public void updatePermissions() {
        // Player
        Player player = this.getPlayer();
        if (player != null) {
            // Attachment
            PermissionAttachment attachment;
            if (EntityUtil.perms.containsKey(playerUUID)) {
                attachment = EntityUtil.perms.get(playerUUID);
            } else {
                attachment = player.addAttachment(VampireRevamp.get());
            }

            if (attachment != null) {
                // Permissions
                MConf mconf = plugin.mConf;
                Map<String, Boolean> permissions = (this.isVampire() ? mconf.getUpdatePermsVampire() : mconf.getUpdatePermsHuman());

                // Update
                EntityUtil.updatePermissions(attachment, permissions);
            }
        }
    }
    */

    // -------------------------------------------- //
    // UPDATE > POTION EFFECTS
    // -------------------------------------------- //

    public void updatePotionEffects() {
        final int okDuration = 300;

        // TODO: I made this dirty fix for lower tps.
        // TODO: The real solution is to tick based on millis and not ticks.
        //final int targetDuration = okDuration*2;
        final int targetDuration = okDuration * 4;

        // Find the player and their conf
        Player player = this.getPlayer();
        if (player != null && !player.isDead()) {
            MConf mconf = plugin.mConf;

            // Add effects based their
            if (this.isHuman()) {
                mconf.getEffectConfHuman().addPotionEffects(player, targetDuration, okDuration);
            }

            if (this.isInfected()) {
                mconf.getEffectConfInfected().addPotionEffects(player, targetDuration, okDuration);
            }

            if (this.isVampire() && this.getTemp() <= mconf.getSunStopEffectsTemp()) {
                mconf.getEffectConfVampire().addPotionEffects(player, targetDuration, okDuration);
            }

            if (this.isVampire() && mconf.isNightvisionCanBeUsed() && this.isUsingNightVision()) {
                mconf.getEffectConfNightvision().addPotionEffects(player, targetDuration, okDuration);
            }

            if (this.isVampire() && this.isBloodlusting()) {
                mconf.getEffectConfBloodlust().addPotionEffects(player, targetDuration, okDuration);
            }
        }
    }

    // -------------------------------------------- //
    // TICK
    // -------------------------------------------- //

    public void tick(long millis) {
        this.tickRadTemp(millis);
        this.tickInfection(millis);
        this.tickRegen(millis);
        this.tickBloodlust(millis);
        this.tickPotionEffects(millis);
        this.tickEffects(millis);
        this.tickTruce(millis);
    }

    public void tickRadTemp(long millis) {
        // Update rad and temp
        Player me = this.getPlayer();
        if (me != null) {
            MConf mconf = plugin.mConf;

            if (me.getGameMode() != GameMode.CREATIVE && this.isVampire() && !me.isDead()) {
                this.rad = mconf.getBaseRad() + SunUtil.calcPlayerIrradiation(me);
                double tempDelta = mconf.getTempPerRadAndMilli() * this.rad * millis;
                this.addTemp(tempDelta);
            } else {
                this.rad = 0;
                this.temp = 0;
            }
        }
    }

    public void tickInfection(long millis) {
        Player me = this.getPlayer();
        if (this.isInfected() && me != null
                && me.getGameMode() != GameMode.CREATIVE
                && me.getGameMode() != GameMode.SPECTATOR) {
            MConf mconf = plugin.mConf;

            int indexOld = this.infectionGetMessageIndex();
            this.addInfection(millis * mconf.getInfectionPerMilli());
            int indexNew = this.infectionGetMessageIndex();

            if (!this.isVampire() && indexOld != indexNew) {
                if (mconf.getInfectionProgressDamage() != 0)
                    me.damage(mconf.getInfectionProgressDamage());
                if (mconf.getInfectionProgressNauseaTicks() > 0)
                    FxUtil.ensure(PotionEffectType.CONFUSION, me, mconf.getInfectionProgressNauseaTicks());

                this.msg(plugin.mLang.infectionFeeling.get(indexNew));
                this.msg(plugin.mLang.infectionHint.get(MathUtil.random.nextInt(plugin.mLang.infectionHint.size())));
            }
        }
    }

    public int infectionGetMessageIndex() {
        return (int) ((plugin.mLang.infectionFeeling.size() + 1) * this.getInfection() / 1D) - 1;
    }

    public void tickRegen(long millis) {
        Player me = this.getPlayer();
        MConf mconf = plugin.mConf;
        if (this.isVampire() && this.getTemp() <= mconf.getSunStopEffectsTemp()
                && me != null
                && me.getGameMode() != GameMode.CREATIVE
                && me.getGameMode() != GameMode.SPECTATOR
                && !me.isDead()
                && me.getHealth() < me.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()
                && this.getFood().get() >= mconf.getRegenMinFood()) {
            long millisSinceLastDamage = System.currentTimeMillis() - this.lastDamageMillis;
            if (millisSinceLastDamage >= mconf.getRegenDelayMillis()) {
                double foodDiff = this.getFood().add(-mconf.getRegenFoodPerMilli() * millis);
                double nosferatuFactor = this.isNosferatu() ? 1.5 : 1;
                double healthTarget = me.getHealth() - foodDiff * mconf.getRegenHealthPerFood() * nosferatuFactor;

                healthTarget = Math.min(healthTarget, me.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
                healthTarget = Math.max(healthTarget, 0D);

                me.setHealth(healthTarget);
            }
        }
    }

    public void tickBloodlust(long millis) {
        Player me = this.getPlayer();
        if (this.isVampire() && this.isBloodlusting()
                && me != null && !me.isDead()
                && me.getGameMode() != GameMode.CREATIVE
                && me.getGameMode() != GameMode.SPECTATOR) {
            MConf mconf = plugin.mConf;
            this.getFood().add(millis * mconf.getBloodlustFoodPerMilli());
            if (this.getFood().get() < mconf.getBloodlustMinFood())
                this.setBloodlusting(false);
        }
    }

    public void tickPotionEffects(long millis) {
        // TODO: Will update too often!?
        this.updatePotionEffects();
    }

    public void tickEffects(long millis) {
        Player me = this.getPlayer();
        if (me != null && !me.isDead()
                && me.getGameMode() != GameMode.CREATIVE
                && me.getGameMode() != GameMode.SPECTATOR) {
            MConf mconf = plugin.mConf;

            // FX: Smoke
            if (this.fxSmokeMillis > 0) {
                this.fxSmokeMillis -= millis;
                double dcount = mconf.getFxSmokePerMilli() * millis;
                long lcount = MathUtil.probabilityRound(dcount);
                for (long i = lcount; i > 0; i--)
                    FxUtil.smoke(me);
            }

            // FX: Ender
            if (this.fxEnderMillis > 0) {
                this.fxEnderMillis -= millis;
                double dcount = mconf.getFxEnderPerMilli() * millis;
                long lcount = MathUtil.probabilityRound(dcount);
                for (long i = lcount; i > 0; i--)
                    FxUtil.ender(me, mconf.getFxEnderRandomMaxLen());
            }

            // Vampire sun reactions
            if (this.isVampire()) {
                // Buffs
                if (!this.isNosferatu()) {
                    if (this.getTemp() > mconf.getSunNauseaTemp())
                        FxUtil.ensure(PotionEffectType.CONFUSION, me, mconf.getSunNauseaTicks());
                    if (this.getTemp() > mconf.getSunWeaknessTemp())
                        FxUtil.ensure(PotionEffectType.WEAKNESS, me, mconf.getSunWeaknessTicks());
                    if (this.getTemp() > mconf.getSunSlowTemp())
                        FxUtil.ensure(PotionEffectType.SLOW, me, mconf.getSunSlowTicks());
                    if (this.getTemp() > mconf.getSunBlindnessTemp())
                        FxUtil.ensure(PotionEffectType.BLINDNESS, me, mconf.getSunBlindnessTicks());
                    if (this.getTemp() > mconf.getSunBurnTemp())
                        FxUtil.ensureBurn(me, mconf.getSunBurnTicks());
                }

                // Fx
                double dsmokes = mconf.getSunSmokesPerTempAndMilli() * this.temp * millis;
                long lsmokes = MathUtil.probabilityRound(dsmokes);
                for (long i = lsmokes; i > 0; i--)
                    FxUtil.smoke(me);

                double dflames = mconf.getSunFlamesPerTempAndMilli() * this.temp * millis;
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
        Player me = this.getPlayer();
        if (me != null) {
            MConf mconf = plugin.mConf;

            UPlayer vyou = this.tradeOfferedFrom;

            // Any offer available?
            if (vyou == null || System.currentTimeMillis() - this.tradeOfferedAtMillis > mconf.getTradeOfferToleranceMillis()) {
                this.msg(plugin.mLang.tradeAcceptNone);
            } // Standing close enough?
            else if (!this.withinDistanceOf(vyou, mconf.getTradeOfferMaxDistance())) {
                this.msg(plugin.mLang.tradeNotClose, vyou.player.getDisplayName());
            } else {
                Player you = vyou.getPlayer();
                double amount = this.tradeOfferedAmount;

                // Enough blood?
                double enough = 0;
                if (vyou.isVampire()) {
                    // blood is only food for vampires
                    enough = vyou.getFood().get();
                } else {
                    // but blood is health for humans
                    enough = vyou.getPlayer().getHealth();
                }

                if (this.tradeOfferedAmount > enough) {
                    vyou.msg(plugin.mLang.tradeLackingOut);
                    this.msg(plugin.mLang.tradeLackingIn, you.getDisplayName());
                } else {
                    // Transfer blood (food for vampires, life for humans)
                    if (vyou.isVampire()) {
                        vyou.getFood().add(-amount);
                    } else {
                        vyou.getPlayer().damage(amount);
                    }

                    this.getFood().add(amount * mconf.getTradePercentage());

                    // Risk infection/boost infection
                    if (vyou.isVampire() && !this.isVampire()) {
                        if (this.isInfected()) {
                            this.addInfection(0.01D);
                        }
                        else if (MathUtil.random.nextDouble() * 20 < amount) {
                            this.addInfection(0.05D, InfectionReason.TRADE, vyou);
                        }
                    }
                    // Trader Messages
                    vyou.msg(plugin.mLang.tradeTransferOut, me.getDisplayName(), amount);
                    this.msg(plugin.mLang.tradeTransferIn, amount, you.getDisplayName());

                    // Who noticed?
                    Location tradeLocation = me.getLocation();
                    World tradeWorld = tradeLocation.getWorld();
                    Location l1 = me.getEyeLocation();
                    Location l2 = you.getEyeLocation();
                    for (Player player : tradeWorld.getPlayers()) {
                        if (player.getLocation().distance(tradeLocation) <= mconf.getTradeVisualDistance()) {
                            player.playEffect(l1, Effect.POTION_BREAK, 5);
                            player.playEffect(l2, Effect.POTION_BREAK, 5);
                            if (!player.equals(me) && !player.equals(you)) {
                                String message = TextUtil.parse(plugin.mLang.tradeSeen, me.getDisplayName(), you.getDisplayName());
                                player.sendMessage(message);
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

    public void tradeOffer(UPlayer vyou, double amount) {
        Player you = vyou.getPlayer();
        Player me = this.getPlayer();
        if (you != null && me != null) {
            MConf mconf = plugin.mConf;

            if (!this.withinDistanceOf(vyou, mconf.getTradeOfferMaxDistance())) {
                this.msg(plugin.mLang.tradeNotClose, vyou.getPlayer().getDisplayName());
            } else if (this == vyou) {
                this.msg(plugin.mLang.tradeSelf);
                FxUtil.ensure(PotionEffectType.CONFUSION, me, 12 * 20);
            } else {
                vyou.tradeOfferedFrom = this;
                vyou.tradeOfferedAtMillis = System.currentTimeMillis();
                vyou.tradeOfferedAmount = amount;

                this.msg(plugin.mLang.tradeOfferOut, amount, you.getDisplayName());
                vyou.msg(plugin.mLang.tradeOfferIn, me.getDisplayName(), amount);
                vyou.msg(plugin.mLang.tradeAcceptHelp, "/" + plugin.mConf.getAliasesVampire() + " " + plugin.mConf.getAliasesVampireAccept().get(0));
            }
        }
    }

    public boolean withinDistanceOf(UPlayer vyou, double maxDistance) {
        boolean res = false;
        Player me = this.getPlayer();
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

    public void tickTruce(long millis) {
        if (this.isVampire() && this.truceIsBroken()) {

            this.truceBreakMillisLeftAlter(-millis);

            if (!this.truceIsBroken()) {
                this.truceRestore();
            }
        }
    }

    public boolean truceIsBroken() {
        return this.truceBreakMillisLeft != 0;
    }

    public void truceBreak() {
        Player player = this.getPlayer();
        if (player != null) {
            MConf mconf = plugin.mConf;

            if (!this.truceIsBroken()) {
                this.msg(plugin.mLang.truceBroken);
            }
            this.truceBreakMillisLeftSet(mconf.getTruceBreakMillis());
        }
    }

    public void truceRestore() {
        this.msg(plugin.mLang.truceRestored);
        this.truceBreakMillisLeftSet(0);

        Player me = this.getPlayer();
        if (me != null) {
            MConf mconf = plugin.mConf;

            // Untarget the player.
            for (LivingEntity entity : me.getWorld().getLivingEntities()) {
                if (mconf.getTruceEntityTypes().contains(entity.getType())
                        && entity instanceof Creature) {
                    Creature creature = (Creature) entity;

                    Entity target = creature.getTarget();
                    if (me.equals(target))
                        creature.setTarget(null);
                }
            }
        }
    }

    public long truceBreakMillisLeftGet() {
        return this.truceBreakMillisLeft;
    }

    private void truceBreakMillisLeftSet(long ticks) {
        if (ticks < 0) {
            this.truceBreakMillisLeft = 0;
        } else {
            this.truceBreakMillisLeft = ticks;
        }
    }

    private void truceBreakMillisLeftAlter(long delta) {
        this.truceBreakMillisLeftSet(this.truceBreakMillisLeftGet() + delta);
    }

    // -------------------------------------------- //
    // COMBAT
    // -------------------------------------------- //

    public double combatDamageFactor() {
        double damageFactor = 0D;
        Player me = this.getPlayer();

        if (me != null) {
            MConf mconf = plugin.mConf;

            if (this.isBloodlusting())
                damageFactor = mconf.getCombatDamageFactorWithBloodlust();
            else
                damageFactor = mconf.getCombatDamageFactorWithoutBloodlust();
        }

        return damageFactor;
    }

    public double combatInfectRisk() {
        double infectRisk = 0D;

        Player me = this.getPlayer();
        if (me != null) {
            MConf mconf = plugin.mConf;

            if (this.isVampire()) {
                if (this.isIntending())
                    infectRisk = mconf.getInfectionRiskAtCloseCombatWithIntent();
                else
                    infectRisk = mconf.getInfectionRiskAtCloseCombatWithoutIntent();
            }
        }

        return infectRisk;
    }
}
