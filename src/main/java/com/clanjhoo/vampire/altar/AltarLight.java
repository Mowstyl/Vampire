package com.clanjhoo.vampire.altar;

import co.aikar.commands.MessageType;
import com.clanjhoo.vampire.util.HolyWaterUtil;
import com.clanjhoo.vampire.keyproviders.AltarMessageKeys;
import com.clanjhoo.vampire.keyproviders.HolyWaterMessageKeys;
import com.clanjhoo.vampire.Perm;
import com.clanjhoo.vampire.VampireRevamp;
import com.clanjhoo.vampire.config.SingleAltarConfig;
import com.clanjhoo.vampire.entity.VPlayer;
import com.clanjhoo.vampire.util.ResourceUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

public class AltarLight extends Altar {
	private final HolyWaterUtil holyUtil;


	public AltarLight(VampireRevamp plugin)
	{
		SingleAltarConfig lightAltar = plugin.getVampireConfig().altar.lightAltar;
		this.coreMaterial = lightAltar.coreMaterial;
		this.materialCounts = lightAltar.buildMaterials;
		this.resources = lightAltar.activate;
		this.isDark = false;
		this.plugin = plugin;
		holyUtil = HolyWaterUtil.get(plugin);
		resUtil = ResourceUtil.get(plugin);
	}
	
	@Override
	public boolean use(final VPlayer vPlayer, final Player player)
	{
		boolean success = false;
		watch(vPlayer, player);
		
		if (resUtil.hasPermission(player, Perm.ALTAR_LIGHT, true)) {
			if (!vPlayer.isVampire() && playerHoldsWaterBottle(player)) {
				if (resUtil.playerRemoveAttempt(player,
						plugin.getVampireConfig().holyWater.resources,
						HolyWaterMessageKeys.CREATE_SUCCESS,
						HolyWaterMessageKeys.CREATE_FAIL)) {
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
						ResourceUtil.playerAdd(player, holyUtil.createHolyWater(player));
						plugin.sendMessage(player,
								MessageType.INFO,
								HolyWaterMessageKeys.CREATE_RESULT);
						vPlayer.runFxEnderBurst();
					}, 1);
					success = true;
				}
			}
			else {
				plugin.sendMessage(player,
						MessageType.INFO,
						AltarMessageKeys.ALTAR_LIGHT_COMMON);
				vPlayer.runFxEnder();

				if (vPlayer.isVampire()) {
					if (resUtil.playerRemoveAttempt(player,
							this.resources,
							AltarMessageKeys.ACTIVATE_SUCCESS,
							AltarMessageKeys.ACTIVATE_FAIL)) {
						Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
							plugin.sendMessage(player,
									MessageType.INFO,
									AltarMessageKeys.ALTAR_LIGHT_VAMPIRE);
							player.getWorld().strikeLightningEffect(player.getLocation().add(0, 3, 0));
							vPlayer.runFxEnderBurst();
							vPlayer.setVampire(false);
						}, 1);
						success = true;
					}
				} else if (vPlayer.isHealthy()) {
					plugin.sendMessage(player,
							MessageType.INFO,
							AltarMessageKeys.ALTAR_LIGHT_HEALTHY);
				} else if (vPlayer.isInfected()) {
					plugin.sendMessage(player,
							MessageType.INFO,
							AltarMessageKeys.ALTAR_LIGHT_INFECTED);
					vPlayer.setInfection(0);
					vPlayer.runFxEnderBurst();
				}
			}
		}

		return success;
	}
	
	protected boolean playerHoldsWaterBottle(Player player)
	{
		boolean holdsWater = false;

		if (player != null) {
			ItemStack item = player.getInventory().getItemInMainHand();
			if (item != null && item.getType() == Material.POTION && item.hasItemMeta() && item.getItemMeta() instanceof PotionMeta) {
				PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
				holdsWater = plugin.getVersionCompat().getBasePotionType(potionMeta) == PotionType.WATER;
			}
		}

		return holdsWater;
	}
	
}
