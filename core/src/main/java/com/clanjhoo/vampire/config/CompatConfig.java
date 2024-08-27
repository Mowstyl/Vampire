package com.clanjhoo.vampire.config;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;

public class CompatConfig {
    public final boolean useWorldGuardRegions;
    public final boolean damageWithMcmmo;
    public final boolean preventWerewolfHybrids;
    public final boolean useWerewolfSilverSword;
    public final double silverDamageFactor;
    public final boolean useVampirePermGroup;
    public final String vampirePermissionGroup;

    public CompatConfig() {
        useWorldGuardRegions = true;
        damageWithMcmmo = true;
        preventWerewolfHybrids = true;
        useWerewolfSilverSword = false;
        silverDamageFactor = 1.2;
        useVampirePermGroup = false;
        vampirePermissionGroup = "vampiregroup";
    }

    public CompatConfig(@NotNull ConfigurationSection cs) {
        CompatConfig def = new CompatConfig();

        useWorldGuardRegions = cs.getBoolean("useWorldGuardRegions", def.useWorldGuardRegions);
        damageWithMcmmo = cs.getBoolean("damageWithMcmmo", def.damageWithMcmmo);
        preventWerewolfHybrids = cs.getBoolean("preventWerewolfHybrids", def.preventWerewolfHybrids);
        useWerewolfSilverSword = cs.getBoolean("useWerewolfSilverSword", def.useWerewolfSilverSword);
        silverDamageFactor = cs.getDouble("silverDamageFactor", def.silverDamageFactor);
        useVampirePermGroup = cs.getBoolean("useVampirePermGroup", def.useVampirePermGroup);
        vampirePermissionGroup = cs.getString("vampirePermissionGroup", def.vampirePermissionGroup);
    }

    protected boolean saveConfigToFile(BufferedWriter configWriter, String indent, int level) {
        boolean result = PluginConfig.writeLine(configWriter, "# Whether to check time and weather WG flags or not when calculating the radiation", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "useWorldGuardRegions: " + this.useWorldGuardRegions, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Whether to scale the damage with MCMMO skills or not", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "damageWithMcmmo: " + this.damageWithMcmmo, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Whether to prevent vampirewolves or not (they might still be prevented by Werewolves plugin)", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "preventWerewolfHybrids: " + this.preventWerewolfHybrids, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Whether to check if a vampire has been hurt by a silver sword from Werewolves plugin or not", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "useWerewolfSilverSword: " + this.useWerewolfSilverSword, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# The damage multiplier when a vampire gets hit by a silver sword (only if the previous option was enabled)", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "silverDamageFactor: " + this.silverDamageFactor, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# (Vault needed) Whether to add vampires to a specified permission group or not", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "useVampirePermGroup: " + this.useVampirePermGroup, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# The permission group in which vampires will be put", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "vampirePermissionGroup: " + this.vampirePermissionGroup, indent, level);

        return result;
    }

    @Override
    public String toString() {
        return "CompatConfig{" +
                "useWorldGuardRegions=" + useWorldGuardRegions +
                ", damageWithMcmmo=" + damageWithMcmmo +
                ", preventWerewolfHybrids=" + preventWerewolfHybrids +
                ", useWerewolfSilverSword=" + useWerewolfSilverSword +
                ", silverDamageFactor=" + silverDamageFactor +
                ", useVampirePermGroup=" + useVampirePermGroup +
                ", vampirePermissionGroup=" + vampirePermissionGroup +
                '}';
    }
}
