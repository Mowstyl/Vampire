package com.clanjhoo.vampire.config;

import com.clanjhoo.vampire.VampireRevamp;
import com.clanjhoo.vampire.util.CollectionUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;


public class RadiationConfig {
    public final double opacityPerArmorPiece;
    public final double baseRadiation;
    public final double tempPerRadAndMilli;
    public final RadiationEffectConfig removeBuffs;
    public final List<RadiationEffectConfig> effects;
    public final RadiationEffectConfig burn;
    public final double smokesPerTempAndMilli;
    public final double flamesPerTempAndMilli;
    public final boolean radiationRingEnabled;
    private final VampireRevamp plugin;


    public RadiationConfig(VampireRevamp plugin) {
        this.plugin = plugin;
        opacityPerArmorPiece = 0.125;
        baseRadiation = -0.2;
        tempPerRadAndMilli = 0.0001;
        removeBuffs = new RadiationEffectConfig(true, 0.2, true);
        effects = CollectionUtil.list(
                new RadiationEffectConfig(plugin.getVersionCompat().getNauseaEffect(), 0, 0.2, 200, false),
                new RadiationEffectConfig(PotionEffectType.WEAKNESS, 1, 0.3, 200, false),
                new RadiationEffectConfig(PotionEffectType.WEAKNESS, 0, 0.3, 200, true),
                new RadiationEffectConfig(plugin.getVersionCompat().getSlownessEffect(), 0, 0.5, 200, false),
                new RadiationEffectConfig(PotionEffectType.BLINDNESS, 0, 0.8, 200, false)
        );
        burn = new RadiationEffectConfig(true, 0.9, 60, true);
        smokesPerTempAndMilli = 0.012;
        flamesPerTempAndMilli = 0.0004;

        radiationRingEnabled = false;
    }

    public RadiationConfig(VampireRevamp plugin, @NotNull ConfigurationSection cs) {
        this.plugin = plugin;
        RadiationConfig def = new RadiationConfig(plugin);

        opacityPerArmorPiece = cs.getDouble("opacityPerArmorPiece", def.opacityPerArmorPiece);
        baseRadiation = cs.getDouble("baseRadiation", def.baseRadiation);
        tempPerRadAndMilli = cs.getDouble("tempPerRadAndMilli", def.tempPerRadAndMilli);
        boolean remEnabled = cs.getBoolean("removeBuffs.enabled", def.removeBuffs.enabled);
        double remTemp = cs.getDouble("removeBuffs.temperature", def.removeBuffs.temperature);
        boolean remNosf = cs.getBoolean("removeBuffs.affectNosferatu", def.removeBuffs.affectNosferatu);
        removeBuffs = new RadiationEffectConfig(remEnabled, remTemp, remNosf);
        radiationRingEnabled = cs.getBoolean("radiationRingEnabled", def.radiationRingEnabled);

        List<Map<?, ?>> auxLEff;
        List<RadiationEffectConfig> effs = null;
        if (cs.contains("effects")) {
            auxLEff = cs.getMapList("effects");
            effs = new LinkedList<>();
            for (Map<?, ?> minimap : auxLEff) {
                PotionEffectType type = null;
                Integer str = null;
                Double temp = null;
                Integer ticks = null;
                Boolean an = null;

                label:
                for (Map.Entry<?, ?> entry : minimap.entrySet()) {
                    String key = (String) entry.getKey();
                    switch (key) {
                        case "type":
                            String typeName = (String) entry.getValue();
                            type = PotionEffectType.getByName(typeName);

                            if (type == null) {
                                plugin.log(Level.WARNING, "PotionEffectType " + typeName + " doesn't exist!");
                                break label;
                            }
                            break;
                        case "strength":
                            str = (Integer) entry.getValue();
                            break;
                        case "temperature":
                            temp = (Double) entry.getValue();

                            if (temp < 0 || temp > 1) {
                                plugin.log(Level.WARNING, "Temperature must be between 0 and 1 doesn't exist!");
                                temp = null;
                                break label;
                            }
                            break;
                        case "ticks":
                            ticks = (Integer) entry.getValue();

                            if (ticks < 1) {
                                plugin.log(Level.WARNING, "Ticks must be positive non 0!");
                                ticks = null;
                                break label;
                            }
                            break;
                        case "affectNosferatu":
                            an = (Boolean) entry.getValue();
                            break;
                    }
                }

                if (type == null || temp == null || ticks == null || an == null) {
                    plugin.log(Level.WARNING, "You have to specify a valid type, temperature, ticks and affectNosferatu for all effects!");
                }
                else {
                    if (str == null)
                        str = 0;
                    effs.add(new RadiationEffectConfig(type, str, temp, ticks, an));
                }
            }
        }
        effects = effs != null ? effs : def.effects;

        remEnabled = cs.getBoolean("burn.enabled", def.burn.enabled);
        remTemp = cs.getDouble("burn.temperature", def.burn.temperature);
        int remTicks = cs.getInt("burn.ticks", def.burn.ticks);
        remNosf = cs.getBoolean("burn.affectNosferatu", def.burn.affectNosferatu);
        burn = new RadiationEffectConfig(remEnabled, remTemp, remTicks, remNosf);
        smokesPerTempAndMilli = cs.getDouble("smokesPerTempAndMilli", def.smokesPerTempAndMilli);
        flamesPerTempAndMilli = cs.getDouble("flamesPerTempAndMilli", def.flamesPerTempAndMilli);
    }

