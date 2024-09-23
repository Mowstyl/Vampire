package com.clanjhoo.vampire.config;

import com.clanjhoo.vampire.VampireRevamp;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
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
    private final VampireRevamp plugin;


    public SingleAltarConfig(VampireRevamp plugin, @NotNull Material coreMaterial, @NotNull Map<Material, Integer> buildMaterials, @NotNull Set<ItemStack> activate) {
        this.plugin = plugin;
        this.coreMaterial = coreMaterial;
        this.buildMaterials = buildMaterials;
        this.activate = activate;
    }

    public SingleAltarConfig getSingleAltarConfig(VampireRevamp plugin, @Nullable ConfigurationSection cs) {
        SingleAltarConfig sac = this;

        if (cs != null) {
            Material core = null;
            Set<ItemStack> act = null;

            String matName = cs.getString("coreMaterial");
            if (matName != null) {
                core = plugin.getVersionCompat().getMaterialByName(matName);
            }
            if (core == null) {
                plugin.log(Level.WARNING, "Altar core material " + matName + " doesn't exist!");
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
                            plugin.log(Level.WARNING, "Amount can't be less or equal than 0!");
                            bm = null;
                            break;
                        } else {
                            plugin.log(Level.WARNING, "Material " + entry.getKey() + " doesn't exist!");
                            bm = null;
                            break;
                        }
                    }
                    catch (IllegalArgumentException ex) {
                        plugin.log(Level.WARNING, "Material " + entry.getKey() + " doesn't exist!");
                        bm = null;
                        break;
                    }
                }
            }

            if (bm == null)
                bm = this.buildMaterials;

            if (cs.contains("activate")) {
                List<?> activate = cs.getList("activate");
                try {
                    if (activate != null) {
                        act = PluginConfig.getResources(plugin, (List<Map<String, Object>>) activate);
                    }
                }
                catch (ClassCastException ex) {
                    plugin.log(Level.WARNING, "Wrong format for activate in " + coreMaterial + " altar config");
                }
            }
            act = act != null ? act : this.activate;

            sac = new SingleAltarConfig(plugin, core, bm, act);
        }

        return sac;
    }

    protected boolean saveConfigToFile(BufferedWriter configWriter, String indent, int level) {
        boolean result = PluginConfig.writeLine(configWriter, "# Core material of the altar", indent, level);
        String strong;
        if (coreMaterial instanceof Keyed)
            strong = ((Keyed) coreMaterial).getKey().toString();
        else
            strong = coreMaterial.toString();
        result = result && PluginConfig.writeLine(configWriter, "coreMaterial: " + strong, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Block counts needed to build the altar", indent, level);
        result = result && PluginConfig.writeMap(configWriter, "buildMaterials:",  buildMaterials, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Item counts needed to activate the altar", indent, level);
        result = result && PluginConfig.writeItemCollection(configWriter, "activate:",  activate, indent, level);

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
