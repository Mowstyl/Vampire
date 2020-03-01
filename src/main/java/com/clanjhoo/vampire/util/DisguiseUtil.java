package com.clanjhoo.vampire.util;

import com.clanjhoo.vampire.VampireRevamp;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class DisguiseUtil {
    public static VampireRevamp plugin;

    public static void disguiseEntity(Entity e, DisguiseType disguise) {
        DisguiseAPI.disguiseToAll(e, new MobDisguise(disguise));
    }

    public static void disguiseBat(LivingEntity e) {
        disguiseEntity(e, DisguiseType.BAT);
    }
}
