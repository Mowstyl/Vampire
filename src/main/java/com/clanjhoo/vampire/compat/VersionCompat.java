package com.clanjhoo.vampire.compat;

import com.clanjhoo.vampire.VampireRevamp;
import com.clanjhoo.vampire.util.SemVer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
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
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.projectiles.BlockProjectileSource;
import org.bukkit.projectiles.ProjectileSource;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;


public final class VersionCompat {

    private final static SemVer v1_20_6 = new SemVer(1, 20, 6);
    private final static SemVer v1_20_5 = new SemVer(1, 20, 5);
    private final static SemVer v1_20_3 = new SemVer(1, 20, 3);
    private final static SemVer v1_20_2 = new SemVer(1, 20, 2);
    private final static SemVer minSupported = new SemVer(1, 16, 2);

    private final SemVer currentVersion;
    private final VampireRevamp plugin;


    public VersionCompat(VampireRevamp plugin, SemVer serverVersion) {
        this.plugin = plugin;
        currentVersion = serverVersion;
        if (currentVersion.compareTo(minSupported) < 0) {
            plugin.log(Level.WARNING,
                    "The minimum supported version is " + minSupported
                            + ". Earlier versions will not work as they should.");
        }
    }

    public Material getShortGrass() {
        if (currentVersion.compareTo(v1_20_3) < 0) {
            return Material.valueOf("GRASS");
        }
        return Material.valueOf("SHORT_GRASS");
    }

    public EntityType getMooshroom() {
        if (currentVersion.compareTo(v1_20_5) < 0) {
            return EntityType.valueOf("MUSHROOM_COW");
        }
        return EntityType.valueOf("MOOSHROOM");
    }

    public EntityType getSnowman() {
        if (currentVersion.compareTo(v1_20_5) < 0) {
            return EntityType.valueOf("SNOWMAN");
        }
        return EntityType.valueOf("SNOW_GOLEM");
    }

    public ItemFlag getHidePotionEffectsFlag() {
        if (currentVersion.compareTo(v1_20_5) < 0) {
            return ItemFlag.valueOf("HIDE_POTION_EFFECTS");
        }
        return ItemFlag.valueOf("HIDE_ADDITIONAL_TOOLTIP");
    }

    public PotionEffectType getNauseaEffect() {
        if (currentVersion.compareTo(v1_20_5) < 0) {
            return PotionEffectType.getByName("CONFUSION");
        }
        return PotionEffectType.getByName("NAUSEA");
    }

    public PotionEffectType getJumpEffect() {
        if (currentVersion.compareTo(v1_20_5) < 0) {
            return PotionEffectType.getByName("JUMP");
        }
        return PotionEffectType.getByName("JUMP_BOOST");
    }

    public PotionEffectType getSlownessEffect() {
        if (currentVersion.compareTo(v1_20_5) < 0) {
            return PotionEffectType.getByName("SLOW");
        }
        return PotionEffectType.getByName("SLOWNESS");
    }

    public PotionEffectType getStrengthEffect() {
        if (currentVersion.compareTo(v1_20_5) < 0) {
            return PotionEffectType.getByName("INCREASE_DAMAGE");
        }
        return PotionEffectType.getByName("STRENGTH");
    }

