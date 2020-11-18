package com.clanjhoo.vampire.altar;

import co.aikar.commands.MessageType;
import com.clanjhoo.vampire.keyproviders.AltarMessageKeys;
import com.clanjhoo.vampire.VampireRevamp;
import com.clanjhoo.vampire.config.PluginConfig;
import com.clanjhoo.vampire.entity.UPlayer;
import com.clanjhoo.vampire.util.EntityUtil;
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
    public boolean isDark;
    public Material coreMaterial;
    public Map<Material, Integer> materialCounts;
    public Set<ItemStack> resources;

    public boolean evalBlockUse(Block coreBlock, Player player) {
        boolean blockUse = false;

        VampireRevamp.debugLog(Level.INFO, "Someone clicked " + coreBlock.getType().name());
        VampireRevamp.debugLog(Level.INFO, "Core is " + coreMaterial.name());
        if (EntityUtil.isPlayer(player) && coreBlock.getType() == coreMaterial) {
            VampireRevamp.debugLog(Level.INFO, "Player clicked core!");
            UPlayer uplayer = UPlayer.get(player);
            if (uplayer == null) {
                VampireRevamp.log(Level.WARNING, "Player " + player.toString() + " is not on Vampire database. Please contact a developer.");
                return false;
            }
            PluginConfig conf = VampireRevamp.getVampireConfig();

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
                    String altarName = isDark ? VampireRevamp.getMessage(player, AltarMessageKeys.ALTAR_DARK_NAME) : VampireRevamp.getMessage(player, AltarMessageKeys.ALTAR_LIGHT_NAME);
                    VampireRevamp.sendMessage(player,
                            MessageType.INFO,
                            AltarMessageKeys.INCOMPLETE,
                            "{altar_name}", altarName);
                    for (Entry<Material, Integer> entry : missingMaterialCounts.entrySet()) {
                        Material material = entry.getKey();
                        Integer count = entry.getValue();
                        VampireRevamp.sendMessage(player,
                                MessageType.INFO,
                                AltarMessageKeys.RESOURCE,
                                "{amount}", count.toString(),
                                "{item}", material.name());
                    }
                }
                else {
                    blockUse = this.use(uplayer, player);
                }
            }
        }
        return blockUse;
    }

    public abstract boolean use(UPlayer uplayer, Player player);

    public void watch(UPlayer uplayer, Player player) {
        VampireRevamp.sendMessage(player,
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

