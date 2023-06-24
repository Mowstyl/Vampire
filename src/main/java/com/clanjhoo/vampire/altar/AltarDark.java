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

public class AltarDark extends Altar {
    public AltarDark() {
        SingleAltarConfig darkAltar = VampireRevamp.getVampireConfig().altar.darkAltar;
        this.coreMaterial = darkAltar.coreMaterial;

        this.materialCounts = darkAltar.buildMaterials;

        this.resources = darkAltar.activate;

        this.isDark = true;
    }

    @Override
    public boolean use(final VPlayer uplayer, final Player player) {
        boolean success = false;
        player.sendMessage("");
        VampireRevamp.sendMessage(player,
                MessageType.INFO,
                AltarMessageKeys.ALTAR_DARK_DESC);

        if (Perm.ALTAR_DARK.has(player, true)) {
            VampireRevamp.sendMessage(player,
                    MessageType.INFO,
                    AltarMessageKeys.ALTAR_DARK_COMMON);
            FxUtil.ensure(PotionEffectType.BLINDNESS, player, 12 * 20);
            uplayer.runFxSmoke();

            if (uplayer.isHealthy()) {
                if (ResourceUtil.playerRemoveAttempt(player, this.resources, AltarMessageKeys.ACTIVATE_SUCCESS, AltarMessageKeys.ACTIVATE_FAIL)) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(VampireRevamp.getInstance(), () -> {
                        VampireRevamp.sendMessage(player,
                                MessageType.INFO,
                                AltarMessageKeys.ALTAR_DARK_HEALTHY);
                        player.getWorld().strikeLightningEffect(player.getLocation().add(0, 3, 0));
                        uplayer.runFxSmokeBurst();
                        uplayer.addInfection(0.01D, InfectionReason.ALTAR, null);
                    }, 1);
                    success = true;
                }
            } else if (uplayer.isVampire()) {
                VampireRevamp.sendMessage(player,
                        MessageType.INFO,
                        AltarMessageKeys.ALTAR_DARK_VAMPIRE);
            } else if (uplayer.isInfected()) {
                VampireRevamp.sendMessage(player,
                        MessageType.INFO,
                        AltarMessageKeys.ALTAR_DARK_INFECTED);
            }
        }

        return success;
    }
}
