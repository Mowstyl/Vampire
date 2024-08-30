package com.clanjhoo.vampire.compat;

import com.clanjhoo.vampire.VampireRevamp;
import com.clanjhoo.vampire.util.SemVer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.*;
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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Pattern;


public final class VersionCompat {
    public final static SemVer v1_21 = new SemVer(1, 21);  // Breeze mob
    public final static SemVer v1_20_6 = new SemVer(1, 20, 6);
    public final static SemVer v1_20_5 = new SemVer(1, 20, 5);
    public final static SemVer v1_20_3 = new SemVer(1, 20, 3);
    public final static SemVer v1_20_2 = new SemVer(1, 20, 2);
    public final static SemVer v1_19 = new SemVer(1, 19);  // Warden
    public final static SemVer v1_18 = new SemVer(1, 18);  // NamespacedKeys
    public final static SemVer v1_17 = new SemVer(1, 17);  // AbstractSkeleton
    public final static SemVer v1_16_2 = new SemVer(1, 16, 2);  // EntityCategory
    public final static SemVer v1_16 = new SemVer(1, 16);  // Nether Update
    public final static SemVer v1_15 = new SemVer(1, 15);  // Bees
    public final static SemVer v1_14 = new SemVer(1, 14);  // PersistentDataContainer
    public final static SemVer v1_13 = new SemVer(1, 13);
    public final static SemVer minSupported = new SemVer(1, 17);  // Java 17

    private final SemVer currentVersion;
    private final VampireRevamp plugin;
    private final Set<EntityType> undeadMobsPre1205;


    public VersionCompat(@NotNull VampireRevamp plugin, @NotNull SemVer serverVersion) {
        this.plugin = plugin;
        currentVersion = serverVersion;
        if (currentVersion.compareTo(minSupported) < 0) {
            plugin.log(Level.WARNING,
                    "The minimum supported version is " + minSupported
                            + ". Earlier versions will not work as they should.");
        }
        undeadMobsPre1205 = new HashSet<>();
        undeadMobsPre1205.add(EntityType.ZOMBIE);
        undeadMobsPre1205.add(EntityType.ZOMBIE_VILLAGER);
        undeadMobsPre1205.add(EntityType.HUSK);
        undeadMobsPre1205.add(EntityType.DROWNED);
        undeadMobsPre1205.add(EntityType.ZOMBIE_HORSE);
        undeadMobsPre1205.add(EntityType.SKELETON_HORSE);
        undeadMobsPre1205.add(EntityType.SKELETON);
        undeadMobsPre1205.add(EntityType.STRAY);
        undeadMobsPre1205.add(EntityType.WITHER_SKELETON);
        undeadMobsPre1205.add(EntityType.WITHER);
        undeadMobsPre1205.add(EntityType.PHANTOM);
        undeadMobsPre1205.add(getEntityTypeByName("zombified_piglin"));
        if (currentVersion.compareTo(v1_16) >= 0)
            undeadMobsPre1205.add(EntityType.valueOf("ZOGLIN"));
    }

    @NotNull
    public ItemFlag getHidePotionEffectsFlag() {
        if (currentVersion.compareTo(v1_20_5) < 0) {
            return ItemFlag.valueOf("HIDE_POTION_EFFECTS");
        }
        return ItemFlag.valueOf("HIDE_ADDITIONAL_TOOLTIP");
    }

    private static final Pattern VALID_KEY = Pattern.compile("[a-z0-9/._-]+");

    @Nullable
    public static NamespacedKey keyFromString(@NotNull String raw) {
        // Extracted from SpigotMC NamespacedKey.fromString
        if (raw.isEmpty())
            throw new IllegalArgumentException("Input string must not be empty or null");
        raw = raw.toLowerCase();
        String[] components = raw.split(":", 3);
        if (components.length > 2) {
            return null;
        } else {
            String key = components.length == 2 ? components[1] : "";
            String namespace;
            if (components.length == 1) {
                namespace = components[0];
                if (!namespace.isEmpty() && VALID_KEY.matcher(namespace).matches()) {
                    return NamespacedKey.minecraft(namespace);
                } else {
                    return null;
                }
            } else if (components.length == 2 && !VALID_KEY.matcher(key).matches()) {
                return null;
            } else {
                namespace = components[0];
                if (namespace.isEmpty()) {
                    return NamespacedKey.minecraft(key);
                } else {
                    return !VALID_KEY.matcher(namespace).matches() ? null : new NamespacedKey(namespace, key);
                }
            }
        }
    }

