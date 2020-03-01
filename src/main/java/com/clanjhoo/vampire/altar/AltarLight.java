package com.clanjhoo.vampire.altar;

import com.clanjhoo.vampire.HolyWaterUtil;
import com.clanjhoo.vampire.Perm;
import com.clanjhoo.vampire.VampireRevamp;
import com.clanjhoo.vampire.entity.MLang;
import com.clanjhoo.vampire.entity.MConf;
import com.clanjhoo.vampire.entity.UPlayer;
import com.clanjhoo.vampire.util.ResourceUtil;
import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

import java.util.HashMap;

public class AltarLight extends Altar
{
	public AltarLight(VampireRevamp plugin)
	{
		this.plugin = plugin;
		this.name = this.plugin.mLang.altarLightName;
		this.desc = this.plugin.mLang.altarLightDesc;
		
		this.coreMaterial = Material.LAPIS_BLOCK;
		
		this.materialCounts = new HashMap<>();
		this.materialCounts.put(Material.GLOWSTONE, 30);
		this.materialCounts.put(Material.DANDELION, 5);
		this.materialCounts.put(Material.POPPY, 5);
		this.materialCounts.put(Material.DIAMOND_BLOCK, 2);
		
		this.resources = ImmutableList.of(
			new ItemStack(Material.WATER_BUCKET, 1),
			new ItemStack(Material.DIAMOND, 1),
			new ItemStack(Material.SUGAR, 20),
			new ItemStack(Material.WHEAT, 20)
		);
	}
	
	@Override
	public boolean use(final UPlayer uplayer, final Player player)
	{
		boolean success = false;
		MConf mconf = plugin.mConf;
		uplayer.msg("");
		uplayer.msg(this.desc);
		
		if (Perm.ALTAR_LIGHT.has(player, true)) {
			if (!uplayer.isVampire() && playerHoldsWaterBottle(player)) {
				if (ResourceUtil.playerRemoveAttempt(player, mconf.getHolyWaterResources(), plugin.mLang.altarLightWaterResourceSuccess, plugin.mLang.altarLightWaterResourceFail)) {
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						public void run() {
							ResourceUtil.playerAdd(player, HolyWaterUtil.createHolyWater());
							uplayer.msg(plugin.mLang.altarLightWaterResult);
							uplayer.runFxEnderBurst();
						}
					}, 1);
					success = true;
				}
			}
			else {
				uplayer.msg(plugin.mLang.altarLightCommon);
				uplayer.runFxEnder();

				if (uplayer.isVampire()) {
					if (ResourceUtil.playerRemoveAttempt(player, this.resources, plugin.mLang.altarResourceSuccess, plugin.mLang.altarResourceFail)) {
						Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
							public void run() {
								uplayer.msg(plugin.mLang.altarLightVampire);
								player.getWorld().strikeLightningEffect(player.getLocation().add(0, 3, 0));
								uplayer.runFxEnderBurst();
								uplayer.setVampire(false);
							}
						}, 1);
						success = true;
					}
				} else if (uplayer.isHealthy()) {
					uplayer.msg(plugin.mLang.altarLightHealthy);
				} else if (uplayer.isInfected()) {
					uplayer.msg(plugin.mLang.altarLightInfected);
					uplayer.setInfection(0);
					uplayer.runFxEnderBurst();
				}
			}
		}

		return success;
	}
	
	protected static boolean playerHoldsWaterBottle(Player player)
	{
		boolean holdsWater = false;

		if (player != null) {
			ItemStack item = player.getInventory().getItemInMainHand();
			if (item != null && item.getType() == Material.POTION && item.hasItemMeta() && item.getItemMeta() instanceof PotionMeta) {
				PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
				holdsWater = potionMeta.getBasePotionData().getType() == PotionType.WATER;
			}
		}

		return holdsWater;
	}
	
}
