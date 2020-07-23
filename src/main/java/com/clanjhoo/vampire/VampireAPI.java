package com.clanjhoo.vampire;

import com.clanjhoo.vampire.entity.UPlayer;
import org.bukkit.entity.Player;

public class VampireAPI {
    public static boolean isHealthy(Player player) {
        UPlayer uplayer = UPlayer.get(player);

        return uplayer == null || uplayer.isHealthy();
    }

    public static boolean isUnhealthy(Player player) {
        UPlayer uplayer = UPlayer.get(player);

        return uplayer != null && uplayer.isUnhealthy();
    }

    public static boolean isHuman(Player player) {
        UPlayer uplayer = UPlayer.get(player);

        return uplayer == null || uplayer.isHuman();
    }

    public static boolean isInfected(Player player) {
        UPlayer uplayer = UPlayer.get(player);

        return uplayer != null && uplayer.isInfected();
    }

    public static boolean isVampire(Player player) {
        UPlayer uplayer = UPlayer.get(player);

        return uplayer != null && uplayer.isVampire();
    }

    public static boolean isNosferatu(Player player) {
        UPlayer uplayer = UPlayer.get(player);

        return uplayer != null && uplayer.isNosferatu();
    }
}