    @Nullable
    public Material getMaterialByName(@NotNull String name) {
        NamespacedKey key = keyFromString(name);
        if(key == null)
            throw new IllegalArgumentException(name + " is not a valid key");
        String raw = key.getKey().toString();

        if (raw.equalsIgnoreCase("mushroom_soup"))
            name = new NamespacedKey(key.getNamespace(), "mushroom_stew").toString();
        else if (raw.equalsIgnoreCase("sulphur"))
            name = new NamespacedKey(key.getNamespace(), "gunpowder").toString();
        else {
            if (currentVersion.compareTo(v1_20_3) >= 0) {
                if (raw.equalsIgnoreCase("grass"))
                    name = new NamespacedKey(key.getNamespace(), "short_grass").toString();
            }
            else {
                if (raw.equalsIgnoreCase("short_grass"))
                    name = new NamespacedKey(key.getNamespace(), "grass").toString();
            }
            if (currentVersion.compareTo(v1_13) >= 0) {
                if (raw.equalsIgnoreCase("red_rose"))
                    name = new NamespacedKey(key.getNamespace(), "poppy").toString();
                if (raw.equalsIgnoreCase("yellow_flower"))
                    name = new NamespacedKey(key.getNamespace(), "dandelion").toString();
            }
            else {
                if (raw.equalsIgnoreCase("poppy"))
                    name = new NamespacedKey(key.getNamespace(), "red_rose").toString();
                if (raw.equalsIgnoreCase("dandelion"))
                    name = new NamespacedKey(key.getNamespace(), "yellow_flower").toString();
            }
        }

        Material type = Material.matchMaterial(name);
        if (type == null) {
            type = Material.matchMaterial(name, true);
            if (type != null)
                plugin.log(Level.WARNING, "Material " + raw + " is legacy. Please use " + type.name() + " instead.");
        }

        return type;
    }

    @Nullable
    public PotionEffectType getPotionEffectByName(@NotNull String name) {
        PotionEffectType type;
        NamespacedKey key = keyFromString(name);
        if(key == null)
            throw new IllegalArgumentException(name + " is not a valid key");
        String raw = key.getKey().toString();

        if (currentVersion.compareTo(v1_18) >= 0) {
            if (raw.equalsIgnoreCase("jump"))
                key = new NamespacedKey(key.getNamespace(), "jump_boost");
            else if (raw.equalsIgnoreCase("confusion"))
                key = new NamespacedKey(key.getNamespace(), "nausea");
            else if (raw.equalsIgnoreCase("increase_damage"))
                key = new NamespacedKey(key.getNamespace(), "strength");
            else if (raw.equalsIgnoreCase("slow"))
                key = new NamespacedKey(key.getNamespace(), "slowness");
        }
        else {
            if (raw.equalsIgnoreCase("jump_boost"))
                key = new NamespacedKey(key.getNamespace(), "jump");
            else if (raw.equalsIgnoreCase("nausea"))
                key = new NamespacedKey(key.getNamespace(), "confusion");
            else if (raw.equalsIgnoreCase("strength"))
                key = new NamespacedKey(key.getNamespace(), "increase_damage");
            else if (raw.equalsIgnoreCase("slowness"))
                key = new NamespacedKey(key.getNamespace(), "slow");
        }

        if (Keyed.class.isAssignableFrom(PotionEffectType.class)) {
            try {
                type = (PotionEffectType) PotionEffectType.class.getMethod("getByKey", NamespacedKey.class).invoke(null, key);
            } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException ex) {
                throw new RuntimeException(ex);
            }
        }
        else {
            type = PotionEffectType.getByName(key.getKey());
        }

