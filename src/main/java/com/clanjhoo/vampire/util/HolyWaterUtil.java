package com.clanjhoo.vampire.util;

import com.clanjhoo.vampire.VampireRevamp;
import com.clanjhoo.vampire.keyproviders.HolyWaterMessageKeys;
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
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class HolyWaterUtil {
    private final static Map<String, HolyWaterUtil> instances = new ConcurrentHashMap<>(1);

    private final VampireRevamp plugin;
    public final NamespacedKey HOLY_WATER_KEY;
    public final PotionEffect HOLY_WATER_CUSTOM_EFFECT;


    private HolyWaterUtil(VampireRevamp plugin) {
        this.plugin = plugin;
        HOLY_WATER_KEY = new NamespacedKey(plugin, "HolyWater");
        HOLY_WATER_CUSTOM_EFFECT = new PotionEffect(PotionEffectType.REGENERATION, 20, 0);
    }

    public static HolyWaterUtil get(VampireRevamp plugin) {
        return instances.computeIfAbsent(plugin.getName(), (k) -> new HolyWaterUtil(plugin));
    }

    public ItemStack createHolyWater(Player creator) {
        ItemStack ret = new ItemStack(Material.SPLASH_POTION);
        PotionMeta meta = (PotionMeta) ret.getItemMeta();
        assert meta != null;
        Component holyWaterName = plugin.getMessage(creator, HolyWaterMessageKeys.NAME);
        List<Component> holyWaterLore = plugin.getMessageList(creator, HolyWaterMessageKeys.LORE);
        plugin.getVersionCompat().setBasePotionType(meta, PotionType.WATER);
        if (plugin.isPaperMc()) {
            BungeeComponentSerializer serializer = BungeeComponentSerializer.get();
            meta.setDisplayNameComponent(serializer.serialize(holyWaterName));
            meta.setLoreComponents(holyWaterLore.stream()
                    .map(serializer::serialize)
                    .collect(Collectors.toList()));
        }
        else {
            LegacyComponentSerializer serializer = LegacyComponentSerializer.legacySection();
            meta.setDisplayName(serializer.serialize(holyWaterName));
            meta.setLore(holyWaterLore.stream()
                    .map(serializer::serialize)
                    .collect(Collectors.toList()));
        }
        //meta.addCustomEffect(HOLY_WATER_CUSTOM_EFFECT, false);
        PersistentDataContainer potionDC = meta.getPersistentDataContainer();
        potionDC.set(HOLY_WATER_KEY, BooleanTagType.TYPE, true);
        ret.setItemMeta(meta);

        return ret;
    }

    public boolean isHolyWater(ThrownPotion potion) {
        return isHolyWater(potion.getItem());
    }

    public boolean isHolyWater(ItemStack item) {
        if (!item.getType().equals(Material.SPLASH_POTION))
            return false;

        PotionMeta meta = (PotionMeta) item.getItemMeta();
        assert meta != null;
        if (!Objects.equals(plugin.getVersionCompat().getBasePotionType(meta), PotionType.WATER))
            return false;

        PersistentDataContainer itemDC =  meta.getPersistentDataContainer();
        Boolean result = itemDC.get(HOLY_WATER_KEY, BooleanTagType.TYPE);
        return result != null && result;
    }
}
