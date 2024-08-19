package com.clanjhoo.vampire;

import com.clanjhoo.vampire.entity.VPlayer;
import com.clanjhoo.vampire.util.RingUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class VampireAPI {
    public static CompletableFuture<Boolean> isHealthy(@NotNull Player player) {
        return VampireRevamp.syncTaskVPlayer(player, null, null)
                .thenApply((VPlayer::isHealthy));
    }

    public static CompletableFuture<Boolean> isUnhealthy(@NotNull Player player) {
        return VampireRevamp.syncTaskVPlayer(player, null, null)
                .thenApply((VPlayer::isUnhealthy));
    }

    public static CompletableFuture<Boolean> isHuman(@NotNull Player player) {
        return VampireRevamp.syncTaskVPlayer(player, null, null)
                .thenApply((VPlayer::isHuman));
    }

    public static CompletableFuture<Boolean> isInfected(@NotNull Player player) {
        return VampireRevamp.syncTaskVPlayer(player, null, null)
                .thenApply((VPlayer::isInfected));
    }

    public static CompletableFuture<Boolean> isVampire(@NotNull Player player) {
        return VampireRevamp.syncTaskVPlayer(player, null, null)
                .thenApply((VPlayer::isVampire));
    }

    public static CompletableFuture<Boolean> isNosferatu(@NotNull Player player) {
        return VampireRevamp.syncTaskVPlayer(player, null, null)
                .thenApply((VPlayer::isNosferatu));
    }

    public static boolean isSunRing(@NotNull ItemStack item) {
        return RingUtil.isSunRing(item);
    }
}