        return type;
    }

    @Nullable
    public EntityType getEntityTypeByName(@NotNull String name) {
        NamespacedKey key = keyFromString(name);
        if(key == null)
            throw new IllegalArgumentException(name + " is not a valid key");
        String raw = key.getKey();

        if (currentVersion.compareTo(v1_16) >= 0) {
            if (raw.equalsIgnoreCase("pig_zombie"))
                key = new NamespacedKey(key.getNamespace(), "zombified_piglin");
        }
        else {
            if (raw.equalsIgnoreCase("zombified_piglin"))
                key = new NamespacedKey(key.getNamespace(), "pig_zombie");
        }

        return EntityType.fromName(key.getKey());
    }

    @Nullable
    @Contract("null -> null")
    public PotionType getBasePotionType(@Nullable PotionMeta meta) {
        if (meta == null)
            return null;
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

    public void setBasePotionType(@Nullable PotionMeta meta, @Nullable PotionType type) {
        if (meta == null)
            return;
        try {
            if (currentVersion.compareTo(v1_20_2) < 0) {
                if (type == null)
                    throw new IllegalArgumentException("PotionType cannot be null for versions before " + v1_20_2);
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
            else {
                aux = PotionMeta.class.getMethod("setBasePotionType", PotionType.class);
                aux = PotionMeta.class.getMethod("getBasePotionType");
            }
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isUndead(@Nullable EntityType entityType) {
        if (entityType == null || !entityType.isAlive())
            return false;

        if (currentVersion.compareTo(v1_20_5) < 0) {
            return undeadMobsPre1205.contains(entityType);
        }
        Tag<EntityType> undeadTag;

        try {
            // ENTITY_TYPES_INVERTED_HEALING_AND_HARM
            // ENTITY_TYPES_IGNORES_POISON_AND_REGEN
            // ENTITY_TYPES_SENSITIVE_TO_SMITE
            // ENTITY_TYPES_WITHER_FRIENDS <- NOPE, it also has Ghast
            undeadTag = (Tag<EntityType>) Tag.class.getField("ENTITY_TYPES_INVERTED_HEALING_AND_HARM").get(null);
        }
        catch (NoSuchFieldException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }

        return undeadTag.isTagged(entityType);
    }

    public boolean isSkeleton(@Nullable EntityType entityType) {
        if (entityType == null || !entityType.isAlive())
            return false;
        if (entityType == EntityType.SKELETON_HORSE || entityType == EntityType.WITHER)
            return true;

        if (currentVersion.compareTo(v1_20_5) >= 0) {
            try {
                Tag<EntityType> arthropodTag = (Tag<EntityType>) Tag.class.getField("ENTITY_TYPES_SKELETONS").get(null);
                return arthropodTag.isTagged(entityType);
            } catch (NoSuchFieldException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }

        Class<? extends Entity> entityClazz = entityType.getEntityClass();
        Class<?> skeletonClazz = Skeleton.class;
        if (currentVersion.compareTo(v1_17) >= 0) {
            try {
                skeletonClazz = Class.forName("org.bukkit.entity.AbstractSkeleton");
            }
            catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }

        return skeletonClazz.isAssignableFrom(entityClazz);
    }

    public boolean isHumanoid(@Nullable EntityType entityType) {
        if (entityType == null || !entityType.isAlive() || entityType == EntityType.RAVAGER)
            return false;
        if (entityType == EntityType.ENDERMAN
                || (currentVersion.compareTo(v1_19) >= 0 && entityType == EntityType.valueOf("WARDEN")))
            return true;
        Class<? extends Entity> entityClazz = entityType.getEntityClass();
        if (AbstractVillager.class.isAssignableFrom(entityClazz))
            return true;
        if (Raider.class.isAssignableFrom(entityClazz))
            return true;

        Class<?> piglinClazz;
        try {
            if (currentVersion.compareTo(v1_16_2) >= 0)
                piglinClazz = Class.forName("org.bukkit.entity.PiglinAbstract");
            else if (currentVersion.compareTo(v1_16) >= 0)
                piglinClazz = Class.forName("org.bukkit.entity.Piglin");
            else
                return false;
        }
        catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }

        return piglinClazz.isAssignableFrom(entityClazz);
    }

    public boolean isArthropod(@Nullable EntityType entityType) {
        if (entityType == null || !entityType.isAlive())
            return false;

        if (currentVersion.compareTo(v1_20_5) >= 0) {
            try {
                Tag<EntityType> arthropodTag = (Tag<EntityType>) Tag.class.getField("ENTITY_TYPES_ARTHROPOD").get(null);
                return arthropodTag.isTagged(entityType);
            } catch (NoSuchFieldException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }

        Class<? extends Entity> entityClazz = entityType.getEntityClass();
        if (Spider.class.isAssignableFrom(entityClazz)
                || entityType == EntityType.SILVERFISH
                || entityType == EntityType.ENDERMITE)
            return true;

        return currentVersion.compareTo(v1_15) >= 0 && entityType == EntityType.valueOf("BEE");
    }

    public boolean isGolem(@Nullable EntityType entityType) {
        if (entityType == null || !entityType.isAlive())
            return false;

        Class<? extends Entity> entityClazz = entityType.getEntityClass();
        return Golem.class.isAssignableFrom(entityClazz);
    }

    public boolean isSlime(@Nullable EntityType entityType) {
        if (entityType == null || !entityType.isAlive())
            return false;

        Class<? extends Entity> entityClazz = entityType.getEntityClass();
        return Slime.class.isAssignableFrom(entityClazz);
    }

    public boolean isElemental(@Nullable EntityType entityType) {
        if (entityType == null || !entityType.isAlive())
            return false;

        boolean result = entityType == EntityType.BLAZE;
        if (!result && currentVersion.compareTo(v1_21) >= 0)
            return entityType == EntityType.valueOf("BREEZE");
        return result;
    }

    public boolean isSpirit(@Nullable EntityType entityType) {
        if (entityType == null || !entityType.isAlive())
            return false;

        boolean result = entityType == EntityType.GHAST || entityType == EntityType.VEX;
        if (!result && currentVersion.compareTo(v1_21) >= 0)
            result = entityType == EntityType.valueOf("ALLAY");

        return result;
    }

    public boolean isBoss(@Nullable EntityType entityType) {
        if (entityType == null || !entityType.isAlive())
            return false;

        Class<? extends Entity> entityClazz = entityType.getEntityClass();
        if (Boss.class.isAssignableFrom(entityClazz))
            return true;

        return entityType == EntityType.ELDER_GUARDIAN
                || (currentVersion.compareTo(v1_19) >= 0 && entityType == EntityType.valueOf("WARDEN"));
    }

    @NotNull
    public EntityDamageEvent getProjectileDamageEvent(@NotNull Player target, @NotNull Projectile projectile, double damage) {
        EntityDamageEvent triggeredEvent;
        ProjectileSource shooter = projectile.getShooter();
        DamageCause cause = DamageCause.CUSTOM;

        List<Object> initArgs;
        Constructor<? extends EntityDamageEvent> constructor;

        try {
            if (currentVersion.compareTo(v1_20_5) >= 0) {
                Class<?> dmgSourceClazz = Class.forName("org.bukkit.damage.DamageSource");
                Class<?> dmgSrcBuilderClazz = Class.forName("org.bukkit.damage.DamageSource$Builder");
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
                Class<?> dmgSrcBuilderClazz = Class.forName("org.bukkit.damage.DamageSource$Builder");
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
        getHidePotionEffectsFlag();

        if(getPotionEffectByName("jump_boost") == null)
            throw new AssertionError("Error testing jump_boost effect getter");
        if(getPotionEffectByName("jump_boost") != getPotionEffectByName("jump"))
            throw new AssertionError("Error comparing jump effect getters");
        if (currentVersion.compareTo(v1_18) >= 0) {
            if (getPotionEffectByName("minecraft:blindness") == null)
                throw new AssertionError("Error testing blindness effect getter with namespace");
        }
        if(getPotionEffectByName("nausea") == null)
            throw new AssertionError("Error testing nausea effect getter");
        if(getPotionEffectByName("strength") == null)
            throw new AssertionError("Error testing strength effect getter");
        if(getPotionEffectByName("slowness") == null)
            throw new AssertionError("Error testing slowness effect getter");

        if(getEntityTypeByName("PIG_ZOMBIE") != getEntityTypeByName("zombified_piglin"))
            throw new AssertionError("Error testing zombified piglin entity type getter");
        if (currentVersion.compareTo(v1_18) >= 0) {
            if(getEntityTypeByName("minecraft:zombie") != EntityType.ZOMBIE)
                throw new AssertionError("Error testing zombie entity type getter with namespace");
        }

        testPotionMetaMethods();
        testEventMethods();
    }
}
