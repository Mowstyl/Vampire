package com.clanjhoo.vampire.config;

import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nonnull;
import java.io.BufferedWriter;

public class ShriekConfig {
    public final int waitMessageCooldownMillis;
    public final int cooldownMillis;

    public ShriekConfig() {
        waitMessageCooldownMillis = 500;
        cooldownMillis = 30000;
    }

    public ShriekConfig(@Nonnull ConfigurationSection cs) {
        ShriekConfig def = new ShriekConfig();

        waitMessageCooldownMillis = cs.getInt("waitMessageCooldownMillis", def.waitMessageCooldownMillis);
        cooldownMillis = cs.getInt("cooldownMillis", def.cooldownMillis);
    }

    protected boolean saveConfigToFile(BufferedWriter configWriter, String indent, int level) {
        boolean result = PluginConfig.writeLine(configWriter, "waitMessageCooldownMillis: " + this.waitMessageCooldownMillis, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "cooldownMillis: " + this.cooldownMillis, indent, level);

        return result;
    }

    @Override
    public String toString() {
        return "ShriekConfig{" +
                "waitMessageCooldownMillis=" + waitMessageCooldownMillis +
                ", cooldownMillis=" + cooldownMillis +
                '}';
    }
}
