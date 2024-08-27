package com.clanjhoo.vampire.util;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.projectiles.ProjectileSource;

public class EventUtil {
    public static boolean isCombatEvent(EntityDamageEvent event) {
        return (event.getCause() == DamageCause.ENTITY_ATTACK || event.getCause() == DamageCause.PROJECTILE)
                && event instanceof EntityDamageByEntityEvent;
    }

    public static boolean isCloseCombatEvent(EntityDamageEvent event) {
        return event.getCause() == DamageCause.ENTITY_ATTACK
                && event instanceof EntityDamageByEntityEvent;
    }

    public static Entity getLiableDamager(EntityDamageEvent event) {
        Entity ret = null;

        if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent edbeEvent = (EntityDamageByEntityEvent) event;

            ret = edbeEvent.getDamager();
            if (ret instanceof Projectile) {
                Projectile projectile = (Projectile) ret;
                ProjectileSource projectileSource = projectile.getShooter();

                if (projectileSource instanceof Entity) {
                    ret = (Entity) projectileSource;
                }
            }
        }

        return ret;
    }

    public static void setDamage(EntityDamageEvent event, double newDamage) {
        // Check New Damage
        if (Double.isFinite(newDamage)) {
            // Get Old Damage
            final double oldDamage = event.getDamage(DamageModifier.BASE);

            if (Double.isFinite(oldDamage)) {
                // No Change?
                // No Old Damage? (Avoid DBZ)
                if (newDamage != oldDamage && oldDamage != 0) {
                    // Get Factor
                    final double factor = newDamage / oldDamage;

                    // Check Factor
                    if (Double.isFinite(factor)) {
                        // Now scale all damage modifiers!
                        for (DamageModifier modifier : DamageModifier.values()) {
                            // Is this modifier used in the event?
                            if (event.isApplicable(modifier)) {
                                // Is it the base damage? Cool! Just set it directly!
                                // We could have used the factor too but then we might suffer floating point degredation.
                                if (modifier == DamageModifier.BASE) {
                                    event.setDamage(modifier, newDamage);
                                } else {
                                    // Apply Factor
                                    event.setDamage(modifier, factor * event.getDamage(modifier));
                                }
                            }
                        }
                    } else {
                        throw new IllegalStateException("not finite factor: " + factor + " damage: " + newDamage + " oldDamage: " + oldDamage);
                    }
                }
            } else {
                throw new IllegalStateException("not finite oldDamage: " + oldDamage);
            }
        } else {
            throw new IllegalStateException("not finite newDamage: " + newDamage);
        }
    }

    // Same as above but scales directly.
    public static void scaleDamage(EntityDamageEvent event, double factor) {
        // Clean Input
        if (Double.isFinite(factor)) {
            // No Change?
            if (!MathUtil.equalsishNumber(factor, 1)) {
                for (DamageModifier modifier : DamageModifier.values()) {
                    // Is this modifier used in the event?
                    if (event.isApplicable(modifier)) {
                        // Apply Factor
                        event.setDamage(modifier, factor * event.getDamage(modifier));
                    }
                }
            }
        } else {
            throw new IllegalStateException("not finite factor: " + factor);
        }
    }
}
