package com.clanjhoo.vampire.config;

import com.clanjhoo.dbhandler.data.StorageType;
import com.clanjhoo.vampire.VampireRevamp;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.util.logging.Level;

public class StorageConfig {
    public final StorageType storageType;
    public final String address;
    public final int port;
    public final String database;
    public final String username;
    public final String password;
    public final String prefix;

    public StorageConfig() {
        storageType = StorageType.JSON;
        address = "";
        port = 3306;
        database = "";
        username = "";
        password = "";
        prefix = "vampire_";
    }

    public StorageConfig(@NotNull ConfigurationSection cs) {
        StorageConfig def = new StorageConfig();

        String driver = cs.getString("driver");
        if (driver == null || driver.equalsIgnoreCase("JSON")) {
            storageType = StorageType.JSON;
        }
        else if (driver.equalsIgnoreCase("MYSQL")) {
            storageType = StorageType.MYSQL;
        }
        else if (driver.equalsIgnoreCase("MARIADB")) {
            storageType = StorageType.MARIADB;
        }
        else {
            VampireRevamp.log(Level.WARNING, "StorageType " + driver + " doesn't exist! Defaulting to json");
            storageType = StorageType.JSON;
        }
        address = cs.getString("address", def.address);
        port = cs.getInt("port", def.port);
        database = cs.getString("database", def.database);
        username = cs.getString("username", def.username);
        password = cs.getString("password", def.password);
        prefix = cs.getString("prefix", def.prefix);
    }

    protected boolean saveConfigToFile(BufferedWriter configWriter, String indent, int level) {
        boolean result = PluginConfig.writeLine(configWriter, "# Storage driver to use. Pick between json or mariadb (mysql also works)", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "driver: " + this.storageType.name().toLowerCase(), indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Address of the database server (not used with json)", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "address: " + this.address, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Port of the database server (not used with json)", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "port: " + this.port, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Name of the database (not used with json)", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "database: " + this.database, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Username to use for the database (not used with json)", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "username: " + this.username, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Password to use for the database (not used with json)", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "password: " + this.password, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Prefix that will be prepended to all table names (not used with json)", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "prefix: " + this.prefix, indent, level);

        return result;
    }

    @Override
    public String toString() {
        return "GeneralConfig{" +
                "driver=" + storageType.name().toLowerCase() +
                ", address=" + address +
                ", port=" + port +
                ", database=" + database +
                ", username=" + username +
                ", password=" + password +
                ", prefix=" + prefix +
                '}';
    }
}
