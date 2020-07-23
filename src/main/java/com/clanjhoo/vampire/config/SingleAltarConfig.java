package com.clanjhoo.vampire.config;

import com.clanjhoo.vampire.VampireRevamp;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

    public SingleAltarConfig(@Nonnull Material coreMaterial, @Nonnull Map<Material, Integer> buildMaterials, @Nonnull Set<ItemStack> activate) {
        this.coreMaterial = coreMaterial;
        this.buildMaterials = buildMaterials;
        this.activate = activate;
    }

    public SingleAltarConfig getSingleAltarConfig(@Nullable ConfigurationSection cs) {
        SingleAltarConfig sac = this;

        if (cs != null) {
            Material core = null;
            Map<Material, Integer> other = null;
            Set<ItemStack> act = null;

            core = Material.matchMaterial(cs.getString("coreMaterial"));
            if (core == null) {
                VampireRevamp.log(Level.WARNING, "Material " + cs.getString("coreMaterial") + " doesn't exist!");
                core = this.coreMaterial;
            }

            if (cs.contains("buildMaterials")) {
                List<Map<?, ?>> auxLES = cs.getMapList("buildMaterials");
                other = new HashMap<>();
                for (Map<?, ?> minimap : auxLES) {
                    for (Map.Entry<?, ?> entry : minimap.entrySet()) {
                        Material mat = Material.matchMaterial((String) entry.getKey());
                        int amount = (Integer) entry.getValue();

                        if (mat != null && amount > 0) {
                            other.put(mat, amount);
                        } else if (amount <= 0) {
                            VampireRevamp.log(Level.WARNING, "Amount can't be less or equal than 0!");
                            other = null;
                            break;
                        } else {
                            VampireRevamp.log(Level.WARNING, "PotionEffectType " + entry.getKey() + " doesn't exist!");
                            other = null;
                            break;
                        }
                    }
                    if (other == null) ;
                    break;
                }
            }
            if (other == null) {
                other = this.buildMaterials;
            }

            if (cs.contains("activate")) {
                act = PluginConfig.getResources((List<Map<String, Object>>) cs.getList("activate"));
            }
            act = act != null ? act : this.activate;

            sac = new SingleAltarConfig(core, other, act);
        }

        return sac;
    }

    protected boolean saveConfigToFile(BufferedWriter configWriter, String indent, int level) {
        boolean result = PluginConfig.writeLine(configWriter, "coreMaterial: " + this.coreMaterial, indent, level);
        result = result && PluginConfig.writeMap(configWriter, "buildMaterials:",  this.buildMaterials, indent, level);
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
