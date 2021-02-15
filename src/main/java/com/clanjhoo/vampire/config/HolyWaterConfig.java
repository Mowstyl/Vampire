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

public class HolyWaterConfig {
    public final int splashRadius;
    public final double temperature;
    public final Set<ItemStack> resources;

    public HolyWaterConfig() {
        splashRadius = 6;
        temperature = 0.7;
        resources = CollectionUtil.set(
                PluginConfig.getIngredient(Material.POTION, 1, PotionType.WATER)
        );

        if (new SemVer(1, 14).compareTo(VampireRevamp.getServerVersion()) <= 0) {
            resources.add(PluginConfig.getIngredient(Material.LAPIS_LAZULI, 1));
        }
        else {
            resources.add(PluginConfig.getIngredient(Material.getMaterial("INK_SACK"), 1, (short) 4));
        }
    }

    public HolyWaterConfig(@NotNull ConfigurationSection cs) {
        HolyWaterConfig def = new HolyWaterConfig();

        splashRadius = cs.getInt("splashRadius", def.splashRadius);
        temperature = cs.getDouble("temperature", def.temperature);

        Set<ItemStack> resset = null;
        if (cs.contains("resources")) {
            resset = PluginConfig.getResources((List<Map<String, Object>>) cs.getList("resources"));
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
