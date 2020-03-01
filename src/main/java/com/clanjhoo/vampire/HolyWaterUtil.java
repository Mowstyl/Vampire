package com.clanjhoo.vampire;

import com.clanjhoo.vampire.util.CollectionUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class HolyWaterUtil {
    public final static String HOLY_WATER_NAME = ChatColor.GREEN.toString() + "Holy Water";
    public final static List<String> HOLY_WATER_LORE = CollectionUtil.list(
            "Ordinary water infused with lapis.",
            "Very dangerous to the unholy."
    );

    public final static PotionEffect HOLY_WATER_CUSTOM_EFFECT = new PotionEffect(PotionEffectType.REGENERATION, 20, 0);

    public static ItemStack createHolyWater() {
        ItemStack ret = new ItemStack(Material.SPLASH_POTION);

        PotionMeta meta = (PotionMeta) ret.getItemMeta();
        meta.setDisplayName(HOLY_WATER_NAME);
        meta.setLore(HOLY_WATER_LORE);
        meta.addCustomEffect(HOLY_WATER_CUSTOM_EFFECT, false);
        ret.setItemMeta(meta);

        return ret;
    }

    public static boolean isHolyWater(ThrownPotion potion) {
        return isHolyWater(potion.getItem());
    }

    public static boolean isHolyWater(ItemStack item) {
        boolean isHoly = false;

        if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            isHoly = HOLY_WATER_NAME.equals(item.getItemMeta().getDisplayName());
        }

        return isHoly;
    }

}
