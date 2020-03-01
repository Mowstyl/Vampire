package com.clanjhoo.vampire.altar;

import com.clanjhoo.vampire.VampireRevamp;
import com.clanjhoo.vampire.entity.MConf;
import com.clanjhoo.vampire.entity.UPlayer;
import com.clanjhoo.vampire.util.EntityUtil;
import com.clanjhoo.vampire.util.TextUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public abstract class Altar {
    public String name;
    public String desc;
    public Material coreMaterial;
    public Map<Material, Integer> materialCounts;
    public List<ItemStack> resources;
    public VampireRevamp plugin;

    public boolean evalBlockUse(Block coreBlock, Player player) {
        boolean blockUse = false;
        String message;

        if (EntityUtil.isPlayer(player) && coreBlock.getType() == coreMaterial) {
            UPlayer uplayer = UPlayer.get(player);
            MConf mconf = plugin.mConf;

            // Make sure we include the coreBlock material in the wanted ones
            if (!this.materialCounts.containsKey(this.coreMaterial)) {
                this.materialCounts.put(this.coreMaterial, 1);
            }

            ArrayList<Block> blocks = getCubeBlocks(coreBlock, mconf.getAltarSearchRadius());
            Map<Material, Integer> nearbyMaterialCounts = countMaterials(blocks, this.materialCounts.keySet());

            int requiredMaterialCountSum = this.sumCollection(this.materialCounts.values());
            int nearbyMaterialCountSum = this.sumCollection(nearbyMaterialCounts.values());

            // If the blocks are to far from looking anything like an altar we will just skip.
            if (nearbyMaterialCountSum >= requiredMaterialCountSum * mconf.getAltarMinRatioForInfo()) {

                // What alter blocks are missing?
                Map<Material, Integer> missingMaterialCounts = this.getMissingMaterialCounts(nearbyMaterialCounts);

                // Is the altar complete?
                if (this.sumCollection(missingMaterialCounts.values()) > 0) {
                    // Send info on what to do to finish the altar
                    message = TextUtil.parse(plugin.mLang.altarIncomplete, this.name);
                    player.sendMessage(message);
                    for (Entry<Material, Integer> entry : missingMaterialCounts.entrySet()) {
                        Material material = entry.getKey();
                        int count = entry.getValue();
                        message = TextUtil.parse("<h>%d <p>%s", count, material.name());
                        player.sendMessage(message);
                    }
                } else {
                    blockUse = this.use(uplayer, player);
                }
            }
        }
        return blockUse;
    }

    public abstract boolean use(UPlayer uplayer, Player player);

    public void watch(UPlayer uplayer, Player player) {
        uplayer.msg(this.desc);
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