    protected boolean saveConfigToFile(BufferedWriter configWriter, String indent, int level) {
        boolean result = PluginConfig.writeLine(configWriter, "# Opacity added by each piece of armor worn by a player", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "opacityPerArmorPiece: " + this.opacityPerArmorPiece, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Radiation value when Sun is totally blocked. Higher negative values make the temperature decrease faster when not under the sun. Positive values is like setting eternal sunlight everywhere", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "baseRadiation: " + this.baseRadiation, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Temperature added per radiation point each millisecond", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "tempPerRadAndMilli: " + this.tempPerRadAndMilli, indent, level);

        List<String> auxData = removeBuffs.getData();
        result = result && PluginConfig.writeLine(configWriter, "# Buffs will be removed from the specified types of vampires when specified temperature is reached", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "removeBuffs:", indent, level);
        for (String auxDatum : auxData)
            result = result && PluginConfig.writeLine(configWriter, auxDatum, indent, level + 1);

        result = result && PluginConfig.writeLine(configWriter, "effects:", indent, level);
        for (RadiationEffectConfig effectConfig : effects) {
            auxData = effectConfig.getData();
            result = result && PluginConfig.writeLine(configWriter, "- " + auxData.get(0), indent, level + 1);
            for (int i = 1; i < auxData.size(); i++)
                result = result && PluginConfig.writeLine(configWriter, "  " + auxData.get(i), indent, level + 1);
        }

        auxData = burn.getData();
        result = result && PluginConfig.writeLine(configWriter, "# Vampires will start burning when specified temperature is reached", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "burn:", indent, level);
        for (String auxDatum : auxData)
            result = result && PluginConfig.writeLine(configWriter, auxDatum, indent, level + 1);

        result = result && PluginConfig.writeLine(configWriter, "smokesPerTempAndMilli: " + this.smokesPerTempAndMilli, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "flamesPerTempAndMilli: " + this.flamesPerTempAndMilli, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Whether or not to enable anti-radiation rings", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "radiationRingEnabled: " + this.radiationRingEnabled, indent, level);

        return result;
    }

    @Override
    public String toString() {
        return "RadiationConfig{" +
                "opacityPerArmorPiece=" + opacityPerArmorPiece +
                ", baseRadiation=" + baseRadiation +
                ", tempPerRadAndMilli=" + tempPerRadAndMilli +
                ", removeBuffs=" + removeBuffs +
                ", effects=" + effects +
                ", burn=" + burn +
                ", smokesPerTempAndMilli=" + smokesPerTempAndMilli +
                ", flamesPerTempAndMilli=" + flamesPerTempAndMilli +
                ", radiationRingEnabled=" + radiationRingEnabled +
                '}';
    }
}
