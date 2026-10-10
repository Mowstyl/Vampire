package com.clanjhoo.vampire.util;

import com.clanjhoo.vampire.cmd.MessageType;
import com.clanjhoo.vampire.keyproviders.AltarMessageKeys;
import com.clanjhoo.vampire.keyproviders.CommandMessageKeys;
import com.clanjhoo.vampire.Perm;
import com.clanjhoo.vampire.VampireRevamp;
import com.clanjhoo.vampire.keyproviders.KeyProvider;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class ResourceUtil {
	private final static Map<String, ResourceUtil> instances = new ConcurrentHashMap<>(1);

	private final VampireRevamp plugin;


	private ResourceUtil(VampireRevamp plugin) {
		this.plugin = plugin;
	}

	public static ResourceUtil get(VampireRevamp plugin) {
		return instances.computeIfAbsent(plugin.getName(), (k) -> new ResourceUtil(plugin));
	}

	public boolean hasPermission(@NotNull Permissible permissible, @NotNull Perm permission, boolean verbose)
	{
		String permissionId = createPermissionId(plugin, permission.name());
		boolean hasPerm = false;

		if (permissible.hasPermission(permissionId)) {
			hasPerm = true;
		}
		else if (verbose && permissible instanceof CommandSender)
		{
			plugin.sendMessage((CommandSender) permissible,
					MessageType.ERROR,
					CommandMessageKeys.NOT_ENOUGH_PERMS,
					"{action}", permission.name(),
					"{perm}", permissionId);
		}

		return hasPerm;
	}

	public boolean hasPermission(@NotNull Permissible permissible, Perm permission)
	{
		return hasPermission(permissible, permission, false);
	}

	public static String createPermissionId(@NotNull Plugin plugin, @NotNull String enumName) {
		return "vampire." + enumName.toLowerCase().replace('_', '.');
	}

	public boolean playerHas(Player player, ItemStack stack) {
		int requiredAmount = stack.getAmount();
		
		int actualAmount = 0;
		for (ItemStack pstack : player.getInventory().getStorageContents()) {
			if (pstack != null && pstack.isSimilar(stack))
				actualAmount += pstack.getAmount();
		}
		
		return actualAmount >= requiredAmount;
	}
	
	public boolean playerHas(Player player, Collection<? extends ItemStack> stacks) {
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
		player.getInventory().removeItem(stacks);
		player.updateInventory();
	}
	
	public static void playerAdd(Player player, Collection<? extends ItemStack> stacks) {
		Inventory inventory = player.getInventory();
		Map<Integer, ItemStack> result = inventory.addItem(stacks.toArray(new ItemStack[0]));
		for (ItemStack remaining : result.values())
			player.getWorld().dropItem(player.getLocation(), remaining);
		player.updateInventory();
	}
	
	public static void playerAdd(Player player, ItemStack stack) {
		Inventory inventory = player.getInventory();
		Map<Integer, ItemStack> result = inventory.addItem(stack);
		for (ItemStack remaining : result.values())
			player.getWorld().dropItem(player.getLocation(), remaining);
		player.updateInventory();
	}
	
	public void describe(Collection<? extends ItemStack> stacks, Player player) {
		for (ItemStack stack : stacks) {
			plugin.sendMessage(player,
					MessageType.INFO,
					AltarMessageKeys.RESOURCE,
					"{amount}", String.format("%d", stack.getAmount()),
					"{item}", stack.getType().name());
		}
	}
	
	public boolean playerRemoveAttempt(Player player, Collection<? extends ItemStack> stacks, KeyProvider success, KeyProvider fail) {
		boolean result = false;
		if (playerHas(player, stacks)) {
			playerRemove(player, stacks);
			result = true;
		}
		plugin.sendMessage(player,
				MessageType.INFO,
				result ? success : fail);
		describe(stacks, player);
		
		return result;
	}

	public ItemStack getWaterBottles(int qty) {
		ItemStack bottles = null;

		if (qty > 0 && qty <= 64) {
			bottles = new ItemStack(Material.POTION, qty);
			PotionMeta meta = (PotionMeta) bottles.getItemMeta();
			plugin.getVersionCompat().setBasePotionType(meta, PotionType.WATER);
			bottles.setItemMeta(meta);
		}

		return bottles;
	}
}
