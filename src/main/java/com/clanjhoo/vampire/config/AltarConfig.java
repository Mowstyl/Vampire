package com.clanjhoo.vampire.config;

import com.clanjhoo.vampire.VampireRevamp;
import com.clanjhoo.vampire.util.CollectionUtil;
import com.clanjhoo.vampire.util.SemVer;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.util.Map;
import java.util.Set;

public class AltarConfig {
    public final int searchRadius;
    public final int minRatioForInfo;
    public final boolean checkIfBlockInHand;
    public final SingleAltarConfig darkAltar;
    public final SingleAltarConfig lightAltar;
    private final VampireRevamp plugin;


    public AltarConfig(VampireRevamp plugin) {
        this.plugin = plugin;
        searchRadius = 10;
        minRatioForInfo = 0;
        checkIfBlockInHand = true;
        Map<Material, Integer> buildDark = CollectionUtil.map(
                Material.OBSIDIAN, 30,
                Material.DEAD_BUSH, 5,
                Material.DIAMOND_BLOCK, 2,
                Material.GOLD_BLOCK, 1
        );
        Set<ItemStack> activateDark = CollectionUtil.set(
                PluginConfig.getIngredient(plugin, Material.BONE, 10),
                PluginConfig.getIngredient(plugin, Material.REDSTONE, 10)
        );

        if (new SemVer(1, 13).compareTo(plugin.getServerVersion()) <= 0) {
            buildDark.put(Material.COBWEB, 5);
            activateDark.add(PluginConfig.getIngredient(plugin, Material.MUSHROOM_STEW, 1));
            activateDark.add(PluginConfig.getIngredient(plugin, Material.GUNPOWDER, 10));
        }
        else {
            buildDark.put(Material.getMaterial("WEB"), 5);
            activateDark.add(PluginConfig.getIngredient(plugin, Material.getMaterial("MUSHROOM_SOUP"), 1));
            activateDark.add(PluginConfig.getIngredient(plugin, Material.getMaterial("SULPHUR"), 10));
        }

        darkAltar = new SingleAltarConfig(
                plugin,
                Material.GOLD_BLOCK,
                buildDark,
                activateDark

        );

        Map<Material, Integer> buildLight = CollectionUtil.map(
                Material.GLOWSTONE, 30,
                Material.DIAMOND_BLOCK, 2,
                Material.LAPIS_BLOCK, 1
        );

        if (new SemVer(1, 13).compareTo(plugin.getServerVersion()) <= 0) {
            buildLight.put(Material.POPPY, 5);
            buildLight.put(Material.DANDELION, 5);
        }
        else {
            buildLight.put(Material.getMaterial("RED_ROSE"), 5);
            buildLight.put(Material.getMaterial("YELLOW_FLOWER"), 5);
        }

        lightAltar = new SingleAltarConfig(
                plugin,
                Material.LAPIS_BLOCK,
                buildLight,
                CollectionUtil.set(
                        PluginConfig.getIngredient(plugin, Material.WATER_BUCKET, 1),
                        PluginConfig.getIngredient(plugin, Material.DIAMOND, 1),
                        PluginConfig.getIngredient(plugin, Material.SUGAR, 20),
                        PluginConfig.getIngredient(plugin, Material.WHEAT, 20)
                )
        );
    }

    public AltarConfig(VampireRevamp plugin, @NotNull ConfigurationSection cs) {
        this.plugin = plugin;
        AltarConfig def = new AltarConfig(plugin);

        searchRadius = cs.getInt("searchRadius", def.searchRadius);
        minRatioForInfo = cs.getInt("minRatioForInfo", def.minRatioForInfo);
        checkIfBlockInHand = cs.getBoolean("checkIfBlockInHand", def.checkIfBlockInHand);
        darkAltar = def.darkAltar.getSingleAltarConfig(plugin, cs.getConfigurationSection("darkAltar"));
        lightAltar = def.lightAltar.getSingleAltarConfig(plugin, cs.getConfigurationSection("lightAltar"));
    }

    protected boolean saveConfigToFile(BufferedWriter configWriter, String indent, int level) {
        boolean result = PluginConfig.writeLine(configWriter, "# Max range around the coreblock in which other altar blocks can be used to build the altar", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "searchRadius: " + this.searchRadius, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "minRatioForInfo: " + this.minRatioForInfo, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Whether to check if the player clicking the altar core block has a block in hand or not, to avoid annoying builders", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "checkIfBlockInHand: " + this.checkIfBlockInHand, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "darkAltar:", indent, level);
        result = result && this.darkAltar.saveConfigToFile(configWriter, indent, level + 1);
        result = result && PluginConfig.writeLine(configWriter, "lightAltar:", indent, level);
        result = result && this.lightAltar.saveConfigToFile(configWriter, indent, level + 1);

        return result;
    }

    @Override
    public String toString() {
        return "AltarConfig{" +
                "searchRadius=" + searchRadius +
                ", minRatioForInfo=" + minRatioForInfo +
                ", checkIfBlockInHand=" + checkIfBlockInHand +
                ", darkAltar=" + darkAltar +
                ", lightAltar=" + lightAltar +
                '}';
    }
}
