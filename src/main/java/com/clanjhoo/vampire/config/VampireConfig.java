package com.clanjhoo.vampire.config;

import com.clanjhoo.vampire.VampireRevamp;
import com.clanjhoo.vampire.util.CollectionUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public class VampireConfig {
    public final Set<DamageCause> blockDamageFrom;
    public final Set<RegainReason> blockHealthFrom;
    public final BloodlustConfig bloodlust;
    public final NightvisionConfig nightvision;
    public final ShriekConfig shriek;
    public final IntendConfig intend;
    public final BatusiConfig batusi;
    public final RegenConfig regen;
    public final RegenConfig regenNosferatu;
    public final double damageFactor;
    public final int respawnFood;
    public final int respawnHealth;
    public final HolyItemConfig holyItem;
    public final boolean canEatCake;

    public VampireConfig() {
        blockDamageFrom = CollectionUtil.set(
                DamageCause.DROWNING,
                DamageCause.FALL,
                DamageCause.STARVATION
        );

        blockHealthFrom = CollectionUtil.set(
                RegainReason.SATIATED,
                RegainReason.REGEN
        );

        bloodlust = new BloodlustConfig();
        nightvision = new NightvisionConfig();
        shriek = new ShriekConfig();
        intend = new IntendConfig();
        batusi = new BatusiConfig();
        regen = new RegenConfig(false);
        regenNosferatu = new RegenConfig(true);
        damageFactor = 1;
        respawnFood = 20;
        respawnHealth = 20;
        holyItem = new HolyItemConfig();
        canEatCake = false;
    }

    public VampireConfig(@NotNull ConfigurationSection cs) {
        VampireConfig def = new VampireConfig();

        List<String> auxLDC = null;
        Set<DamageCause> auxSDC = new HashSet<>();
        if (cs.contains("blockDamageFrom")) {
            auxLDC = cs.getStringList("blockDamageFrom");
            for (String dcName : auxLDC) {
                try {
                    DamageCause aux = DamageCause.valueOf(dcName.toUpperCase());
                    auxSDC.add(aux);
                }
                catch (IllegalArgumentException ex) {
                    VampireRevamp.log(Level.WARNING, "DamageCause " + dcName + " doesn't exist!");
                }
            }
        }
        blockDamageFrom = auxLDC != null ? auxSDC : def.blockDamageFrom;

        List<String> auxLRR = null;
        Set<RegainReason> auxSRR = new HashSet<>();
        if (cs.contains("blockHealthFrom")) {
            auxLRR = cs.getStringList("blockHealthFrom");
            for (String rrName : auxLRR) {
                try {
                    RegainReason aux = RegainReason.valueOf(rrName);

                    auxSRR.add(aux);
                } catch (IllegalArgumentException ex) {
                    VampireRevamp.log(Level.WARNING, "RegainReason " + rrName + " doesn't exist!");
                }
            }
        }
        blockHealthFrom = auxLRR != null ? auxSRR : def.blockHealthFrom;

        ConfigurationSection aux;

        aux = cs.getConfigurationSection("bloodlust");
        if (aux != null)
            bloodlust = new BloodlustConfig(aux);
        else
            bloodlust = new BloodlustConfig();

        aux = cs.getConfigurationSection("nightvision");
        if (aux != null)
            nightvision = new NightvisionConfig(aux);
        else
            nightvision = new NightvisionConfig();

        aux = cs.getConfigurationSection("shriek");
        if (aux != null)
            shriek = new ShriekConfig(aux);
        else
            shriek = new ShriekConfig();

        aux = cs.getConfigurationSection("intend");
        if (aux != null)
            intend = new IntendConfig(aux);
        else
            intend = new IntendConfig();

        aux = cs.getConfigurationSection("batusi");
        if (aux != null)
            batusi = new BatusiConfig(aux);
        else
            batusi = new BatusiConfig();

        aux = cs.getConfigurationSection("regen");
        if (aux != null)
            regen = new RegenConfig(aux, false);
        else
            regen = new RegenConfig(false);

        aux = cs.getConfigurationSection("regenNosferatu");
        if (aux != null)
            regenNosferatu = new RegenConfig(aux, true);
        else
            regenNosferatu = new RegenConfig(true);

        damageFactor = cs.getDouble("damageFactor", def.damageFactor);

        respawnFood = cs.getInt("respawnFood", def.respawnFood);

        respawnHealth = cs.getInt("respawnFood", def.respawnHealth);

        aux = cs.getConfigurationSection("holyItem");
        if (aux != null)
            holyItem = new HolyItemConfig(aux);
        else
            holyItem = new HolyItemConfig();

        canEatCake = cs.getBoolean("canEatCake", def.canEatCake);
    }

    protected boolean saveConfigToFile(BufferedWriter configWriter, String indent, int level) {
        boolean result = PluginConfig.writeLine(configWriter, "# Damage from this sources will be blocked if the affected entity is a vampire", indent, level);
        result = result && PluginConfig.writeCollection(configWriter, "blockDamageFrom:",  this.blockDamageFrom, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Healing from this sources will be blocked if the affected entity is a vampire", indent, level);
        result = result && PluginConfig.writeCollection(configWriter, "blockHealthFrom:",  this.blockHealthFrom, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "bloodlust:", indent, level);
        result = result && this.bloodlust.saveConfigToFile(configWriter, indent, level + 1);
        result = result && PluginConfig.writeLine(configWriter, "nightvision:", indent, level);
        result = result && this.nightvision.saveConfigToFile(configWriter, indent, level + 1);
        result = result && PluginConfig.writeLine(configWriter, "shriek:", indent, level);
        result = result && this.shriek.saveConfigToFile(configWriter, indent, level + 1);
        result = result && PluginConfig.writeLine(configWriter, "intend:", indent, level);
        result = result && this.intend.saveConfigToFile(configWriter, indent, level + 1);
        result = result && PluginConfig.writeLine(configWriter, "batusi:", indent, level);
        result = result && this.batusi.saveConfigToFile(configWriter, indent, level + 1);
        result = result && PluginConfig.writeLine(configWriter, "regen:", indent, level);
        result = result && this.regen.saveConfigToFile(configWriter, indent, level + 1);
        result = result && PluginConfig.writeLine(configWriter, "regenNosferatu:", indent, level);
        result = result && this.regenNosferatu.saveConfigToFile(configWriter, indent, level + 1);
        result = result && PluginConfig.writeLine(configWriter, "# Damage multiplier applied to all attacks performed by a vampire player", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "damageFactor: " + this.damageFactor, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Food level when a vampire player is respawned", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "respawnFood: " + this.respawnFood, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Health level when a vampire player is respawned", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "respawnHealth: " + this.respawnHealth, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "holyItem:", indent, level);
        result = result && this.holyItem.saveConfigToFile(configWriter, indent, level + 1);
        result = result && PluginConfig.writeLine(configWriter, "# Whether vampires can eat cakes or not", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "canEatCake: " + this.canEatCake, indent, level);

        return result;
    }

    @Override
    public String toString() {
        return "VampireConfig{" +
                "blockDamageFrom=" + blockDamageFrom +
                ", blockHealthFrom=" + blockHealthFrom +
                ", bloodlust=" + bloodlust +
                ", nightvision=" + nightvision +
                ", shriek=" + shriek +
                ", intend=" + intend +
                ", batusi=" + batusi +
                ", regen=" + regen +
                ", regenNosferatu=" + regenNosferatu +
                ", damageFactor=" + damageFactor +
                ", respawnFood=" + respawnFood +
                ", respawnHealth=" + respawnHealth +
                ", holyItem=" + holyItem +
                ", canEatCake=" + canEatCake +
                '}';
    }
}
