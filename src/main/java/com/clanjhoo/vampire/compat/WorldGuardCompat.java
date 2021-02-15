package com.clanjhoo.vampire.compat;

import com.clanjhoo.vampire.VampireRevamp;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;

public class WorldGuardCompat {
    public final boolean useWG;
    public final boolean oldWG;
    public final Object IRRADIATE_VAMPIRES_FLAG;
    private Method getRegionContainerMethod;
    private Method getRegionManagerMethod;
    private Method getApplicableRegionsMethod;
    private Method queryValue;
    private Method wrapPlayer;
    private Class<?> WorldGuard;
    private Class<?> WorldGuardPlugin;
    private Class<?> FlagField;
    private Class<?> RegionContainer;
    private Class<?> RegionManager;
    private Class<?> WorldGuardInstance;
    private Class<?> World;
    private Class<?> Vector;
    private Class<?> FlagRegistry;
    private Class<?> Flag;
    private Class<?> StateFlag;
    Class<?> RegionAssociable;
    Class<?> ApplicableRegionSet;
    private Object worldGuardInstance;
    private Object worldGuardPluginInstance;

    public WorldGuardCompat() {
        boolean auxOldWG;
        boolean auxUseWG;
        Object flag = null;
        worldGuardInstance = null;
        auxOldWG = false;
        auxUseWG = VampireRevamp.getVampireConfig().compatibility.useWorldGuardRegions;
        getRegionContainerMethod = null;
        getRegionManagerMethod = null;
        getApplicableRegionsMethod = null;
        FlagField = null;

        if (auxUseWG) {
            try {
                WorldGuardPlugin = Class.forName("com.sk89q.worldguard.bukkit.WorldGuardPlugin");
                worldGuardPluginInstance = WorldGuardPlugin.getMethod("inst").invoke(null);
                wrapPlayer = WorldGuardPlugin.getMethod("wrapPlayer", Player.class);
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException ignore) {}

            try {
                WorldGuard = Class.forName("com.sk89q.worldguard.WorldGuard");
            } catch (ClassNotFoundException ex) {
                auxOldWG = true;
            }

            if (auxOldWG) {
                    WorldGuard = WorldGuardPlugin;
            }

            auxUseWG = WorldGuard != null && WorldGuardPlugin != null;
        }

        oldWG = auxOldWG;

        if (auxUseWG) {
            try {
                if (oldWG) {
                        World = org.bukkit.World.class;
                        Vector = org.bukkit.Location.class;
                        WorldGuardInstance = WorldGuard;
                        RegionContainer = Class.forName("com.sk89q.worldguard.bukkit.RegionContainer");
                        FlagField = Class.forName("com.sk89q.worldguard.protection.flags.DefaultFlag");
                }
                else {
                        World = Class.forName("com.sk89q.worldedit.world.World");
                        Vector = Class.forName("com.sk89q.worldedit.math.BlockVector3");
                        WorldGuardInstance = Class.forName("com.sk89q.worldguard.internal.platform.WorldGuardPlatform");
                        RegionContainer = Class.forName("com.sk89q.worldguard.protection.regions.RegionContainer");
                        FlagField = Class.forName("com.sk89q.worldguard.protection.flags.Flags");
                }
            } catch (ClassNotFoundException ex) {
                VampireRevamp.log(Level.WARNING, "Error getting classes from WorldGuard.");
                ex.printStackTrace();
                auxUseWG = false;
            }
        }

        if (auxUseWG) {
            try {
                StateFlag = Class.forName("com.sk89q.worldguard.protection.flags.StateFlag");
                Flag = Class.forName("com.sk89q.worldguard.protection.flags.Flag");
                RegionAssociable = Class.forName("com.sk89q.worldguard.protection.association.RegionAssociable");
                ApplicableRegionSet = Class.forName("com.sk89q.worldguard.protection.ApplicableRegionSet");

                Constructor<?> NewStateFlag = StateFlag.getConstructor(String.class, boolean.class);
                flag = NewStateFlag.newInstance("irradiate-vampires", true);
                FlagRegistry = Class.forName("com.sk89q.worldguard.protection.flags.registry.FlagRegistry");
                RegionManager = Class.forName("com.sk89q.worldguard.protection.managers.RegionManager");
                getRegionContainerMethod = WorldGuardInstance.getMethod("getRegionContainer");
                getRegionManagerMethod = RegionContainer.getMethod("get", World);
                getApplicableRegionsMethod = RegionManager.getMethod("getApplicableRegions", Vector);
                queryValue = ApplicableRegionSet.getMethod("queryValue", RegionAssociable, Flag);

                Method getFlagRegistry = WorldGuard.getMethod("getFlagRegistry");

                Object instance;
                Method getInstance;
                if (oldWG) {
                    getInstance = WorldGuard.getMethod("inst");
                }
                else {
                    getInstance = WorldGuard.getMethod("getInstance");
                }
                instance = getInstance.invoke(null);

                Object registry = getFlagRegistry.invoke(instance);
                Method register = FlagRegistry.getMethod("register", Flag);

                try {
                    register.invoke(registry, flag);
                } catch (RuntimeException ex) {
                    Class<?> FlagConflictException = Class.forName("com.sk89q.worldguard.protection.flags.registry.FlagConflictException");
                    if (FlagConflictException.isInstance(ex)) {
                        Method get = FlagRegistry.getMethod("get", String.class);
                        Object existing = get.invoke(registry, "irradiate-vampires");
                        if (StateFlag.isInstance(existing)) {
                            VampireRevamp.log(Level.WARNING, "Other plugin created the irradiation flag! Trying to use it");
                            flag = existing;
                        } else {
                            // types don't match - this is bad news! some other plugin conflicts with you
                            // hopefully this never actually happens
                            VampireRevamp.log(Level.SEVERE, "Other plugin created the irradiation flag! Cannot solve it!");
                            flag = null;
                        }
                    }
                    else {
                        throw ex;
                    }
                }
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException ex) {
                VampireRevamp.log(Level.WARNING, "Error getting flags from WorldGuard.");
                auxUseWG = false;
                ex.printStackTrace();
            }
            VampireRevamp.log(Level.INFO, "WorldGuard hooks enabled.");
        }
        else {
            VampireRevamp.log(Level.WARNING, "WorldGuard plugin not detected. Disabling WorldGuard hooks.");
        }

        IRRADIATE_VAMPIRES_FLAG = flag;
        useWG = auxUseWG;
    }

