package com.clanjhoo.vampire.config;

import com.clanjhoo.vampire.VampireRevamp;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.util.logging.Level;

public class InfectionConfig {
    public final boolean canInfectHorses;
    public final double amountInOneHour;
    public final double amountPerMilli;
    public final int progressNauseaTicks;
    public final int progressDamage;
    public final double chance;

    public InfectionConfig() {
        canInfectHorses = true;
        amountInOneHour = 0.25;
        amountPerMilli = amountInOneHour / (1000D * 60D * 60D);
        progressNauseaTicks = 240;
        progressDamage = 1;
        chance = 0.003;
    }

    public InfectionConfig(@NotNull ConfigurationSection cs) {
        InfectionConfig def = new InfectionConfig();

        canInfectHorses = cs.getBoolean("canInfectHorses", def.canInfectHorses);

        double percent = cs.getDouble("amountInOneHour", def.amountInOneHour);
        if (percent <= 0) {
            VampireRevamp.log(Level.WARNING, "amountInOneHour can't be less or equals 0!");
            percent = def.amountInOneHour;
        }
        amountInOneHour = percent;

        amountPerMilli = amountInOneHour / (1000D * 60D * 60D);
        progressNauseaTicks = cs.getInt("progressNauseaTicks", def.progressNauseaTicks);
        progressDamage = cs.getInt("progressDamage", def.progressDamage);
        chance = cs.getDouble("chance", def.chance);
    }

    protected boolean saveConfigToFile(BufferedWriter configWriter, String indent, int level) {
        boolean result = PluginConfig.writeLine(configWriter, "# Whether vampires can infect horses (turning them into zombie horses) or not", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "canInfectHorses: " + this.canInfectHorses, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Each gameplay hour an infected player will have the infection increased by this amount (1 being 100%)", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "amountInOneHour: " + this.amountInOneHour, indent, level);
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
                "canInfectHorses=" + canInfectHorses +
                ", amountInOneHour=" + amountInOneHour +
                ", amountPerMilli=" + amountPerMilli +
                ", progressNauseaTicks=" + progressNauseaTicks +
                ", progressDamage=" + progressDamage +
                ", chance=" + chance +
                '}';
    }
}
