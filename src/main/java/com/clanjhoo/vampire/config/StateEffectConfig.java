package com.clanjhoo.vampire.config;

import com.clanjhoo.vampire.VampireRevamp;
import com.clanjhoo.vampire.entity.VPlayer;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventPriority;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedWriter;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Level;


public class StateEffectConfig implements Comparable<StateEffectConfig> {
    private final EventPriority priority;
    private final Map<PotionEffectType, Integer> effectToStrength;
    private Function<VPlayer, Boolean> passesChecks;
    private final VampireRevamp plugin;


    public StateEffectConfig(VampireRevamp plugin, EventPriority priority, Map<PotionEffectType, Integer> effectToStrength) {
        this.plugin = plugin;
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
                    plugin.log(Level.WARNING, "EventPriority " + aux + " doesn't exist!");
                }
            }
            epri = epri != null ? epri : this.priority;
            List<Map<?, ?>> auxLES = cs.getMapList("effectToStrength");
            Map<PotionEffectType, Integer> mes = null;
            if (cs.contains("effectToStrength")) {
                mes = new HashMap<>();
                for (Map<?, ?> minimap : auxLES) {
                    for (Map.Entry<?, ?> entry : minimap.entrySet()) {
                        PotionEffectType pet = plugin.getVersionCompat().getPotionEffectByName((String) entry.getKey());
                        int strength = (Integer) entry.getValue();

                        if (pet != null)
                            mes.put(pet, strength);
                        else
                            plugin.log(Level.WARNING, "PotionEffectType " + entry.getKey() + " doesn't exist!");
                    }
                }
            }
            else {
                mes = this.effectToStrength;
            }
            result = new StateEffectConfig(plugin, epri, mes);

            result.passesChecks = this.passesChecks;
        }

        return result;
    }

    public void setPassesChecks(Function<VPlayer, Boolean> passesChecks) {
        this.passesChecks = passesChecks;
    }

    public Function<VPlayer, Boolean> getPassesChecks() {
        return passesChecks;
    }

    @Override
    public int compareTo(StateEffectConfig o) {
        int val = this.priority.compareTo(o.priority);
        if (val != 0)
            return val;
        return toString().compareTo(o.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof StateEffectConfig))
            return false;
        return toString().equals(o.toString());
    }

    @Override
    public int hashCode() {
        return toString().hashCode() + 6470;
    }

    // -------------------------------------------- //
    // UTIL
    // -------------------------------------------- //

    public void addPotionEffects(LivingEntity entity, int targetDuration)
    {
        for (Map.Entry<PotionEffectType, Integer> entry : effectToStrength.entrySet()) {
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
