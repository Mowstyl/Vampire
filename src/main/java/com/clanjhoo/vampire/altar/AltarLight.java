package com.clanjhoo.vampire.altar;

import co.aikar.commands.MessageType;
import com.clanjhoo.vampire.HolyWaterUtil;
import com.clanjhoo.vampire.keyproviders.AltarMessageKeys;
import com.clanjhoo.vampire.keyproviders.HolyWaterMessageKeys;
import com.clanjhoo.vampire.Perm;
import com.clanjhoo.vampire.VampireRevamp;
import com.clanjhoo.vampire.config.SingleAltarConfig;
import com.clanjhoo.vampire.entity.UPlayer;
import com.clanjhoo.vampire.util.ResourceUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

public class AltarLight extends Altar
{
	public AltarLight()
	{
		SingleAltarConfig lightAltar = VampireRevamp.getVampireConfig().altar.lightAltar;
		this.coreMaterial = lightAltar.coreMaterial;

		this.materialCounts = lightAltar.buildMaterials;

		this.resources = lightAltar.activate;

		this.isDark = false;
	}
	
	@Override
	public boolean use(final UPlayer uplayer, final Player player)
	{
		boolean success = false;
		player.sendMessage("");
		VampireRevamp.sendMessage(player,
				MessageType.INFO,
				AltarMessageKeys.ALTAR_LIGHT_DESC);
		
		if (Perm.ALTAR_LIGHT.has(player, true)) {
			if (!uplayer.isVampire() && playerHoldsWaterBottle(player)) {
				if (ResourceUtil.playerRemoveAttempt(player,
						VampireRevamp.getVampireConfig().holyWater.resources,
						HolyWaterMessageKeys.CREATE_SUCCESS,
						HolyWaterMessageKeys.CREATE_FAIL)) {
					Bukkit.getScheduler().scheduleSyncDelayedTask(VampireRevamp.getInstance(), new Runnable() {
						public void run() {
							ResourceUtil.playerAdd(player, HolyWaterUtil.createHolyWater(player));
							VampireRevamp.sendMessage(player,
									MessageType.INFO,
									HolyWaterMessageKeys.CREATE_RESULT);
							uplayer.runFxEnderBurst();
						}
					}, 1);
					success = true;
				}
			}
			else {
				VampireRevamp.sendMessage(player,
						MessageType.INFO,
						AltarMessageKeys.ALTAR_LIGHT_COMMON);
				uplayer.runFxEnder();

				if (uplayer.isVampire()) {
					if (ResourceUtil.playerRemoveAttempt(player,
							this.resources,
							AltarMessageKeys.ACTIVATE_SUCCESS,
							AltarMessageKeys.ACTIVATE_FAIL)) {
						Bukkit.getScheduler().scheduleSyncDelayedTask(VampireRevamp.getInstance(), new Runnable() {
							public void run() {
								VampireRevamp.sendMessage(player,
										MessageType.INFO,
										AltarMessageKeys.ALTAR_LIGHT_VAMPIRE);
								player.getWorld().strikeLightningEffect(player.getLocation().add(0, 3, 0));
								uplayer.runFxEnderBurst();
								uplayer.setVampire(false);
							}
						}, 1);
						success = true;
					}
				} else if (uplayer.isHealthy()) {
					VampireRevamp.sendMessage(player,
							MessageType.INFO,
							AltarMessageKeys.ALTAR_LIGHT_HEALTHY);
				} else if (uplayer.isInfected()) {
					VampireRevamp.sendMessage(player,
							MessageType.INFO,
							AltarMessageKeys.ALTAR_LIGHT_INFECTED);
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
