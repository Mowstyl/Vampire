package com.clanjhoo.vampire.compat;

import com.clanjhoo.vampire.VampireRevamp;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.weather.WeatherType;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class WorldGuardCompat {
    private boolean useWG;
    public final StateFlag IRRADIATE_VAMPIRES_FLAG;
    public final StateFlag ENTRY_VAMPIRES_FLAG;
    private final WorldGuard worldGuardInstance;
    private final WorldGuardPlugin worldGuardPlugin;
    private final VampireRevamp plugin;

    public WorldGuardCompat(VampireRevamp plugin) {
        this.plugin = plugin;
        boolean auxUseWG = plugin.getVampireConfig().compatibility.useWorldGuardRegions;

        worldGuardInstance = WorldGuard.getInstance();
        worldGuardPlugin = WorldGuardPlugin.inst();
        IRRADIATE_VAMPIRES_FLAG = registerStateFlag("irradiate-vampires", true);
        ENTRY_VAMPIRES_FLAG = registerStateFlag("entry-vampires", true);
        useWG = auxUseWG && (ENTRY_VAMPIRES_FLAG != null || IRRADIATE_VAMPIRES_FLAG != null);

        plugin.log(Level.INFO, "WorldGuard hooks enabled.");
    }

    public boolean usingWG() {
        return useWG;
    }

    private StateFlag registerStateFlag(String flagName, boolean defaultValue) {
        StateFlag newFlag = new StateFlag(flagName, defaultValue);
        FlagRegistry registry = worldGuardInstance.getFlagRegistry();

        try {
            registry.register(newFlag);
        } catch (FlagConflictException ex) {
            Flag<?> existing = registry.get(flagName);
            if (existing instanceof StateFlag) {
                plugin.log(Level.WARNING, "Other plugin created the " + flagName + " flag! Trying to use it");
                newFlag = (StateFlag) existing;
            } else {
                // types don't match - this is bad news! some other plugin conflicts with you
                // hopefully this never actually happens
                plugin.log(Level.SEVERE, "Other plugin created the " + flagName + " flag! Cannot solve it!");
                newFlag = null;
            }
        }
        return newFlag;
    }

    private Boolean testFlag(Player player, Location location, StateFlag flag) {
        Boolean result = null;

        if (useWG) {
            RegionContainer rc = worldGuardInstance.getPlatform().getRegionContainer();
            RegionQuery rq = rc.createQuery();
            result = rq.testState(BukkitAdapter.adapt(location), worldGuardPlugin.wrapPlayer(player), flag);
        }

        return result;
    }

    private <V> V queryFlag(Player player, Location location, Flag<V> flag) {
        V result = null;

        if (useWG) {
            RegionContainer rc = worldGuardInstance.getPlatform().getRegionContainer();
            RegionQuery rq = rc.createQuery();
            result = rq.queryValue(BukkitAdapter.adapt(location), worldGuardPlugin.wrapPlayer(player), flag);
        }

        return result;
    }

    public boolean isSkyClear(Player player, Location location) {
        boolean clear = !location.getWorld().isThundering() && !location.getWorld().hasStorm();
        WeatherType qres = queryFlag(player, location, Flags.WEATHER_LOCK);
        if (qres != null) {
            clear = !qres.getName().equals(org.bukkit.WeatherType.DOWNFALL.name());
        }
        return clear;
    }

    public long getTime(Player player, Location location) {
        long rtime = location.getWorld().getTime();
        String aux = queryFlag(player, location, Flags.TIME_LOCK);

        if (aux != null) {
            if (!aux.contains(":")) {
                rtime = Long.parseLong(aux);
            }
            else {
                String[] rawTimes = aux.split(":");
                int h, m;
                double wtime, s = 0;
                h = Integer.parseInt(rawTimes[0]);
                m = Integer.parseInt(rawTimes[1]);
                if (rawTimes.length > 2) {
                    s = Double.parseDouble(rawTimes[2]);
                }
                h -= 6;  // Minecraft days start at 06:00
                if (h < 0) {
                    h += 24;
                }
                wtime = h + m * 60 + s * 3600;
                rtime = (long) (wtime * 1000);
            }
        }
        return rtime;
    }

    public boolean isIrradiationEnabled(Player player, Location location) {
        boolean irradiationEnabled = true;

        if (IRRADIATE_VAMPIRES_FLAG != null) {
            Boolean aux = testFlag(player, location, IRRADIATE_VAMPIRES_FLAG);
            if (aux != null) {
                irradiationEnabled = aux;
            }
        }

        return irradiationEnabled;
    }

    public boolean canVampiresEnter(Player player, Location location) {
        boolean canVampiresEnter = true;

        if (ENTRY_VAMPIRES_FLAG != null) {
            Boolean aux = testFlag(player, location, ENTRY_VAMPIRES_FLAG);
            if (aux != null) {
                canVampiresEnter = aux;
            }
        }

        return canVampiresEnter;
    }

    public void setEnabled(boolean enabled) {
        if (enabled && ENTRY_VAMPIRES_FLAG == null && IRRADIATE_VAMPIRES_FLAG == null) {
            plugin.log(Level.WARNING, "WorldGuard plugin not detected. Disabling WorldGuard hooks.");
            return;
        }
        useWG = enabled;
    }
}
