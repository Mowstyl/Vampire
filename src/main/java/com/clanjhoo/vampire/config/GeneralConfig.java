package com.clanjhoo.vampire.config;

import com.clanjhoo.vampire.VampireRevamp;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nonnull;
import java.io.BufferedWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;

public class GeneralConfig {
    public final boolean debug;
    public final int taskDelayMillis;
    public final boolean damageWithMcmmo;
    public final Set<Material> dropSelfMaterials;
    public final boolean useWorldGuardRegions;
    public final boolean useOldFoodFormula;
    public final boolean enableWerewolvesHook;
    private final Set<String> worldBlacklist;
    public final Locale defaultLocale;

    public GeneralConfig() {
        debug = false;
        taskDelayMillis = 500;
        damageWithMcmmo = true;
        dropSelfMaterials = new HashSet<>();
        useWorldGuardRegions = true;
        useOldFoodFormula = false;
        enableWerewolvesHook = true;
        worldBlacklist = new HashSet<>();
        defaultLocale = Locale.ENGLISH;
    }

    public GeneralConfig(@Nonnull ConfigurationSection cs) {
        GeneralConfig def = new GeneralConfig();

        debug = cs.getBoolean("debug", def.debug);
        taskDelayMillis = cs.getInt("taskDelayMillis", def.taskDelayMillis);
        damageWithMcmmo = cs.getBoolean("damageWithMcmmo", def.damageWithMcmmo);
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
        useWorldGuardRegions = cs.getBoolean("useWorldGuardRegions", def.useWorldGuardRegions);
        useOldFoodFormula = cs.getBoolean("useOldFoodFormula", def.useOldFoodFormula);
        enableWerewolvesHook = cs.getBoolean("enableWerewolvesHook", def.enableWerewolvesHook);
        Set<String> auxSWorlds = null;
        if (cs.contains("worldBlacklist")) {
            auxSWorlds = new HashSet<>(cs.getStringList("worldBlacklist"));
        }
        worldBlacklist = auxSWorlds != null ? auxSWorlds : def.worldBlacklist;

        String locstring = cs.getString("defaultLocale");
        defaultLocale = cs.contains("defaultLocale") && locstring != null && !locstring.isEmpty() ? new Locale(locstring) : def.defaultLocale;
    }

    public boolean isBlacklisted(World world) {
        return worldBlacklist.contains(world.getName());
    }

    protected boolean saveConfigToFile(BufferedWriter configWriter, String indent, int level) {
        boolean result = PluginConfig.writeLine(configWriter, "debug: " + this.debug, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "taskDelayMillis: " + this.taskDelayMillis, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "damageWithMcmmo: " + this.damageWithMcmmo, indent, level);
        result = result && PluginConfig.writeCollection(configWriter, "dropSelfMaterials:",  this.dropSelfMaterials, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "useWorldGuardRegions: " + this.useWorldGuardRegions, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "useOldFoodFormula: " + this.useOldFoodFormula, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "enableWerewolvesHook: " + this.enableWerewolvesHook, indent, level);
        result = result && PluginConfig.writeCollection(configWriter, "worldBlacklist:",  this.worldBlacklist, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "defaultLocale: \"" + this.defaultLocale + "\"", indent, level);

        return result;
    }

    @Override
    public String toString() {
        return "GeneralConfig{" +
                "debug=" + debug +
                ", taskDelayMillis=" + taskDelayMillis +
                ", damageWithMcmmo=" + damageWithMcmmo +
                ", dropSelfMaterials=" + dropSelfMaterials +
                ", useWorldGuardRegions=" + useWorldGuardRegions +
                ", useOldFoodFormula=" + useOldFoodFormula +
                ", enableWerewolvesHook=" + enableWerewolvesHook +
                ", worldBlacklist=" + worldBlacklist +
                ", defaultLocale=" + defaultLocale +
                '}';
    }
}
