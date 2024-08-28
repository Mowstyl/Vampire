package com.clanjhoo.vampire.config;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;

public class FXConfig {
    public final double smokePerMilli;
    public final double enderPerMilli;
    public final int enderRandomMaxLen;
    public final int smokeBurstCount;
    public final int flameBurstCount;
    public final int enderBurstCount;

    public FXConfig() {
        smokePerMilli = 0.008;
        enderPerMilli = 0.002;
        enderRandomMaxLen = 1;
        smokeBurstCount = 30;
        flameBurstCount = 5;
        enderBurstCount = 3;
    }

    public FXConfig(@NotNull ConfigurationSection cs) {
        FXConfig def = new FXConfig();

        smokePerMilli = cs.getDouble("smokePerMilli", def.smokePerMilli);
        enderPerMilli = cs.getDouble("enderPerMilli", def.enderPerMilli);
        enderRandomMaxLen = cs.getInt("enderRandomMaxLen", def.enderRandomMaxLen);
        smokeBurstCount = cs.getInt("smokeBurstCount", def.smokeBurstCount);
        flameBurstCount = cs.getInt("flameBurstCount", def.flameBurstCount);
        enderBurstCount = cs.getInt("enderBurstCount", def.enderBurstCount);
    }

    protected boolean saveConfigToFile(BufferedWriter configWriter, String indent, int level) {
        boolean result = PluginConfig.writeLine(configWriter, "smokePerMilli: " + this.smokePerMilli, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "enderPerMilli: " + this.enderPerMilli, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "enderRandomMaxLen: " + this.enderRandomMaxLen, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "enderBurstCount: " + this.enderBurstCount, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "smokeBurstCount: " + this.smokeBurstCount, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "flameBurstCount: " + this.flameBurstCount, indent, level);

        return result;
    }

    @Override
    public String toString() {
        return "FXConfig{" +
                "smokePerMilli=" + smokePerMilli +
                ", enderPerMilli=" + enderPerMilli +
                ", enderRandomMaxLen=" + enderRandomMaxLen +
                ", smokeBurstCount=" + smokeBurstCount +
                ", flameBurstCount=" + flameBurstCount +
                ", enderBurstCount=" + enderBurstCount +
                '}';
    }
}
