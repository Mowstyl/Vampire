package com.clanjhoo.vampire.compat;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public abstract class AbstractVersionCompat {
    public abstract Material getShortGrass();

    public abstract EntityType getMooshroom();
    public abstract EntityType getSnowman();

    public abstract PotionEffectType getNauseaEffect();
    public abstract PotionEffectType getJumpEffect();
    public abstract PotionEffectType getSlownessEffect();
    public abstract PotionEffectType getStrengthEffect();

    public abstract PotionType getBasePotionType(PotionMeta meta);
    public abstract void setBasePotionType(PotionMeta meta, PotionType type);

    public abstract ItemFlag getHidePotionEffectsFlag();

    public abstract EntityDamageEvent getProjectileDamageEvent(Player target, Projectile projectile, double damage);
}
