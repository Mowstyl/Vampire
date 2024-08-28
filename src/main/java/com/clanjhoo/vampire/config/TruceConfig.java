package com.clanjhoo.vampire.config;

import com.clanjhoo.vampire.VampireRevamp;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;


public class TruceConfig {
    public final int breakMillis;
    public final boolean checkGamemode;
    private final VampireRevamp plugin;


    public TruceConfig(VampireRevamp plugin) {
        this.plugin = plugin;
        breakMillis = 60000;
        checkGamemode = false;
    }

    public TruceConfig(VampireRevamp plugin, @NotNull ConfigurationSection cs) {
        this.plugin = plugin;
        TruceConfig def = new TruceConfig(plugin);

        breakMillis = cs.getInt("breakMillis", def.breakMillis);

        checkGamemode = cs.getBoolean("checkGamemode", def.checkGamemode);
    }

    protected boolean saveConfigToFile(BufferedWriter configWriter, String indent, int level) {
        boolean result = PluginConfig.writeLine(configWriter, "# Milliseconds that have to pass for the truce to be restored when broken", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "breakMillis: " + this.breakMillis, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Whether only survival players can break truce or not", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "checkGamemode: " + this.checkGamemode, indent, level);

        return result;
    }

    @Override
    public String toString() {
        return "TruceConfig{" +
                "breakMillis=" + breakMillis +
                '}';
    }
}
