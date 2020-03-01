package com.clanjhoo.vampire.util;

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

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ResourceUtil {
	public static VampireRevamp plugin;

	public static boolean hasPermission(@Nonnull Permissible permissible, @Nonnull Perm permission, boolean verbose)
	{
		String permissionId = createPermissionId(plugin, permission.name());
		boolean hasPerm = false;

		if (permissible.hasPermission(permissionId)) {
			hasPerm = true;
		}
		else if (verbose && permissible instanceof CommandSender)
		{
			CommandSender sender = (CommandSender) permissible;
			String message = "You don't have permissions to do " + permission.name() + "!";
			sender.sendMessage(TextUtil.parse(message));
		}

		return hasPerm;
	}

	public static boolean hasPermission(Permissible permissible, Perm permission)
	{
		return hasPermission(permissible, permission, false);
	}

	public static String createPermissionId(@Nonnull Plugin plugin, @Nonnull String enumName) {
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
	
	public static String describe(Collection<? extends ItemStack> stacks) {
		List<String> lines = new ArrayList<>();
		for (ItemStack stack : stacks) {
			String desc = describe(stack.getType(), stack.getDurability());
			lines.add(TextUtil.parse("<h>%d <p>%s", stack.getAmount(), desc));
					
		}
		return TextUtil.implode(lines, TextUtil.parse("<i>, "));
	}
	
	public static String describe(Material type, short damage) {
		if (type == Material.POTION && damage == 0) return "Water Bottle";
		if (type == Material.LAPIS_LAZULI && damage == 0 ) return "Lapis Lazuli Dye";
		if (type == Material.CHARCOAL && damage == 0 ) return "Charcoal";
		
		return type.name();
	}
	
	public static boolean playerRemoveAttempt(Player player, Collection<? extends ItemStack> stacks, String success, String fail) {
		if ( ! playerHas(player, stacks))
		{
			player.sendMessage(TextUtil.parse(fail));
			player.sendMessage(describe(stacks));
			return false;
		}

		playerRemove(player, stacks);

		player.sendMessage(TextUtil.parse(success));
		player.sendMessage(describe(stacks));
		
		return true;
	}

	public static ItemStack getWaterBottles(int qty) {
		ItemStack bottles = null;

		if (qty > 0 && qty <= 64) {
			bottles = new ItemStack(Material.POTION, qty);
			PotionMeta meta = (PotionMeta) bottles.getItemMeta();
			PotionData data = new PotionData(PotionType.WATER);
			meta.setBasePotionData(data);
			bottles.setItemMeta(meta);
		}

		return bottles;
	}
}