    public Object getWorldGuardInstance() {
        if (useWG) {
            if (worldGuardInstance == null) {
                try {
                    if (!oldWG) {
                        Method getInstance = WorldGuard.getMethod("getInstance");
                        Method getPlatform = WorldGuard.getMethod("getPlatform");
                        Object wginst = getInstance.invoke(null);
                        worldGuardInstance = getPlatform.invoke(wginst);
                    } else {
                        Method inst = WorldGuard.getMethod("inst");
                        worldGuardInstance = inst.invoke(null);
                    }
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
                    VampireRevamp.log(Level.WARNING, "Error getting WorldGuard instance");
                    ex.printStackTrace();
                }
            }
        }

        return worldGuardInstance;
    }

    public Object getRegionContainer() {
        Object rc = null;

        if (useWG) {
            try {
                rc = getRegionContainerMethod.invoke(getWorldGuardInstance());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return rc;
    }

    public Object getRegionManager(Object rc, org.bukkit.World world) {
        Object rm = null;

        if (useWG) {
            try {
                Object data = world;

                if (!oldWG) {
                    try {
                        Class<?> BukkitAdapter = Class.forName("com.sk89q.worldedit.bukkit.BukkitAdapter");
                        Method adapt = BukkitAdapter.getMethod("adapt", org.bukkit.World.class);
                        data = adapt.invoke(null, world);
                    }
                    catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
                        VampireRevamp.log(Level.WARNING, "Error while using BukkitAdapter. Please send the plugin developers a message with the current WorldGuard and WorldEdit version.");
                    }
                }

                rm = getRegionManagerMethod.invoke(rc, data);
            } catch (IllegalAccessException | InvocationTargetException ex) {
                ex.printStackTrace();
            }
        }

        return rm;
    }
    public Object getApplicableRegions(Object regionManager, Location loc) {
        Object ars = null;

        if (useWG) {
            try {
                Object data = loc;

                if (!oldWG) {
                    Method at = Vector.getMethod("at", double.class, double.class, double.class);
                    data = at.invoke(null, loc.getX(), loc.getY(), loc.getZ());
                }

                ars = getApplicableRegionsMethod.invoke(regionManager, data);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return ars;
    }

    public Object getFlag(String flagName) {
        Object flag = null;

        if (useWG) {
            try {
                flag = FlagField.getField(flagName).get(null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return flag;
    }

    public Object queryFlag(Player player, Object flag) {
        Object result = null;

        if (useWG) {
            try {
                Object rm = getRegionManager(getRegionContainer(), player.getWorld());
                Object ars = getApplicableRegions(rm, player.getLocation());
                result = queryValue.invoke(ars, wrapPlayer(player), flag);
            } catch (InvocationTargetException | IllegalAccessException ex) {
                VampireRevamp.log(Level.WARNING, "Error querying flags");
                ex.printStackTrace();
            }
        }

        return result;
    }

    public Object wrapPlayer(Player player) {
        Object result = null;

        if (useWG) {
            try {
                result = wrapPlayer.invoke(worldGuardPluginInstance, player);
            } catch (InvocationTargetException | IllegalAccessException ignore) {}
        }

        return result;
    }
}
