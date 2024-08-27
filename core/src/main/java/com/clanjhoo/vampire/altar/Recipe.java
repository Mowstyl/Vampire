package com.clanjhoo.vampire.altar;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Recipe
{
	public final Map<Material, Integer> materialQuantities;
	
	// GSON needs this noarg constructor;
	public Recipe()
	{
		this(new HashMap<>());
	}
	
	public Recipe(Map<Material, Integer> materialQuantities)
	{
		this.materialQuantities = materialQuantities;
	}
	
	public void removeFromPlayer(Player player)
	{
		Inventory inventory = player.getInventory();
		for (Material material: this.materialQuantities.keySet()) {
			inventory.removeItem(new ItemStack(material, this.materialQuantities.get(material)));
		}
		player.updateInventory(); // It is ok to use this method though it is deprecated.
	}
	
	public boolean playerHasEnough(Player player)
	{
		Inventory inventory = player.getInventory();
		for (Material material: this.materialQuantities.keySet()) {
			if (getMaterialCountFromInventory(material, inventory) < this.materialQuantities.get(material))
			{
				return false;
			}
		}
		return true;
	}
	
	public static int getMaterialCountFromInventory(Material material, Inventory inventory)
	{
		int count = 0;
		for(ItemStack stack : inventory.all(material).values())
		{
			count += stack.getAmount();
		}
		return count;
	}
	
	public String getRecipeLine()
	{
		ArrayList<String> lines = new ArrayList<>();
		//for (Entry<Material, Integer> item : MUtil.entriesSortedByValues(this.materialQuantities)) {
		for (Entry<Material, Integer> item : this.materialQuantities.entrySet()) {
			Material material = item.getKey();
			int count = item.getValue();
			lines.add(String.format("<light_purple>%d</light_purple> <dark_aqua>%s</dark_aqua>", count, material.name()));
		}
		return String.join("<yellow>, </yellow>", lines);
	}
	
}
