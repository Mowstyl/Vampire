package com.clanjhoo.vampire.util;

import co.aikar.commands.MessageType;
import co.aikar.locales.MessageKeyProvider;
import com.clanjhoo.vampire.keyproviders.AltarMessageKeys;
import com.clanjhoo.vampire.keyproviders.CommandMessageKeys;
import com.clanjhoo.vampire.Perm;
import com.clanjhoo.vampire.VampireRevamp;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.logging.Level;

public class ResourceUtil {
	public static VampireRevamp plugin;

	public static boolean hasPermission(@NotNull Permissible permissible, @NotNull Perm permission, boolean verbose)
	{
		String permissionId = createPermissionId(plugin, permission.name());
		boolean hasPerm = false;

		if (permissible.hasPermission(permissionId)) {
			hasPerm = true;
		}
		else if (verbose && permissible instanceof CommandSender)
		{
			VampireRevamp.sendMessage((CommandSender) permissible,
					MessageType.ERROR,
					CommandMessageKeys.NOT_ENOUGH_PERMS,
					"{action}", permission.name(),
					"{perm}", permissionId);
		}

		return hasPerm;
	}

	public static boolean hasPermission(@NotNull Permissible permissible, Perm permission)
	{
		return hasPermission(permissible, permission, false);
	}

	public static String createPermissionId(@NotNull Plugin plugin, @NotNull String enumName) {
		return "vampire." + enumName.toLowerCase().replace('_', '.');
	}

	public static boolean playerHas(Player player, ItemStack stack) {
		Material requiredType = stack.getType();
		int requiredAmount = stack.getAmount();
		PotionType requiredPotion = null;
		if (requiredType == Material.POTION) {
			requiredPotion = ((PotionMeta) stack.getItemMeta()).getBasePotionData().getType();
		}
		
		int actualAmount = 0;
		for (ItemStack pstack : player.getInventory().getContents()) {
			if (pstack != null && pstack.getType() == requiredType) {
				if (requiredPotion == null
						|| ((PotionMeta) pstack.getItemMeta()).getBasePotionData().getType() == requiredPotion) {
					actualAmount += pstack.getAmount();
				}
			}
		}
		
		return actualAmount >= requiredAmount;
	}
	
	public static boolean playerHas(Player player, Collection<? extends ItemStack> stacks) {
		boolean hasAll = true;
		for (ItemStack stack : stacks) {
			if (!playerHas(player, stack)) {
				hasAll = false;
				break;
			}
		}
		return hasAll;
	}
	
	public static void playerRemove(Player player, Collection<? extends ItemStack> stacks) {
		playerRemove(player, stacks.toArray(new ItemStack[0]));
	}
	
	public static void playerRemove(Player player, ItemStack... stacks) {
		VampireRevamp.debugLog(Level.INFO, "Removing stacks!");
		player.getInventory().removeItem(stacks);
		player.updateInventory();
	}
	
	public static void playerAdd(Player player, Collection<? extends ItemStack> stacks) {
		Inventory inventory = player.getInventory();
		inventory.addItem(stacks.toArray(new ItemStack[0]));
		player.updateInventory();
	}
	
	public static void playerAdd(Player player, ItemStack stack) {
		Inventory inventory = player.getInventory();
		inventory.addItem(stack);
		player.updateInventory();
	}
	
	public static void describe(Collection<? extends ItemStack> stacks, Player player) {
		for (ItemStack stack : stacks) {
			VampireRevamp.sendMessage(player,
					MessageType.INFO,
					AltarMessageKeys.RESOURCE,
					"{amount}", String.format("%d", stack.getAmount()),
					"{item}", stack.getType().name());
		}
	}
	
	public static boolean playerRemoveAttempt(Player player, Collection<? extends ItemStack> stacks, MessageKeyProvider success, MessageKeyProvider fail) {
		boolean result = false;
		if (playerHas(player, stacks)) {
			playerRemove(player, stacks);
			result = true;
		}
		VampireRevamp.sendMessage(player,
				MessageType.INFO,
				result ? success : fail);
		describe(stacks, player);
		
		return result;
	}

	public static ItemStack getWaterBottles(int qty) {
		ItemStack bottles = null;

		if (qty > 0 && qty <= 64) {
			bottles = new ItemStack(Material.POTION, qty);
			PotionMeta meta = (PotionMeta) bottles.getItemMeta();
			meta.setBasePotionType(PotionType.WATER);
			bottles.setItemMeta(meta);
		}

		return bottles;
	}
}
