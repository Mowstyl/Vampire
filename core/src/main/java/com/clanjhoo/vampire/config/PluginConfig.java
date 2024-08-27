package com.clanjhoo.vampire.config;

import com.clanjhoo.vampire.VampireRevamp;
import com.clanjhoo.vampire.util.CollectionUtil;
import com.clanjhoo.vampire.util.SemVer;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;
import java.util.logging.Level;

public class PluginConfig {
    private final int lastVersion = 1;
    private final int version;
    public final GeneralConfig general;
    public final CompatConfig compatibility;
    public final FXConfig specialEffects;
    public final VampireConfig vampire;
    public final PotionEffectsConfig potionEffects;
    public final TruceConfig truce;
    public final InfectionConfig infection;
    public final TradeConfig trade;
    public final Map<EntityType, Double> fullFoodQuotient;
    public final HolyWaterConfig holyWater;
    public final AltarConfig altar;
    public final RadiationConfig radiation;
    public final StorageConfig storage;

    private static final double NO_BLOOD = 0;
    private static final double NON_HUMAN = 0.25;
    private static final double BOSS = 0.75;
    private static final double HUMAN = 0.5;
    private static final double PLAYER = 1;

    private static final Map<EntityType, Double> baseFoodQuotient = CollectionUtil.map(
            EntityType.PLAYER, PLAYER,
            EntityType.BAT, NON_HUMAN,
            EntityType.CHICKEN, NON_HUMAN,
            EntityType.COW, NON_HUMAN,
            EntityType.DONKEY, NON_HUMAN,
            EntityType.HORSE, NON_HUMAN,
            VampireRevamp.getVersionCompat().getMooshroom(), NON_HUMAN,
            EntityType.MULE, NON_HUMAN,
            EntityType.OCELOT, NON_HUMAN,
            EntityType.PARROT, NON_HUMAN,
            EntityType.PIG, NON_HUMAN,
            EntityType.RABBIT, NON_HUMAN,
            EntityType.SHEEP, NON_HUMAN,
            VampireRevamp.getVersionCompat().getSnowman(), NO_BLOOD,
            EntityType.SQUID, NON_HUMAN,
            EntityType.VILLAGER, HUMAN,
            EntityType.IRON_GOLEM, NO_BLOOD,
            EntityType.LLAMA, NON_HUMAN,
            EntityType.POLAR_BEAR, NON_HUMAN,
            EntityType.WOLF, NON_HUMAN,
            EntityType.CAVE_SPIDER, NO_BLOOD,
            EntityType.ENDERMAN, HUMAN,
            EntityType.SPIDER, NO_BLOOD,
            EntityType.BLAZE, NO_BLOOD,
            EntityType.CREEPER, NO_BLOOD,
            EntityType.ELDER_GUARDIAN, BOSS,
            EntityType.ENDERMITE, NO_BLOOD,
            EntityType.EVOKER, HUMAN,
            EntityType.GHAST, NON_HUMAN,
            EntityType.GUARDIAN, NON_HUMAN,
            EntityType.MAGMA_CUBE, NO_BLOOD,
            EntityType.SHULKER, NO_BLOOD,
            EntityType.SILVERFISH, NO_BLOOD,
            EntityType.SLIME, NO_BLOOD,
            EntityType.VEX, NO_BLOOD,
            EntityType.VINDICATOR, HUMAN,
            EntityType.WITCH, HUMAN,
            EntityType.ENDER_DRAGON, BOSS,
            EntityType.ILLUSIONER, HUMAN
    );

    public static boolean writeLine(BufferedWriter configWriter, String text, String indent, int level) {
        boolean res = false;
        try {
            for (int i = 0; i < level; i++)
                configWriter.write(indent);
            configWriter.write(text);
            configWriter.newLine();
            res = true;
        } catch (IOException ex) {
            VampireRevamp.log(Level.WARNING, "Error while indenting!");
        }
        return res;
    }

