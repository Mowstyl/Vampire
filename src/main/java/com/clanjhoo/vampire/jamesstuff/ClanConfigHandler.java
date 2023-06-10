package com.clanjhoo.vampire.jamesstuff;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ClanConfigHandler {

    private static ClanConfigHandler instance;

    public static ClanConfigHandler getInstance() {
        if (instance == null) instance = new ClanConfigHandler();
        return instance;
    }

    private Plugin server = Bukkit.getPluginManager().getPlugin("VampireRevamp");
    private File configFile = new File(server.getDataFolder(), "clansandlevel.yml");
    private FileConfiguration yaml;

    public ClanConfigHandler() {
        if (!configFile.exists()) server.saveResource("clansandlevel.yml", false);
        yaml = YamlConfiguration.loadConfiguration(configFile);
    }

    public HashMap<String, PlayerClanHandler> refreshData() {
        configFile = new File(server.getDataFolder(), "clansandlevel.yml");
        yaml = YamlConfiguration.loadConfiguration(configFile);
        HashMap<String, PlayerClanHandler> clanInstances = new HashMap<>();
        for(String cur : yaml.getConfigurationSection("players").getKeys(false)) {
            try {
                String currentLine = "players." + cur;
                PlayerClanHandler newClan = new PlayerClanHandler(cur, ClanInstance.getInstance().getClanFromName(yaml.getString(currentLine + ".clan")));
                clanInstances.put(cur, newClan);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return clanInstances;
    }

    public void saveData() {
        for (PlayerClanHandler current : ClanInstance.getInstance().getInstances()) {
            String currentPoint = "players." + current.getUuid() + ".";
            yaml.set(currentPoint + "clan", current.getClan().getClanName());
            yaml.set(currentPoint + "level", 1);
        }
        try {
            yaml.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
