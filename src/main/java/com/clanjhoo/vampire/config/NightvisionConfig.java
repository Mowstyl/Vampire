package com.clanjhoo.vampire.config;

import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nonnull;
import java.io.BufferedWriter;

public class NightvisionConfig {
    public final boolean enabled;

    public NightvisionConfig() {
        enabled = true;
    }

    public NightvisionConfig(@Nonnull ConfigurationSection cs) {
        NightvisionConfig def = new NightvisionConfig();

        enabled = cs.getBoolean("enabled", def.enabled);
    }

    protected boolean saveConfigToFile(BufferedWriter configWriter, String indent, int level) {
        boolean result = PluginConfig.writeLine(configWriter, "# Whether to enable nightvision or not", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "enabled: " + this.enabled, indent, level);

        return result;
    }

    @Override
    public String toString() {
        return "NightvisionConfig{" +
                "enabled=" + enabled +
                '}';
    }
}
