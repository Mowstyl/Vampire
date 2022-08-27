package com.clanjhoo.vampire;

import com.clanjhoo.vampire.entity.VPlayer;
import com.clanjhoo.vampire.util.RingUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class VampireAPI {
    public static boolean isHealthy(@NotNull Player player) {
        VPlayer uplayer = VampireRevamp.getVPlayerManager().tryGetDataNow(player.getUniqueId());
        return uplayer.isHealthy();
    }

    public static boolean isUnhealthy(@NotNull Player player) {
        VPlayer uplayer = VampireRevamp.getVPlayerManager().tryGetDataNow(player.getUniqueId());
        return uplayer.isUnhealthy();
    }

    public static boolean isHuman(@NotNull Player player) {
        VPlayer uplayer = VampireRevamp.getVPlayerManager().tryGetDataNow(player.getUniqueId());
        return uplayer.isHuman();
    }

    public static boolean isInfected(@NotNull Player player) {
        VPlayer uplayer = VampireRevamp.getVPlayerManager().tryGetDataNow(player.getUniqueId());
        return uplayer.isInfected();
    }

    public static boolean isVampire(@NotNull Player player) {
        VPlayer uplayer = VampireRevamp.getVPlayerManager().tryGetDataNow(player.getUniqueId());
        return uplayer.isVampire();
    }

    public static boolean isNosferatu(@NotNull Player player) {
        VPlayer uplayer = VampireRevamp.getVPlayerManager().tryGetDataNow(player.getUniqueId());
        return uplayer.isNosferatu();
    }

    public static boolean isSunRing(@NotNull ItemStack item) {
        return RingUtil.isSunRing(item);
    }
}
