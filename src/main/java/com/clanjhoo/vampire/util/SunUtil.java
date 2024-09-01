package com.clanjhoo.vampire.util;

import com.clanjhoo.vampire.VampireRevamp;

import com.clanjhoo.vampire.compat.WorldGuardCompat;
import com.clanjhoo.vampire.entity.VPlayer;
import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SunUtil
{
	// -------------------------------------------- //
	// CONSTANTS
	// -------------------------------------------- //
	private final static Map<Material, Double> opacity;
	static {
		opacity = new ConcurrentHashMap<>();
		for (Material type : Registry.MATERIAL) {
			opacity.put(type, getMaterialOpacity(type));
		}
	}
	
	public final static int MID_DAY_TICKS = 6000;
	public final static int DAY_TICKS = 24000;
	public final static int HALF_DAY_TICKS = DAY_TICKS / 2;
	public final static int DAYTIME_TICKS = 14000;
	public final static int HALF_DAYTIME_TICKS = DAYTIME_TICKS / 2;
	public final static double HALF_PI = Math.PI / 2;
	public final static double MDTICKS_TO_ANGLE_FACTIOR = HALF_PI / HALF_DAYTIME_TICKS;

	public final VampireRevamp plugin;

	public SunUtil(VampireRevamp plugin) {
		this.plugin = plugin;
	}
	// -------------------------------------------- //
	// SOLAR RADIATION CALCULATION
	// -------------------------------------------- //
	
	/**
	 * This time of day relative to mid day.
	 * 0 means midday. -7000 mean start of sunrise. +7000 means end of sundown.
	 */
	public int calcMidDeltaTicks(World world, Player player)
	{
		long rtime = world.getTime();

		if (plugin.isWorldGuardEnabled()) {
			WorldGuardCompat wg = plugin.getWorldGuardCompat();
			rtime = wg.getTime(player, player.getLocation());
		}

		int ret = (int) ((rtime - MID_DAY_TICKS) % DAY_TICKS);

		if (ret >= HALF_DAY_TICKS)
		{
			ret -= DAY_TICKS;
		}

		return ret;
	}
	
	/**
	 * The insolation angle in radians.
	 * 0 means directly from above. -Pi/2 means start of sunrise etc.
	 */
	public double calcSunAngle(World world, Player player)
	{
		int mdticks = calcMidDeltaTicks(world, player);
		return MDTICKS_TO_ANGLE_FACTIOR * mdticks;
	}
	
	/**
	 * A value between 0 and 1. 0 means no sun at all. 1 means sun directly from above.
	 * http://en.wikipedia.org/wiki/Effect_of_sun_angle_on_climate
	 */
	public double calcSolarRad(World world, Player player) {
		if (world.getEnvironment() != Environment.NORMAL)
			return 0d;
		boolean storming = world.hasStorm();

		if (plugin.isWorldGuardEnabled()) {
			WorldGuardCompat wg = plugin.getWorldGuardCompat();
			storming = !wg.isSkyClear(player, player.getLocation());
		}

		if (storming)
			return 0d;
		double angle = calcSunAngle(world, player);
		double absangle = Math.abs(angle);
		if (absangle >= HALF_PI)
			return 0;
		double a = HALF_PI - absangle;
		//P.p.log("calcSolarRadiation", Math.sin(a));
		return Math.sin(a);
	}
	
	// -------------------------------------------- //
	// TERRAIN OPACITY CALCULATION
	// -------------------------------------------- //

	public double calcTerrainOpacity(Block block, RayTraceResult result)
	{
		if (plugin.getVampireConfig().radiation.useOldRadiationFormula) {
			return oldTerrainOpacity(block);
		}

		return newTerrainOpacity(block, result);
	}

	private double newTerrainOpacity(Block block, RayTraceResult result)
	{
		double intensity = block.getLightFromSky() / 15D;
		double transparency = 1D;
		if (result != null) {
			Block hit = result.getHitBlock();
			if (hit != null) {
				transparency -= SunUtil.opacity.get(hit.getType()) / 2;
			}
		}
		return 1 - transparency * intensity;
	}

	/**
	 * The sum of the opacity above and including the block.
	 */
	private double oldTerrainOpacity(Block block) {
		double ret = 0;

		int x = block.getX();
		int z = block.getZ();
		World world = block.getWorld();
		int maxy = world.getMaxHeight() -1;

		for (int y = block.getY(); y <= maxy && ret < 1d; y++)
		{
			Material type = world.getBlockAt(x, y, z).getType();
			ret += SunUtil.opacity.get(type);
		}

		if (ret > 1.0D) ret = 1d;

		//P.p.log("calcTerrainOpacity",ret);

		return ret;
	}

	private static double getMaterialOpacity(Material type) {
		String name = type.name();
		double opacity;
		if (name.contains("AIR") || type == Material.BARRIER)
			opacity = 0;
		else if (type == Material.GLASS || type == Material.GLASS_PANE)
			opacity = 0.05;
		else if (type == Material.VINE || type == Material.IRON_BARS || type == Material.LADDER
				|| type == Material.COCOA || name.contains("GLASS_PANE") || name.contains("SIGN")
				|| name.contains("TORCH") || name.contains("CHAIN") || name.contains("TRIPWIRE"))
			opacity = 0.1;
		else if (type.isTransparent() || type == Material.WATER || type == Material.ICE
				|| type == Material.COBWEB || name.contains("GLASS"))
			opacity = 0.25;
		else if (name.contains("LEAVES") || name.contains("PLANT") || name.contains("BANNER") || name.contains("FENCE"))
			opacity = 0.5;
		else if (!type.isOccluding())
			opacity = 0.75;
		else
			opacity = 1;
		return opacity;
	}

	// -------------------------------------------- //
	// ARMOR OPACITY CALCULATION
	// -------------------------------------------- //
	
	/**
	 * The armor opacity against solar radiation.
	 * http://en.wikipedia.org/wiki/Opacity_%28optics%29
	 */
	public double calcArmorOpacity(Player player)
	{
		double ret = 0;
		for (ItemStack itemStack : player.getInventory().getArmorContents()) {
			if (itemStack == null) continue;
			if (itemStack.getAmount() == 0) continue;
			if (itemStack.getType() == Material.AIR) continue;
			ret += plugin.getVampireConfig().radiation.opacityPerArmorPiece;
		}
		return ret;
	}

	// -------------------------------------------- //
	// PLAYER CALCULATIONS
	// -------------------------------------------- //
	
	/**
	 * The player irradiation is a value between 0 and 1.
	 * It is based on the irradiation from the sun but the 
	 * opacity of the terrain and player armor is taken into acocunt.
	 */
	public double calcPlayerIrradiation(VPlayer vPlayer, Player player)
	{
		// Player must exist.
		if ( ! player.isOnline()) return 0;
		if (player.isDead()) return 0;
		
		// Insolation
		World world = player.getWorld();
		double ret = calcSolarRad(world, player);
		if (ret == 0) return 0;
		
		// Terrain
		Block block = player.getLocation().getBlock().getRelative(0, 1, 0);
		double terrainOpacity = calcTerrainOpacity(block, vPlayer.getLastRayTrace());
		ret *= (1-terrainOpacity);
		if (ret == 0) return 0;
		
		// Armor
		double armorOpacity = calcArmorOpacity(player);
		ret *= (1-armorOpacity);

		return ret;
	}
}
