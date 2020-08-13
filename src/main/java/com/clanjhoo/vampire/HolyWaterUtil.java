package com.clanjhoo.vampire;

import com.clanjhoo.vampire.keyproviders.HolyWaterMessageKeys;
import com.clanjhoo.vampire.util.CollectionUtil;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HolyWaterUtil {

    public final static PotionEffect HOLY_WATER_CUSTOM_EFFECT = new PotionEffect(PotionEffectType.REGENERATION, 20, 0);

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
        ret.setItemMeta(meta);

        NBTItem nbti = new NBTItem(ret);
        if (nbti.getCompound("VampireRevamp") == null) {
            nbti.addCompound("VampireRevamp");
        }
        nbti.getCompound("VampireRevamp").setBoolean("HolyWater", true);

        return nbti.getItem();
    }

    public static boolean isHolyWater(ThrownPotion potion) {
        return isHolyWater(potion.getItem());
    }

    public static boolean isHolyWater(ItemStack item) {
        boolean isHoly = false;

        if (item != null &&
                item.getType() != Material.AIR &&
                item.getType() != Material.CAVE_AIR &&
                item.getType() != Material.VOID_AIR) {
            NBTCompound nbtitem = (new NBTItem(item)).getCompound("VampireRevamp");
            isHoly = nbtitem != null && nbtitem.hasKey("HolyWater") && nbtitem.getBoolean("HolyWater");
        }

        return isHoly;
    }

}
