package com.clanjhoo.vampire;

import com.clanjhoo.vampire.keyproviders.HolyWaterMessageKeys;
import com.clanjhoo.vampire.util.BooleanTagType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.Arrays;
import java.util.List;

public class HolyWaterUtil {

    public static final NamespacedKey HOLY_WATER_KEY = new NamespacedKey(VampireRevamp.getInstance(), "HolyWater");
    public static final PotionEffect HOLY_WATER_CUSTOM_EFFECT = new PotionEffect(PotionEffectType.REGENERATION, 20, 0);

    public static ItemStack createHolyWater(Player creator) {
        ItemStack ret = new ItemStack(Material.SPLASH_POTION);
        PotionData pd = new PotionData(PotionType.WATER);
        PotionMeta meta = (PotionMeta) ret.getItemMeta();
        String holyWaterName = VampireRevamp.getMessage(creator, HolyWaterMessageKeys.NAME);
        String rawLore = VampireRevamp.getMessage(creator, HolyWaterMessageKeys.LORE);
        List<String> holyWaterLore = Arrays.asList(rawLore.split("\\n"));
        meta.setBasePotionData(pd);
        meta.setDisplayName(holyWaterName);
        meta.setLore(holyWaterLore);
        //meta.addCustomEffect(HOLY_WATER_CUSTOM_EFFECT, false);
        PersistentDataContainer potionDC = meta.getPersistentDataContainer();
        potionDC.set(HOLY_WATER_KEY, BooleanTagType.TYPE, true);
        ret.setItemMeta(meta);

        return ret;
    }

    public static boolean isHolyWater(ThrownPotion potion) {
        return isHolyWater(potion.getItem());
    }

    public static boolean isHolyWater(ItemStack item) {
        if (!item.getType().equals(Material.SPLASH_POTION)) {
            return false;
        }

        PotionMeta meta = (PotionMeta) item.getItemMeta();
        if (!meta.getBasePotionData().getType().equals(PotionType.WATER)) {
            return false;
        }

        PersistentDataContainer itemDC =  meta.getPersistentDataContainer();
        return itemDC.get(HOLY_WATER_KEY, BooleanTagType.TYPE);
    }

}
