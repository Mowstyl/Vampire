package com.clanjhoo.vampire.config;

import com.clanjhoo.vampire.VampireRevamp;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public class HolyItemConfig {
    public final double damageFactor;
    public final Set<Material> materials;

    public HolyItemConfig() {
        damageFactor = 1.2;
        materials = new HashSet<>();
        materials.add(Material.STICK);
    }

    public HolyItemConfig(@NotNull ConfigurationSection cs) {
        HolyItemConfig def = new HolyItemConfig();

        damageFactor = cs.getDouble("damageFactor", def.damageFactor);
        Set<Material> auxSMats = null;
        if (cs.contains("materials")) {
            List<String> auxLMats = cs.getStringList("materials");
            auxSMats = new HashSet<>();
            for (String matName : auxLMats) {
                Material aux = Material.matchMaterial(matName);
                if (aux == null)
                    VampireRevamp.log(Level.WARNING, "Material " + matName + " doesn't exist!");
                else
                    auxSMats.add(aux);
            }
        }
        materials = auxSMats != null ? auxSMats : def.materials;
    }

    protected boolean saveConfigToFile(BufferedWriter configWriter, String indent, int level) {
        boolean result = PluginConfig.writeLine(configWriter, "# Damage multiplier applied when a vampire is hit by an object in this list (old woodDamageFactor)", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "damageFactor: " + this.damageFactor, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# List of materials considered holy. A damage multiplier will be applied when a Vampire gets hit by any of them", indent, level);
        result = result && PluginConfig.writeCollection(configWriter, "materials:", this.materials, indent, level);

        return result;
    }

    @Override
    public String toString() {
        return "HolyItemConfig{" +
                "damageFactor=" + damageFactor +
                ", materials=" + materials +
                '}';
    }
}
