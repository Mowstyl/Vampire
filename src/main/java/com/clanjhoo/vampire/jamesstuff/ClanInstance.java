package com.clanjhoo.vampire.jamesstuff;

import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;

public class ClanInstance {

    private static ClanInstance instance;
    private HashMap<String, Clan> clans;
    private HashMap<String, PlayerClanHandler> instances;

    public ClanInstance() {
        instances = new HashMap<>();
        makeClans();
    }

    public static ClanInstance getInstance() {
        if (instance == null) instance = new ClanInstance();
        return instance;
    }

    public Clan getClanFromName(String name) {
        return clans.get(name);
    }

    public ArrayList<PlayerClanHandler> getInstances() {
        return new ArrayList<>(instances.values());
    }

    public PlayerClanHandler getPlayerInfo(String uuid) {
        if (!instances.containsKey(uuid)) {
            PlayerClanHandler handle = new PlayerClanHandler(uuid, getClanFromName("Blood"));
            instances.put(uuid, handle);
        }
        return instances.get(uuid);
    }

    private void makeClans() {
        clans = new HashMap<>();
        Clan blood = new Clan("Blood");
        clans.put("Blood", blood);
        Clan shadow = new Clan("Shadow");
        clans.put("Shadow", shadow);
    }

    public void reload() {
        instances = ClanConfigHandler.getInstance().refreshData();
    }
}
