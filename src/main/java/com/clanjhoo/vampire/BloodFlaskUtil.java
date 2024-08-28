package com.clanjhoo.vampire;

import com.clanjhoo.vampire.entity.VPlayer;
import com.clanjhoo.vampire.keyproviders.SkillMessageKeys;
import com.clanjhoo.vampire.util.BooleanTagType;
import com.clanjhoo.vampire.util.Tuple;
import com.clanjhoo.vampire.util.UUIDTagType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class BloodFlaskUtil {
    private final VampireRevamp plugin;
    public final PotionEffect BLOOD_FLASK_CUSTOM_EFFECT;
    public final NamespacedKey BLOOD_FLASK_KEY;
    public final NamespacedKey BLOOD_FLASK_AMOUNT;
    public final NamespacedKey BLOOD_FLASK_VAMPIRIC;
    public final NamespacedKey BLOOD_FLASK_OWNER;


    public BloodFlaskUtil(VampireRevamp plugin) {
        this.plugin = plugin;
        BLOOD_FLASK_CUSTOM_EFFECT = new PotionEffect(plugin.getVersionCompat().getStrengthEffect(), 20, 0);
        BLOOD_FLASK_KEY = new NamespacedKey(plugin, "flask");
        BLOOD_FLASK_AMOUNT = new NamespacedKey(plugin, "amount");
        BLOOD_FLASK_VAMPIRIC = new NamespacedKey(plugin, "vampiric");
        BLOOD_FLASK_OWNER = new NamespacedKey(plugin, "owner");
    }

    public ItemStack createBloodFlask(Player creator, double amount, boolean isVampiric) {
        // Create a new item stack of material potion ...
        ItemStack ret = new ItemStack(Material.POTION);

        // ... and convert the isVampiric boolean into a string ...
        SkillMessageKeys flaskVampKey = isVampiric ? SkillMessageKeys.FLASK_VAMPIRIC_TRUE : SkillMessageKeys.FLASK_VAMPIRIC_FALSE;
        Component metaVampiric = plugin.getMessage(creator, flaskVampKey);

        // ... create the item lore ...
        List<Component> lore = new ArrayList<>(plugin.getMessageList(creator, SkillMessageKeys.FLASK_AMOUNT, new Tuple<>("{amount}", Component.text(amount / 2))));
        lore.add(metaVampiric);

        // ... and set the item meta ...
        Component displayName = plugin.getMessage(creator, SkillMessageKeys.FLASK_NAME);
        PotionMeta meta = (PotionMeta) ret.getItemMeta();
        assert meta != null;

        if (plugin.isPaperMc()) {
            BungeeComponentSerializer serializer = BungeeComponentSerializer.get();
            meta.setDisplayNameComponent(serializer.serialize(displayName));
            meta.setLoreComponents(lore.stream()
                    .map(serializer::serialize)
                    .collect(Collectors.toList()));
        }
        else {
            LegacyComponentSerializer serializer = LegacyComponentSerializer.legacySection();
            meta.setDisplayName(serializer.serialize(displayName));
            meta.setLore(lore.stream()
                    .map(serializer::serialize)
                    .collect(Collectors.toList()));
        }
        meta.addCustomEffect(BLOOD_FLASK_CUSTOM_EFFECT, false);
        meta.addItemFlags(plugin.getVersionCompat().getHidePotionEffectsFlag(), ItemFlag.HIDE_ATTRIBUTES);

        PersistentDataContainer flaskDC = meta.getPersistentDataContainer();
        PersistentDataContainer flaskTag = flaskDC.getAdapterContext().newPersistentDataContainer();
        flaskTag.set(BLOOD_FLASK_AMOUNT, PersistentDataType.DOUBLE, amount);
        flaskTag.set(BLOOD_FLASK_VAMPIRIC, BooleanTagType.TYPE, isVampiric);
        flaskTag.set(BLOOD_FLASK_OWNER, UUIDTagType.TYPE, creator.getUniqueId());
        flaskDC.set(BLOOD_FLASK_KEY, PersistentDataType.TAG_CONTAINER, flaskTag);

        ret.setItemMeta(meta);

        // ... finally, return the result.
        return ret;
    }

    @Nullable
    public BloodFlaskData getBloodFlaskData(@NotNull ItemStack item) {
        Double amount;
        Boolean isVampiric;
        UUID owner;

        ItemMeta meta = item.getItemMeta();
        if (meta == null)
            return null;

        PersistentDataContainer flaskTag = meta.getPersistentDataContainer().get(BLOOD_FLASK_KEY, PersistentDataType.TAG_CONTAINER);
        if (flaskTag == null)
            return null;

        amount = flaskTag.get(BLOOD_FLASK_AMOUNT, PersistentDataType.DOUBLE);
        isVampiric = flaskTag.get(BLOOD_FLASK_VAMPIRIC, BooleanTagType.TYPE);
        owner = flaskTag.get(BLOOD_FLASK_OWNER, UUIDTagType.TYPE);
        if (amount == null || isVampiric == null || owner == null) {
            plugin.log(Level.WARNING, "Found incomplete flask tag");
            return null;
        }

        return new BloodFlaskData(owner, amount, isVampiric);
    }

    private static boolean playerConsumeGlassBottle(@NotNull Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.GLASS_BOTTLE) {
            int amount = item.getAmount();
            if (amount > 1)
                item.setAmount(amount - 1);
            else
                item = null;

            player.getInventory().setItemInMainHand(item);
            return true;
        }
        return false;
    }

    public boolean fillBottle(VPlayer vPlayer, double amount) {
        Player player = vPlayer.getPlayer();
        if (player == null)
            return false;
        if (!BloodFlaskUtil.playerConsumeGlassBottle(player))
            return false;

        PlayerInventory playerInventory = player.getInventory();
        ItemStack flask = createBloodFlask(player, amount, vPlayer.isVampire());
        Map<Integer, ItemStack> result = playerInventory.addItem(flask);
        if (!result.isEmpty())
            player.getWorld().dropItem(player.getLocation(), flask);

        return true;
    }

    public static class BloodFlaskData {
        private final UUID owner;
        private final double amount;
        private final boolean isVampiric;

        public BloodFlaskData(UUID owner, double amount, boolean isVampiric) {
            this.owner = owner;
            this.amount = amount;
            this.isVampiric = isVampiric;
        }

        public UUID getOwner() {
            return owner;
        }

        public double getAmount() {
            return amount;
        }

        public boolean isVampiric() {
            return isVampiric;
        }
    }
}
