package com.clanjhoo.vampire.config;

import com.clanjhoo.vampire.VampireRevamp;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nonnull;
import java.io.BufferedWriter;
import java.util.logging.Level;

public class InfectionConfig {
    public final double percentInOneHour;
    public final double amountPerMilli;
    public final int progressNauseaTicks;
    public final int progressDamage;
    public final double chance;

    public InfectionConfig() {
        percentInOneHour = 0.25;
        amountPerMilli = percentInOneHour / (1000D * 60D * 60D);
        progressNauseaTicks = 240;
        progressDamage = 1;
        chance = 0.003;
    }

    public InfectionConfig(@Nonnull ConfigurationSection cs) {
        InfectionConfig def = new InfectionConfig();

        double percent = cs.getDouble("percentInOneHour", def.percentInOneHour);
        if (percent <= 0) {
            VampireRevamp.log(Level.WARNING, "percentInOneHour can't be less or equals 0!");
            percent = def.percentInOneHour;
        }
        percentInOneHour = percent;

        amountPerMilli = percentInOneHour / (1000D * 60D * 60D);
        progressNauseaTicks = cs.getInt("progressNauseaTicks", def.progressNauseaTicks);
        progressDamage = cs.getInt("progressDamage", def.progressDamage);
        chance = cs.getDouble("chance", def.chance);
    }

    protected boolean saveConfigToFile(BufferedWriter configWriter, String indent, int level) {
        boolean result = PluginConfig.writeLine(configWriter, "# Each gameplay hour an infected player will have the infection increased by this percentage", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "percentInOneHour: " + this.percentInOneHour, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Duration of the nausea caused sometimes by the infection", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "progressNauseaTicks: " + this.progressNauseaTicks, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Damage done by the infection to the infected player sometimes", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "progressDamage: " + this.progressDamage, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Chance of being infected by a vampire not intending to infect", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "chance: " + this.chance, indent, level);

        return result;
    }

    @Override
    public String toString() {
        return "InfectionConfig{" +
                "percentInOneHour=" + percentInOneHour +
                ", amountPerMilli=" + amountPerMilli +
                ", progressNauseaTicks=" + progressNauseaTicks +
                ", progressDamage=" + progressDamage +
                ", chance=" + chance +
                '}';
    }
}
