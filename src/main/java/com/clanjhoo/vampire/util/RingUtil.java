package com.clanjhoo.vampire.util;

import com.clanjhoo.vampire.VampireRevamp;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

public class RingUtil {
    public static final NamespacedKey SUN_RING_KEY = new NamespacedKey(VampireRevamp.getInstance(), "IgnoreRadiation");

    public static ItemStack getSunRing() {
        ItemStack ring = new ItemStack(Material.IRON_NUGGET, 1);
        ItemMeta ringMeta = ring.getItemMeta();
        PersistentDataContainer ringDC =  ringMeta.getPersistentDataContainer();
        ringDC.set(SUN_RING_KEY, BooleanTagType.TYPE, true);
        ring.setItemMeta(ringMeta);
        return ring;
    }

    public static boolean isSunRing(ItemStack item) {
        if (!item.getType().equals(Material.IRON_NUGGET)) {
            return false;
        }
        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer itemDC =  itemMeta.getPersistentDataContainer();

        Boolean result = itemDC.get(SUN_RING_KEY, BooleanTagType.TYPE);
        return result != null && result;
    }
}
