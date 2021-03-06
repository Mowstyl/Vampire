package com.clanjhoo.vampire.config;

import com.clanjhoo.vampire.VampireRevamp;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public class SingleAltarConfig {
    public final Material coreMaterial;
    public final Map<Material, Integer> buildMaterials;
    public final Set<ItemStack> activate;

    public SingleAltarConfig(@NotNull Material coreMaterial, @NotNull Map<Material, Integer> buildMaterials, @NotNull Set<ItemStack> activate) {
        this.coreMaterial = coreMaterial;
        this.buildMaterials = buildMaterials;
        this.activate = activate;
    }

    public SingleAltarConfig getSingleAltarConfig(@Nullable ConfigurationSection cs) {
        SingleAltarConfig sac = this;

        if (cs != null) {
            Material core = null;
            Set<ItemStack> act = null;

            core = Material.matchMaterial(cs.getString("coreMaterial"));
            if (core == null) {
                VampireRevamp.log(Level.WARNING, "Material " + cs.getString("coreMaterial") + " doesn't exist!");
                core = this.coreMaterial;
            }


            Map<?, ?> auxbm = PluginConfig.getMap(cs, "buildMaterials");
            Map<Material, Integer> bm = null;

            if (auxbm != null) {
                bm = new HashMap<>();
                for (Map.Entry<?, ?> entry : auxbm.entrySet()) {
                    try {
                        Material mat = Material.matchMaterial((String) entry.getKey());
                        int amount = (Integer) entry.getValue();

                        if (mat != null && amount > 0) {
                            bm.put(mat, amount);
                        } else if (amount <= 0) {
                            VampireRevamp.log(Level.WARNING, "Amount can't be less or equal than 0!");
                            bm = null;
                            break;
                        } else {
                            VampireRevamp.log(Level.WARNING, "Material " + entry.getKey() + " doesn't exist!");
                            bm = null;
                            break;
                        }
                    }
                    catch (IllegalArgumentException ex) {
                        VampireRevamp.log(Level.WARNING, "Material " + entry.getKey() + " doesn't exist!");
                        bm = null;
                        break;
                    }
                }
            }

            if (bm == null)
                bm = this.buildMaterials;

            if (cs.contains("activate")) {
                act = PluginConfig.getResources((List<Map<String, Object>>) cs.getList("activate"));
            }
            act = act != null ? act : this.activate;

            sac = new SingleAltarConfig(core, bm, act);
        }

        return sac;
    }

    protected boolean saveConfigToFile(BufferedWriter configWriter, String indent, int level) {
        boolean result = PluginConfig.writeLine(configWriter, "# Core material of the altar", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "coreMaterial: " + this.coreMaterial, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Block counts needed to build the altar", indent, level);
        result = result && PluginConfig.writeMap(configWriter, "buildMaterials:",  this.buildMaterials, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Item counts needed to activate the altar", indent, level);
        result = result && PluginConfig.writeItemCollection(configWriter, "activate:",  this.activate, indent, level);

        return result;
    }

    @Override
    public String toString() {
        return "SingleAltarConfig{" +
                "coreMaterial=" + coreMaterial +
                ", buildMaterials=" + buildMaterials +
                ", activate=" + activate +
                '}';
    }
}
