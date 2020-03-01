package com.clanjhoo.vampire.entity;

import com.clanjhoo.vampire.PotionEffectConf;
import com.clanjhoo.vampire.VampireRevamp;
import com.clanjhoo.vampire.altar.AltarDark;
import com.clanjhoo.vampire.altar.AltarLight;
import com.clanjhoo.vampire.util.CollectionUtil;
import com.clanjhoo.vampire.util.ResourceUtil;
import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class MConf {
    // -------------------------------------------- //
    // META
    // -------------------------------------------- //

    public MConf(VampireRevamp plugin) {
        altarDark = new AltarDark(plugin);
        altarLight = new AltarLight(plugin);
    }

    public static MConf load(VampireRevamp plugin) {
        MConf i = null;
        File dataFolder = plugin.getDataFolder();
        File configFile = new File(dataFolder, "mconf.json");
        Gson gson = plugin.gson;

        try {
            if (!configFile.exists()) {
                configFile.createNewFile();
                i = new MConf(plugin);
                Writer writer = new FileWriter(configFile);
                gson.toJson(i, writer);
                writer.flush();
                writer.close();
            } else {
                try {
                    Reader reader = new FileReader(configFile);
                    i = gson.fromJson(reader, MConf.class);
                    reader.close();
                }
                catch (Exception ex) {
                    Bukkit.getLogger().log(Level.SEVERE, "Couldn't parse mconf.json. Resetting to defaults!");
                    i = new MConf(plugin);
                    Writer writer = new FileWriter(configFile);
                    gson.toJson(i, writer);
                    writer.flush();
                    writer.close();
                    ex.printStackTrace();
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        return i;
    }

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    private boolean enabled = false;

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    // Aliases
    private List<String> aliasesVampire = CollectionUtil.list("v", "vampire");

    public List<String> getAliasesVampire() {
        return this.aliasesVampire.stream().distinct().collect(Collectors.toList());
    }

    public void setAliasesVampire(List<String> aliasesVampire) {
        this.aliasesVampire = aliasesVampire.stream().distinct().collect(Collectors.toList());
    }

    private List<String> aliasesVampireShow = CollectionUtil.list("show");

    public List<String> getAliasesVampireShow() {
        return this.aliasesVampireShow.stream().distinct().collect(Collectors.toList());
    }

    public void setAliasesVampireShow(List<String> aliasesVampireShow) {
        this.aliasesVampireShow = aliasesVampireShow.stream().distinct().collect(Collectors.toList());
    }

    private List<String> aliasesVampireModeBloodlust = CollectionUtil.list("bloodlust");

    public List<String> getAliasesVampireModeBloodlust() {
        return this.aliasesVampireModeBloodlust.stream().distinct().collect(Collectors.toList());
    }

    public void setAliasesVampireModeBloodlust(List<String> aliasesVampireModeBloodlust) {
        this.aliasesVampireModeBloodlust = aliasesVampireModeBloodlust.stream().distinct().collect(Collectors.toList());
    }

    private List<String> aliasesVampireModeIntend = CollectionUtil.list("intend");

    public List<String> getAliasesVampireModeIntend() {
        return this.aliasesVampireModeIntend.stream().distinct().collect(Collectors.toList());
    }

    public void setAliasesVampireModeIntend(List<String> aliasesVampireModeIntend) {
        this.aliasesVampireModeIntend = aliasesVampireModeIntend.stream().distinct().collect(Collectors.toList());
    }

    private List<String> aliasesVampireModeNightvision = CollectionUtil.list("nv", "nightvision");

    public List<String> getAliasesVampireModeNightvision() {
        return this.aliasesVampireModeNightvision.stream().distinct().collect(Collectors.toList());
    }

    public void setAliasesVampireModeNightvision(List<String> aliasesVampireModeNightvision) {
        this.aliasesVampireModeNightvision = aliasesVampireModeNightvision.stream().distinct().collect(Collectors.toList());
    }

    private List<String> aliasesVampireOffer = CollectionUtil.list("offer");

    public List<String> getAliasesVampireOffer() {
        return this.aliasesVampireOffer.stream().distinct().collect(Collectors.toList());
    }

    public void setAliasesVampireOffer(List<String> aliasesVampireOffer) {
        this.aliasesVampireOffer = aliasesVampireOffer.stream().distinct().collect(Collectors.toList());
    }

    private List<String> aliasesVampireAccept = CollectionUtil.list("accept");

    public List<String> getAliasesVampireAccept() {
        return this.aliasesVampireAccept.stream().distinct().collect(Collectors.toList());
    }

    public void setAliasesVampireAccept(List<String> aliasesVampireAccept) {
        this.aliasesVampireAccept = aliasesVampireAccept.stream().distinct().collect(Collectors.toList());
    }

    private List<String> aliasesVampireFlask = CollectionUtil.list("flask");

    public List<String> getAliasesVampireFlask() {
        return this.aliasesVampireFlask.stream().distinct().collect(Collectors.toList());
    }

    public void setAliasesVampireFlask(List<String> aliasesVampireFlask) {
        this.aliasesVampireFlask = aliasesVampireFlask.stream().distinct().collect(Collectors.toList());
    }

    private List<String> aliasesVampireShriek = CollectionUtil.list("shriek");

    public List<String> getAliasesVampireShriek() {
        return this.aliasesVampireShriek.stream().distinct().collect(Collectors.toList());
    }

    public void setAliasesVampireShriek(List<String> aliasesVampireShriek) {
        this.aliasesVampireShriek = aliasesVampireShriek.stream().distinct().collect(Collectors.toList());
    }

    private List<String> aliasesVampireModeBatusi = CollectionUtil.list("batusi");

    public List<String> getAliasesVampireModeBatusi() {
        return this.aliasesVampireModeBatusi.stream().distinct().collect(Collectors.toList());
    }

    public void setAliasesVampireModeBatusi(List<String> aliasesVampireModeBatusi) {
        this.aliasesVampireModeBatusi = aliasesVampireModeBatusi.stream().distinct().collect(Collectors.toList());
    }

    private List<String> aliasesVampireList = CollectionUtil.list("list");

    public List<String> getAliasesVampireList() {
        return this.aliasesVampireList.stream().distinct().collect(Collectors.toList());
    }

    public void setAliasesVampireList(List<String> aliasesVampireList) {
        this.aliasesVampireList = aliasesVampireList.stream().distinct().collect(Collectors.toList());
    }

    private List<String> aliasesVampireSet = CollectionUtil.list("set");

    public List<String> getAliasesVampireSet() {
        return this.aliasesVampireSet.stream().distinct().collect(Collectors.toList());
    }

    public void setAliasesVampireSet(List<String> aliasesVampireSet) {
        this.aliasesVampireSet = aliasesVampireSet.stream().distinct().collect(Collectors.toList());
    }

    private List<String> aliasesVampireSetVampire = CollectionUtil.list("vampire", "v");

    public List<String> getAliasesVampireSetVampire() {
        return this.aliasesVampireSetVampire.stream().distinct().collect(Collectors.toList());
    }

    public void setAliasesVampireSetVampire(List<String> aliasesVampireSetVampire) {
        this.aliasesVampireSetVampire = aliasesVampireSetVampire.stream().distinct().collect(Collectors.toList());
    }

    private List<String> aliasesVampireSetNosferatu = CollectionUtil.list("nosferatu", "n");

    public List<String> getAliasesVampireSetNosferatu() {
        return this.aliasesVampireSetNosferatu.stream().distinct().collect(Collectors.toList());
    }

    public void setAliasesVampireSetNosferatu(List<String> aliasesVampireSetNosferatu) {
        this.aliasesVampireSetNosferatu = aliasesVampireSetNosferatu.stream().distinct().collect(Collectors.toList());
    }

    private List<String> aliasesVampireSetInfection = CollectionUtil.list("infection", "i");

    public List<String> getAliasesVampireSetInfection() {
        return this.aliasesVampireSetInfection.stream().distinct().collect(Collectors.toList());
    }

    public void setAliasesVampireSetInfection(List<String> aliasesVampireSetInfection) {
        this.aliasesVampireSetInfection = aliasesVampireSetInfection.stream().distinct().collect(Collectors.toList());
    }

    private List<String> aliasesVampireSetFood = CollectionUtil.list("food", "f");

    public List<String> getAliasesVampireSetFood() {
        return this.aliasesVampireSetFood.stream().distinct().collect(Collectors.toList());
    }

    public void setAliasesVampireSetFood(List<String> aliasesVampireSetFood) {
        this.aliasesVampireSetFood = aliasesVampireSetFood.stream().distinct().collect(Collectors.toList());
    }

    private List<String> aliasesVampireSetHealth = CollectionUtil.list("health", "h");

    public List<String> getAliasesVampireSetHealth() {
        return this.aliasesVampireSetHealth.stream().distinct().collect(Collectors.toList());
    }

    public void setAliasesVampireSetHealth(List<String> aliasesVampireSetHealth) {
        this.aliasesVampireSetHealth = aliasesVampireSetHealth.stream().distinct().collect(Collectors.toList());
    }

    private List<String> aliasesVampireEditConfig = CollectionUtil.list("config");

    public List<String> getAliasesVampireEditConfig() {
        return this.aliasesVampireEditConfig.stream().distinct().collect(Collectors.toList());
    }

    public void setAliasesVampireEditConfig(List<String> aliasesVampireEditConfig) {
        this.aliasesVampireEditConfig = aliasesVampireEditConfig.stream().distinct().collect(Collectors.toList());
    }

    private List<String> aliasesVampireEditLang = CollectionUtil.list("lang");

    public List<String> getAliasesVampireEditLang() {
        return this.aliasesVampireEditLang.stream().distinct().collect(Collectors.toList());
    }

    public void setAliasesVampireEditLang(List<String> aliasesVampireEditLang) {
        this.aliasesVampireEditLang = aliasesVampireEditLang.stream().distinct().collect(Collectors.toList());
    }

    private List<String> aliasesVampireVersion = CollectionUtil.list("v", "version");

    public List<String> getAliasesVampireVersion() {
        return this.aliasesVampireVersion.stream().distinct().collect(Collectors.toList());
    }

    public void setAliasesVampireVersion(List<String> aliasesVampireVersion) {
        this.aliasesVampireVersion = aliasesVampireVersion.stream().distinct().collect(Collectors.toList());
    }

    public long taskDelayMillis = 500L; // Half a second

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    // Should the vampire strength be allowed together with skull splitter.
    private boolean combatDamageFactorWithMcmmoAbilities = false;

    public boolean isCombatDamageFactorWithMcmmoAbilities() {
        return this.combatDamageFactorWithMcmmoAbilities;
    }

    public void setCombatDamageFactorWithMcmmoAbilities(boolean combatDamageFactorWithMcmmoAbilities) {
        this.combatDamageFactorWithMcmmoAbilities = combatDamageFactorWithMcmmoAbilities;
    }

    // -------------------------------------------- //
    // FX
    // -------------------------------------------- //

    private double fxSmokePerMilli = 8D / 1000D; // 8 smokes per second

    public double getFxSmokePerMilli() {
        return this.fxSmokePerMilli;
    }

    public void setFxSmokePerMilli(double fxSmokePerMilli) {
        this.fxSmokePerMilli = fxSmokePerMilli;
    }

    private double fxEnderPerMilli = 2D / 1000D; // 2 enders per second

    public double getFxEnderPerMilli() {
        return this.fxEnderPerMilli;
    }

    public void setFxEnderPerMilli(double fxEnderPerMilli) {
        this.fxEnderPerMilli = fxEnderPerMilli;
    }

    private int fxEnderRandomMaxLen = 1;

    public int getFxEnderRandomMaxLen() {
        return this.fxEnderRandomMaxLen;
    }

    public void setFxEnderRandomMaxLen(int fxEnderRandomMaxLen) {
        this.fxEnderRandomMaxLen = fxEnderRandomMaxLen;
    }

    private double fxSmokeBurstCount = 30D;

    public double getFxSmokeBurstCount() {
        return this.fxSmokeBurstCount;
    }

    public void setFxSmokeBurstCount(double fxSmokeBurstCount) {
        this.fxSmokeBurstCount = fxSmokeBurstCount;
    }

    private double fxFlameBurstCount = 5D;

    public double getFxFlameBurstCount() {
        return this.fxFlameBurstCount;
    }

    public void setFxFlameBurstCount(double fxFlameBurstCount) {
        this.fxFlameBurstCount = fxFlameBurstCount;
    }

    private double fxEnderBurstCount = 3D;

    public double getFxEnderBurstCount() {
        return this.fxEnderBurstCount;
    }

    public void setFxEnderBurstCount(double fxEnderBurstCount) {
        this.fxEnderBurstCount = fxEnderBurstCount;
    }

    // -------------------------------------------- //
    // SHRIEK
    // -------------------------------------------- //

    private long shriekWaitMessageCooldownMillis = 500;

    public long getShriekWaitMessageCooldownMillis() {
        return this.shriekWaitMessageCooldownMillis;
    }

    public void setShriekWaitMessageCooldownMillis(long shriekWaitMessageCooldownMillis) {
        this.shriekWaitMessageCooldownMillis = shriekWaitMessageCooldownMillis;
    }

    private long shriekCooldownMillis = 30 * 1000;

    public long getShriekCooldownMillis() {
        return this.shriekCooldownMillis;
    }

    public void setShriekCooldownMillis(long shriekCooldownMillis) {
        this.shriekCooldownMillis = shriekCooldownMillis;
    }

    // -------------------------------------------- //
    // MISC
    // -------------------------------------------- //

    private Set<DamageCause> blockDamageFrom = CollectionUtil.set(
            DamageCause.DROWNING,
            DamageCause.FALL,
            DamageCause.STARVATION
    );

    public Set<DamageCause> getBlockDamageFrom() {
        return this.blockDamageFrom;
    }

    public void setBlockDamageFrom(Set<DamageCause> blockDamageFrom) {
        this.blockDamageFrom = blockDamageFrom;
    }

    private Set<RegainReason> blockHealthFrom = CollectionUtil.set(
            RegainReason.SATIATED,
            RegainReason.REGEN
    );

    public Set<RegainReason> getBlockHealthFrom() {
        return this.blockHealthFrom;
    }

    public void setBlockHealthFrom(Set<RegainReason> blockHealthFrom) {
        this.blockHealthFrom = blockHealthFrom;
    }

    // -------------------------------------------- //
    // UPDATE
    // -------------------------------------------- //

    private int updateRespawnFood = 20;

    public int getUpdateRespawnFood() {
        return this.updateRespawnFood;
    }

    public void setUpdateRespawnFood(int updateRespawnFood) {
        this.updateRespawnFood = updateRespawnFood;
    }

    private int updateRespawnHealth = 20;

    public int getUpdateRespawnHealth() {
        return this.updateRespawnHealth;
    }

    public void setUpdateRespawnHealth(int updateRespawnHealth) {
        this.updateRespawnHealth = updateRespawnHealth;
    }

    private boolean updateNameColor = false;

    public boolean isUpdateNameColor() {
        return this.updateNameColor;
    }

    public void setUpdateNameColor(boolean updateNameColor) {
        this.updateNameColor = updateNameColor;
    }

    private ChatColor updateNameColorTo = ChatColor.RED;

    public ChatColor getUpdateNameColorTo() {
        return this.updateNameColorTo;
    }

    public void setUpdateNameColorTo(ChatColor updateNameColorTo) {
        this.updateNameColorTo = updateNameColorTo;
    }

    // -------------------------------------------- //
    // DROP SELF
    // -------------------------------------------- //

    // Disabled by default
    //private Set<Material> dropSelfMaterials = CollectionUtil.set(
    //		Material.COBWEB,
    //		Material.GLOWSTONE,
    //		Material.BOOKSHELF,
    //		Material.DEAD_BUSH
    //);
    private Set<Material> dropSelfMaterials = CollectionUtil.set();

    public Set<Material> getDropSelfMaterials() {
        return this.dropSelfMaterials;
    }

    public void setDropSelfMaterials(Set<Material> dropSelfMaterials) {
        this.dropSelfMaterials = dropSelfMaterials;
    }

    // -------------------------------------------- //
    // BLOODLUST
    // -------------------------------------------- //

    private double bloodlustMinFood = 2.5D;

    public double getBloodlustMinFood() {
        return this.bloodlustMinFood;
    }

    public void setBloodlustMinFood(double bloodlustMinFood) {
        this.bloodlustMinFood = bloodlustMinFood;
    }

    private double bloodlustFoodPerMilli = -20D / (60D * 1000D); // You can bloodlust for 60 seconds

    public double getBloodlustFoodPerMilli() {
        return this.bloodlustFoodPerMilli;
    }

    public void setBloodlustFoodPerMilli(double bloodlustFoodPerMilli) {
        this.bloodlustFoodPerMilli = bloodlustFoodPerMilli;
    }

    private double bloodlustSmokes = 1.5D;

    public double getBloodlustSmokes() {
        return this.bloodlustSmokes;
    }

    public void setBloodlustSmokes(double bloodlustSmokes) {
        this.bloodlustSmokes = bloodlustSmokes;
    }

    // -------------------------------------------- //
    // NIGHTVISION
    // -------------------------------------------- //

    private boolean nightvisionCanBeUsed = true;

    public boolean isNightvisionCanBeUsed() {
        return this.nightvisionCanBeUsed;
    }

    public void setNightvisionCanBeUsed(boolean nightvisionCanBeUsed) {
        this.nightvisionCanBeUsed = nightvisionCanBeUsed;
    }

    // -------------------------------------------- //
    // UNDEAD HORSES
    // -------------------------------------------- //

    private boolean canInfectHorses = true;

    public boolean isCanInfectHorses() {
        return this.canInfectHorses;
    }

    public void setCanInfectHorses(boolean canInfectHorses) {
        this.canInfectHorses = canInfectHorses;
    }

    // -------------------------------------------- //
    // POTION EFFECTS
    // -------------------------------------------- //

    private PotionEffectConf effectConfBloodlust = new PotionEffectConf(EventPriority.HIGHEST, true, 0x1f1f23, CollectionUtil.map(
            PotionEffectType.JUMP, 3,
            PotionEffectType.SPEED, 3
    ));

    public PotionEffectConf getEffectConfBloodlust() {
        return this.effectConfBloodlust;
    }

    public void setEffectConfBloodlust(PotionEffectConf effectConfBloodlust) {
        this.effectConfBloodlust = effectConfBloodlust;
    }

    private PotionEffectConf effectConfNightvision = new PotionEffectConf(EventPriority.HIGH, true, 0, CollectionUtil.map(
            PotionEffectType.NIGHT_VISION, 1
    ));

    public PotionEffectConf getEffectConfNightvision() {
        return this.effectConfNightvision;
    }

    public void setEffectConfNightvision(PotionEffectConf effectConfNightvision) {
        this.effectConfNightvision = effectConfNightvision;
    }

    private PotionEffectConf effectConfVampire = new PotionEffectConf(EventPriority.NORMAL, true, 0, CollectionUtil.map(
            PotionEffectType.JUMP, 1,
            PotionEffectType.SPEED, 1
    ));

    public PotionEffectConf getEffectConfVampire() {
        return this.effectConfVampire;
    }

    public void setEffectConfVampire(PotionEffectConf effectConfVampire) {
        this.effectConfVampire = effectConfVampire;
    }

    private PotionEffectConf effectConfInfected = new PotionEffectConf(EventPriority.NORMAL, true, 0x587653, new HashMap<>());

    public PotionEffectConf getEffectConfInfected() {
        return this.effectConfInfected;
    }

    public void setEffectConfInfected(PotionEffectConf effectConfInfected) {
        this.effectConfInfected = effectConfInfected;
    }

    private PotionEffectConf effectConfHuman = new PotionEffectConf(EventPriority.NORMAL, false, 0, new HashMap<>());

    public PotionEffectConf getEffectConfHuman() {
        return this.effectConfHuman;
    }

    public void setEffectConfHuman(PotionEffectConf effectConfHuman) {
        this.effectConfHuman = effectConfHuman;
    }


    // -------------------------------------------- //
    // REGEN
    // -------------------------------------------- //

    private double regenMinFood = 2.5D;

    public double getRegenMinFood() {
        return this.regenMinFood;
    }

    public void setRegenMinFood(double regenMinFood) {
        this.regenMinFood = regenMinFood;
    }

    private int regenDelayMillis = 10 * 1000;

    public int getRegenDelayMillis() {
        return this.regenDelayMillis;
    }

    public void setRegenDelayMillis(int regenDelayMillis) {
        this.regenDelayMillis = regenDelayMillis;
    }

    private double regenFoodPerMilli = 0.5D / 1000D; // Regen 0.5 food per second

    public double getRegenFoodPerMilli() {
        return this.regenFoodPerMilli;
    }

    public void setRegenFoodPerMilli(double regenFoodPerMilli) {
        this.regenFoodPerMilli = regenFoodPerMilli;
    }

    private double regenHealthPerFood = 2D;

    public double getRegenHealthPerFood() {
        return this.regenHealthPerFood;
    }

    public void setRegenHealthPerFood(double regenHealthPerFood) {
        this.regenHealthPerFood = regenHealthPerFood;
    }

    // -------------------------------------------- //
    // TRUCE
    // -------------------------------------------- //

    // One minute
    private long truceBreakMillis = 60L * 1000L;

    public long getTruceBreakMillis() {
        return this.truceBreakMillis;
    }

    public void setTruceBreakMillis(long truceBreakMillis) {
        this.truceBreakMillis = truceBreakMillis;
    }

    // These are the creature types that won't target vampires
    private Set<EntityType> truceEntityTypes = CollectionUtil.set(
            EntityType.BLAZE,
            EntityType.CAVE_SPIDER,
            EntityType.CREEPER,
            EntityType.ENDERMAN,
            EntityType.GHAST,
            EntityType.GIANT,
            EntityType.MAGMA_CUBE,
            EntityType.PIG_ZOMBIE,
            EntityType.SKELETON,
            EntityType.SPIDER,
            EntityType.ZOMBIE,
            EntityType.WITCH,
            EntityType.GUARDIAN,
            EntityType.SILVERFISH,
            EntityType.ENDERMITE,
            EntityType.DROWNED,
            EntityType.HUSK,
            EntityType.STRAY,
            EntityType.PHANTOM
    );

    public Set<EntityType> getTruceEntityTypes() {
        return this.truceEntityTypes;
    }

    public void setTruceEntityTypes(Set<EntityType> truceEntityTypes) {
        this.truceEntityTypes = truceEntityTypes;
    }

    // -------------------------------------------- //
    // COMBAT
    // -------------------------------------------- //

    private double combatDamageFactorWithoutBloodlust = 1.0;

    public double getCombatDamageFactorWithoutBloodlust() {
        return this.combatDamageFactorWithoutBloodlust;
    }

    public void setCombatDamageFactorWithoutBloodlust(double combatDamageFactorWithoutBloodlust) {
        this.combatDamageFactorWithoutBloodlust = combatDamageFactorWithoutBloodlust;
    }

    private double combatDamageFactorWithBloodlust = 1.15;

    public double getCombatDamageFactorWithBloodlust() {
        return this.combatDamageFactorWithBloodlust;
    }

    public void setCombatDamageFactorWithBloodlust(double combatDamageFactorWithBloodlust) {
        this.combatDamageFactorWithBloodlust = combatDamageFactorWithBloodlust;
    }

    private final static transient int damageDiamondSword = 7;
    private int combatWoodDamage = 3 * damageDiamondSword;

    public int getCombatWoodDamage() {
        return this.combatWoodDamage;
    }

    public void setCombatWoodDamage(int combatWoodDamage) {
        this.combatWoodDamage = combatWoodDamage;
    }

    private Set<Material> combatWoodMaterials = CollectionUtil.set(
            Material.WOODEN_AXE,
            Material.WOODEN_HOE,
            Material.WOODEN_PICKAXE,
            Material.WOODEN_SHOVEL,
            Material.WOODEN_SWORD,
            Material.STICK,
            Material.TORCH,
            Material.REDSTONE_TORCH,
            Material.SPRUCE_SIGN,
            Material.ACACIA_SIGN,
            Material.BIRCH_SIGN,
            Material.DARK_OAK_SIGN,
            Material.JUNGLE_SIGN,
            Material.OAK_SIGN,
            Material.ACACIA_FENCE,
            Material.ACACIA_FENCE_GATE,
            Material.BIRCH_FENCE,
            Material.BIRCH_FENCE_GATE,
            Material.DARK_OAK_FENCE,
            Material.DARK_OAK_FENCE_GATE,
            Material.JUNGLE_FENCE,
            Material.JUNGLE_FENCE_GATE,
            Material.OAK_FENCE,
            Material.OAK_FENCE_GATE,
            Material.SPRUCE_FENCE,
            Material.SPRUCE_FENCE_GATE
    );

    public Set<Material> getCombatWoodMaterials() {
        return this.combatWoodMaterials;
    }

    public void setCombatWoodMaterials(Set<Material> combatWoodMaterials) {
        this.combatWoodMaterials = combatWoodMaterials;
    }

    // -------------------------------------------- //
    // INFECTION
    // -------------------------------------------- //

    // It will take you 0.25h to turn
    private double infectionPerMilli = 0.25D / (1000D * 60D * 60D);

    public double getInfectionPerMilli() {
        return this.infectionPerMilli;
    }

    public void setInfectionPerMilli(double infectionPerMilli) {
        this.infectionPerMilli = infectionPerMilli;
    }

    private int infectionProgressNauseaTicks = 12 * 20;

    public int getInfectionProgressNauseaTicks() {
        return this.infectionProgressNauseaTicks;
    }

    public void setInfectionProgressNauseaTicks(int infectionProgressNauseaTicks) {
        this.infectionProgressNauseaTicks = infectionProgressNauseaTicks;
    }

    private int infectionProgressDamage = 1;

    public int getInfectionProgressDamage() {
        return this.infectionProgressDamage;
    }

    public void setInfectionProgressDamage(int infectionProgressDamage) {
        this.infectionProgressDamage = infectionProgressDamage;
    }

    // We have disabled this feature per default.
    // public Double infectionRiskAtCloseCombatWithoutIntent = 0.003;
    // public Double infectionRiskAtCloseCombatWithIntent = 0.05;
    private double infectionRiskAtCloseCombatWithoutIntent = 0D;

    public double getInfectionRiskAtCloseCombatWithoutIntent() {
        return this.infectionRiskAtCloseCombatWithoutIntent;
    }

    public void setInfectionRiskAtCloseCombatWithoutIntent(double infectionRiskAtCloseCombatWithoutIntent) {
        this.infectionRiskAtCloseCombatWithoutIntent = infectionRiskAtCloseCombatWithoutIntent;
    }

    private double infectionRiskAtCloseCombatWithIntent = 0D;

    public double getInfectionRiskAtCloseCombatWithIntent() {
        return this.infectionRiskAtCloseCombatWithIntent;
    }

    public void setInfectionRiskAtCloseCombatWithIntent(double infectionRiskAtCloseCombatWithIntent) {
        this.infectionRiskAtCloseCombatWithIntent = infectionRiskAtCloseCombatWithIntent;
    }

    // -------------------------------------------- //
    // TRADE
    // -------------------------------------------- //

    private double tradeOfferMaxDistance = 2d;

    public double getTradeOfferMaxDistance() {
        return this.tradeOfferMaxDistance;
    }

    public void setTradeOfferMaxDistance(double tradeOfferMaxDistance) {
        this.tradeOfferMaxDistance = tradeOfferMaxDistance;
    }

    private long tradeOfferToleranceMillis = 20 * 1000;

    public long getTradeOfferToleranceMillis() {
        return this.tradeOfferToleranceMillis;
    }

    public void setTradeOfferToleranceMillis(long tradeOfferToleranceMillis) {
        this.tradeOfferToleranceMillis = tradeOfferToleranceMillis;
    }

    private double tradeVisualDistance = 7D;

    public double getTradeVisualDistance() {
        return this.tradeVisualDistance;
    }

    public void setTradeVisualDistance(double tradeVisualDistance) {
        this.tradeVisualDistance = tradeVisualDistance;
    }

    private double tradePercentage = 1d;

    public double getTradePercentage() {
        return this.tradePercentage;
    }

    public void setTradePercentage(double tradePercentage) {
        this.tradePercentage = tradePercentage;
    }

    // -------------------------------------------- //
    // FOOD
    // -------------------------------------------- //

    private boolean foodCakeAllowed = true;

    public boolean isFoodCakeAllowed() {
        return this.foodCakeAllowed;
    }

    public void setFoodCakeAllowed(boolean foodCakeAllowed) {
        this.foodCakeAllowed = foodCakeAllowed;
    }

    private Map<EntityType, Double> entityTypeFullFoodQuotient = CollectionUtil.map(
            EntityType.PLAYER, 30 / 20D,
            EntityType.VINDICATOR, 20 / 20D,
            EntityType.PILLAGER, 20 / 20D,
            EntityType.VILLAGER, 15 / 20D,
            EntityType.ENDER_DRAGON, 140 / 20D,
            EntityType.MUSHROOM_COW, 20 / 20D,
            EntityType.GIANT, 50 / 20D,
            EntityType.CREEPER, 10 / 20D,
            EntityType.SPIDER, 10 / 20D,
            EntityType.ZOMBIE, 5 / 20D,
            EntityType.GHAST, 10 / 20D,
            EntityType.PIG_ZOMBIE, 10 / 20D,
            EntityType.ENDERMAN, 10 / 20D,
            EntityType.CAVE_SPIDER, 10 / 20D,
            EntityType.PIG, 10 / 20D,
            EntityType.SHEEP, 10 / 20D,
            EntityType.COW, 10 / 20D,
            EntityType.HORSE, 10 / 20D,
            EntityType.CHICKEN, 10 / 20D,
            EntityType.SQUID, 10 / 20D,
            EntityType.OCELOT, 10 / 20D,
            EntityType.IRON_GOLEM, 0 / 20D,
            EntityType.WOLF, 0 / 20D,
            EntityType.SNOWMAN, 0 / 20D,
            EntityType.SILVERFISH, 0 / 20D,
            EntityType.BLAZE, 0 / 20D,
            EntityType.MAGMA_CUBE, 0 / 20D,
            EntityType.SLIME, 0 / 20D,
            EntityType.SKELETON, 0 / 20D);

    public Map<EntityType, Double> getEntityTypeFullFoodQuotient() {
        return this.entityTypeFullFoodQuotient;
    }

    public void setEntityTypeFullFoodQuotient(Map<EntityType, Double> entityTypeFullFoodQuotient) {
        this.entityTypeFullFoodQuotient = entityTypeFullFoodQuotient;
    }

    // -------------------------------------------- //
    // HOLY WATER
    // -------------------------------------------- //

    private double holyWaterSplashRadius = 6D;

    public double getHolyWaterSplashRadius() {
        return this.holyWaterSplashRadius;
    }

    public void setHolyWaterSplashRadius(double holyWaterSplashRadius) {
        this.holyWaterSplashRadius = holyWaterSplashRadius;
    }

    private double holyWaterTemp = 0.7D;

    public double getHolyWaterTemp() {
        return this.holyWaterTemp;
    }

    public void setHolyWaterTemp(double holyWaterTemp) {
        this.holyWaterTemp = holyWaterTemp;
    }

    private List<ItemStack> holyWaterResources = CollectionUtil.list(
            ResourceUtil.getWaterBottles(1),
            new ItemStack(Material.LAPIS_LAZULI, 1)
    );

    public List<ItemStack> getHolyWaterResources() {
        return this.holyWaterResources;
    }

    public void setHolyWaterResources(List<ItemStack> holyWaterResources) {
        this.holyWaterResources = holyWaterResources;
    }

    // -------------------------------------------- //
    // SUN
    // -------------------------------------------- //

    private double opacityPerArmorPiece = 0.125d;

    public double getOpacityPerArmorPiece() {
        return this.opacityPerArmorPiece;
    }

    public void setOpacityPerArmorPiece(double opacityPerArmorPiece) {
        this.opacityPerArmorPiece = opacityPerArmorPiece;
    }

    private double baseRad = -0.2d;

    public double getBaseRad() {
        return this.baseRad;
    }

    public void setBaseRad(double baseRad) {
        this.baseRad = baseRad;
    }

    private double tempPerRadAndMilli = 1d / (10d * 1000d); // it should take 10 seconds to reach max temp in maximum sunlight.

    public double getTempPerRadAndMilli() {
        return this.tempPerRadAndMilli;
    }

    public void setTempPerRadAndMilli(double tempPerRadAndMilli) {
        this.tempPerRadAndMilli = tempPerRadAndMilli;
    }

    private double sunNauseaTemp = 0.20d;

    public double getSunNauseaTemp() {
        return this.sunNauseaTemp;
    }

    public void setSunNauseaTemp(double sunNauseaTemp) {
        this.sunNauseaTemp = sunNauseaTemp;
    }

    private double sunWeaknessTemp = 0.30d;

    public double getSunWeaknessTemp() {
        return this.sunWeaknessTemp;
    }

    public void setSunWeaknessTemp(double sunWeaknessTemp) {
        this.sunWeaknessTemp = sunWeaknessTemp;
    }

    private double sunSlowTemp = 0.50d;

    public double getSunSlowTemp() {
        return this.sunSlowTemp;
    }

    public void setSunSlowTemp(double sunSlowTemp) {
        this.sunSlowTemp = sunSlowTemp;
    }

    private double sunStopEffectsTemp = 0.65d;

    public double getSunStopEffectsTemp() {
        return this.sunStopEffectsTemp;
    }

    public void setSunStopEffectsTemp(double sunStopEffectsTemp) {
        this.sunStopEffectsTemp = sunStopEffectsTemp;
    }

    private double sunBlindnessTemp = 0.80d;

    public double getSunBlindnessTemp() {
        return this.sunBlindnessTemp;
    }

    public void setSunBlindnessTemp(double sunBlindnessTemp) {
        this.sunBlindnessTemp = sunBlindnessTemp;
    }

    private double sunBurnTemp = 0.90d;

    public double getSunBurnTemp() {
        return this.sunBurnTemp;
    }

    public void setSunBurnTemp(double sunBurnTemp) {
        this.sunBurnTemp = sunBurnTemp;
    }

    private int sunNauseaTicks = 10 * 20;

    public int getSunNauseaTicks() {
        return this.sunNauseaTicks;
    }

    public void setSunNauseaTicks(int sunNauseaTicks) {
        this.sunNauseaTicks = sunNauseaTicks;
    }

    private int sunWeaknessTicks = 10 * 20;

    public int getSunWeaknessTicks() {
        return this.sunWeaknessTicks;
    }

    public void setSunWeaknessTicks(int sunWeaknessTicks) {
        this.sunWeaknessTicks = sunWeaknessTicks;
    }

    private int sunSlowTicks = 10 * 20;

    public int getSunSlowTicks() {
        return this.sunSlowTicks;
    }

    public void setSunSlowTicks(int sunSlowTicks) {
        this.sunSlowTicks = sunSlowTicks;
    }

    private int sunBlindnessTicks = 10 * 20;

    public int getSunBlindnessTicks() {
        return this.sunBlindnessTicks;
    }

    public void setSunBlindnessTicks(int sunBlindnessTicks) {
        this.sunBlindnessTicks = sunBlindnessTicks;
    }

    private int sunBurnTicks = 3 * 20;

    public int getSunBurnTicks() {
        return this.sunBurnTicks;
    }

    public void setSunBurnTicks(int sunBurnTicks) {
        this.sunBurnTicks = sunBurnTicks;
    }

    private double sunSmokesPerTempAndMilli = 12D / 1000D; // 12 smokes per second in full sunlight

    public double getSunSmokesPerTempAndMilli() {
        return this.sunSmokesPerTempAndMilli;
    }

    public void setSunSmokesPerTempAndMilli(double sunSmokesPerTempAndMilli) {
        this.sunSmokesPerTempAndMilli = sunSmokesPerTempAndMilli;
    }

    private double sunFlamesPerTempAndMilli = 0.4D / 1000D; // 0.4 flames every second in full sunlight

    public double getSunFlamesPerTempAndMilli() {
        return this.sunFlamesPerTempAndMilli;
    }

    public void setSunFlamesPerTempAndMilli(double sunFlamesPerTempAndMilli) {
        this.sunFlamesPerTempAndMilli = sunFlamesPerTempAndMilli;
    }

    //We assume opacity 1 for all materials not in this map
    private final static transient Double AIR = 0D;
    private final static transient Double GROUND = 0D;
    private final static transient Double STAIRS = 1D;
    private final static transient Double SLABS = 1D;
    private final static transient Double DOOR = 0D;
    private final static transient Double THIN = 0D;
    private final static transient Double STICK = 0.1D;
    private final static transient Double WATER = 0.2D;
    private final static transient Double VEGETATION = 0.2D;
    private final static transient Double FENCE = 0.4D;
    private final static transient Double GLASS = 0.5D;

    private Map<Material, Double> typeOpacity = CollectionUtil.map(
            Material.AIR, AIR, //AIR
            Material.CAVE_AIR, AIR, //AIR
            Material.VOID_AIR, AIR, //AIR
            //1, XD, //STONE
            //2, XD, //GRASS
            //3, XD, //DIRT
            //4, XD, //COBBLESTONE
            //5, XD, //WOOD
            Material.ACACIA_SAPLING, VEGETATION, //SAPLING
            Material.BIRCH_SAPLING, VEGETATION, //SAPLING
            Material.DARK_OAK_SAPLING, VEGETATION, //SAPLING
            Material.JUNGLE_SAPLING, VEGETATION, //SAPLING
            Material.OAK_SAPLING, VEGETATION, //SAPLING
            Material.SPRUCE_SAPLING, VEGETATION, //SAPLING
            //7, XD, //BEDROCK
            Material.WATER, WATER, //WATER
            //9, WATER, //STATIONARY_WATER
            //10, XD, //LAVA
            //11, XD, //STATIONARY_LAVA
            //12, XD, //SAND
            //13, XD, //GRAVEL
            //14, XD, //GOLD_ORE
            //15, XD, //IRON_ORE
            //16, XD, //COAL_ORE
            //17, XD, //LOG
            Material.ACACIA_LEAVES, VEGETATION, //LEAVES
            Material.BIRCH_LEAVES, VEGETATION, //LEAVES
            Material.DARK_OAK_LEAVES, VEGETATION, //LEAVES
            Material.JUNGLE_LEAVES, VEGETATION, //LEAVES
            Material.OAK_LEAVES, VEGETATION, //LEAVES
            Material.SPRUCE_LEAVES, VEGETATION, //LEAVES
            //19, XD, //SPONGE
            Material.GLASS, GLASS, //GLASS
            Material.BLACK_STAINED_GLASS, THIN, //GLASS
            Material.BLUE_STAINED_GLASS, THIN, //GLASS
            Material.BROWN_STAINED_GLASS, THIN, //GLASS
            Material.CYAN_STAINED_GLASS, THIN, //GLASS
            Material.GRAY_STAINED_GLASS, THIN, //GLASS
            Material.GREEN_STAINED_GLASS, THIN, //GLASS
            Material.LIGHT_BLUE_STAINED_GLASS, THIN, //GLASS
            Material.LIGHT_GRAY_STAINED_GLASS, THIN, //GLASS
            Material.LIME_STAINED_GLASS, THIN, //GLASS
            Material.MAGENTA_STAINED_GLASS, THIN, //GLASS
            Material.ORANGE_STAINED_GLASS, THIN, //GLASS
            Material.PINK_STAINED_GLASS, THIN, //GLASS
            Material.PURPLE_STAINED_GLASS, THIN, //GLASS
            Material.RED_STAINED_GLASS, THIN, //GLASS
            Material.WHITE_STAINED_GLASS, THIN, //GLASS
            //21, XD, //LAPIS_ORE
            //22, XD, //LAPIS_BLOCK
            //23, XD, //DISPENSER
            //24, XD, //SANDSTONE
            //25, XD, //NOTE_BLOCK
            //26, XD, //BED_BLOCK
            Material.POWERED_RAIL, GROUND, //POWERED_RAIL
            Material.DETECTOR_RAIL, GROUND, //DETECTOR_RAIL
            //29, XD, //PISTON_STICKY_BASE
            Material.COBWEB, THIN, //WEB
            Material.TALL_GRASS, VEGETATION, //LONG_GRASS
            Material.DEAD_BUSH, VEGETATION, //DEAD_BUSH
            //33, XD, //PISTON_BASE
            Material.PISTON_HEAD, STICK, //PISTON_EXTENSION
            //35, XD, //WOOL
            //36, XD, //PISTON_MOVING_PIECE
            Material.DANDELION, VEGETATION, //FLOWER
            Material.POPPY, VEGETATION, //FLOWER
            Material.BLUE_ORCHID, VEGETATION, //FLOWER
            Material.ALLIUM, VEGETATION, //FLOWER
            Material.AZURE_BLUET, VEGETATION, //FLOWER
            Material.ORANGE_TULIP, VEGETATION, //FLOWER
            Material.PINK_TULIP, VEGETATION, //FLOWER
            Material.RED_TULIP, VEGETATION, //FLOWER
            Material.WHITE_TULIP, VEGETATION, //FLOWER
            Material.OXEYE_DAISY, VEGETATION, //FLOWER
            Material.SUNFLOWER, VEGETATION, //FLOWER
            Material.LILAC, VEGETATION, //FLOWER
            Material.ROSE_BUSH, VEGETATION, //FLOWER
            Material.PEONY, VEGETATION, //FLOWER
            Material.BROWN_MUSHROOM, VEGETATION, //BROWN_MUSHROOM
            Material.RED_MUSHROOM, VEGETATION, //RED_MUSHROOM
            //41, XD, //GOLD_BLOCK
            //42, XD, //IRON_BLOCK
            //43, XD, //DOUBLE_STEP
            Material.SANDSTONE_SLAB, SLABS, //STEP
            Material.RED_SANDSTONE_SLAB, SLABS, //STEP
            Material.STONE_BRICK_SLAB, SLABS, //STEP
            Material.STONE_SLAB, SLABS, //STEP
            Material.BRICK_SLAB, SLABS, //STEP
            Material.COBBLESTONE_SLAB, SLABS, //STEP
            Material.DARK_PRISMARINE_SLAB, SLABS, //STEP
            Material.PRISMARINE_SLAB, SLABS, //STEP
            Material.PRISMARINE_BRICK_SLAB, SLABS, //STEP
            Material.NETHER_BRICK_SLAB, SLABS, //STEP
            Material.QUARTZ_SLAB, SLABS, //STEP
            Material.PURPUR_SLAB, SLABS, //STEP
            Material.PETRIFIED_OAK_SLAB, SLABS, //STEP
            Material.ACACIA_SLAB, SLABS, //STEP
            Material.BIRCH_SLAB, SLABS, //STEP
            Material.DARK_OAK_SLAB, SLABS, //STEP
            Material.JUNGLE_SLAB, SLABS, //STEP
            Material.OAK_SLAB, SLABS, //STEP
            Material.SPRUCE_SLAB, SLABS, //STEP
            Material.STONE_SLAB, SLABS, //STEP
            //45, XD, //BRICK
            //46, XD, //TNT
            //47, XD, //BOOKSHELF
            //48, XD, //MOSSY_COBBLESTONE
            //49, XD, //OBSIDIAN
            Material.TORCH, STICK, //TORCH
            Material.FIRE, THIN, //FIRE
            //52, XD, //MOB_SPAWNER
            //53, STAIRS, //WOOD_STAIRS
            Material.SANDSTONE_STAIRS, STAIRS, //STEP
            Material.RED_SANDSTONE_STAIRS, STAIRS, //STEP
            Material.STONE_BRICK_STAIRS, STAIRS, //STEP
            Material.BRICK_STAIRS, STAIRS, //STEP
            Material.COBBLESTONE_STAIRS, STAIRS, //STEP
            Material.DARK_PRISMARINE_STAIRS, STAIRS, //STEP
            Material.PRISMARINE_STAIRS, STAIRS, //STEP
            Material.PRISMARINE_BRICK_STAIRS, STAIRS, //STEP
            Material.NETHER_BRICK_STAIRS, STAIRS, //STEP
            Material.QUARTZ_STAIRS, STAIRS, //STEP
            Material.PURPUR_STAIRS, STAIRS, //STEP
            Material.ACACIA_STAIRS, STAIRS, //STEP
            Material.BIRCH_STAIRS, STAIRS, //STEP
            Material.DARK_OAK_STAIRS, STAIRS, //STEP
            Material.JUNGLE_STAIRS, STAIRS, //STEP
            Material.OAK_STAIRS, STAIRS, //STEP
            Material.SPRUCE_STAIRS, STAIRS, //STEP
            Material.STONE_STAIRS, STAIRS, //STEP
            //54, XD, //CHEST
            Material.REDSTONE_WIRE, GROUND, //REDSTONE_WIRE
            //56, XD, //DIAMOND_ORE
            //57, XD, //DIAMOND_BLOCK
            //58, XD, //WORKBENCH
            Material.WHEAT, VEGETATION, //CROPS
            Material.POTATOES, VEGETATION, //CROPS
            Material.BEETROOTS, VEGETATION, //CROPS
            Material.CARROTS, VEGETATION, //CROPS
            Material.PUMPKIN_STEM, VEGETATION, //CROPS
            Material.MELON_STEM, VEGETATION, //CROPS
            Material.NETHER_WART, VEGETATION, //CROPS
            //60, XD, //SOIL
            //61, XD, //FURNACE
            //62, XD, //BURNING_FURNACE
            Material.SPRUCE_SIGN, STICK, //SIGN_POST
            Material.SPRUCE_WALL_SIGN, STICK, //SIGN_POST
            Material.ACACIA_SIGN, STICK, //SIGN_POST
            Material.ACACIA_WALL_SIGN, STICK, //SIGN_POST
            Material.BIRCH_SIGN, STICK, //SIGN_POST
            Material.BIRCH_WALL_SIGN, STICK, //SIGN_POST
            Material.DARK_OAK_SIGN, STICK, //SIGN_POST
            Material.DARK_OAK_WALL_SIGN, STICK, //SIGN_POST
            Material.JUNGLE_SIGN, STICK, //SIGN_POST
            Material.JUNGLE_WALL_SIGN, STICK, //SIGN_POST
            Material.OAK_SIGN, STICK, //SIGN_POST
            Material.OAK_WALL_SIGN, STICK, //SIGN_POST
            //64, DOOR, //WOODEN_DOOR
            Material.ACACIA_DOOR, DOOR, //WOODEN_DOOR
            Material.BIRCH_DOOR, DOOR, //WOODEN_DOOR
            Material.DARK_OAK_DOOR, DOOR, //WOODEN_DOOR
            Material.JUNGLE_DOOR, DOOR, //WOODEN_DOOR
            Material.OAK_DOOR, DOOR, //WOODEN_DOOR
            Material.SPRUCE_DOOR, DOOR, //WOODEN_DOOR
            Material.LADDER, THIN, //LADDER
            Material.RAIL, GROUND, //RAILS
            //67, STAIRS, //COBBLESTONE_STAIRS
            //68, THIN, //WALL_SIGN
            Material.LEVER, STICK, //LEVER
            Material.HEAVY_WEIGHTED_PRESSURE_PLATE, GROUND, //PRESSURE_PLATE
            Material.LIGHT_WEIGHTED_PRESSURE_PLATE, GROUND, //PRESSURE_PLATE
            Material.STONE_PRESSURE_PLATE, GROUND, //PRESSURE_PLATE
            Material.IRON_DOOR, DOOR, //IRON_DOOR
            Material.ACACIA_PRESSURE_PLATE, GROUND, //PRESSURE_PLATE
            Material.BIRCH_PRESSURE_PLATE, GROUND, //PRESSURE_PLATE
            Material.DARK_OAK_PRESSURE_PLATE, GROUND, //PRESSURE_PLATE
            Material.JUNGLE_PRESSURE_PLATE, GROUND, //PRESSURE_PLATE
            Material.OAK_PRESSURE_PLATE, GROUND, //PRESSURE_PLATE
            Material.SPRUCE_PRESSURE_PLATE, GROUND, //PRESSURE_PLATE
            //73, XD, //REDSTONE_ORE
            //74, XD, //GLOWING_REDSTONE_ORE
            Material.REDSTONE_TORCH, STICK, //REDSTONE_TORCH
            Material.REDSTONE_WALL_TORCH, STICK, //REDSTONE_WALL_TORCH
            Material.STONE_BUTTON, THIN, //STONE_BUTTON
            Material.ACACIA_BUTTON, THIN, //WOODEN_BUTTON
            Material.BIRCH_BUTTON, THIN, //WOODEN_BUTTON
            Material.DARK_OAK_BUTTON, THIN, //WOODEN_BUTTON
            Material.JUNGLE_BUTTON, THIN, //WOODEN_BUTTON
            Material.OAK_BUTTON, THIN, //WOODEN_BUTTON
            Material.SPRUCE_BUTTON, THIN, //WOODEN_BUTTON
            Material.SNOW, GROUND, //SNOW
            //79, XD, //ICE
            //80, XD, //SNOW_BLOCK
            //81, XD, //CACTUS
            //82, XD, //CLAY
            Material.SUGAR_CANE, VEGETATION, //SUGAR_CANE_BLOCK
            //84, XD, //JUKEBOX
            //85, FENCE, //FENCE
            Material.NETHER_BRICK_FENCE, FENCE, //FENCE
            Material.ACACIA_FENCE, FENCE, //FENCE
            Material.BIRCH_FENCE, FENCE, //FENCE
            Material.DARK_OAK_FENCE, FENCE, //FENCE
            Material.JUNGLE_FENCE, FENCE, //FENCE
            Material.OAK_FENCE, FENCE, //FENCE
            Material.SPRUCE_FENCE, FENCE, //FENCE
            Material.COBBLESTONE_WALL, FENCE, //FENCE
            Material.MOSSY_COBBLESTONE_WALL, FENCE, //FENCE
            //86, XD, //PUMPKIN
            //87, XD, //NETHERRACK
            //88, XD, //SOUL_SAND
            //89, XD, //GLOWSTONE
            //90, XD, //PORTAL
            //91, XD, //JACK_O_LANTERN
            //92, XD, //CAKE_BLOCK
            Material.REPEATER, GROUND, //DIODE_BLOCK
            Material.COMPARATOR, GROUND, //DIODE_BLOCK
            //94, GROUND, //DIODE_BLOCK_ON
            //95, XD, //LOCKED_CHEST
            //96, DOOR, //TRAP_DOOR
            Material.IRON_TRAPDOOR, DOOR, //TRAP_DOOR
            Material.ACACIA_TRAPDOOR, DOOR, //TRAP_DOOR
            Material.BIRCH_TRAPDOOR, DOOR, //TRAP_DOOR
            Material.DARK_OAK_TRAPDOOR, DOOR, //TRAP_DOOR
            Material.JUNGLE_TRAPDOOR, DOOR, //TRAP_DOOR
            Material.OAK_TRAPDOOR, DOOR, //TRAP_DOOR
            Material.SPRUCE_TRAPDOOR, DOOR, //TRAP_DOOR
            //97, XD, //MONSTER_EGGS
            //98, XD, //SMOOTH_BRICK
            //99, XD, //HUGE_MUSHROOM_1
            //100, XD, //HUGE_MUSHROOM_2
            Material.IRON_BARS, FENCE, //IRON_FENCE
            Material.GLASS_PANE, THIN, //THIN_GLASS
            Material.BLACK_STAINED_GLASS_PANE, THIN, //THIN_GLASS
            Material.BLUE_STAINED_GLASS_PANE, THIN, //THIN_GLASS
            Material.BROWN_STAINED_GLASS_PANE, THIN, //THIN_GLASS
            Material.CYAN_STAINED_GLASS_PANE, THIN, //THIN_GLASS
            Material.GRAY_STAINED_GLASS_PANE, THIN, //THIN_GLASS
            Material.GREEN_STAINED_GLASS_PANE, THIN, //THIN_GLASS
            Material.LIGHT_BLUE_STAINED_GLASS_PANE, THIN, //THIN_GLASS
            Material.LIGHT_GRAY_STAINED_GLASS_PANE, THIN, //THIN_GLASS
            Material.LIME_STAINED_GLASS_PANE, THIN, //THIN_GLASS
            Material.MAGENTA_STAINED_GLASS_PANE, THIN, //THIN_GLASS
            Material.ORANGE_STAINED_GLASS_PANE, THIN, //THIN_GLASS
            Material.PINK_STAINED_GLASS_PANE, THIN, //THIN_GLASS
            Material.PURPLE_STAINED_GLASS_PANE, THIN, //THIN_GLASS
            Material.RED_STAINED_GLASS_PANE, THIN, //THIN_GLASS
            Material.WHITE_STAINED_GLASS_PANE, THIN, //THIN_GLASS
            //103, XD, //MELON_BLOCK
            //104, VEGETATION, //PUMPKIN_STEM
            //105, VEGETATION, //MELON_STEM
            Material.VINE, VEGETATION, //VINE
            //107, DOOR, //FENCE_GATE
            Material.ACACIA_FENCE_GATE, DOOR, //FENCE_GATE
            Material.BIRCH_FENCE_GATE, DOOR, //FENCE_GATE
            Material.DARK_OAK_FENCE_GATE, DOOR, //FENCE_GATE
            Material.JUNGLE_FENCE_GATE, DOOR, //FENCE_GATE
            Material.OAK_FENCE_GATE, DOOR, //FENCE_GATE
            Material.SPRUCE_FENCE_GATE, DOOR, //FENCE_GATE
            //108, STAIRS, //BRICK_STAIRS
            //109, STAIRS, //SMOOTH_STAIRS
            //110, XD, //MYCEL
            Material.LILY_PAD, VEGETATION //WATER_LILY
            //112, XD, //NETHER_BRICK
            //113, FENCE, //NETHER_FENCE
            //114, STAIRS, //NETHER_BRICK_STAIRS
            //115, VEGETATION //NETHER_WARTS
            //116, XD, //ENCHANTMENT_TABLE
            //117, XD, //BREWING_STAND
            //118, XD, //CAULDRON
            //119, XD, //ENDER_PORTAL
            //120, XD, //ENDER_PORTAL_FRAME
            //121, XD, //ENDER_STONE
            //122, XD, //DRAGON_EGG
            //123, XD, //REDSTONE_LAMP_OFF
            //124, XD, //REDSTONE_LAMP_ON
            );

    public Map<Material, Double> getTypeOpacity() {
        return this.typeOpacity;
    }

    public void setTypeOpacity(Map<Material, Double> typeOpacity) {
        this.typeOpacity = typeOpacity;
    }

    // -------------------------------------------- //
    // ALTARS
    // -------------------------------------------- //

    private int altarSearchRadius = 10;

    public int getAltarSearchRadius() {
        return this.altarSearchRadius;
    }

    public void setAltarSearchRadius(int altarSearchRadius) {
        this.altarSearchRadius = altarSearchRadius;
    }

    private double altarMinRatioForInfo = 0;

    public double getAltarMinRatioForInfo() {
        return this.altarMinRatioForInfo;
    }

    public void setAltarMinRatioForInfo(double altarMinRatioForInfo) {
        this.altarMinRatioForInfo = altarMinRatioForInfo;
    }

    private AltarDark altarDark;

    public AltarDark getAltarDark() {
        return this.altarDark;
    }

    public void setAltarDark(AltarDark altarDark) {
        this.altarDark = altarDark;
    }

    private AltarLight altarLight;

    public AltarLight getAltarLight() {
        return this.altarLight;
    }

    public void setAltarLight(AltarLight altarLight) {
        this.altarLight = altarLight;
    }

    private boolean useWorldGuardRegions = false;

    public boolean isUseWorldGuardRegions() {
        return this.useWorldGuardRegions;
    }

    public void setUseWorldGuardRegions(boolean useWorldGuardRegions) {
        this.useWorldGuardRegions = useWorldGuardRegions;
    }
}
