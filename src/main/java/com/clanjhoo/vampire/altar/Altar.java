package com.clanjhoo.vampire.altar;

import co.aikar.commands.MessageType;
import com.clanjhoo.vampire.keyproviders.AltarMessageKeys;
import com.clanjhoo.vampire.VampireRevamp;
import com.clanjhoo.vampire.config.PluginConfig;
import com.clanjhoo.vampire.entity.VPlayer;
import com.clanjhoo.vampire.keyproviders.CommandMessageKeys;
import com.clanjhoo.vampire.util.EntityUtil;
import com.clanjhoo.vampire.util.ResourceUtil;
import com.clanjhoo.vampire.util.Tuple;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

public abstract class Altar {
    boolean isDark;
    Material coreMaterial;
    Map<Material, Integer> materialCounts;
    Set<ItemStack> resources;
    VampireRevamp plugin;
    ResourceUtil resUtil;


    public boolean evalBlockUse(Block coreBlock, Player player) {
        boolean blockUse = false;

        plugin.debugLog(Level.INFO, "Someone clicked " + coreBlock.getType().name());
        plugin.debugLog(Level.INFO, "Core is " + coreMaterial.name());
        if (EntityUtil.isPlayer(player) && coreBlock.getType() == coreMaterial) {
            plugin.debugLog(Level.INFO, "Player clicked core!");
            PluginConfig conf = plugin.getVampireConfig();

            // Make sure we include the coreBlock material in the wanted ones
            if (!this.materialCounts.containsKey(this.coreMaterial)) {
                this.materialCounts.put(this.coreMaterial, 1);
            }

            ArrayList<Block> blocks = getCubeBlocks(coreBlock, conf.altar.searchRadius);
            Map<Material, Integer> nearbyMaterialCounts = countMaterials(blocks, this.materialCounts.keySet());

            int requiredMaterialCountSum = this.sumCollection(this.materialCounts.values());
            int nearbyMaterialCountSum = this.sumCollection(nearbyMaterialCounts.values());

            // If the blocks are to far from looking anything like an altar we will just skip.
            if (nearbyMaterialCountSum >= requiredMaterialCountSum * conf.altar.minRatioForInfo) {

                // What alter blocks are missing?
                Map<Material, Integer> missingMaterialCounts = this.getMissingMaterialCounts(nearbyMaterialCounts);

                // Is the altar complete?
                if (this.sumCollection(missingMaterialCounts.values()) > 0) {
                    // Send info on what to do to finish the altar
                    Component altarName = isDark
                            ? plugin.getMessage(player, AltarMessageKeys.ALTAR_DARK_NAME)
                            : plugin.getMessage(player, AltarMessageKeys.ALTAR_LIGHT_NAME);
                    plugin.sendMessage(player,
                            MessageType.INFO,
                            AltarMessageKeys.INCOMPLETE,
                            new Tuple<>("{altar_name}", altarName));
                    for (Entry<Material, Integer> entry : missingMaterialCounts.entrySet()) {
                        Material material = entry.getKey();
                        Integer count = entry.getValue();
                        plugin.sendMessage(player,
                                MessageType.INFO,
                                AltarMessageKeys.RESOURCE,
                                "{amount}", count.toString(),
                                "{item}", material.name());
                    }
                }
                else {
                    VPlayer vPlayer = plugin.getVPlayer(player);
                    if (vPlayer != null) {
                        blockUse = this.use(vPlayer, player);
                    }
                    else {
                        plugin.log(Level.WARNING, "Couldn't find data for player " + player.getName() + " on altar click.");
                        plugin.sendMessage(player,
                                MessageType.ERROR,
                                CommandMessageKeys.DATA_NOT_FOUND);
                    }
                }
            }
        }
        return blockUse;
    }

    public abstract boolean use(VPlayer vPlayer, Player player);

    public void watch(VPlayer vPlayer, Player player) {
        plugin.sendMessage(player,
                MessageType.INFO,
                isDark ? AltarMessageKeys.ALTAR_DARK_DESC : AltarMessageKeys.ALTAR_LIGHT_DESC);
    }

    // ------------------------------------------------------------ //
    // Some calculation utilities
    // ------------------------------------------------------------ //

    public int sumCollection(Collection<Integer> collection) {
        int ret = 0;
        for (Integer i : collection) ret += i;
        return ret;
    }

    public Map<Material, Integer> getMissingMaterialCounts(Map<Material, Integer> floodMaterialCounts) {
        Map<Material, Integer> ret = new HashMap<>();

        for (Entry<Material, Integer> entry : materialCounts.entrySet()) {
            Integer ihave = floodMaterialCounts.get(entry.getKey());
            if (ihave == null) ihave = 0;
            int missing = entry.getValue() - ihave;
            if (missing < 0) missing = 0;
            ret.put(entry.getKey(), missing);
        }

        return ret;
    }

    public static Map<Material, Integer> countMaterials(Collection<Block> blocks, Set<Material> materialsToCount) {
        Map<Material, Integer> ret = new HashMap<>();
        for (Block block : blocks) {
            Material material = block.getType();
            if (materialsToCount.contains(material)) {
                if (!ret.containsKey(material)) {
                    ret.put(material, 1);
                } else {
                    ret.put(material, ret.get(material) + 1);
                }
            }
        }
        return ret;
    }

    public static ArrayList<Block> getCubeBlocks(Block centerBlock, int radius) {
        ArrayList<Block> blocks = new ArrayList<>();

        for (int y = -radius; y <= radius; y++) {
            for (int z = -radius; z <= radius; z++) {
                for (int x = -radius; x <= radius; x++) {
                    blocks.add(centerBlock.getRelative(x, y, z));
                }
            }
        }
        return blocks;
    }
}

