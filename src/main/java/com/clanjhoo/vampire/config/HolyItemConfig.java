package com.clanjhoo.vampire.config;

import com.clanjhoo.vampire.VampireRevamp;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nonnull;
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
    }

    public HolyItemConfig(@Nonnull ConfigurationSection cs) {
        HolyItemConfig def = new HolyItemConfig();

        damageFactor = cs.getDouble("damageFactor", def.damageFactor);
        List<String> auxLMats = cs.getStringList("materials");
        Set<Material> auxSMats = new HashSet<>();
        for (String matName : auxLMats) {
            Material aux = Material.matchMaterial(matName);
            if (aux == null)
                VampireRevamp.log(Level.WARNING, "Material " + matName + " doesn't exist!");
            else
                auxSMats.add(aux);
        }
        materials = auxLMats != null ? auxSMats : def.materials;
    }

    protected boolean saveConfigToFile(BufferedWriter configWriter, String indent, int level) {
        boolean result = PluginConfig.writeLine(configWriter, "damageFactor: " + this.damageFactor, indent, level);
        result = result && PluginConfig.writeCollection(configWriter, "minFood:", this.materials, indent, level);

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
