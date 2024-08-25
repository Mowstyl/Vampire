package com.clanjhoo.vampire;

import com.clanjhoo.vampire.entity.VPlayer;
import com.clanjhoo.vampire.util.RingUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;


public class VampireAPI {

    public static boolean isHealthy(@NotNull Player player) throws AssertionError {
        VPlayer vPlayer = VampireRevamp.getVPlayer(player);
        if (vPlayer == null) {
            throw new AssertionError("Data not loaded yet");
        }
        return vPlayer.isHealthy();
    }

    public static boolean isUnhealthy(@NotNull Player player) throws AssertionError {
        VPlayer vPlayer = VampireRevamp.getVPlayer(player);
        if (vPlayer == null) {
            throw new AssertionError("Data not loaded yet");
        }
        return vPlayer.isUnhealthy();
    }

    public static boolean isHuman(@NotNull Player player) throws AssertionError {
        VPlayer vPlayer = VampireRevamp.getVPlayer(player);
        if (vPlayer == null) {
            throw new AssertionError("Data not loaded yet");
        }
        return vPlayer.isHuman();
    }

    public static boolean isInfected(@NotNull Player player) throws AssertionError {
        VPlayer vPlayer = VampireRevamp.getVPlayer(player);
        if (vPlayer == null) {
            throw new AssertionError("Data not loaded yet");
        }
        return vPlayer.isInfected();
    }

    public static boolean isVampire(@NotNull Player player) throws AssertionError {
        VPlayer vPlayer = VampireRevamp.getVPlayer(player);
        if (vPlayer == null) {
            throw new AssertionError("Data not loaded yet");
        }
        return vPlayer.isVampire();
    }

    public static boolean isNosferatu(@NotNull Player player) throws AssertionError {
        VPlayer vPlayer = VampireRevamp.getVPlayer(player);
        if (vPlayer == null) {
            throw new AssertionError("Data not loaded yet");
        }
        return vPlayer.isNosferatu();
    }

    public static boolean isSunRing(@NotNull ItemStack item) {
        return RingUtil.isSunRing(item);
    }
}
