package com.clanjhoo.vampire.config;

import com.clanjhoo.vampire.VampireRevamp;
import com.clanjhoo.vampire.entity.VPlayer;
import com.clanjhoo.vampire.util.CollectionUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventPriority;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.util.HashMap;


public class PotionEffectsConfig {
    public final int seconds;
    public final StateEffectConfig bloodlust;
    public final StateEffectConfig nightvision;
    public final StateEffectConfig nosferatu;
    public final StateEffectConfig vampire;
    public final StateEffectConfig infected;
    public final StateEffectConfig human;
    private final VampireRevamp plugin;


    public PotionEffectsConfig(VampireRevamp plugin) {
        this.plugin = plugin;
        seconds = 15;
        bloodlust = getBloodlust();
        bloodlust.passesChecks = VPlayer::isBloodlusting;
        nightvision = getNightvision();
        nightvision.passesChecks = VPlayer::isUsingNightVision;
        nosferatu = getNosferatu();
        nosferatu.passesChecks = VPlayer::canHaveNosferatuEffects;
        vampire = getVampire();
        vampire.passesChecks = VPlayer::canHaveVampireEffects;
        infected = getInfected();
        infected.passesChecks = VPlayer::isInfected;
        human = getHuman();
        human.passesChecks = VPlayer::isHuman;
    }

    public PotionEffectsConfig(VampireRevamp plugin, @NotNull ConfigurationSection cs) {
        this.plugin = plugin;
        PotionEffectsConfig def = new PotionEffectsConfig(plugin);

        seconds = cs.getInt("seconds", def.seconds);
        bloodlust = def.bloodlust.getStateEffectConfig(cs.getConfigurationSection("bloodlust"));
        nightvision = def.nightvision.getStateEffectConfig(cs.getConfigurationSection("nightvision"));
        nosferatu = def.nosferatu.getStateEffectConfig(cs.getConfigurationSection("nosferatu"));
        vampire = def.vampire.getStateEffectConfig(cs.getConfigurationSection("vampire"));
        infected = def.infected.getStateEffectConfig(cs.getConfigurationSection("infected"));
        human = def.human.getStateEffectConfig(cs.getConfigurationSection("human"));
    }

    protected boolean saveConfigToFile(BufferedWriter configWriter, String indent, int level) {
        boolean result = PluginConfig.writeLine(configWriter, "# Duration in seconds of the effects", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "seconds: " + this.seconds, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "bloodlust:", indent, level);
        result = result && this.bloodlust.saveConfigToFile(configWriter, indent, level + 1);
        result = result && PluginConfig.writeLine(configWriter, "nightvision:", indent, level);
        result = result && this.nightvision.saveConfigToFile(configWriter, indent, level + 1);
        result = result && PluginConfig.writeLine(configWriter, "nosferatu:", indent, level);
        result = result && this.nosferatu.saveConfigToFile(configWriter, indent, level + 1);
        result = result && PluginConfig.writeLine(configWriter, "vampire:", indent, level);
        result = result && this.vampire.saveConfigToFile(configWriter, indent, level + 1);
        result = result && PluginConfig.writeLine(configWriter, "infected:", indent, level);
        result = result && this.infected.saveConfigToFile(configWriter, indent, level + 1);
        result = result && PluginConfig.writeLine(configWriter, "human:", indent, level);
        result = result && this.human.saveConfigToFile(configWriter, indent, level + 1);

        return result;
    }

    private StateEffectConfig getBloodlust() {
        return new StateEffectConfig(
                plugin,
                EventPriority.HIGHEST,
                CollectionUtil.map(
                        PotionEffectType.SPEED, 3,
                        plugin.getVersionCompat().getJumpEffect(), 4
                )
        );
    }

    private StateEffectConfig getNightvision() {
        return new StateEffectConfig(
                plugin,
                EventPriority.HIGH,
                CollectionUtil.map(
                        PotionEffectType.NIGHT_VISION, 1
                )
        );
    }

    private StateEffectConfig getNosferatu() {
        return new StateEffectConfig(
                plugin,
                EventPriority.NORMAL,
                CollectionUtil.map(
                        PotionEffectType.REGENERATION, 3,
                        PotionEffectType.SPEED, 1,
                        plugin.getVersionCompat().getJumpEffect(), 2
                )
        );
    }

    private StateEffectConfig getVampire() {
        return new StateEffectConfig(
                plugin,
                EventPriority.NORMAL,
                CollectionUtil.map(
                        PotionEffectType.SPEED, 1,
                        plugin.getVersionCompat().getJumpEffect(), 1
                )
        );
    }

    private StateEffectConfig getInfected() {
        return new StateEffectConfig(
                plugin,
                EventPriority.NORMAL,
                new HashMap<>()
        );
    }

    private StateEffectConfig getHuman() {
        return new StateEffectConfig(
                plugin,
                EventPriority.NORMAL,
                new HashMap<>()
        );
    }

    @Override
    public String toString() {
        return "PotionEffectsConfig{" +
                "bloodlust=" + bloodlust +
                ", nightvision=" + nightvision +
                ", nosferatu=" + nosferatu +
                ", vampire=" + vampire +
                ", infected=" + infected +
                ", human=" + human +
                '}';
    }
}
