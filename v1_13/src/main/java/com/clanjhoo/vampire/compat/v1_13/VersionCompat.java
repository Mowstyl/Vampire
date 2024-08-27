package com.clanjhoo.vampire.compat.v1_13;

import com.clanjhoo.vampire.compat.AbstractVersionCompat;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.projectiles.BlockProjectileSource;
import org.bukkit.projectiles.ProjectileSource;

public class VersionCompat extends AbstractVersionCompat {
    @Override
    public Material getShortGrass() {
        return Material.GRASS;
    }

    @Override
    public EntityType getMooshroom() {
        return EntityType.MUSHROOM_COW;
    }

    @Override
    public EntityType getSnowman() {
        return EntityType.SNOWMAN;
    }

    @Override
    public PotionEffectType getNauseaEffect() {
        return PotionEffectType.CONFUSION;
    }

    @Override
    public PotionEffectType getJumpEffect() {
        return PotionEffectType.JUMP;
    }

    @Override
    public PotionEffectType getSlownessEffect() {
        return PotionEffectType.SLOW;
    }

    @Override
    public PotionEffectType getStrengthEffect() {
        return PotionEffectType.INCREASE_DAMAGE;
    }

    @Override
    public PotionType getBasePotionType(PotionMeta meta) {
        return meta.getBasePotionData().getType();
    }

    @Override
    public void setBasePotionType(PotionMeta meta, PotionType type) {
        PotionData orig = meta.getBasePotionData();
        PotionData pd = new PotionData(type, orig.isExtended(), orig.isUpgraded());
        meta.setBasePotionData(pd);
    }

    @Override
    public ItemFlag getHidePotionEffectsFlag() {
        return ItemFlag.HIDE_POTION_EFFECTS;
    }

    @Override
    public EntityDamageEvent getProjectileDamageEvent(Player target, Projectile projectile, double damage) {
        EntityDamageEvent triggeredEvent;
        ProjectileSource shooter = projectile.getShooter();
        if (shooter instanceof Entity) {
            triggeredEvent = new EntityDamageByEntityEvent(
                    ((Entity) shooter),
                    target,
                    DamageCause.CUSTOM,
                    damage);
        }
        else if (shooter instanceof BlockProjectileSource) {
            triggeredEvent = new EntityDamageByBlockEvent(
                    ((BlockProjectileSource) shooter).getBlock(),
                    target,
                    DamageCause.CUSTOM,
                    damage);
        }
        else {
            triggeredEvent = new EntityDamageEvent(
                    target,
                    DamageCause.CUSTOM,
                    damage);
        }

        return triggeredEvent;
    }
}
