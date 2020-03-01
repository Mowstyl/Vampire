package com.clanjhoo.vampire.altar;

import com.clanjhoo.vampire.InfectionReason;
import com.clanjhoo.vampire.Perm;
import com.clanjhoo.vampire.VampireRevamp;
import com.clanjhoo.vampire.entity.MLang;
import com.clanjhoo.vampire.entity.UPlayer;
import com.clanjhoo.vampire.util.CollectionUtil;
import com.clanjhoo.vampire.util.FxUtil;
import com.clanjhoo.vampire.util.ResourceUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class AltarDark extends Altar {
    public AltarDark(VampireRevamp plugin) {
        this.plugin = plugin;
        this.name = this.plugin.mLang.altarDarkName;
        this.desc = this.plugin.mLang.altarDarkDesc;

        this.coreMaterial = Material.GOLD_BLOCK;

        this.materialCounts = CollectionUtil.map(
                Material.OBSIDIAN, 30,
                Material.COBWEB, 5,
                Material.DEAD_BUSH, 5,
                Material.DIAMOND_BLOCK, 2);

        this.resources = CollectionUtil.list(
                new ItemStack(Material.MUSHROOM_STEW, 1),
                new ItemStack(Material.BONE, 10),
                new ItemStack(Material.GUNPOWDER, 10),
                new ItemStack(Material.REDSTONE, 10)
        );
    }

    @Override
    public boolean use(final UPlayer uplayer, final Player player) {
        boolean success = false;
        uplayer.msg("");
        uplayer.msg(this.desc);

        if (Perm.ALTAR_DARK.has(player, true)) {

            uplayer.msg(this.plugin.mLang.altarDarkCommon);
            FxUtil.ensure(PotionEffectType.BLINDNESS, player, 12 * 20);
            uplayer.runFxSmoke();

            if (uplayer.isHealthy()) {
                if (ResourceUtil.playerRemoveAttempt(player, this.resources, this.plugin.mLang.altarResourceSuccess, this.plugin.mLang.altarResourceFail)) {
                    String healthy = this.plugin.mLang.altarDarkHealthy;
                    Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
                        public void run() {
                            uplayer.msg(healthy);
                            player.getWorld().strikeLightningEffect(player.getLocation().add(0, 3, 0));
                            uplayer.runFxSmokeBurst();
                            uplayer.addInfection(0.01D, InfectionReason.ALTAR, null);
                        }
                    }, 1);
                    success = true;
                }
            } else if (uplayer.isVampire()) {
                uplayer.msg(this.plugin.mLang.altarDarkVampire);
            } else if (uplayer.isInfected()) {
                uplayer.msg(this.plugin.mLang.altarDarkInfected);
            }
        }

        return success;
    }
}
