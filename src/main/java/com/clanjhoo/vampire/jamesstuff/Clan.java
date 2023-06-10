package com.clanjhoo.vampire.jamesstuff;

import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.HashMap;

public class Clan {

    private String clanName;
    private ArrayList<PotionEffect> potionEffects = new ArrayList<>();

    public Clan(String clanName) {
        this.clanName = clanName;
        potionEffects = new ArrayList<>();
    }

    public String getClanName() {
        return clanName;
    }

    public void addPotionEffect(PotionEffect e) {
        potionEffects.add(e);
    }

    public ArrayList<PotionEffect> getPotionEffects() {
        return potionEffects;
    }
}
