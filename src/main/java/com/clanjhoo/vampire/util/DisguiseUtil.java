package com.clanjhoo.vampire.util;

import com.clanjhoo.vampire.VampireRevamp;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.TargetedDisguise;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class DisguiseUtil {
    public static VampireRevamp plugin;

    public static void disguiseEntity(Entity e, DisguiseType disguise) {
        TargetedDisguise theDisguise = new MobDisguise(disguise);
        //theDisguise.setDisguiseTarget(TargetedDisguise.TargetType.SHOW_TO_EVERYONE_BUT_THESE_PLAYERS);
        //for (String observer : theDisguise.getObservers()) {
        //    theDisguise.removePlayer(observer);
        //}
        DisguiseAPI.disguiseEntity(e, e, theDisguise);
    }

    public static void disguiseBat(LivingEntity e) {
        disguiseEntity(e, DisguiseType.BAT);
    }
}
