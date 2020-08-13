package com.clanjhoo.vampire.config;

import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nonnull;
import java.io.BufferedWriter;

public class BatusiConfig {
    public final boolean enabled;
    public final int numberOfBats;

    public BatusiConfig() {
        enabled = true;
        numberOfBats = 9;
    }

    public BatusiConfig(@Nonnull ConfigurationSection cs) {
        BatusiConfig def = new BatusiConfig();

        enabled = cs.getBoolean("enabled", def.enabled);
        numberOfBats = cs.getInt("numberOfBats", def.numberOfBats);
    }

    protected boolean saveConfigToFile(BufferedWriter configWriter, String indent, int level) {
        boolean result = PluginConfig.writeLine(configWriter, "# Whether to enable batusi or not", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "enabled: " + this.enabled, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Number of bats to spawn", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "numberOfBats: " + this.numberOfBats, indent, level);

        return result;
    }

    @Override
    public String toString() {
        return "BloodlustConfig{" +
                "enabled=" + enabled +
                ", numberOfBats=" + numberOfBats +
                '}';
    }
}
