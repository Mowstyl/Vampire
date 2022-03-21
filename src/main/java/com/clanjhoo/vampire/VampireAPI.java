package com.clanjhoo.vampire;

import com.clanjhoo.vampire.entity.UPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class VampireAPI {
    public static boolean isHealthy(@NotNull Player player) {
        UPlayer uplayer = VampireRevamp.getPlayerCollection().getDataNow(new Serializable[]{player.getUniqueId()});
        return uplayer.isHealthy();
    }

    public static boolean isUnhealthy(@NotNull Player player) {
        UPlayer uplayer = VampireRevamp.getPlayerCollection().getDataNow(new Serializable[]{player.getUniqueId()});
        return uplayer.isUnhealthy();
    }

    public static boolean isHuman(@NotNull Player player) {
        UPlayer uplayer = VampireRevamp.getPlayerCollection().getDataNow(new Serializable[]{player.getUniqueId()});
        return uplayer.isHuman();
    }

    public static boolean isInfected(@NotNull Player player) {
        UPlayer uplayer = VampireRevamp.getPlayerCollection().getDataNow(new Serializable[]{player.getUniqueId()});
        return uplayer.isInfected();
    }

    public static boolean isVampire(@NotNull Player player) {
        UPlayer uplayer = VampireRevamp.getPlayerCollection().getDataNow(new Serializable[]{player.getUniqueId()});
        return uplayer.isVampire();
    }

    public static boolean isNosferatu(@NotNull Player player) {
        UPlayer uplayer = VampireRevamp.getPlayerCollection().getDataNow(new Serializable[]{player.getUniqueId()});
        return uplayer.isNosferatu();
    }
}
