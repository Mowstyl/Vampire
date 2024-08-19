package com.clanjhoo.vampire.listeners;
import com.clanjhoo.vampire.Perm;
import com.clanjhoo.vampire.VampireRevamp;
import com.clanjhoo.vampire.entity.VPlayer;
import com.clanjhoo.vampire.util.EntityUtil;
import com.clanjhoo.vampire.util.ResourceUtil;
import me.libraryaddict.disguise.events.DisguiseEvent;
import me.libraryaddict.disguise.events.UndisguiseEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class DisguiseListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDisguise(DisguiseEvent event) {
        if (!VampireRevamp.getVampireConfig().vampire.batusi.preventDisguise)
            return;
        Entity aux = event.getEntity();
        if (!EntityUtil.isPlayer(aux))
            return;

        final Player player = (Player) aux;
        VPlayer vPlayer = VampireRevamp.getVPlayerNow(player);
        if (vPlayer == null || !vPlayer.isChangingDisguise())
            return;
        if (!ResourceUtil.hasPermission(player, Perm.MODE_BATUSI_DISGUISE))
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onUndisguise(UndisguiseEvent event) {
        if (!VampireRevamp.getVampireConfig().vampire.batusi.preventDisguise)
            return;
        Entity aux = event.getEntity();
        if (!EntityUtil.isPlayer(aux))
            return;

        final Player player = (Player) aux;
        VPlayer vPlayer = VampireRevamp.getVPlayerNow(player);
        if (vPlayer == null || !vPlayer.isChangingDisguise())
            return;
        // This event is also called when the player already has a disguise before using batusi.
        // We don't care about it in that case.
        if (!ResourceUtil.hasPermission(player, Perm.MODE_BATUSI_DISGUISE)) {
            event.setCancelled(true);
        }
    }
}
