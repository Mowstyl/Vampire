package com.clanjhoo.vampire.config;

import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nonnull;
import java.io.BufferedWriter;

public class TradeConfig {
    public final int offerMaxDistance;
    public final int offerToleranceMillis;
    public final int visualDistance;
    public final int percentage;

    public TradeConfig() {
        offerMaxDistance = 2;
        offerToleranceMillis = 20000;
        visualDistance = 7;
        percentage = 1;
    }

    public TradeConfig(@Nonnull ConfigurationSection cs) {
        TradeConfig def = new TradeConfig();

        offerMaxDistance = cs.getInt("offerMaxDistance", def.offerMaxDistance);
        offerToleranceMillis = cs.getInt("offerToleranceMillis", def.offerToleranceMillis);
        visualDistance = cs.getInt("visualDistance", def.visualDistance);
        percentage = cs.getInt("percentage", def.percentage);
    }

    protected boolean saveConfigToFile(BufferedWriter configWriter, String indent, int level) {
        boolean result = PluginConfig.writeLine(configWriter, "offerMaxDistance: " + this.offerMaxDistance, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "offerToleranceMillis: " + this.offerToleranceMillis, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "visualDistance: " + this.visualDistance, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "percentage: " + this.percentage, indent, level);

        return result;
    }

    @Override
    public String toString() {
        return "TradeConfig{" +
                "offerMaxDistance=" + offerMaxDistance +
                ", offerToleranceMillis=" + offerToleranceMillis +
                ", visualDistance=" + visualDistance +
                ", percentage=" + percentage +
                '}';
    }
}
