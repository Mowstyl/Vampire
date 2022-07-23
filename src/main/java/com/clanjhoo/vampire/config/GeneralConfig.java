package com.clanjhoo.vampire.config;

import com.clanjhoo.vampire.VampireRevamp;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;

public class GeneralConfig {
    public final boolean debug;
    public final int taskDelayMillis;
    public final int batTaskDelayMillis;
    public final Set<Material> dropSelfMaterials;
    public final boolean useOldFoodFormula;
    private final Set<String> worldBlacklist;
    public final Locale defaultLocale;
    public final boolean vampiresUseFoodAsBlood;

    public GeneralConfig() {
        debug = false;
        taskDelayMillis = 500;
        batTaskDelayMillis = 100;
        dropSelfMaterials = new HashSet<>();
        useOldFoodFormula = false;
        worldBlacklist = new HashSet<>();
        defaultLocale = Locale.ENGLISH;
        vampiresUseFoodAsBlood = false;
    }

    public GeneralConfig(@NotNull ConfigurationSection cs) {
        GeneralConfig def = new GeneralConfig();

        debug = cs.getBoolean("debug", def.debug);
        taskDelayMillis = cs.getInt("taskDelayMillis", def.taskDelayMillis);
        batTaskDelayMillis = cs.getInt("batTaskDelayMillis", def.batTaskDelayMillis);
        List<String> auxLMats = null;
        Set<Material> auxSMats = new HashSet<>();
        if (cs.contains("dropSelfMaterials")) {
            auxLMats = cs.getStringList("dropSelfMaterials");
            for (String matName : auxLMats) {
                Material aux = Material.matchMaterial(matName);
                if (aux == null)
                    VampireRevamp.log(Level.WARNING, "Material " + matName + " doesn't exist!");
                else
                    auxSMats.add(aux);
            }
        }
        dropSelfMaterials = auxLMats != null ? auxSMats : def.dropSelfMaterials;
        useOldFoodFormula = cs.getBoolean("useOldFoodFormula", def.useOldFoodFormula);
        Set<String> auxSWorlds = null;
        if (cs.contains("worldBlacklist")) {
            auxSWorlds = new HashSet<>(cs.getStringList("worldBlacklist"));
        }
        worldBlacklist = auxSWorlds != null ? auxSWorlds : def.worldBlacklist;

        String locstring = cs.getString("defaultLocale");
        defaultLocale = cs.contains("defaultLocale") && locstring != null && !locstring.isEmpty() ? new Locale(locstring) : def.defaultLocale;
        vampiresUseFoodAsBlood = cs.getBoolean("vampiresUseFoodAsBlood", def.vampiresUseFoodAsBlood);
    }

    public boolean isBlacklisted(World world) {
        return worldBlacklist.contains(world.getName());
    }

    protected boolean saveConfigToFile(BufferedWriter configWriter, String indent, int level) {
        boolean result = PluginConfig.writeLine(configWriter, "# Whether to spam the console with debug messages only useful when reporting a bug or not", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "debug: " + this.debug, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Milliseconds between each execution of the main task", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "taskDelayMillis: " + this.taskDelayMillis, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Milliseconds between each execution of the bat cloud task", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "batTaskDelayMillis: " + this.batTaskDelayMillis, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# List of materials that drop themselves when broken (when breaking glowstone it drops glowstone dust, unless added here)", indent, level);
        result = result && PluginConfig.writeCollection(configWriter, "dropSelfMaterials:",  this.dropSelfMaterials, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Whether or not to use the old damage -> food formula (vampires feeding) or not", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "useOldFoodFormula: " + this.useOldFoodFormula, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Worlds in which Vampires don't exist. Only humans", indent, level);
        result = result && PluginConfig.writeCollection(configWriter, "worldBlacklist:",  this.worldBlacklist, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# The default language to load. It has to exist in locales folder", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "defaultLocale: \"" + this.defaultLocale + "\"", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# If vampires use their food level to create a blood flask or not (using their health instead)", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "vampiresUseFoodAsBlood: " + this.vampiresUseFoodAsBlood, indent, level);

        return result;
    }

    @Override
    public String toString() {
        return "GeneralConfig{" +
                "debug=" + debug +
                ", taskDelayMillis=" + taskDelayMillis +
                ", batTaskDelayMillis=" + batTaskDelayMillis +
                ", dropSelfMaterials=" + dropSelfMaterials +
                ", useOldFoodFormula=" + useOldFoodFormula +
                ", worldBlacklist=" + worldBlacklist +
                ", defaultLocale=" + defaultLocale +
                ", vampiresUseFoodAsBlood=" + vampiresUseFoodAsBlood +
                '}';
    }
}
