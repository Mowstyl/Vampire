package com.clanjhoo.vampire.config;

import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class RadiationEffectConfig {
    public final boolean enabled;
    public final PotionEffectType type;
    public final int strength;
    public final double temperature;
    public final int ticks;
    public final boolean affectNosferatu;

    public RadiationEffectConfig(boolean enabled, double temperature, boolean affectNosferatu) {
        this(enabled, null, 0, temperature, 0, affectNosferatu);
    }

    public RadiationEffectConfig(boolean enabled, double temperature, int ticks, boolean affectNosferatu) {
        this(enabled, null, 0, temperature, ticks, affectNosferatu);
    }

    public RadiationEffectConfig(PotionEffectType type, int strength, double temperature, int ticks, boolean affectNosferatu) {
        this(true, type, strength, temperature, ticks, affectNosferatu);
    }

    public RadiationEffectConfig(boolean enabled, PotionEffectType type, int strength, double temperature, int ticks, boolean affectNosferatu) {
        this.enabled = enabled;
        this.type = type;
        this.strength = strength;
        this.temperature = temperature;
        this.ticks = ticks;
        this.affectNosferatu = affectNosferatu;
    }

    protected List<String> getData() {
        List<String> data = new ArrayList<>();

        if (type == null) {
            data.add("# Whether or not to enable this feature");
            data.add("enabled: " + this.enabled);
        }
        else {
            data.add("# PotionEffectType");
            data.add("type: " + this.type.getName());
            data.add("# Amplifier");
            data.add("strength: " + this.strength);
        }
        data.add("# Temperature needed to be reached to apply this effect");
        data.add("temperature: " + this.temperature);
        if (ticks > 0) {
            data.add("# Duration of the effect in ticks");
            data.add("ticks: " + this.ticks);
        }
        if (type == null)
            data.add("# Whether or not this feature ALSO affects nosferatu vampires or only basic vampires");
        else
            data.add("# Whether this feature affects ONLY nosferatu vampires or affects ONLY basic vampires");
        data.add("affectNosferatu: " + this.affectNosferatu);

        return data;
    }

    @Override
    public String toString() {
        return "RadiationEffectConfig{" +
                "enabled=" + enabled +
                ", type=" + type +
                ", strength=" + strength +
                ", temperature=" + temperature +
                ", ticks=" + ticks +
                ", affectNosferatu=" + affectNosferatu +
                '}';
    }
}
