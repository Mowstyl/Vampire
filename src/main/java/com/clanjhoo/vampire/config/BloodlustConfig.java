package com.clanjhoo.vampire.config;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;

public class BloodlustConfig {
    public final boolean enabled;
    public final boolean checkGamemode;
    public final double minFood;
    public final double foodPerMilli;
    public final double smokes;
    public final double damageFactor;
    public final double nosferatuDamageFactor;

    public BloodlustConfig() {
        enabled = true;
        checkGamemode = true;
        minFood = 2.5;
        foodPerMilli = -0.001/3;
        smokes = 1.5;
        damageFactor = 1.2;
        nosferatuDamageFactor = 1.2;
    }

    public BloodlustConfig(@NotNull ConfigurationSection cs) {
        BloodlustConfig def = new BloodlustConfig();

        enabled = cs.getBoolean("enabled", def.enabled);
        checkGamemode = cs.getBoolean("checkGamemode", def.checkGamemode);
        minFood = cs.getDouble("minFood", def.minFood);
        foodPerMilli = cs.getDouble("foodPerMilli", def.foodPerMilli);
        smokes = cs.getDouble("smokes", def.smokes);
        damageFactor = cs.getDouble("damageFactor", def.damageFactor);
        nosferatuDamageFactor = cs.getDouble("nosferatuDamageFactor", def.nosferatuDamageFactor);
    }

    protected boolean saveConfigToFile(BufferedWriter configWriter, String indent, int level) {
        boolean result = PluginConfig.writeLine(configWriter, "# Whether to enable bloodlust or not", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "enabled: " + this.enabled, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Whether to avoid Creative players using bloodlust or not", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "checkGamemode: " + this.checkGamemode, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Minimum food level needed to activate bloodlust", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "minFood: " + this.minFood, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Food consumed each ms while bloodlust is active", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "foodPerMilli: " + this.foodPerMilli, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "smokes: " + this.smokes, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Damage multiplier applied to attacks made by a normal vampire in bloodlust mode", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "damageFactor: " + this.damageFactor, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Damage multiplier applied to attacks made by a nosferatu in bloodlust mode", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "nosferatuDamageFactor: " + this.damageFactor, indent, level);

        return result;
    }

    @Override
    public String toString() {
        return "BloodlustConfig{" +
                "enabled=" + enabled +
                ", checkGamemode=" + checkGamemode +
                ", minFood=" + minFood +
                ", foodPerMilli=" + foodPerMilli +
                ", smokes=" + smokes +
                ", damageFactor=" + damageFactor +
                ", nosferatuDamageFactor=" + nosferatuDamageFactor +
                '}';
    }
}
