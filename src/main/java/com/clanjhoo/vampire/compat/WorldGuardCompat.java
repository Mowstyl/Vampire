package com.clanjhoo.vampire.compat;

import com.clanjhoo.vampire.VampireRevamp;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.lang.reflect.Method;
import java.util.logging.Level;

public class WorldGuardCompat {
    public final boolean useWG;
    public final boolean oldWG;
    private Method getRegionContainerMethod;
    private Method getRegionManagerMethod;
    private Method getApplicableRegionsMethod;
    private Class<?> FlagField;

    public WorldGuardCompat() {
        boolean auxOldWG;
        boolean auxUseWG;
        auxOldWG = true;
        auxUseWG = VampireRevamp.getVampireConfig().general.useWorldGuardRegions;
        getRegionContainerMethod = null;
        getRegionManagerMethod = null;
        getApplicableRegionsMethod = null;
        FlagField = null;

        if (auxUseWG && !Bukkit.getServer().getPluginManager().isPluginEnabled("WorldGuard")) {
            VampireRevamp.log(Level.WARNING, "WorldGuard plugin not detected. Disabling WorldGuard hooks.");
            auxUseWG = false;
        }

        if (auxUseWG) {
            try {
                getRegionContainerMethod = WorldGuardPlugin.inst().getClass().getMethod("getRegionContainer");
                getRegionManagerMethod = Class.forName("com.sk89q.worldguard.bukkit.RegionContainer").getMethod("get", World.class);
                getApplicableRegionsMethod = RegionManager.class.getMethod("getApplicableRegions", Location.class);
                FlagField = Class.forName("com.sk89q.worldguard.protection.flags.DefaultFlag");
            } catch (Exception ex) {
                auxOldWG = false;
            }
        }

        oldWG = auxOldWG;
        if (auxUseWG && !oldWG) {
            try {
                getRegionContainerMethod = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getClass().getMethod("getRegionContainer");
                getRegionManagerMethod = com.sk89q.worldguard.protection.regions.RegionContainer.class.getMethod("get", com.sk89q.worldedit.world.World.class);
                getApplicableRegionsMethod = RegionManager.class.getMethod("getApplicableRegions", com.sk89q.worldedit.math.BlockVector3.class);
                FlagField = Flags.class;
            } catch (Exception ex) {
                ex.printStackTrace();
                auxUseWG = false;
            }
        }
        useWG = auxUseWG;
    }

    public Object getRegionContainer() {
        Object rc = null;

        try {
            if (!oldWG) {
                rc = getRegionContainerMethod.invoke(com.sk89q.worldguard.WorldGuard.getInstance().getPlatform());
            }
            else {
                rc = getRegionContainerMethod.invoke(WorldGuardPlugin.inst());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return rc;
    }

    public RegionManager getRegionManager(Object rc, World world) {
        RegionManager rm = null;

        try {
            Object data = world;

            if (!oldWG) {
                data = com.sk89q.worldedit.bukkit.BukkitAdapter.adapt(world);
            }

            rm = (RegionManager) getRegionManagerMethod.invoke(rc, data);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return rm;
    }

    public ApplicableRegionSet getApplicableRegions(RegionManager rm, Location loc) {
        ApplicableRegionSet ars = null;

        try {
            Object data = loc;

            if (!oldWG) {
                data = com.sk89q.worldedit.math.BlockVector3.at(loc.getX(), loc.getY(), loc.getZ());
            }

            ars = (ApplicableRegionSet) getApplicableRegionsMethod.invoke(rm, data);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return ars;
    }

    public Flag<?> getFlag(String flagName) {
        Flag<?> flag = null;

        try {
            flag = (Flag<?>) FlagField.getField(flagName).get(null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return flag;
    }
}