    protected static boolean writeMap(BufferedWriter configWriter, String header, Map<?, ?> itemMap, String indent, int level) {
        boolean res;
        try {
            for (int i = 0; i < level; i++)
                configWriter.write(indent);
            configWriter.write(header);

            if (itemMap.isEmpty()) {
                configWriter.write(" []");
                configWriter.newLine();
                res = true;
            }
            else {
                configWriter.newLine();
                res = true;
                for (Map.Entry<?, ?> entry : itemMap.entrySet()) {
                    String key = entry.getKey().toString();
                    if (entry.getKey() instanceof PotionEffectType)
                        key = VampireRevamp.getServerVersion().compareTo(new SemVer(1, 18)) < 0
                                ? ((PotionEffectType) entry.getKey()).getName()
                                : ((PotionEffectType) entry.getKey()).getKey().toString();
                    res = res && writeLine(configWriter, "- " + key + ": " + entry.getValue().toString(), indent, level + 1);
                }
            }
        } catch (IOException ex) {
            res = false;
            VampireRevamp.log(Level.WARNING, "Error while indenting!");
        }
        return res;
    }

    protected static boolean writePotionEffectCollection(BufferedWriter configWriter, String header, Collection<PotionEffect> effects, String indent, int level) {
        boolean res;
        try {
            for (int i = 0; i < level; i++)
                configWriter.write(indent);
            configWriter.write(header);

            if (effects.isEmpty()) {
                configWriter.write(" []");
                configWriter.newLine();
                res = true;
            }
            else {
                configWriter.newLine();
                res = true;
                for (PotionEffect effect : effects) {
                    res = res && writeLine(configWriter, "- data: \"" + effect.toString() + "\"", indent, level + 1);
                }

            }
        } catch (IOException ex) {
            res = false;
            VampireRevamp.log(Level.WARNING, "Error while indenting!");
        }
        return res;
    }

    protected static boolean writeItemCollection(BufferedWriter configWriter, String header, Collection<ItemStack> itemSet, String indent, int level) {
        boolean res;
        try {
            for (int i = 0; i < level; i++)
                configWriter.write(indent);
            configWriter.write(header);

            if (itemSet.isEmpty()) {
                configWriter.write(" []");
                configWriter.newLine();
                res = true;
            }
            else {
                configWriter.newLine();
                res = true;
                for (ItemStack item : itemSet) {
                    ItemMeta meta = item.getItemMeta();
                    res = res && writeLine(configWriter, "- material: " + item.getType(), indent, level + 1);
                    res = res && writeLine(configWriter, "  amount: " + item.getAmount(), indent, level + 1);
                    if (meta instanceof Damageable) {
                        Damageable dmg = (Damageable) meta;
                        res = res && writeLine(configWriter, "  damage: " + dmg.getDamage(), indent, level + 1);
                    }
                    if (meta instanceof PotionMeta) {
                        PotionMeta pd = ((PotionMeta) item.getItemMeta());
                        res = res && writeLine(configWriter, "  meta:", indent, level + 1);
                        res = res && writeLine(configWriter, "type: " + VampireRevamp.getVersionCompat().getBasePotionType(pd), indent, level + 2);
                        // I think no one will ever need this
                        // res = res && writePotionEffectCollection(configWriter, "effects:", pd.getCustomEffects(), indent, level + 2);
                    }
                }

            }
        } catch (IOException ex) {
            res = false;
            VampireRevamp.log(Level.WARNING, "Error while indenting!");
        }
        return res;
    }

    protected static boolean writeCollection(BufferedWriter configWriter, String header, Collection<?> itemSet, String indent, int level) {
        boolean res;
        try {
            for (int i = 0; i < level; i++)
                configWriter.write(indent);
            configWriter.write(header);

            if (itemSet.isEmpty()) {
                configWriter.write(" []");
                configWriter.newLine();
                res = true;
            }
            else {
                configWriter.newLine();
                res = true;
                for (Object o : itemSet)
                    res = res && writeLine(configWriter, "- " + o.toString(), indent, level + 1);
            }
        } catch (IOException ex) {
            res = false;
            VampireRevamp.log(Level.WARNING, "Error while indenting!");
        }
        return res;
    }