    public PotionType getBasePotionType(PotionMeta meta) {
        try {
            if (currentVersion.compareTo(v1_20_2) < 0) {
                Class<?> potDataClazz = Class.forName("org.bukkit.potion.PotionData");
                Object pd = PotionMeta.class.getMethod("getBasePotionData").invoke(meta);
                return (PotionType) potDataClazz.getMethod("getType").invoke(pd);
            }
            return (PotionType) PotionMeta.class
                    .getMethod("getBasePotionType")
                    .invoke(meta);
        } catch (ClassNotFoundException | InvocationTargetException
                 | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public void setBasePotionType(PotionMeta meta, PotionType type) {
        try {
            if (currentVersion.compareTo(v1_20_2) < 0) {
                Class<?> potDataClazz = Class.forName("org.bukkit.potion.PotionData");
                Object orig = PotionMeta.class.getMethod("getBasePotionData").invoke(meta);
                Object pd = potDataClazz.getConstructor(PotionType.class, boolean.class, boolean.class)
                        .newInstance(
                                type,
                                potDataClazz.getMethod("isExtended").invoke(orig),
                                potDataClazz.getMethod("isUpgraded").invoke(orig));
                PotionMeta.class.getMethod("setBasePotionData", potDataClazz).invoke(meta, pd);
                return;
            }
            PotionMeta.class.getMethod("setBasePotionType", PotionType.class).invoke(meta, type);
        } catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException
                 | NoSuchMethodException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    private void testPotionMetaMethods() {
        try {
            Method aux;
            if (currentVersion.compareTo(v1_20_2) < 0) {
                Class<?> potDataClazz = Class.forName("org.bukkit.potion.PotionData");
                Constructor<?> newPotionData = potDataClazz.getConstructor(PotionType.class, boolean.class, boolean.class);
                aux = PotionMeta.class.getMethod("getBasePotionData");
                aux = PotionMeta.class.getMethod("setBasePotionData", potDataClazz);
                aux = potDataClazz.getMethod("isExtended");
                aux = potDataClazz.getMethod("isUpgraded");
                aux = potDataClazz.getMethod("getType");
            }
            aux = PotionMeta.class.getMethod("setBasePotionType", PotionType.class);
            aux = PotionMeta.class.getMethod("getBasePotionType");
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public EntityDamageEvent getProjectileDamageEvent(Player target, Projectile projectile, double damage) {
        EntityDamageEvent triggeredEvent;
        ProjectileSource shooter = projectile.getShooter();
        DamageCause cause = DamageCause.CUSTOM;

        List<Object> initArgs;
        Constructor<? extends EntityDamageEvent> constructor;

        try {
            if (currentVersion.compareTo(v1_20_5) >= 0) {
                Class<?> dmgSourceClazz = Class.forName("org.bukkit.damage.DamageSource");
                Class<?> dmgSrcBuilderClazz = Class.forName("org.bukkit.damage.DamageSource.Builder");
                Class<?> dmgTypeClazz = Class.forName("org.bukkit.damage.DamageType");

                Object magicDmgType = dmgTypeClazz.getField("MAGIC").get(null);
                Object dmgSrcBuilder = dmgSourceClazz.getMethod("builder", dmgTypeClazz).invoke(null, magicDmgType);
                dmgSrcBuilder = dmgSrcBuilderClazz.getMethod("withDirectEntity", Entity.class).invoke(dmgSrcBuilder, projectile);
                dmgSrcBuilder = dmgSrcBuilderClazz.getMethod("withDamageLocation", Location.class).invoke(dmgSrcBuilder, target.getLocation());
                if (shooter instanceof Entity) {
                    dmgSrcBuilder = dmgSrcBuilderClazz.getMethod("withCausingEntity", Entity.class).invoke(dmgSrcBuilder, shooter);
                }
                Object dmgSource = dmgSrcBuilderClazz.getMethod("build").invoke(dmgSrcBuilder);

                if (shooter instanceof Entity) {
                    constructor = EntityDamageByEntityEvent.class.getConstructor(Entity.class, Entity.class, DamageCause.class, dmgSourceClazz, double.class);
                    initArgs = List.of((Entity) shooter, target, cause, dmgSource, damage);
                }
                else if (shooter instanceof BlockProjectileSource) {
                    BlockProjectileSource blockShooter = (BlockProjectileSource) shooter;
                    if (currentVersion.compareTo(v1_20_6) >= 0) {
                        constructor = EntityDamageByBlockEvent.class
                                .getConstructor(Block.class, BlockState.class, Entity.class, DamageCause.class, dmgSourceClazz, double.class);
                        initArgs = List.of(
                                blockShooter.getBlock(),
                                blockShooter.getBlock().getState(),
                                target,
                                cause,
                                dmgSource,
                                damage);
                    }
                    else {

                        if (plugin.isPaperMc()) {
                            constructor = EntityDamageByBlockEvent.class
                                    .getConstructor(Block.class, Entity.class, DamageCause.class, dmgSourceClazz, double.class, BlockState.class);
                            initArgs = List.of(
                                    blockShooter.getBlock(),
                                    target,
                                    DamageCause.CUSTOM,
                                    dmgSource,
                                    damage,
                                    blockShooter.getBlock().getState());
                        }
                        else {
                            constructor = EntityDamageByBlockEvent.class
                                    .getConstructor(Block.class, Entity.class, DamageCause.class, dmgSourceClazz, double.class);
                            initArgs = List.of(blockShooter.getBlock(), target, DamageCause.CUSTOM, dmgSource, damage);
                        }
                    }
                } else {
                    constructor = EntityDamageEvent.class.getConstructor(Entity.class, DamageCause.class, dmgSourceClazz, double.class);
                    initArgs = List.of(target, cause, dmgSource, damage);
                }
            }
            else {
                if (shooter instanceof Entity) {
                    constructor = EntityDamageByEntityEvent.class
                            .getConstructor(Entity.class, Entity.class, DamageCause.class, double.class);
                    initArgs = List.of(shooter, target, cause, damage);
                }
                else if (shooter instanceof BlockProjectileSource) {
                    constructor = EntityDamageByBlockEvent.class
                            .getConstructor(Block.class, Entity.class, DamageCause.class, double.class);
                    initArgs = List.of(((BlockProjectileSource) shooter).getBlock(), target, cause, damage);
                }
                else {
                    constructor = EntityDamageEvent.class
                            .getConstructor(Entity.class, DamageCause.class, double.class);
                    initArgs = List.of(target, cause, damage);
                }
            }
            triggeredEvent = constructor.newInstance(initArgs);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException
                 | NoSuchMethodException | NoSuchFieldException | ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }

        return triggeredEvent;
    }

    private void testEventMethods() {
        Constructor<? extends EntityDamageEvent> constructor;
        Method aux;

        try {
            if (currentVersion.compareTo(v1_20_5) >= 0) {
                Class<?> dmgSourceClazz = Class.forName("org.bukkit.damage.DamageSource");
                Class<?> dmgSrcBuilderClazz = Class.forName("org.bukkit.damage.DamageSource.Builder");
                Class<?> dmgTypeClazz = Class.forName("org.bukkit.damage.DamageType");

                Object magicDmgType = dmgTypeClazz.getField("MAGIC").get(null);
                Object dmgSrcBuilder = dmgSourceClazz.getMethod("builder", dmgTypeClazz).invoke(null, magicDmgType);
                aux = dmgSrcBuilderClazz.getMethod("withDirectEntity", Entity.class);
                aux = dmgSrcBuilderClazz.getMethod("withDamageLocation", Location.class);
                aux = dmgSrcBuilderClazz.getMethod("withCausingEntity", Entity.class);
                Object dmgSource = dmgSrcBuilderClazz.getMethod("build").invoke(dmgSrcBuilder);

                constructor = EntityDamageByEntityEvent.class.getConstructor(Entity.class, Entity.class, DamageCause.class, dmgSourceClazz, double.class);
                if (currentVersion.compareTo(v1_20_6) >= 0) {
                    constructor = EntityDamageByBlockEvent.class
                            .getConstructor(Block.class, BlockState.class, Entity.class, DamageCause.class, dmgSourceClazz, double.class);
                }
                else {

                    if (plugin.isPaperMc()) {
                        constructor = EntityDamageByBlockEvent.class
                                .getConstructor(Block.class, Entity.class, DamageCause.class, dmgSourceClazz, double.class, BlockState.class);
                    }
                    else {
                        constructor = EntityDamageByBlockEvent.class
                                .getConstructor(Block.class, Entity.class, DamageCause.class, dmgSourceClazz, double.class);
                    }
                }
                constructor = EntityDamageEvent.class.getConstructor(Entity.class, DamageCause.class, dmgSourceClazz, double.class);
            }
            else {
                constructor = EntityDamageByEntityEvent.class
                        .getConstructor(Entity.class, Entity.class, DamageCause.class, double.class);
                constructor = EntityDamageByBlockEvent.class
                        .getConstructor(Block.class, Entity.class, DamageCause.class, double.class);
                constructor = EntityDamageEvent.class
                        .getConstructor(Entity.class, DamageCause.class, double.class);
            }
        } catch (InvocationTargetException | IllegalAccessException
                 | NoSuchMethodException | NoSuchFieldException | ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void test() {
        getShortGrass();
        getMooshroom();
        getSnowman();
        getHidePotionEffectsFlag();
        assert getNauseaEffect() != null;
        assert getJumpEffect() != null;
        assert getSlownessEffect() != null;
        assert getStrengthEffect() != null;
        testPotionMetaMethods();
        testEventMethods();
    }
}
