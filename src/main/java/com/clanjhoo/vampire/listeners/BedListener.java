package com.clanjhoo.vampire.listeners;
import co.aikar.commands.MessageType;
import com.clanjhoo.vampire.VampireRevamp;
import com.clanjhoo.vampire.entity.VPlayer;
import com.clanjhoo.vampire.keyproviders.VampirismMessageKeys;
import com.clanjhoo.vampire.util.EntityUtil;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.Set;

public class BedListener implements Listener {
    private final Set<Player> sleepers = new HashSet<>();
    private final Set<Player> wakeUp = new HashSet<>();

    // net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket
    public final PacketListener leaveButtonListener = new PacketAdapter(
            VampireRevamp.getInstance(),
            ListenerPriority.MONITOR,
            PacketType.Play.Client.ENTITY_ACTION)
    {
        @Override
        public void onPacketReceiving(PacketEvent event) {
            EnumWrappers.PlayerAction action = event.getPacket().getPlayerActions().read(0);
            if (action != EnumWrappers.PlayerAction.STOP_SLEEPING)
                return;

            Player player = event.getPlayer();
            wakeUp.add(player);
        }
    };

    // -------------------------------------------- //
    // SLEEP
    // -------------------------------------------- //

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void trySleep(PlayerBedEnterEvent event) {
        if (VampireRevamp.getVampireConfig().general.isBlacklisted(event.getBed().getWorld()))
            return;
        Player player = event.getPlayer();
        World world = event.getBed().getWorld();
        // If day sleeping is allowed for vampires ...
        if (!EntityUtil.isPlayer(player))
            return;

        // ... the player is a vampire ...
        VPlayer vPlayer = VampireRevamp.getVPlayerNow(player);
        if (vPlayer == null || !vPlayer.isVampire())
            return;
        // ... and tries to sleep at night ...
        long time = world.getTime();
        if (time >= 12000) {
            // ... we cancel
            VampireRevamp.sendMessage(event.getPlayer(),
                    MessageType.INFO,
                    VampirismMessageKeys.CANT_SLEEP);
            event.setCancelled(true);
        }
        else {
            if (!((Bed) event.getBed().getBlockData()).isOccupied()) {
                event.setUseBed(Event.Result.ALLOW);
                event.setCancelled(false);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDisconnect(PlayerQuitEvent event) {
        sleepers.remove(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void wakeUp(PlayerBedLeaveEvent event) {
        Player player = event.getPlayer();
        World world = event.getBed().getWorld();
        if (!EntityUtil.isPlayer(player))
            return;
        // If the player has pressed the Leave Bed button we leave
        if (wakeUp.contains(player)) {
            sleepers.remove(player);
            wakeUp.remove(player);
            return;
        }

        // If day sleeping is allowed for vampires in this world ...
        if (VampireRevamp.getVampireConfig().general.isBlacklisted(world)) {
            sleepers.remove(player);
            return;
        }

        long time = world.getTime();
        VPlayer vPlayer = VampireRevamp.getVPlayerNow(player);
        // ... the player is not a vampire ...
        if (vPlayer == null || !vPlayer.isVampire()) {
            // ... we exit
            sleepers.remove(player);
            return;
        }

        if (player.isDeeplySleeping()) {
            sleepers.add(player);
        }
        long worldPlayerCount = world.getPlayers().stream().filter((p) -> EntityUtil.isPlayer(p) && !p.isSleepingIgnored()).count();
        Integer percentage = player.getWorld().getGameRuleValue(GameRule.PLAYERS_SLEEPING_PERCENTAGE);
        if (percentage == null) {
            percentage = player.getWorld().getGameRuleDefault(GameRule.PLAYERS_SLEEPING_PERCENTAGE);
        }
        if (percentage == null) {
            percentage = 100;
        }
        long sleepingPlayers = sleepers.stream().filter((p) -> p.isValid() && p.isOnline() && !p.isDead() && p.getWorld().equals(world)).count();

        // Enough people sleeping
        if (time < 12000 && sleepingPlayers >= (percentage / 100.0) * worldPlayerCount) {
            // ... and the daylight cycle is enabled ...
            Boolean doDayLightCycle = player.getWorld().getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE);
            if (doDayLightCycle == null) {
                doDayLightCycle = player.getWorld().getGameRuleDefault(GameRule.DO_DAYLIGHT_CYCLE);
            }
            if (!Boolean.FALSE.equals(doDayLightCycle)) {
                // ... we set time to night
                //player.getWorld().setTime(11834);
                world.setTime(12000);
            }
        }
        // ... a vampire with not enough people sleeping ...
        else if (time < 12000) {
            // ... we cancel the wake-up event since no one asked for it
            event.setCancelled(true);
            return;
        }
        sleepers.remove(player);
    }
}
