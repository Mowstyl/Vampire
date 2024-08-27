package com.clanjhoo.vampire;

import com.clanjhoo.vampire.keyproviders.HolyWaterMessageKeys;
import com.clanjhoo.vampire.util.BooleanTagType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.List;
import java.util.Objects;

public class HolyWaterUtil {

    public static final NamespacedKey HOLY_WATER_KEY = new NamespacedKey(VampireRevamp.getInstance(), "HolyWater");
    public static final PotionEffect HOLY_WATER_CUSTOM_EFFECT = new PotionEffect(PotionEffectType.REGENERATION, 20, 0);

    public static ItemStack createHolyWater(Player creator) {
        ItemStack ret = new ItemStack(Material.SPLASH_POTION);
        PotionMeta meta = (PotionMeta) ret.getItemMeta();
        assert meta != null;
        Component holyWaterName = VampireRevamp.getMessage(creator, HolyWaterMessageKeys.NAME);
        List<Component> holyWaterLore = VampireRevamp.getMessageList(creator, HolyWaterMessageKeys.LORE);
        VampireRevamp.getVersionCompat().setBasePotionType(meta, PotionType.WATER);
        if (VampireRevamp.isPaperMc()) {
            BungeeComponentSerializer serializer = BungeeComponentSerializer.get();
            meta.setDisplayNameComponent(serializer.serialize(holyWaterName));
            meta.setLoreComponents(holyWaterLore.stream()
                    .map(serializer::serialize)
                    .toList());
        }
        else {
            LegacyComponentSerializer serializer = LegacyComponentSerializer.legacySection();
            meta.setDisplayName(serializer.serialize(holyWaterName));
            meta.setLore(holyWaterLore.stream()
                    .map(serializer::serialize)
                    .toList());
        }
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
        if (!item.getType().equals(Material.SPLASH_POTION))
            return false;

        PotionMeta meta = (PotionMeta) item.getItemMeta();
        assert meta != null;
        if (!Objects.equals(VampireRevamp.getVersionCompat().getBasePotionType(meta), PotionType.WATER))
            return false;

        PersistentDataContainer itemDC =  meta.getPersistentDataContainer();
        Boolean result = itemDC.get(HOLY_WATER_KEY, BooleanTagType.TYPE);
        return result != null && result;
    }
}
