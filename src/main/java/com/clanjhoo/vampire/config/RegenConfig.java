package com.clanjhoo.vampire.config;

import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nonnull;
import java.io.BufferedWriter;

public class RegenConfig {
    public final boolean enabled;
    public final double minFood;
    public final int delayMillis;
    public final double foodPerMilli;
    public final double healthPerFood;

    public RegenConfig(boolean isNosferatu) {
        if (!isNosferatu) {
            enabled = true;
            minFood = 2.5;
            delayMillis = 10000;
            foodPerMilli = 0.0005;
            healthPerFood = 2;
        }
        else {
            enabled = true;
            minFood = 2.5;
            delayMillis = 5000;
            foodPerMilli = 0.0005;
            healthPerFood = 4;
        }
    }

    public RegenConfig(@Nonnull ConfigurationSection cs, boolean isNosferatu) {
        RegenConfig def = new RegenConfig(isNosferatu);

        enabled = cs.getBoolean("enabled", def.enabled);
        minFood = cs.getDouble("minFood", def.minFood);
        delayMillis = cs.getInt("delayMillis", def.delayMillis);
        foodPerMilli = cs.getDouble("foodPerMilli", def.foodPerMilli);
        healthPerFood = cs.getDouble("healthPerFood", def.healthPerFood);
    }

    protected boolean saveConfigToFile(BufferedWriter configWriter, String indent, int level) {
        boolean result = PluginConfig.writeLine(configWriter, "# Whether or not to enable regeneration when out of combat", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "enabled: " + this.enabled, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Minimum food level needed to start regenerating the vampire player", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "minFood: " + this.minFood, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Delay in ms between each health regeneration", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "delayMillis: " + this.delayMillis, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Food consumed each ms when this feature is enabled (if the player is not at full health)", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "foodPerMilli: " + this.foodPerMilli, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Health points gained by each food point", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "healthPerFood: " + this.healthPerFood, indent, level);

        return result;
    }

    @Override
    public String toString() {
        return "RegenConfig{" +
                "enabled=" + enabled +
                ", minFood=" + minFood +
                ", delayMillis=" + delayMillis +
                ", foodPerMilli=" + foodPerMilli +
                ", healthPerFood=" + healthPerFood +
                '}';
    }
}