    private boolean saveConfigToFile(BufferedWriter configWriter, String indent, int level) {
        boolean result = writeLine(configWriter, "# Vampire Revamped configuration file", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# DO NOT EDIT THIS LINE", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "version: " + version, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Autogenerated for MC " + VampireRevamp.getServerVersion(), indent, level);
        result = result && PluginConfig.writeLine(configWriter, "", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "general:", indent, level);
        result = result && this.general.saveConfigToFile(configWriter, indent, level + 1);
        result = result && PluginConfig.writeLine(configWriter, "compatibility:", indent, level);
        result = result && this.compatibility.saveConfigToFile(configWriter, indent, level + 1);
        result = result && PluginConfig.writeLine(configWriter, "specialEffects:", indent, level);
        result = result && this.specialEffects.saveConfigToFile(configWriter, indent, level + 1);
        result = result && PluginConfig.writeLine(configWriter, "vampire:", indent, level);
        result = result && this.vampire.saveConfigToFile(configWriter, indent, level + 1);
        result = result && PluginConfig.writeLine(configWriter, "potionEffects:", indent, level);
        result = result && this.potionEffects.saveConfigToFile(configWriter, indent, level + 1);
        result = result && PluginConfig.writeLine(configWriter, "truce:", indent, level);
        result = result && this.truce.saveConfigToFile(configWriter, indent, level + 1);
        result = result && PluginConfig.writeLine(configWriter, "infection:", indent, level);
        result = result && this.infection.saveConfigToFile(configWriter, indent, level + 1);
        result = result && PluginConfig.writeLine(configWriter, "trade:", indent, level);
        result = result && this.trade.saveConfigToFile(configWriter, indent, level + 1);
        result = result && writeLine(configWriter, "# Food given by hitting each entity", indent, level);
        result = result && writeMap(configWriter, "fullFoodQuotient:",  this.fullFoodQuotient, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "holyWater:", indent, level);
        result = result && this.holyWater.saveConfigToFile(configWriter, indent, level + 1);
        result = result && PluginConfig.writeLine(configWriter, "altar:", indent, level);
        result = result && this.altar.saveConfigToFile(configWriter, indent, level + 1);
        result = result && PluginConfig.writeLine(configWriter, "radiation:", indent, level);
        result = result && this.radiation.saveConfigToFile(configWriter, indent, level + 1);
        result = result && PluginConfig.writeLine(configWriter, "storage:", indent, level);
        result = result && this.storage.saveConfigToFile(configWriter, indent, level + 1);


        return result;
    }

    public boolean saveConfigToFile(File destination) {
        boolean res = true;
        String indent = "    ";
        try {
            res = destination.createNewFile();
        } catch (IOException ex) {
            res = false;
            ex.printStackTrace();
        }

        if (!res) {
            VampireRevamp.log(Level.WARNING, "Couldn't create default config file!");
            return res;
        }

        FileOutputStream confStream = null;
        BufferedWriter confWriter = null;
        boolean open = false;

        try {
            confStream = new FileOutputStream(destination);
            confWriter = new BufferedWriter(new OutputStreamWriter(confStream));
            open = true;

            res = saveConfigToFile(confWriter, indent, 0);
        } catch (IOException ex) {
            res = false;
            ex.printStackTrace();
        }

        if (!res)
            VampireRevamp.log(Level.WARNING, "Couldn't save default config file!");

        try {
            if (open)
                confWriter.close();
        } catch (IOException ex) {
            VampireRevamp.log(Level.WARNING, "Failed to close the file!");
            ex.printStackTrace();
        }

        return res;
    }

    public static void updateFoodMap() {
        SemVer version = VampireRevamp.getServerVersion();

        if (new SemVer(1, 13).compareTo(version) <= 0) {
            baseFoodQuotient.put(EntityType.COD, NON_HUMAN);
            baseFoodQuotient.put(EntityType.SALMON, NON_HUMAN);
            baseFoodQuotient.put(EntityType.TROPICAL_FISH, NON_HUMAN);
            baseFoodQuotient.put(EntityType.PUFFERFISH, NON_HUMAN);
            baseFoodQuotient.put(EntityType.TURTLE, NON_HUMAN);
            baseFoodQuotient.put(EntityType.DOLPHIN, NON_HUMAN);
        }

        if (new SemVer(1, 14).compareTo(version) <= 0) {
            baseFoodQuotient.put(EntityType.CAT, NON_HUMAN);
            baseFoodQuotient.put(EntityType.FOX, NON_HUMAN);
            baseFoodQuotient.put(EntityType.WANDERING_TRADER, HUMAN);
            baseFoodQuotient.put(EntityType.PANDA, NON_HUMAN);
            baseFoodQuotient.put(EntityType.PILLAGER, HUMAN);
            baseFoodQuotient.put(EntityType.RAVAGER, NON_HUMAN);
        }

        if (new SemVer(1, 15).compareTo(version) <= 0) {
            baseFoodQuotient.put(EntityType.BEE, NO_BLOOD);
        }

        if (new SemVer(1, 16).compareTo(version) <= 0) {
            baseFoodQuotient.put(EntityType.STRIDER, NON_HUMAN);
            baseFoodQuotient.put(EntityType.PIGLIN, HUMAN);
            baseFoodQuotient.put(EntityType.HOGLIN, NON_HUMAN);
        }
    }

    public PluginConfig() {
        updateFoodMap();

        version = lastVersion;
        general = new GeneralConfig();
        compatibility = new CompatConfig();
        specialEffects = new FXConfig();
        vampire = new VampireConfig();
        potionEffects = new PotionEffectsConfig();
        truce = new TruceConfig();
        infection = new InfectionConfig();
        trade = new TradeConfig();
        fullFoodQuotient = baseFoodQuotient;

        holyWater = new HolyWaterConfig();
        altar = new AltarConfig();
        radiation = new RadiationConfig();
        storage = new StorageConfig();
    }

    public PluginConfig(FileConfiguration config) {
        ConfigurationSection aux;

        version = config.getInt("version", lastVersion);

        aux = config.getConfigurationSection("general");
        if (aux != null) {
            general = new GeneralConfig(aux);
        }
        else {
            general = new GeneralConfig();
        }

        aux = config.getConfigurationSection("compatibility");
        if (aux != null) {
            compatibility = new CompatConfig(aux);
        }
        else {
            compatibility = new CompatConfig();
        }

        aux = config.getConfigurationSection("specialEffects");
        if (aux != null) {
            specialEffects = new FXConfig(aux);
        }
        else {
            specialEffects = new FXConfig();
        }

        aux = config.getConfigurationSection("vampire");
        if (aux != null) {
            vampire = new VampireConfig(aux);
        }
        else {
            vampire = new VampireConfig();
        }

        aux = config.getConfigurationSection("potionEffects");
        if (aux != null) {
            potionEffects = new PotionEffectsConfig(aux);
        }
        else {
            potionEffects = new PotionEffectsConfig();
        }

        aux = config.getConfigurationSection("truce");
        if (aux != null) {
            truce = new TruceConfig(aux);
        }
        else {
            truce = new TruceConfig();
        }

        aux = config.getConfigurationSection("infection");
        if (aux != null) {
            infection = new InfectionConfig(aux);
        }
        else {
            infection = new InfectionConfig();
        }

        aux = config.getConfigurationSection("trade");
        if (aux != null) {
            trade = new TradeConfig(aux);
        }
        else {
            trade = new TradeConfig();
        }

        PluginConfig def = new PluginConfig();
        Map<?, ?> auxffq = getMap(config, "fullFoodQuotient");
        Map<EntityType, Double> ffq = null;

        if (auxffq != null) {
            ffq = new HashMap<>();
            for (Map.Entry<?, ?> entry : auxffq.entrySet()) {
                try {
                    EntityType ent = EntityType.valueOf(((String) entry.getKey()).toUpperCase());
                    double quotient = (Double) entry.getValue();

                    ffq.put(ent, quotient);
                }
                catch (IllegalArgumentException ex) {
                    VampireRevamp.log(Level.WARNING, "EntityType " + entry.getKey() + " doesn't exist!");
                    ffq = null;
                    break;
                }
            }
        }

        if (ffq == null)
            ffq = def.fullFoodQuotient;

        fullFoodQuotient = ffq;

        aux = config.getConfigurationSection("holyWater");
        if (aux != null) {
            holyWater = new HolyWaterConfig(aux);
        }
        else {
            holyWater = new HolyWaterConfig();
        }

        aux = config.getConfigurationSection("altar");
        if (aux != null) {
            altar = new AltarConfig(aux);
        }
        else {
            altar = new AltarConfig();
        }

        aux = config.getConfigurationSection("radiation");
        if (aux != null) {
            radiation = new RadiationConfig(aux);
        }
        else {
            radiation = new RadiationConfig();
        }

        aux = config.getConfigurationSection("storage");
        if (aux != null) {
            storage = new StorageConfig(aux);
        }
        else {
            storage = new StorageConfig();
        }
    }

    public static Map<?, ?> getMap(ConfigurationSection cs, String section) {
        Map<Object, Object> auxMap = null;

        if (cs.contains(section)) {
            List<Map<?, ?>> auxLES = cs.getMapList(section);
            auxMap = new HashMap<>();
            for (Map<?, ?> minimap : auxLES) {
                auxMap.putAll(minimap);
            }
        }

        return auxMap;
    }

    public static ItemStack getIngredient(Material material, int amount) {
        return getIngredient(material, amount, null, (short) 0);
    }

    public static ItemStack getIngredient(Material material, int amount, int damage) {
        return getIngredient(material, amount, null, damage);
    }

    public static ItemStack getIngredient(Material material, int amount, PotionType type) {
        return getIngredient(material, amount, type, (short) 0);
    }

    public static ItemStack getIngredient(Material material, int amount, PotionType type, int damage) {
        ItemStack ingredient;
        if (new SemVer(1, 14).compareTo(VampireRevamp.getServerVersion()) <= 0) {
            ingredient = new ItemStack(material, amount);
        }
        else {
            ingredient = new ItemStack(material, amount, (short) damage);
        }

        ItemMeta meta = ingredient.getItemMeta();
        if (meta == null)
            return ingredient;
        if (meta instanceof Damageable) {
            Damageable dmg = (Damageable) meta;
            dmg.setDamage(damage);
        }
        if (meta instanceof PotionMeta) {
            PotionMeta meth = (PotionMeta) meta;
            VampireRevamp.getVersionCompat().setBasePotionType(meth, type);
        }
        ingredient.setItemMeta(meta);

        return ingredient;
    }

    public static Set<ItemStack> getResources(@NotNull List<Map<String, Object>> rawsources) {
        return getResources(rawsources, false);
    }

    public static Set<ItemStack> getResources(List<Map<String, Object>> rawsources, boolean forceBlock) {
        Set<ItemStack> resources = new HashSet<>();

        for (Map<String, Object> source : rawsources) {
            ItemStack item = null;
            String matName = (String) source.get("material");
            int amount = (Integer) source.getOrDefault("amount", 1);

            if (matName != null && amount > 0) {
                Material material = Material.matchMaterial(matName);

                if (material != null && (!forceBlock || material.isBlock())) {
                    item = new ItemStack(material, amount);
                    if (source.containsKey("durability")) {
                        if (new SemVer(1, 14).compareTo(VampireRevamp.getServerVersion()) > 0) {
                            short durability = ((Integer) source.get("durability")).shortValue();
                            item = new ItemStack(material, amount, durability);
                        } else {
                            item = new ItemStack(material, amount);
                            VampireRevamp.log(Level.WARNING, "Ignoring durability (only supported for versions prior 1.14). If you believe this is an error, please contact us in the Spigot discussion.");
                        }
                    }
                    else {
                        item = new ItemStack(material, amount);
                    }

                    if (source.containsKey("meta") && item.getItemMeta() instanceof PotionMeta) {
                        PotionMeta meth = (PotionMeta) item.getItemMeta();
                        PotionType type;
                        Map<String, Object> meta = (Map<String, Object>) source.get("meta");
                        String typeName = (String) meta.get("type");

                        try {
                            type = PotionType.valueOf(typeName);
                            VampireRevamp.getVersionCompat().setBasePotionType(meth, type);
                            item.setItemMeta(meth);
                        }
                        catch (IllegalArgumentException ex) {
                            VampireRevamp.log(Level.WARNING, "PotionType " + typeName + " doesn't exist!");
                            item = null;
                        }
                    }
                    else if (source.containsKey("meta")) {
                        VampireRevamp.log(Level.WARNING, "Ignoring meta (only supported for potions). If you believe this is an error, please contact us in the Spigot discussion.");
                    }
                }
                else {
                    if (forceBlock)
                        VampireRevamp.log(Level.WARNING, "Material " + matName + " doesn't exist or is not a block!");
                    else
                        VampireRevamp.log(Level.WARNING, "Material " + matName + " doesn't exist!");
                }
            }
            else {
                VampireRevamp.log(Level.WARNING, "Bad Material name or amount!");
            }

            if (item == null) {
                resources = null;
                break;
            }
            else {
                resources.add(item);
            }
        }

        return resources;
    }

    @Override
    public String toString() {
        return "PluginConfig{" +
                "general=" + general +
                ", specialEffects=" + specialEffects +
                ", vampire=" + vampire +
                ", potionEffects=" + potionEffects +
                ", truce=" + truce +
                ", infection=" + infection +
                ", trade=" + trade +
                ", fullFoodQuotient=" + fullFoodQuotient +
                ", holyWater=" + holyWater +
                ", altar=" + altar +
                ", radiation=" + radiation +
                '}';
    }
}
