package com.clanjhoo.vampire.config;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;

public class BatusiConfig {
    public final boolean enabled;
    public final int numberOfBats;
    public final boolean nosferatuOnly;
    public final boolean disableOnHit;
    public final boolean enableFlight;
    public final boolean preventDisguise;

    public BatusiConfig() {
        enabled = true;
        numberOfBats = 9;
        nosferatuOnly = true;
        disableOnHit = true;
        enableFlight = true;
        preventDisguise = true;
    }

    public BatusiConfig(@NotNull ConfigurationSection cs) {
        BatusiConfig def = new BatusiConfig();

        enabled = cs.getBoolean("enabled", def.enabled);
        numberOfBats = cs.getInt("numberOfBats", def.numberOfBats);
        nosferatuOnly = cs.getBoolean("nosferatuOnly", def.nosferatuOnly);
        disableOnHit = cs.getBoolean("disableOnHit", def.disableOnHit);
        enableFlight = cs.getBoolean("enableFlight", def.enableFlight);
        preventDisguise = cs.getBoolean("preventDisguise", def.preventDisguise);
    }

    protected boolean saveConfigToFile(BufferedWriter configWriter, String indent, int level) {
        boolean result = PluginConfig.writeLine(configWriter, "# Whether to enable batusi or not", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "enabled: " + this.enabled, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Number of bats to spawn", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "numberOfBats: " + this.numberOfBats, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Whether only nosferatu can use batusi or not", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "nosferatuOnly: " + this.nosferatuOnly, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Whether to enable flight when in bat cloud mode or not", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "disableOnHit: " + this.disableOnHit, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Whether to enable flight when in bat cloud mode or not", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "enableFlight: " + this.enableFlight, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Whether to prevent players in batusi being disguised/undisguised", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "preventDisguise: " + this.enableFlight, indent, level);

        return result;
    }

    @Override
    public String toString() {
        return "BloodlustConfig{" +
                "enabled=" + enabled +
                ", numberOfBats=" + numberOfBats +
                ", nosferatuOnly=" + nosferatuOnly +
                ", disableOnHit=" + disableOnHit +
                ", enableFlight=" + enableFlight +
                ", preventDisguise=" + preventDisguise +
                '}';
    }
}
