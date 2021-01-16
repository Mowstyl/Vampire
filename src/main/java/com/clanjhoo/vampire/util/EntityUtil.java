package com.clanjhoo.vampire.util;

import com.clanjhoo.vampire.VampireRevamp;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.Metadatable;
import org.bukkit.permissions.PermissionAttachment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class EntityUtil {
    public static final Map<UUID, PermissionAttachment> perms = new ConcurrentHashMap<>();

    public static Player getAsPlayer(Entity entity) {
        Player player = null;

        if (entity != null && entity.getType() == EntityType.PLAYER && entity instanceof Player) {
            player = (Player) entity;
        }

        return player;
    }

    // -------------------------------------------- //
    // IS(NT) NPC, SENDER, PLAYER
    // -------------------------------------------- //

    public static boolean isNpc(Object object) {
        boolean buliano = false;

        if (object instanceof Metadatable) {
            Metadatable metadatable = (Metadatable) object;
            try {
                buliano = metadatable.hasMetadata("NPC");
            } catch (UnsupportedOperationException ignore) {
                // ProtocolLib
                // UnsupportedOperationException: The method hasMetadata is not supported for temporary players.
            }
        }

        return buliano;
    }

    public static boolean isSender(Object object) {
        boolean buliano = false;

        if (object != null && object instanceof CommandSender) {
            buliano = !isNpc(object);
        }

        return buliano;
    }

    public static boolean isPlayer(Object object) {
        boolean buliano = false;

        if (object instanceof Player) {
            buliano = !isNpc(object);
        }

        return buliano;
    }

    // -------------------------------------------- //
    // PACKET
    // -------------------------------------------- //

    /**
     * Updates the players food and health information.
     */
    public static void sendHealthFoodUpdatePacket(Player player) {
        // No need for nms anymore.
        // We can trigger this packet through the use of this bukkit api method:
        player.setHealthScaled(player.isHealthScaled());
    }

    // -------------------------------------------- //
    // Inventory
    // -------------------------------------------- //

    // This method is used to clean out inconsistent air entries.
    public static ItemStack clean(ItemStack item) {
        ItemStack istack = item;

        // NOTE: In 1.9 zero quantity is a thing.
        if (item.getType() == Material.AIR || item.getAmount() <= 0) {
            istack = null;
        }

        return istack;
    }

    public static void clean(ItemStack[] items) {
        for (int i = 0; i < items.length; i++) {
            items[i] = clean(items[i]);
        }
    }

    public static ItemStack getWeapon(HumanEntity human) {
        ItemStack weapon = null;

        if (human != null) {
            weapon = human.getInventory().getItemInMainHand();
            weapon = clean(weapon);
        }

        return weapon;
    }

    public static boolean despawnBats(Player p) {
        boolean result = false;
        VampireRevamp plugin = VampireRevamp.getInstance();

        try {
            if (plugin.batmap.containsKey(p.getUniqueId())) {
                List<LivingEntity> entities = plugin.batmap.get(p.getUniqueId());

                for (LivingEntity entity : entities) {
                    plugin.bats.remove(entity);
                    if (entity.isValid() && !entity.isDead()) {
                        entity.remove();
                    }
                }

                plugin.batmap.remove(p.getUniqueId());
            }

            result = true;
        }
        catch (Exception ex) {
            plugin.getLogger().log(Level.WARNING, "Error despawning bats!: " + ex.getMessage());
            ex.printStackTrace();
        }

        return result;
    }

    public static boolean spawnBats(Player p, int qty) {
        boolean result = false;
        VampireRevamp plugin = VampireRevamp.getInstance();

        try {
            if (plugin.batmap.containsKey(p.getUniqueId())) {
                despawnBats(p);
            }

            List<LivingEntity> entities = new ArrayList<LivingEntity>();

            for (int i = 0; i < qty; i++) {
                Bat fakeBat = (Bat) p.getWorld().spawnEntity(p.getLocation(), EntityType.BAT);
                if (fakeBat != null) {
                    fakeBat.setCustomName(p.getDisplayName());
                    //fakeBat.setCustomNameVisible(true);
                    //fakeBat.getAttribute(Attribute.GENERIC_FLYING_SPEED).setBaseValue(p.getAttribute(Attribute.GENERIC_FLYING_SPEED).getBaseValue());
                    entities.add(fakeBat);
                    plugin.bats.add(fakeBat);
                }
            }

            plugin.batmap.put(p.getUniqueId(), entities);
            result = true;
        }
        catch (Exception ex) {
            VampireRevamp.log(Level.WARNING, "Error spawning bats!: " + ex.getMessage());
            ex.printStackTrace();
        }

        return result;
    }
}
