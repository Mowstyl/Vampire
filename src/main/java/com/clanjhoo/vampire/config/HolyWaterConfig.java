package com.clanjhoo.vampire.config;

import com.clanjhoo.vampire.VampireRevamp;
import com.clanjhoo.vampire.util.CollectionUtil;
import com.clanjhoo.vampire.util.SemVer;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public class HolyWaterConfig {
    public final int splashRadius;
    public final double temperature;
    public final Set<ItemStack> resources;
    private final VampireRevamp plugin;


    public HolyWaterConfig(VampireRevamp plugin) {
        this.plugin = plugin;
        splashRadius = 6;
        temperature = 0.7;
        resources = CollectionUtil.set(
                PluginConfig.getIngredient(plugin, Material.POTION, 1, PotionType.WATER)
        );

        String lapisMaterial = "LAPIS_LAZULI";
        short lapisDamage = 0;
        if (plugin.getServerVersion().compareTo(new SemVer(1, 14)) < 0) {
            lapisMaterial = "INK_SACK";
            lapisDamage = 4;
        }
        resources.add(PluginConfig.getIngredient(plugin, Material.valueOf(lapisMaterial), 1, lapisDamage));
    }

    public HolyWaterConfig(VampireRevamp plugin, @NotNull ConfigurationSection cs) {
        this.plugin = plugin;
        HolyWaterConfig def = new HolyWaterConfig(plugin);

        splashRadius = cs.getInt("splashRadius", def.splashRadius);
        temperature = cs.getDouble("temperature", def.temperature);

        Set<ItemStack> resset = null;
        if (cs.contains("resources")) {
            List<?> resources = cs.getList("resources");
            try {
                if (resources != null) {
                    resset = PluginConfig.getResources(plugin, (List<Map<String, Object>>) resources);
                }
            }
            catch (ClassCastException ex) {
                plugin.log(Level.WARNING, "Wrong format for resources in holy water config");
            }
        }
        resources = resset != null ? resset : def.resources;
    }

    protected boolean saveConfigToFile(BufferedWriter configWriter, String indent, int level) {
        boolean result = PluginConfig.writeLine(configWriter, "# Players located inside a sphere with this radius centered around the place the potion broke will be affected by it", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "splashRadius: " + this.splashRadius, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Temperature added to vampires hit by the holy water", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "temperature: " + this.temperature, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Ingredients needed to make holy water in a light altar", indent, level);
        result = result && PluginConfig.writeItemCollection(configWriter, "resources:", this.resources, indent, level);

        return result;
    }

    @Override
    public String toString() {
        return "HolyWaterConfig{" +
                "splashRadius=" + splashRadius +
                ", temperature=" + temperature +
                ", resources=" + resources +
                '}';
    }
}
