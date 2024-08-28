package com.clanjhoo.vampire.altar;

import co.aikar.commands.MessageType;
import com.clanjhoo.vampire.InfectionReason;
import com.clanjhoo.vampire.keyproviders.AltarMessageKeys;
import com.clanjhoo.vampire.Perm;
import com.clanjhoo.vampire.VampireRevamp;
import com.clanjhoo.vampire.config.SingleAltarConfig;
import com.clanjhoo.vampire.entity.VPlayer;
import com.clanjhoo.vampire.util.FxUtil;
import com.clanjhoo.vampire.util.ResourceUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class AltarDark extends Altar {
    public AltarDark(VampireRevamp plugin) {
        SingleAltarConfig darkAltar = plugin.getVampireConfig().altar.darkAltar;
        this.coreMaterial = darkAltar.coreMaterial;
        this.materialCounts = darkAltar.buildMaterials;
        this.resources = darkAltar.activate;
        this.isDark = true;
        this.plugin = plugin;
        resUtil = new ResourceUtil(plugin);
    }

    @Override
    public boolean use(final VPlayer vPlayer, final Player player) {
        boolean success = false;
        watch(vPlayer, player);

        if (resUtil.hasPermission(player, Perm.ALTAR_DARK, true)) {
            plugin.sendMessage(player,
                    MessageType.INFO,
                    AltarMessageKeys.ALTAR_DARK_COMMON);
            FxUtil.ensure(PotionEffectType.BLINDNESS, player, 12 * 20);
            vPlayer.runFxSmoke();

            if (vPlayer.isHealthy()) {
                if (resUtil.playerRemoveAttempt(player, this.resources, AltarMessageKeys.ACTIVATE_SUCCESS, AltarMessageKeys.ACTIVATE_FAIL)) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                        plugin.sendMessage(player,
                                MessageType.INFO,
                                AltarMessageKeys.ALTAR_DARK_HEALTHY);
                        player.getWorld().strikeLightningEffect(player.getLocation().add(0, 3, 0));
                        vPlayer.runFxSmokeBurst();
                        vPlayer.addInfection(0.01D, InfectionReason.ALTAR, (UUID) null);
                    }, 1);
                    success = true;
                }
            } else if (vPlayer.isVampire()) {
                plugin.sendMessage(player,
                        MessageType.INFO,
                        AltarMessageKeys.ALTAR_DARK_VAMPIRE);
            } else if (vPlayer.isInfected()) {
                plugin.sendMessage(player,
                        MessageType.INFO,
                        AltarMessageKeys.ALTAR_DARK_INFECTED);
            }
        }

        return success;
    }
}
