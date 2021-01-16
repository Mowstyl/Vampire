package com.clanjhoo.vampire;

import com.clanjhoo.vampire.entity.UPlayer;
import com.clanjhoo.vampire.entity.UPlayerColl;
import org.bukkit.entity.Player;

public class VampireAPI {
    public static boolean isHealthy(Player player) {
        UPlayer uplayer = UPlayerColl.get(player.getUniqueId());
        return uplayer.isHealthy();
    }

    public static boolean isUnhealthy(Player player) {
        UPlayer uplayer = UPlayerColl.get(player.getUniqueId());
        return uplayer.isUnhealthy();
    }

    public static boolean isHuman(Player player) {
        UPlayer uplayer = UPlayerColl.get(player.getUniqueId());
        return uplayer.isHuman();
    }

    public static boolean isInfected(Player player) {
        UPlayer uplayer = UPlayerColl.get(player.getUniqueId());
        return uplayer.isInfected();
    }

    public static boolean isVampire(Player player) {
        UPlayer uplayer = UPlayerColl.get(player.getUniqueId());
        return uplayer.isVampire();
    }

    public static boolean isNosferatu(Player player) {
        UPlayer uplayer = UPlayerColl.get(player.getUniqueId());
        return uplayer.isNosferatu();
    }
}
