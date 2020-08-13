package com.clanjhoo.vampire.config;

import com.clanjhoo.vampire.VampireRevamp;
import com.clanjhoo.vampire.entity.UPlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventPriority;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nullable;
import java.io.BufferedWriter;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Level;

public class StateEffectConfig implements Comparable<StateEffectConfig>{
    public final EventPriority priority;
    public final Map<PotionEffectType, Integer> effectToStrength;
    public Function<UPlayer, Boolean> passesChecks;

    public StateEffectConfig(EventPriority priority, Map<PotionEffectType, Integer> effectToStrength) {
        this.priority = priority;
        this.effectToStrength = effectToStrength;
    }

    protected boolean saveConfigToFile(BufferedWriter configWriter, String indent, int level) {
        boolean result = PluginConfig.writeLine(configWriter, "# The higher the priority, the later this effects will be applied (therefore overriding lower priority effects when sharing any potion effect)", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "priority: " + this.priority, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Name of an effect: Amplifier", indent, level);
        result = result && PluginConfig.writeMap(configWriter, "effectToStrength:", this.effectToStrength, indent, level);

        return result;
    }

    public StateEffectConfig getStateEffectConfig(@Nullable ConfigurationSection cs) { // Takes "this" as the default configuration
        StateEffectConfig result = this;
        if (cs != null) {
            String aux = cs.getString("priority");
            EventPriority epri = null;
            if (aux != null) {
                try {
                    epri = EventPriority.valueOf(aux.toUpperCase());
                } catch (IllegalArgumentException ex) {
                    VampireRevamp.log(Level.WARNING, "EventPriority " + aux + " doesn't exist!");
                }
            }
            epri = epri != null ? epri : this.priority;
            List<Map<?, ?>> auxLES = cs.getMapList("effectToStrength");
            Map<PotionEffectType, Integer> mes = null;
            if (cs.contains("effectToStrength")) {
                mes = new HashMap<>();
                for (Map<?, ?> minimap : auxLES) {
                    for (Map.Entry<?, ?> entry : minimap.entrySet()) {
                        PotionEffectType pet = PotionEffectType.getByName((String) entry.getKey());
                        int strength = (Integer) entry.getValue();

                        if (pet != null)
                            mes.put(pet, strength);
                        else
                            VampireRevamp.log(Level.WARNING, "PotionEffectType " + entry.getKey() + " doesn't exist!");
                    }
                }
            }
            else {
                mes = this.effectToStrength;
            }
            result = new StateEffectConfig(epri, mes);

            result.passesChecks = this.passesChecks;
        }

        return result;
    }

    @Override
    public int compareTo(StateEffectConfig o) {
        return this.priority.compareTo(o.priority);
    }

    // -------------------------------------------- //
    // UTIL
    // -------------------------------------------- //

    public void addPotionEffects(LivingEntity entity, int targetDuration)
    {
        for (Map.Entry<PotionEffectType, Integer> entry : effectToStrength.entrySet()) {
            if (entity.hasPotionEffect(entry.getKey()))
                entity.removePotionEffect(entry.getKey());
            entity.addPotionEffect(new PotionEffect(entry.getKey(), targetDuration, entry.getValue(), true, false));
        }
    }

    public void removePotionEffects(LivingEntity entity)
    {
        for (Map.Entry<PotionEffectType, Integer> entry : effectToStrength.entrySet()) {
            if (entity.hasPotionEffect(entry.getKey()))
                entity.removePotionEffect(entry.getKey());
        }
    }

    @Override
    public String toString() {
        return "StateEffectConfig{" +
                "priority=" + priority +
                ", effectToStrength=" + effectToStrength +
                ", passesChecks=" + passesChecks +
                '}';
    }
}
