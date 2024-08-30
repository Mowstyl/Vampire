package com.clanjhoo.vampire.config;

import com.clanjhoo.vampire.VampireRevamp;
import com.clanjhoo.vampire.compat.VersionCompat;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;


public class TruceConfig {
    public final int breakMillis;
    public final boolean checkGamemode;
    public final Set<EntityType> entityTypes;
    private final VampireRevamp plugin;


    public TruceConfig(VampireRevamp plugin) {
        this.plugin = plugin;
        breakMillis = 60000;
        checkGamemode = false;
        VersionCompat vc = plugin.getVersionCompat();
        entityTypes = Arrays.stream(EntityType.values())
                .filter(et -> !vc.isBoss(et) && (vc.isUndead(et) || vc.isSpirit(et)))
                .collect(Collectors.toSet());
    }

    public TruceConfig(VampireRevamp plugin, @NotNull ConfigurationSection cs) {
        this.plugin = plugin;
        TruceConfig def = new TruceConfig(plugin);

        breakMillis = cs.getInt("breakMillis", def.breakMillis);

        checkGamemode = cs.getBoolean("checkGamemode", def.checkGamemode);

        Set<EntityType> auxSEnts = null;
        if (cs.contains("entityTypes")) {
            List<String> auxLEnts = cs.getStringList("entityTypes");
            auxSEnts = new HashSet<>();
            for (String entName : auxLEnts) {
                try {
                    EntityType aux = plugin.getVersionCompat().getEntityTypeByName(entName);
                    auxSEnts.add(aux);
                }
                catch (IllegalArgumentException ex) {
                    plugin.log(Level.WARNING, "EntityType " + entName + " doesn't exist!");
                }
            }
        }

        entityTypes = auxSEnts != null ? auxSEnts : def.entityTypes;
    }

    protected boolean saveConfigToFile(BufferedWriter configWriter, String indent, int level) {
        boolean result = PluginConfig.writeLine(configWriter, "# Milliseconds that have to pass for the truce to be restored when broken", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "breakMillis: " + this.breakMillis, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Whether only survival players can break truce or not", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "checkGamemode: " + this.checkGamemode, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Entities in truce with vampire players", indent, level);
        result = result && PluginConfig.writeCollection(configWriter, "entityTypes:",  this.entityTypes, indent, level);

        return result;
    }

    @Override
    public String toString() {
        return "TruceConfig{" +
                "breakMillis=" + breakMillis +
                ", entityTypes=" + entityTypes +
                '}';
    }
}
